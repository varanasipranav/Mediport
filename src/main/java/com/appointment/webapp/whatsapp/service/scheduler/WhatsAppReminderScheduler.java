package com.appointment.webapp.whatsapp.service.scheduler;

import com.appointment.webapp.model.Appointment;
import com.appointment.webapp.whatsapp.i18n.Lang;
import com.appointment.webapp.whatsapp.i18n.LanguageService;
import com.appointment.webapp.whatsapp.i18n.MessageCatalog;
import com.appointment.webapp.whatsapp.service.appointment.WhatsAppAppointmentManagementService;
import com.appointment.webapp.whatsapp.service.messaging.WhatsAppMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Appointment reminders over WhatsApp: 24 hours and 2 hours before the visit, in the patient's language.
 * The jobs run every 30 minutes and each appointment falls into one 30-minute window once, so nobody is
 * reminded twice.
 */
@Service
public class WhatsAppReminderScheduler {

    private static final Logger logger = LoggerFactory.getLogger(WhatsAppReminderScheduler.class);

    /** Reminders already sent (kind:appointmentId). The windows already prevent repeats; this guards overlaps and restarts. */
    private final Set<String> sentReminders = ConcurrentHashMap.newKeySet();

    private final WhatsAppAppointmentManagementService appointments;
    private final WhatsAppMessageSender sender;
    private final MessageCatalog messages;
    private final LanguageService languages;

    public WhatsAppReminderScheduler(WhatsAppAppointmentManagementService appointments, WhatsAppMessageSender sender,
                                     MessageCatalog messages, LanguageService languages) {
        this.appointments = appointments;
        this.sender = sender;
        this.messages = messages;
        this.languages = languages;
    }

    private List<Appointment> unsent(String kind, List<Appointment> candidates) {
        if (sentReminders.size() > 10000) {
            sentReminders.clear();
        }
        return candidates.stream().filter(a -> sentReminders.add(kind + ":" + a.getId())).collect(Collectors.toList());
    }

    @Scheduled(cron = "0 0,30 * * * *", zone = "${app.timezone:Asia/Kolkata}")
    public void send24HourReminders() {
        try {
            List<Appointment> due = unsent("24h", appointments.getAppointmentsStartingInWindow(24));
            logger.info("{} appointments need a 24h reminder", due.size());
            due.forEach(a -> send(a, "rem.24h", "24h"));
        } catch (Exception e) {
            logger.error("Error sending 24-hour reminders", e);
        }
    }

    @Scheduled(cron = "0 0,30 * * * *", zone = "${app.timezone:Asia/Kolkata}")
    public void send2HourReminders() {
        try {
            List<Appointment> due = unsent("2h", appointments.getAppointmentsStartingInWindow(2));
            logger.info("{} appointments need a 2h reminder", due.size());
            due.forEach(a -> send(a, "rem.2h", "2h"));
        } catch (Exception e) {
            logger.error("Error sending 2-hour reminders", e);
        }
    }

    private void send(Appointment a, String key, String label) {
        try {
            String phone = a.getPatient().getWhatsappPhone();
            if (phone == null || phone.isEmpty()) {
                logger.warn("Patient {} has no WhatsApp number - {} reminder skipped", a.getPatient().getId(), label);
                return;
            }

            Lang lang = languages.of(a.getPatient());
            String text = "rem.24h".equals(key)
                ? messages.t(lang, key, a.getPatient().getFirstName(), a.getDoctor().getFirstName(), a.getDoctor().getLastName(),
                    messages.date(a.getAppointmentDate()), messages.time(a.getScheduledTime()), a.getQueueNumber(), a.getId())
                : messages.t(lang, key, a.getPatient().getFirstName(), a.getDoctor().getFirstName(), a.getDoctor().getLastName(),
                    messages.time(a.getScheduledTime()), a.getQueueNumber());

            if (sender.sendTextMessage(phone, text) != null) {
                logger.info("{} reminder sent for appointment {} ({})", label, a.getId(), lang.code());
            } else {
                logger.error("Failed to send {} reminder for appointment {}", label, a.getId());
            }
        } catch (Exception e) {
            logger.error("Error sending {} reminder for appointment {}", label, a.getId(), e);
        }
    }
}
