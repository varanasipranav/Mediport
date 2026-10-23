package com.appointment.webapp.tenancy;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.flyway.autoconfigure.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.simple.JdbcClient;

import javax.sql.DataSource;

/**
 * Tenancy on: the application's own DataSource is the small master database (clinics, demo requests, logins).
 * Clinic data is reached through {@link TenantConnectionProvider}, never through this DataSource.
 */
@Configuration
@EnableConfigurationProperties(TenancyProperties.class)
public class MasterDataSourceConfig {

    @Bean
    @Primary
    @ConditionalOnProperty(name = "app.tenancy.enabled", havingValue = "true")
    public DataSource masterDataSource(TenancyProperties props,
                                       @Value("${spring.datasource.username:root}") String user,
                                       @Value("${spring.datasource.password:}") String password) {
        MasterMigrator.ensureSchemaExists(props, user, password);
        HikariConfig c = new HikariConfig();
        c.setJdbcUrl(props.urlFor(props.getMasterSchema()));
        c.setUsername(user);
        c.setPassword(password);
        c.setMaximumPoolSize(5);
        c.setMinimumIdle(0);
        c.setPoolName("master");
        HikariDataSource ds = new HikariDataSource(c);
        MasterMigrator.migrate(ds);
        return ds;
    }

    /**
     * JdbcClient gets a pool of its own on purpose. Hibernate's transaction manager binds the *application's*
     * DataSource to the current transaction; if the master JdbcClient shared it, master queries made during a clinic
     * transaction would silently run on that clinic's connection.
     */
    @Bean(destroyMethod = "close")
    @ConditionalOnProperty(name = "app.tenancy.enabled", havingValue = "true")
    public HikariDataSource masterJdbcPool(TenancyProperties props,
                                           @Value("${spring.datasource.username:root}") String user,
                                           @Value("${spring.datasource.password:}") String password) {
        HikariConfig c = new HikariConfig();
        c.setJdbcUrl(props.urlFor(props.getMasterSchema()));
        c.setUsername(user);
        c.setPassword(password);
        c.setMaximumPoolSize(5);
        c.setMinimumIdle(0);
        c.setPoolName("master-jdbc");
        return new HikariDataSource(c);
    }

    @Bean
    @ConditionalOnProperty(name = "app.tenancy.enabled", havingValue = "true")
    public JdbcClient masterJdbc(@org.springframework.beans.factory.annotation.Qualifier("masterJdbcPool") DataSource masterJdbcPool) {
        return JdbcClient.create(masterJdbcPool);
    }

    /** Boot's own Flyway run must not touch the master database: tenants are migrated by TenantMigrator. */
    @Bean
    @ConditionalOnProperty(name = "app.tenancy.enabled", havingValue = "true")
    public FlywayMigrationStrategy noBootFlyway() {
        return flyway -> { };
    }
}
