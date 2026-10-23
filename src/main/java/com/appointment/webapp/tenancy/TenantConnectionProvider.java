package com.appointment.webapp.tenancy;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Hands Hibernate a connection to the right clinic's schema. One small lazily-created pool per clinic.
 * Fails closed: with no clinic selected there is no connection at all.
 */
@Component
@ConditionalOnProperty(name = "app.tenancy.enabled", havingValue = "true")
public class TenantConnectionProvider implements MultiTenantConnectionProvider<String>, DisposableBean {

    private final TenancyProperties props;
    private final TenantRegistry registry;
    private final String user;
    private final String password;
    private final ConcurrentHashMap<String, HikariDataSource> pools = new ConcurrentHashMap<>();

    public TenantConnectionProvider(TenancyProperties props, @Lazy TenantRegistry registry,
                                    @Value("${spring.datasource.username:root}") String user,
                                    @Value("${spring.datasource.password:}") String password) {
        this.props = props;
        this.registry = registry;
        this.user = user;
        this.password = password;
    }

    private HikariDataSource pool(String siteId) {
        if (siteId == null || TenantIdentifierResolver.NONE.equals(siteId)) {
            throw new IllegalStateException("Database access without a clinic (site id) is not allowed");
        }
        return pools.computeIfAbsent(siteId, id -> {
            TenantInfo t = registry.find(id).orElseThrow(() -> new IllegalStateException("Unknown clinic " + id));
            HikariConfig c = new HikariConfig();
            c.setJdbcUrl(props.urlFor(t.schemaName()));
            c.setUsername(user);
            c.setPassword(password);
            c.setMaximumPoolSize(props.getPoolSize());
            c.setMinimumIdle(0);
            c.setIdleTimeout(120_000);
            c.setMaxLifetime(25 * 60_000);
            c.setPoolName("tenant-" + id);
            return new HikariDataSource(c);
        });
    }

    /** Drops a clinic's pool (after suspension, schema removal or credential change). */
    public void evict(String siteId) {
        HikariDataSource ds = pools.remove(siteId);
        if (ds != null) ds.close();
    }

    @Override
    public Connection getAnyConnection() throws SQLException {
        // Only used by Hibernate for start-up probing, which is switched off when tenancy is on.
        String fallback = props.getLegacySiteId();
        if (fallback == null || fallback.isBlank() || registry.find(fallback).isEmpty()) {
            throw new SQLException("No clinic available for an anonymous connection");
        }
        return pool(fallback).getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        return pool(tenantIdentifier).getConnection();
    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean isUnwrappableAs(Class<?> unwrapType) {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void destroy() {
        pools.values().forEach(HikariDataSource::close);
        pools.clear();
    }
}
