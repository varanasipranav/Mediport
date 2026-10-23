package com.appointment.webapp.tenancy;

import org.flywaydb.core.Flyway;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/** Creates and migrates the master database (db/master). */
public final class MasterMigrator {

    private MasterMigrator() {
    }

    public static void ensureSchemaExists(TenancyProperties props, String user, String password) {
        try (Connection c = DriverManager.getConnection(props.getJdbcBase() + "/?" + props.getJdbcParams(), user, password);
             Statement s = c.createStatement()) {
            s.execute("CREATE DATABASE IF NOT EXISTS `" + props.getMasterSchema() + "` CHARACTER SET utf8mb4");
        } catch (Exception e) {
            throw new IllegalStateException("Cannot create master database " + props.getMasterSchema(), e);
        }
    }

    public static void migrate(DataSource master) {
        Flyway.configure()
                .dataSource(master)
                .locations("classpath:db/master")
                .table("flyway_schema_history_master")
                .load()
                .migrate();
    }
}
