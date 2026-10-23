package com.appointment.webapp.tenancy;

import com.appointment.webapp.service.DataInitializer;
import com.appointment.webapp.service.DemoDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Base64;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Onboards a clinic: validates the site code, creates its own MySQL schema, migrates it, seeds roles and the first admin
 * (and optionally sample doctors), then switches it on. Any failure removes what this call created, so it can be retried.
 */
@Service
@ConditionalOnProperty(name = "app.tenancy.enabled", havingValue = "true")
public class TenantProvisioningService {

    private static final Logger log = LoggerFactory.getLogger(TenantProvisioningService.class);

    public static final Pattern CODE = Pattern.compile("^[a-z][a-z0-9]{2,29}$");
    private static final Set<String> RESERVED = Set.of("www", "app", "api", "admin", "platform", "console", "mail", "static",
            "assets", "demo", "test", "login", "logout", "master", "root", "system", "mediport", "support", "help", "billing");

    public record Request(String siteId, String displayName, String tagline, String address, String phone, String email,
                          boolean sampleData, String waPhoneNumberId, String waAccessToken, String waAppSecret) {
    }

    /** The password is returned once and never stored in plain text. */
    public record Result(TenantInfo tenant, String adminLogin, String adminPassword, String sampleDoctorsNote) {
    }

    private final TenancyProperties props;
    private final MasterRepository master;
    private final TenantRegistry registry;
    private final TenantConnectionProvider connections;
    private final TenantMigrator migrator;
    private final DataInitializer dataInitializer;
    private final TenantSeeder seeder;
    private final DemoDataService demoData;
    private final String user;
    private final String password;
    private final String timezone;
    private final String region;
    private final ConcurrentHashMap<String, Object> locks = new ConcurrentHashMap<>();
    private final java.util.concurrent.ExecutorService cleanThreads = java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor();

    public TenantProvisioningService(TenancyProperties props, MasterRepository master, TenantRegistry registry,
                                     TenantConnectionProvider connections, TenantMigrator migrator,
                                     DataInitializer dataInitializer, TenantSeeder seeder, DemoDataService demoData,
                                     @Value("${spring.datasource.username:root}") String user,
                                     @Value("${spring.datasource.password:}") String password,
                                     @Value("${app.timezone:Asia/Kolkata}") String timezone,
                                     @Value("${app.default-region:IN}") String region) {
        this.props = props;
        this.master = master;
        this.registry = registry;
        this.connections = connections;
        this.migrator = migrator;
        this.dataInitializer = dataInitializer;
        this.seeder = seeder;
        this.demoData = demoData;
        this.user = user;
        this.password = password;
        this.timezone = timezone;
        this.region = region;
    }

    /** null if the code is fine, otherwise what is wrong with it. */
    public String validateCode(String code) {
        if (code == null || !CODE.matcher(code).matches()) {
            return "Use 3-30 lowercase letters or digits, starting with a letter";
        }
        if (RESERVED.contains(code) && !code.equals(props.getLegacySiteId())) {
            return "That code is reserved";
        }
        if (master.siteExists(code)) {
            return "That code is already taken";
        }
        return null;
    }

    public Result provision(Request req) {
        String code = req.siteId() == null ? "" : req.siteId().trim().toLowerCase();
        Object lock = locks.computeIfAbsent(code, k -> new Object());
        synchronized (lock) {
            try {
                return doProvision(code, req);
            } finally {
                locks.remove(code);
            }
        }
    }

