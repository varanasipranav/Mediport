package com.appointment.webapp.security;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomRememberMeServices extends PersistentTokenBasedRememberMeServices {

    public CustomRememberMeServices(String key, UserDetailsService userDetailsService,
            PersistentTokenRepository tokenRepository) {
        super(key, userDetailsService, tokenRepository);
    }

    @Override
    protected void onLoginSuccess(HttpServletRequest request, HttpServletResponse response,
            org.springframework.security.core.Authentication successfulAuthentication) {
        String username = successfulAuthentication.getName();
        logger.debug("Creating new persistent login for user " + username);
        if (successfulAuthentication.getPrincipal() instanceof com.appointment.webapp.tenancy.TenantUser tu) {
            // store "user@clinic" so that auto-login finds the right clinic's user again
            successfulAuthentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                    tu.qualifiedName(), null, successfulAuthentication.getAuthorities());
        }
        super.onLoginSuccess(request, response, successfulAuthentication);
    }
}
