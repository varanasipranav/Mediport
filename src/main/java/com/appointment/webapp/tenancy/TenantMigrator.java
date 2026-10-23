package com.appointment.webapp.tenancy;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/** Runs the clinic Flyway scripts (db/migration) against one clinic's schema. Existing databases are baselined at V1. */
@Component
@ConditionalOnProperty(name = "app.tenancy.enabled", havingValue = "true")
public class TenantMigrator {

    private final TenancyProperties props;
    private final String user;
    private final String password;

    public TenantMigrator(TenancyProperties props,
                          @Value("${spring.datasource.username:root}") String user,
                          @Value("${spring.datasource.password:}") String password) {
        this.props = props;
        this.user = user;
        this.password = password;
    }

    /** @return the schema version after migrating */
    public String migrate(String schemaName) {
        Flyway flyway = Flyway.configure()
                .dataSource(props.urlFor(schemaName), user, password)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .baselineVersion("1")
                .load();
        flyway.migrate();
        var current = flyway.info().current();
        return current == null ? null : current.getVersion().getVersion();
    }
}
