package com.appointment.webapp.whatsapp.service.scheduler;

import com.appointment.webapp.model.Appointment;
import com.appointment.webapp.whatsapp.service.appointment.WhatsAppAppointmentManagementService;
import com.appointment.webapp.whatsapp.service.messaging.WhatsAppMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * WhatsApp Appointment Reminder Scheduler
 * Sends automated reminders 24 hours and 2 hours before appointments
 */
@Service
public class WhatsAppReminderScheduler {

    private static final Logger logger = LoggerFactory.getLogger(WhatsAppReminderScheduler.class);

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /** Reminders already sent (kind:appointmentId). Windows already prevent repeats; this guards overlaps/restarts. */
    private final java.util.Set<String> sentReminders = java.util.concurrent.ConcurrentHashMap.newKeySet();

    @Autowired
    private WhatsAppAppointmentManagementService appointmentManagementService;

    private List<Appointment> dedupe(String kind, List<Appointment> appointments) {
        if (sentReminders.size() > 10000) {
            sentReminders.clear();
        }
        return appointments.stream()
            .filter(a -> sentReminders.add(kind + ":" + a.getId()))
            .collect(java.util.stream.Collectors.toList());
    }

    @Autowired
    private WhatsAppMessageSender messageSender;

    /**
     * Send 24-hour reminders
     * Runs every hour
     */
    @Scheduled(cron = "0 0,30 * * * *", zone = "${app.timezone:Asia/Kolkata}") // Every 30 min, aligned with the 30-min reminder window
    public void send24HourReminders() {
        logger.info("🔔 Running 24-hour reminder check...");

        try {
            List<Appointment> appointments = dedupe("24h", appointmentManagementService.getAppointmentsStartingInWindow(24));

            logger.info("Found {} appointments needing 24h reminder", appointments.size());

            for (Appointment appointment : appointments) {
                send24HourReminder(appointment);
            }

            logger.info("✅ 24-hour reminders sent: {}", appointments.size());

        } catch (Exception e) {
            logger.error("Error sending 24-hour reminders", e);
        }
    }

    /**
     * Send 2-hour reminders
     * Runs every 30 minutes
     */
    @Scheduled(cron = "0 0,30 * * * *", zone = "${app.timezone:Asia/Kolkata}") // Every 30 min
    public void send2HourReminders() {
        logger.info("🔔 Running 2-hour reminder check...");

        try {
            List<Appointment> appointments = dedupe("2h", appointmentManagementService.getAppointmentsStartingInWindow(2));

            logger.info("Found {} appointments needing 2h reminder", appointments.size());

            for (Appointment appointment : appointments) {
                send2HourReminder(appointment);
            }

            logger.info("✅ 2-hour reminders sent: {}", appointments.size());

        } catch (Exception e) {
            logger.error("Error sending 2-hour reminders", e);
        }
    }

    /**
     * Send 24-hour reminder
     */
    private void send24HourReminder(Appointment appointment) {
        try {
            String phone = appointment.getPatient().getWhatsappPhone();

            if (phone == null || phone.isEmpty()) {
                logger.warn("Patient {} has no WhatsApp phone number", appointment.getPatient().getId());
                return;
            }

            String message = String.format(
                "🔔 *Reminder: Appointment Tomorrow*\n\n" +
                "Hello *%s*!\n\n" +
                "This is a reminder that you have an appointment tomorrow:\n\n" +
                "👨‍⚕️ *Doctor:* Dr. %s %s\n" +
                "📅 *Date:* Tomorrow (%s)\n" +
                "⏰ *Time:* %s\n" +
                "🔢 *Queue Number:* #%d\n" +
                "📋 *Appointment ID:* #%d\n\n" +
                "Please arrive 10 minutes early.\n" +
                "See you tomorrow! 👋",
                appointment.getPatient().getFirstName(),
                appointment.getDoctor().getFirstName(),
                appointment.getDoctor().getLastName(),
                appointment.getAppointmentDate().format(DATE_FORMATTER),
                appointment.getScheduledTime().format(TIME_FORMATTER),
                appointment.getQueueNumber(),
                appointment.getId()
            );

            String result = messageSender.sendTextMessage(phone, message);

            if (result != null) {
                logger.info("✅ 24h reminder sent for appointment {}", appointment.getId());
                // TODO: Mark reminder as sent in database
            } else {
                logger.error("❌ Failed to send 24h reminder for appointment {}", appointment.getId());
            }

        } catch (Exception e) {
            logger.error("Error sending 24h reminder for appointment {}", appointment.getId(), e);
        }
    }

    /**
     * Send 2-hour reminder
     */
    private void send2HourReminder(Appointment appointment) {
        try {
            String phone = appointment.getPatient().getWhatsappPhone();

            if (phone == null || phone.isEmpty()) {
                logger.warn("Patient {} has no WhatsApp phone number", appointment.getPatient().getId());
                return;
            }

            String message = String.format(
                "🔔 *Reminder: Appointment in 2 Hours*\n\n" +
                "Hello *%s*!\n\n" +
                "Your appointment is coming up soon:\n\n" +
                "👨‍⚕️ *Doctor:* Dr. %s %s\n" +
                "⏰ *Time:* %s (in approx. 2 hours)\n" +
                "🔢 *Queue Number:* #%d\n\n" +
                "Please start preparing to leave.\n" +
                "Arrive 10 minutes early.\n\n" +
                "See you soon! 👋",
                appointment.getPatient().getFirstName(),
                appointment.getDoctor().getFirstName(),
                appointment.getDoctor().getLastName(),
                appointment.getScheduledTime().format(TIME_FORMATTER),
                appointment.getQueueNumber()
            );

            String result = messageSender.sendTextMessage(phone, message);

            if (result != null) {
                logger.info("✅ 2h reminder sent for appointment {}", appointment.getId());
                // TODO: Mark reminder as sent in database
            } else {
                logger.error("❌ Failed to send 2h reminder for appointment {}", appointment.getId());
            }

        } catch (Exception e) {
            logger.error("Error sending 2h reminder for appointment {}", appointment.getId(), e);
        }
    }
}