    private Result doProvision(String code, Request req) {
        String problem = validateCode(code);
        if (problem != null) throw new IllegalArgumentException(problem);
        if (req.displayName() == null || req.displayName().isBlank()) throw new IllegalArgumentException("Clinic name is required");

        String schema = props.getSchemaPrefix() + code;
        boolean schemaCreated = false;
        boolean rowInserted = false;
        try {
            byte[] keyBytes = new byte[32];
            new SecureRandom().nextBytes(keyBytes);
            try {
                master.insertTenant(new TenantInfo(code, schema, req.displayName().trim(), TenantInfo.PROVISIONING, timezone, region,
                        Base64.getUrlEncoder().withoutPadding().encodeToString(keyBytes), req.tagline(), req.address(), req.phone(),
                        req.email(), null, req.waPhoneNumberId(), req.waAccessToken(), req.waAppSecret(), null, null));
            } catch (org.springframework.dao.DuplicateKeyException e) {
                throw new IllegalArgumentException("That WhatsApp number already belongs to another clinic");
            }
            rowInserted = true;

            createSchema(schema);
            schemaCreated = true;

            String version = migrator.migrate(schema);
            master.setSchemaVersion(code, version, null);
            registry.evict(code);
            TenantInfo tenant = registry.find(code).orElseThrow();

            String adminPassword = TenantSeeder.randomPassword();
            String[] sampleNote = {null};
            inClinic(tenant, () -> {
                dataInitializer.init();
                seeder.createAdmin(adminPassword, req.email(), req.displayName());
                if (req.sampleData()) {
                    var r = demoData.seedDoctors("system");
                    sampleNote[0] = r.created().size() + " sample doctors added (password for all: " + r.password() + ")";
                }
            });
            master.addLogin("admin", code);
            master.setStatus(code, TenantInfo.ACTIVE, null);
            registry.evict(code);
            log.info("Clinic '{}' provisioned in schema {}", code, schema);
            return new Result(registry.find(code).orElseThrow(), "admin@" + code, adminPassword, sampleNote[0]);
        } catch (RuntimeException e) {
            log.error("Provisioning clinic '{}' failed - cleaning up", code, e);
            connections.evict(code);
            if (schemaCreated) dropSchema(schema);
            if (rowInserted) master.deleteTenant(code);
            registry.evict(code);
            throw e;
        }
    }

    public void suspend(String siteId) {
        master.setStatus(siteId, TenantInfo.SUSPENDED, null);
        registry.evict(siteId);
        connections.evict(siteId);
    }

    public void activate(String siteId) {
        master.setStatus(siteId, TenantInfo.ACTIVE, null);
        registry.evict(siteId);
    }

    /** Re-runs the clinic migrations (after a failed deploy). */
    public String retryMigration(String siteId) {
        TenantInfo t = master.findTenant(siteId).orElseThrow(() -> new IllegalArgumentException("Unknown clinic"));
        try {
            String version = migrator.migrate(t.schemaName());
            master.setSchemaVersion(siteId, version, null);
            registry.evict(siteId);
            return version;
        } catch (RuntimeException e) {
            master.setSchemaVersion(siteId, t.schemaVersion(), "Migration failed: " + e.getMessage());
            throw e;
        }
    }

    /** New random password for the clinic's admin; shown once. */
    public String resetAdminPassword(String siteId) {
        TenantInfo t = registry.find(siteId).orElseThrow(() -> new IllegalArgumentException("Unknown clinic"));
        String pw = TenantSeeder.randomPassword();
        boolean[] ok = {false};
        inClinic(t, () -> ok[0] = seeder.resetAdminPassword(pw));
        if (!ok[0]) throw new IllegalStateException("This clinic has no 'admin' user");
        return pw;
    }

    public void updateWhatsApp(String siteId, String phoneNumberId, String accessToken, String appSecret) {
        master.updateWhatsApp(siteId, phoneNumberId, accessToken, appSecret);
        registry.evict(siteId);
    }

    /**
     * Runs clinic work on a fresh thread. A web request already holds an open JPA session bound to the thread
     * (open-in-view) that was created before any clinic was chosen; clinic work needs a session of its own.
     */
    private void inClinic(TenantInfo tenant, Runnable work) {
        try {
            java.util.concurrent.CompletableFuture.runAsync(() -> TenantContext.runAs(tenant, work), cleanThreads).join();
        } catch (java.util.concurrent.CompletionException e) {
            if (e.getCause() instanceof RuntimeException re) throw re;
            throw e;
        }
    }

    // ---- schema handling (names are validated by CODE before they reach SQL)

    private void createSchema(String schema) {
        if (!schema.matches("[a-z0-9_]{3,40}")) throw new IllegalArgumentException("Bad schema name");
        try (Connection c = DriverManager.getConnection(props.getJdbcBase() + "/?" + props.getJdbcParams(), user, password);
             Statement s = c.createStatement()) {
            var rs = s.executeQuery("select count(*) from information_schema.schemata where schema_name = '" + schema + "'");
            rs.next();
            if (rs.getInt(1) > 0) throw new IllegalStateException("Database " + schema + " already exists");
            s.execute("CREATE DATABASE `" + schema + "` CHARACTER SET utf8mb4");
        } catch (java.sql.SQLException e) {
            throw new IllegalStateException("Could not create database " + schema + ": " + e.getMessage(), e);
        }
    }

    private void dropSchema(String schema) {
        try (Connection c = DriverManager.getConnection(props.getJdbcBase() + "/?" + props.getJdbcParams(), user, password);
             Statement s = c.createStatement()) {
            s.execute("DROP DATABASE IF EXISTS `" + schema + "`");
        } catch (Exception e) {
            log.error("Could not remove half-created database {}", schema, e);
        }
    }
}
