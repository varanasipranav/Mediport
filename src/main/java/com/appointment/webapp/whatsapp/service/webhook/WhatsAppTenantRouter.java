package com.appointment.webapp.whatsapp.service.webhook;

import com.appointment.webapp.tenancy.MasterRepository;
import com.appointment.webapp.tenancy.TenancyProperties;
import com.appointment.webapp.tenancy.TenantInfo;
import com.appointment.webapp.whatsapp.config.WhatsAppConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Finds the clinic a WhatsApp message belongs to from the business number that received it (metadata.phone_number_id).
 * Each clinic with its own number is registered in the master database. The platform's own number (the one in
 * application.properties) belongs to the first clinic, so the original single-clinic setup keeps working.
 * With tenancy off, nothing is routed and messages are processed as before.
 */
@Component
public class WhatsAppTenantRouter {

    private final Optional<MasterRepository> master;
    private final TenancyProperties props;
    private final WhatsAppConfig whatsAppConfig;
    private final ObjectMapper mapper;

    public WhatsAppTenantRouter(Optional<MasterRepository> master, TenancyProperties props, WhatsAppConfig whatsAppConfig, ObjectMapper mapper) {
        this.master = master;
        this.props = props;
        this.whatsAppConfig = whatsAppConfig;
        this.mapper = mapper;
    }

    public boolean enabled() {
        return props.isEnabled() && master.isPresent();
    }

    /** The clinic for this business number, or empty if unknown (the message is then ignored). */
    public Optional<TenantInfo> clinicFor(String phoneNumberId) {
        if (!enabled() || phoneNumberId == null) return Optional.empty();
        Optional<TenantInfo> own = master.get().findTenantByWhatsAppNumber(phoneNumberId);
        if (own.isPresent()) return own.filter(TenantInfo::active);
        if (phoneNumberId.equals(whatsAppConfig.getApi().getPhoneNumberId())) {
            return master.get().findTenant(props.getLegacySiteId()).filter(TenantInfo::active);
        }
        return Optional.empty();
    }

    /**
     * The app secret to verify a webhook body with: the clinic's own secret if its number has one,
     * otherwise null (= the platform secret). Reads only the phone_number_id from the still-unverified body.
     */
    public String appSecretFor(String payload) {
        if (!enabled()) return null;
        try {
            JsonNode root = mapper.readTree(payload);
            for (JsonNode entry : root.path("entry")) {
                for (JsonNode change : entry.path("changes")) {
                    String id = change.path("value").path("metadata").path("phone_number_id").asText(null);
                    if (id != null) {
                        return master.get().findTenantByWhatsAppNumber(id).map(TenantInfo::waAppSecret).orElse(null);
                    }
                }
            }
        } catch (Exception ignored) {
            // an unreadable body fails signature checks anyway
        }
        return null;
    }
}
