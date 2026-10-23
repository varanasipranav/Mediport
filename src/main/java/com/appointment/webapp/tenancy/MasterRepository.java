package com.appointment.webapp.tenancy;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** All access to the master database (plain SQL, no entities). */
@Repository
@ConditionalOnProperty(name = "app.tenancy.enabled", havingValue = "true")
public class MasterRepository {

    private static final String COLS = "site_id, schema_name, display_name, status, timezone, default_region, rx_signing_key, "
            + "tagline, address, phone, email, registration_no, wa_phone_number_id, wa_access_token, wa_app_secret, schema_version, last_error";

    private final JdbcClient jdbc;

    public MasterRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    private static TenantInfo map(java.sql.ResultSet rs, int i) throws java.sql.SQLException {
        return new TenantInfo(rs.getString("site_id"), rs.getString("schema_name"), rs.getString("display_name"),
                rs.getString("status"), rs.getString("timezone"), rs.getString("default_region"), rs.getString("rx_signing_key"),
                rs.getString("tagline"), rs.getString("address"), rs.getString("phone"), rs.getString("email"),
                rs.getString("registration_no"), rs.getString("wa_phone_number_id"), rs.getString("wa_access_token"),
                rs.getString("wa_app_secret"), rs.getString("schema_version"), rs.getString("last_error"));
    }

    private static String cut(String s) {
        return s == null ? null : s.substring(0, Math.min(500, s.length()));
    }

    private static String blank(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }

    public Optional<TenantInfo> findTenant(String siteId) {
        return jdbc.sql("select " + COLS + " from tenants where site_id = :id").param("id", siteId)
                .query(MasterRepository::map).optional();
    }

    public Optional<TenantInfo> findTenantByWhatsAppNumber(String phoneNumberId) {
        return jdbc.sql("select " + COLS + " from tenants where wa_phone_number_id = :id").param("id", phoneNumberId)
                .query(MasterRepository::map).optional();
    }

    public List<TenantInfo> allTenants() {
        return jdbc.sql("select " + COLS + " from tenants order by site_id").query(MasterRepository::map).list();
    }

    public boolean siteExists(String siteId) {
        return jdbc.sql("select count(*) from tenants where site_id = :id").param("id", siteId).query(Long.class).single() > 0;
    }

    public void insertTenant(TenantInfo t) {
        jdbc.sql("insert into tenants (site_id, schema_name, display_name, status, timezone, default_region, rx_signing_key, tagline, address, phone, email, registration_no, wa_phone_number_id, wa_access_token, wa_app_secret) "
                        + "values (:site, :schema, :name, :status, :tz, :region, :key, :tagline, :address, :phone, :email, :reg, :waId, :waToken, :waSecret)")
                .param("site", t.siteId()).param("schema", t.schemaName()).param("name", t.displayName()).param("status", t.status())
                .param("tz", t.timezone()).param("region", t.defaultRegion()).param("key", t.rxSigningKey())
                .param("tagline", t.tagline()).param("address", t.address()).param("phone", t.phone()).param("email", t.email())
                .param("reg", t.registrationNo()).param("waId", blank(t.waPhoneNumberId())).param("waToken", blank(t.waAccessToken()))
                .param("waSecret", blank(t.waAppSecret())).update();
    }

    public void setStatus(String siteId, String status, String error) {
        jdbc.sql("update tenants set status = :s, last_error = :e, provisioned_at = case when :s = 'ACTIVE' and provisioned_at is null then now() else provisioned_at end where site_id = :id")
                .param("s", status).param("e", cut(error)).param("id", siteId).update();
    }

    public void setSchemaVersion(String siteId, String version, String error) {
        jdbc.sql("update tenants set schema_version = :v, last_error = :e where site_id = :id")
                .param("v", version).param("e", cut(error)).param("id", siteId).update();
    }

    public void updateWhatsApp(String siteId, String phoneNumberId, String accessToken, String appSecret) {
        jdbc.sql("update tenants set wa_phone_number_id = :p, wa_access_token = :t, wa_app_secret = :s where site_id = :id")
                .param("p", blank(phoneNumberId)).param("t", blank(accessToken)).param("s", blank(appSecret)).param("id", siteId).update();
    }

