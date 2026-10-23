package com.appointment.webapp.service;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Stores phone numbers the way WhatsApp reports them (E.164, e.g. +919876543210) so a patient entered at the
 * front desk is recognised when they message the bot.
 */
@Component
public class PhoneNormalizer {

    private final PhoneNumberUtil util = PhoneNumberUtil.getInstance();
    private final String defaultRegion;

    public PhoneNormalizer(@Value("${app.default-region:IN}") String defaultRegion) {
        this.defaultRegion = defaultRegion;
    }

    private String region() {
        var t = com.appointment.webapp.tenancy.TenantContext.get();
        return t != null && t.defaultRegion() != null && !t.defaultRegion().isBlank() ? t.defaultRegion() : defaultRegion;
    }

    /** E.164 form, or null if the input is blank. Throws if it cannot be a real number. */
    public String toE164(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            Phonenumber.PhoneNumber parsed = util.parse(raw.trim(), region());
            if (!util.isValidNumber(parsed)) {
                throw new BookingException("\"" + raw.trim() + "\" is not a valid phone number");
            }
            return util.format(parsed, PhoneNumberUtil.PhoneNumberFormat.E164);
        } catch (NumberParseException e) {
            throw new BookingException("\"" + raw.trim() + "\" is not a valid phone number");
        }
    }
}
