package com.appointment.webapp.tenancy;

import com.appointment.webapp.model.User;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Keeps the master login directory in step with each clinic's users, so a bare "username" finds its clinic at sign-in.
 * Does nothing unless tenancy is on and a clinic is current. A failure here never breaks saving the user.
 */
public class LoginDirectoryListener {

    private static final Logger log = LoggerFactory.getLogger(LoginDirectoryListener.class);

    @PostPersist
    public void added(User user) {
        sync(user, true);
    }

    @PostRemove
    public void removed(User user) {
        sync(user, false);
    }

    private void sync(User user, boolean add) {
        MasterRepository master = LoginDirectoryHolder.get();
        String site = TenantContext.siteId();
        if (master == null || site == null || user.getUsername() == null) return;
        try {
            if (add) master.addLogin(user.getUsername(), site); else master.removeLogin(user.getUsername(), site);
        } catch (Exception e) {
            log.warn("Could not update login directory for {}@{}: {}", user.getUsername(), site,
                    e instanceof org.springframework.core.NestedRuntimeException n ? n.getMostSpecificCause().getMessage() : e.getMessage());
        }
    }
}