    public void deleteTenant(String siteId) {
        jdbc.sql("delete from login_directory where site_id = :id").param("id", siteId).update();
        jdbc.sql("delete from tenants where site_id = :id").param("id", siteId).update();
    }

    // ---- login directory

    public List<String> sitesForUsername(String username) {
        return jdbc.sql("select d.site_id from login_directory d join tenants t on t.site_id = d.site_id where d.username = :u and t.status = 'ACTIVE' order by d.site_id")
                .param("u", username).query(String.class).list();
    }

    public void addLogin(String username, String siteId) {
        jdbc.sql("insert ignore into login_directory (username, site_id) values (:u, :s)").param("u", username).param("s", siteId).update();
    }

    public void removeLogin(String username, String siteId) {
        jdbc.sql("delete from login_directory where username = :u and site_id = :s").param("u", username).param("s", siteId).update();
    }

    // ---- demo requests

    public record DemoRequest(long id, String clinicName, String contactName, String email, String phone, String city,
                              String doctorsCount, String requestedCode, String message, String status, String siteId,
                              java.time.LocalDateTime createdAt) {
    }

    private static DemoRequest mapDemo(java.sql.ResultSet rs, int i) throws java.sql.SQLException {
        var created = rs.getTimestamp("created_at");
        return new DemoRequest(rs.getLong("id"), rs.getString("clinic_name"), rs.getString("contact_name"), rs.getString("email"),
                rs.getString("phone"), rs.getString("city"), rs.getString("doctors_count"), rs.getString("requested_code"),
                rs.getString("message"), rs.getString("status"), rs.getString("site_id"),
                created == null ? null : created.toLocalDateTime());
    }

    public void insertDemoRequest(DemoRequest r, String ip) {
        jdbc.sql("insert into demo_requests (clinic_name, contact_name, email, phone, city, doctors_count, requested_code, message, ip) "
                        + "values (:cn, :ct, :em, :ph, :city, :dc, :code, :msg, :ip)")
                .param("cn", r.clinicName()).param("ct", r.contactName()).param("em", r.email()).param("ph", r.phone())
                .param("city", blank(r.city())).param("dc", blank(r.doctorsCount())).param("code", blank(r.requestedCode()))
                .param("msg", blank(r.message())).param("ip", ip).update();
    }

    public List<DemoRequest> demoRequests(String status) {
        String where = status == null ? "" : " where status = :s";
        var q = jdbc.sql("select id, clinic_name, contact_name, email, phone, city, doctors_count, requested_code, message, status, site_id, created_at from demo_requests"
                + where + " order by id desc limit 200");
        if (status != null) q = q.param("s", status);
        return q.query(MasterRepository::mapDemo).list();
    }

    public Optional<DemoRequest> findDemoRequest(long id) {
        return jdbc.sql("select id, clinic_name, contact_name, email, phone, city, doctors_count, requested_code, message, status, site_id, created_at from demo_requests where id = :id")
                .param("id", id).query(MasterRepository::mapDemo).optional();
    }

    public void decideDemoRequest(long id, String status, String siteId, String by) {
        jdbc.sql("update demo_requests set status = :s, site_id = :site, decided_at = now(), decided_by = :by where id = :id")
                .param("s", status).param("site", siteId).param("by", by).param("id", id).update();
    }

    // ---- platform operators

    public Optional<String[]> findPlatformUser(String username) {
        return jdbc.sql("select username, password_hash from platform_users where username = :u").param("u", username)
                .query((rs, i) -> new String[] {rs.getString(1), rs.getString(2)}).optional();
    }

    public long countPlatformUsers() {
        return jdbc.sql("select count(*) from platform_users").query(Long.class).single();
    }

    public void insertPlatformUser(String username, String hash) {
        jdbc.sql("insert into platform_users (username, password_hash) values (:u, :h)").param("u", username).param("h", hash).update();
    }
}
