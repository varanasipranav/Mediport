package com.appointment.webapp.tenancy;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** app.tenancy.* settings. With enabled=false (default) the app runs as a single clinic exactly as before. */
@ConfigurationProperties(prefix = "app.tenancy")
public class TenancyProperties {

    private boolean enabled = false;
    /** JDBC server URL without schema, e.g. jdbc:mysql://localhost:3306 */
    private String jdbcBase = "jdbc:mysql://localhost:3306";
    private String jdbcParams = "useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private String masterSchema = "platform_master";
    private String schemaPrefix = "t_";
    /** The pre-existing single-clinic database that becomes the first tenant ("" = none). */
    private String legacySchema = "hospital";
    private String legacySiteId = "hospital";
    private int poolSize = 5;

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String getJdbcBase() { return jdbcBase; }
    public void setJdbcBase(String jdbcBase) { this.jdbcBase = jdbcBase; }
    public String getJdbcParams() { return jdbcParams; }
    public void setJdbcParams(String jdbcParams) { this.jdbcParams = jdbcParams; }
    public String getMasterSchema() { return masterSchema; }
    public void setMasterSchema(String masterSchema) { this.masterSchema = masterSchema; }
    public String getSchemaPrefix() { return schemaPrefix; }
    public void setSchemaPrefix(String schemaPrefix) { this.schemaPrefix = schemaPrefix; }
    public String getLegacySchema() { return legacySchema; }
    public void setLegacySchema(String legacySchema) { this.legacySchema = legacySchema; }
    public String getLegacySiteId() { return legacySiteId; }
    public void setLegacySiteId(String legacySiteId) { this.legacySiteId = legacySiteId; }
    public int getPoolSize() { return poolSize; }
    public void setPoolSize(int poolSize) { this.poolSize = poolSize; }

    public String urlFor(String schema) {
        return jdbcBase + "/" + schema + "?" + jdbcParams;
    }
}
