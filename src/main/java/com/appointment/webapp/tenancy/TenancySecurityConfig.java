package com.appointment.webapp.tenancy;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "app.tenancy.enabled", havingValue = "true")
public class TenancySecurityConfig {

    @Bean
    public SiteFilter siteFilter(TenantRegistry registry, TenancyProperties props) {
        return new SiteFilter(registry, props.getLegacySiteId());
    }

    /** The filter runs inside the Spring Security chain only; do not also let the servlet container register it. */
    @Bean
    public FilterRegistrationBean<SiteFilter> siteFilterRegistration(SiteFilter filter) {
        FilterRegistrationBean<SiteFilter> reg = new FilterRegistrationBean<>(filter);
        reg.setEnabled(false);
        return reg;
    }
}
