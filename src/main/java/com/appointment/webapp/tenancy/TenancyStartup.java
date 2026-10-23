package com.appointment.webapp.tenancy;

import com.appointment.webapp.config.ClinicProperties;
import com.appointment.webapp.service.DataInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Start-up with tenancy on: registers the pre-existing database as the first clinic, migrates every clinic schema,
 * refreshes roles/privileges in each, and backfills the login directory for the first clinic.
 */
@Component
@Order(1)
@ConditionalOnProperty(name = "app.tenancy.enabled", havingValue = "true")
public class TenancyStartup implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(TenancyStartup.class);

    private final TenancyProperties props;
    private final MasterRepository master;
    private final TenantRegistry registry;
    private final TenantMigrator migrator;
    private final DataInitializer dataInitializer;
    private final ClinicProperties clinic;
    private final String rxKey;
    private final String timezone;
    private final String region;
    private final JdbcClient masterJdbc;

    public TenancyStartup(TenancyProperties props, MasterRepository master, TenantRegistry registry, TenantMigrator migrator,
                          DataInitializer dataInitializer, ClinicProperties clinic, JdbcClient masterJdbc,
                          @Value("${app.rx.signing-key:}") String rxKey,
                          @Value("${app.timezone:Asia/Kolkata}") String timezone,
                          @Value("${app.default-region:IN}") String region) {
        this.props = props;
        this.master = master;
        this.registry = registry;
        this.migrator = migrator;
        this.dataInitializer = dataInitializer;
        this.clinic = clinic;
        this.masterJdbc = masterJdbc;
        this.rxKey = rxKey;
        this.timezone = timezone;
        this.region = region;
    }

    @Override
    public void run(ApplicationArguments args) {
        registerLegacyClinic();
        for (TenantInfo t : master.allTenants()) {
            if (!t.active() && !TenantInfo.SUSPENDED.equals(t.status())) continue;
            try {
                String version = migrator.migrate(t.schemaName());
                master.setSchemaVersion(t.siteId(), version, null);
                registry.evict(t.siteId());
                TenantContext.runAs(registry.find(t.siteId()).orElse(t), dataInitializer::init);
            } catch (Exception e) {
                log.error("Clinic {} could not be migrated - it stays offline until fixed", t.siteId(), e);
                master.setSchemaVersion(t.siteId(), null, "Migration failed: " + e.getMessage());
                registry.evict(t.siteId());
            }
        }
        backfillLegacyLogins();
    }

    private void registerLegacyClinic() {
        String schema = props.getLegacySchema();
        String site = props.getLegacySiteId();
        if (schema == null || schema.isBlank() || master.siteExists(site)) return;
        Long exists = masterJdbc.sql("select count(*) from information_schema.schemata where schema_name = :s")
                .param("s", schema).query(Long.class).single();
        if (exists == null || exists == 0) return;
        String key = rxKey == null || rxKey.isBlank() ? java.util.UUID.randomUUID().toString() : rxKey;
        master.insertTenant(new TenantInfo(site, schema, clinic.getName(), TenantInfo.ACTIVE, timezone, region, key,
                clinic.getTagline(), clinic.getAddress(), clinic.getPhone(), clinic.getEmail(), clinic.getRegistrationNo(),
                null, null, null, null, null));
        master.setStatus(site, TenantInfo.ACTIVE, null);
        log.info("Registered existing database '{}' as clinic '{}'", schema, site);
    }

    private void backfillLegacyLogins() {
        String site = props.getLegacySiteId();
        var t = master.findTenant(site);
        if (t.isEmpty() || !t.get().active() || t.get().schemaVersion() == null) return;
        List<String> users = masterJdbc.sql("select username from `" + t.get().schemaName() + "`.users").query(String.class).list();
        users.forEach(u -> master.addLogin(u, site));
    }
}
