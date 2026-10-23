package com.appointment.webapp.tenancy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Runs background work for every active clinic, one after the other, each with its own tenant context.
 * With tenancy off the task runs once with no tenant, so call sites work in both modes.
 */
@Component
public class TenantRunner {

    private static final Logger log = LoggerFactory.getLogger(TenantRunner.class);

    @Autowired(required = false)
    private MasterRepository master;

    @Value("${app.tenancy.enabled:false}")
    private boolean enabled;

    public boolean enabled() {
        return enabled;
    }

    public void forEachTenant(String jobName, Runnable task) {
        if (!enabled || master == null) {
            task.run();
            return;
        }
        List<TenantInfo> tenants;
        try {
            tenants = master.allTenants();
        } catch (Exception e) {
            log.error("{}: cannot list clinics", jobName, e);
            return;
        }
        for (TenantInfo t : tenants) {
            if (!t.active() || t.lastError() != null && t.schemaVersion() == null) continue;
            try {
                TenantContext.runAs(t, task);
            } catch (Exception e) {
                log.error("{}: failed for clinic {}", jobName, t.siteId(), e);
            }
        }
    }

    /** Wraps a task so it runs later (other thread) in the tenant that is current now. */
    public static Runnable wrap(Runnable task) {
        TenantInfo captured = TenantContext.get();
        return () -> {
            if (captured == null) {
                task.run();
            } else {
                TenantContext.runAs(captured, task);
            }
        };
    }

    public static <T> Callable<T> wrap(Callable<T> task) {
        TenantInfo captured = TenantContext.get();
        return () -> captured == null ? task.call() : TenantContext.callAs(captured, task);
    }
}
