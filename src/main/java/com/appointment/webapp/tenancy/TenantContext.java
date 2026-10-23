package com.appointment.webapp.tenancy;

import java.util.concurrent.Callable;

/**
 * The clinic the current thread is working for. Set by {@link SiteFilter} for web requests and by
 * {@link #runAs} for background work; always cleared afterwards. Plain ThreadLocal on purpose: tenants must be
 * handed to other threads explicitly (see {@link TenantRunner#wrap}).
 */
public final class TenantContext {

    private static final ThreadLocal<TenantInfo> CURRENT = new ThreadLocal<>();

    private TenantContext() {
    }

    public static TenantInfo get() {
        return CURRENT.get();
    }

    public static String siteId() {
        TenantInfo t = CURRENT.get();
        return t == null ? null : t.siteId();
    }

    public static TenantInfo require() {
        TenantInfo t = CURRENT.get();
        if (t == null) {
            throw new IllegalStateException("No clinic (site) is selected for this request");
        }
        return t;
    }

    public static void set(TenantInfo tenant) {
        CURRENT.set(tenant);
    }

    public static void clear() {
        CURRENT.remove();
    }

    /** Runs the task as the given tenant and restores whatever was set before. */
    public static void runAs(TenantInfo tenant, Runnable task) {
        TenantInfo previous = CURRENT.get();
        CURRENT.set(tenant);
        try {
            task.run();
        } finally {
            if (previous == null) CURRENT.remove(); else CURRENT.set(previous);
        }
    }

    public static <T> T callAs(TenantInfo tenant, Callable<T> task) {
        TenantInfo previous = CURRENT.get();
        CURRENT.set(tenant);
        try {
            return task.call();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            if (previous == null) CURRENT.remove(); else CURRENT.set(previous);
        }
    }
}
