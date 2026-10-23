package com.appointment.webapp.tenancy;

import org.springframework.security.core.AuthenticationException;

/** The same username exists in several clinics: the user must sign in as username@clinic. */
public class SiteRequiredException extends AuthenticationException {

    public SiteRequiredException(String message) {
        super(message);
    }
}
