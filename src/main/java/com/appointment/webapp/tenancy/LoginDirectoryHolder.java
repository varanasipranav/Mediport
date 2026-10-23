package com.appointment.webapp.tenancy;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/** Gives the JPA entity listener (not a Spring bean) access to the master repository. */
@Component
@ConditionalOnProperty(name = "app.tenancy.enabled", havingValue = "true")
public class LoginDirectoryHolder {

    private static volatile MasterRepository master;

    public LoginDirectoryHolder(MasterRepository repository) {
        master = repository;
    }

    static MasterRepository get() {
        return master;
    }
}
