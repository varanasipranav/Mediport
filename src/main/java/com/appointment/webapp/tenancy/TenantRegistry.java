package com.appointment.webapp.tenancy;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Short-lived cache of clinic rows so a request does not hit the master database every time. */
@Component
@ConditionalOnProperty(name = "app.tenancy.enabled", havingValue = "true")
public class TenantRegistry {

    private static final long TTL_MS = 30_000;

    private record Entry(TenantInfo tenant, long at) {
    }

    private final MasterRepository master;
    private final ConcurrentHashMap<String, Entry> cache = new ConcurrentHashMap<>();

    public TenantRegistry(MasterRepository master) {
        this.master = master;
    }

    public Optional<TenantInfo> find(String siteId) {
        if (siteId == null) return Optional.empty();
        Entry e = cache.get(siteId);
        if (e != null && System.currentTimeMillis() - e.at < TTL_MS) return Optional.of(e.tenant);
        Optional<TenantInfo> fresh = master.findTenant(siteId);
        if (fresh.isPresent()) cache.put(siteId, new Entry(fresh.get(), System.currentTimeMillis()));
        else cache.remove(siteId);
        return fresh;
    }

    public void evict(String siteId) {
        cache.remove(siteId);
    }
}
