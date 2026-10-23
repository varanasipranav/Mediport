package com.appointment.webapp.tenancy;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "app.tenancy.enabled", havingValue = "true")
public class TenancyHibernateConfig {

    @Bean
    public HibernatePropertiesCustomizer multiTenancyCustomizer(TenantConnectionProvider provider, TenantIdentifierResolver resolver) {
        return props -> {
            props.put("hibernate.multi_tenant_connection_provider", provider);
            props.put("hibernate.tenant_identifier_resolver", resolver);
            // Flyway owns every clinic schema; Hibernate must not probe or validate one at start-up.
            props.put("hibernate.hbm2ddl.auto", "none");
            props.put("hibernate.boot.allow_jdbc_metadata_access", "false");
            props.remove("hibernate.connection.datasource");
        };
    }
}
