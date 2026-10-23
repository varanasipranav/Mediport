package com.appointment.webapp.tenancy;

import com.appointment.webapp.service.CustomUserDetailsService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Login with tenancy on. The typed name is "username@clinic", or just "username" when only one clinic has it.
 * The clinic's own database is then asked for the user, and the result carries the site id for the rest of the session.
 * Remember-me tokens use the "username@clinic" form, so auto-login comes back through here too.
 */
@Service
@Primary
@ConditionalOnProperty(name = "app.tenancy.enabled", havingValue = "true")
public class TenantAwareUserDetailsService implements UserDetailsService {

    private final MasterRepository master;
    private final TenantRegistry registry;
    private final CustomUserDetailsService delegate;

    public TenantAwareUserDetailsService(MasterRepository master, TenantRegistry registry, CustomUserDetailsService delegate) {
        this.master = master;
        this.registry = registry;
        this.delegate = delegate;
    }

    @Override
    public UserDetails loadUserByUsername(String typed) throws UsernameNotFoundException {
        String name = typed == null ? "" : typed.trim();
        String siteId;
        int at = name.lastIndexOf('@');
        if (at > 0 && at < name.length() - 1) {
            siteId = name.substring(at + 1).toLowerCase();
            name = name.substring(0, at);
        } else {
            List<String> sites = master.sitesForUsername(name);
            if (sites.size() > 1) {
                throw new SiteRequiredException("Enter your clinic code as username@clinic");
            }
            if (sites.isEmpty()) {
                throw new UsernameNotFoundException("Unknown user");
            }
            siteId = sites.get(0);
        }
        TenantInfo tenant = registry.find(siteId).filter(TenantInfo::active)
                .orElseThrow(() -> new UsernameNotFoundException("Unknown user"));
        final String lookup = name;
        UserDetails found = TenantContext.callAs(tenant, () -> delegate.loadUserByUsername(lookup));
        return new TenantUser(tenant.siteId(), found.getUsername(), found.getPassword(), found.isEnabled(),
                found.isAccountNonLocked(), found.getAuthorities());
    }
}
