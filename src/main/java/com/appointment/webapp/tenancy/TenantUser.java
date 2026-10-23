package com.appointment.webapp.tenancy;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Objects;

/**
 * A signed-in user together with the clinic (site) they belong to. Equality includes the site, so two clinics' "admin"
 * accounts never collide in the session registry or in "one session per user".
 */
public class TenantUser extends User {

    private final String siteId;

    public TenantUser(String siteId, String username, String password, boolean enabled, boolean accountNonLocked,
                      Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, true, true, accountNonLocked, authorities);
        this.siteId = siteId;
    }

    public String getSiteId() {
        return siteId;
    }

    /** "admin@sunrise" - the form that identifies this user uniquely across clinics. */
    public String qualifiedName() {
        return getUsername() + "@" + siteId;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof TenantUser other && super.equals(o) && Objects.equals(siteId, other.siteId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), siteId);
    }
}
