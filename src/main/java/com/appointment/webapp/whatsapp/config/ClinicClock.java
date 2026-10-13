package com.appointment.webapp.whatsapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * "Now" and "today" in the clinic's own time zone, so WhatsApp dates and reminders
 * don't depend on where the server happens to run.
 */
@Component
public class ClinicClock {

    private final ZoneId zone;

    public ClinicClock(@Value("${app.timezone:Asia/Kolkata}") String zoneId) {
        this.zone = ZoneId.of(zoneId);
    }

    public ZoneId zone() {
        return zone;
    }

    public LocalDate today() {
        return LocalDate.now(zone);
    }

    public LocalDateTime now() {
        return LocalDateTime.now(zone);
    }
}
