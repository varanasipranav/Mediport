package com.appointment.webapp.tenancy;

/** One clinic ("site"): where its data lives and its per-clinic settings. Read from the master database. */
public record TenantInfo(
        String siteId,
        String schemaName,
        String displayName,
        String status,
        String timezone,
        String defaultRegion,
        String rxSigningKey,
        String tagline,
        String address,
        String phone,
        String email,
        String registrationNo,
        String waPhoneNumberId,
        String waAccessToken,
        String waAppSecret,
        String schemaVersion,
        String lastError) {

    public static final String ACTIVE = "ACTIVE";
    public static final String SUSPENDED = "SUSPENDED";
    public static final String PROVISIONING = "PROVISIONING";
    public static final String FAILED = "FAILED";

    public boolean active() {
        return ACTIVE.equals(status);
    }
}
