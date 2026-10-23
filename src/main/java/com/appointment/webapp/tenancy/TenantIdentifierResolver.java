package com.appointment.webapp.tenancy;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/** Tells Hibernate which clinic the current thread belongs to. No clinic => sentinel, which the provider rejects. */
@Component
@ConditionalOnProperty(name = "app.tenancy.enabled", havingValue = "true")
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver<String> {

    public static final String NONE = "__none__";

    @Override
    public String resolveCurrentTenantIdentifier() {
        String id = TenantContext.siteId();
        return id == null ? NONE : id;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }
}
