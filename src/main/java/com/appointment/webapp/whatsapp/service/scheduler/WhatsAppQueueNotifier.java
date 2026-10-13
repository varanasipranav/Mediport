package com.appointment.webapp.whatsapp.service.scheduler;

import com.appointment.webapp.model.Appointment;
import com.appointment.webapp.model.NotificationLog;
import com.appointment.webapp.model.enums.AppointmentStatus;
import com.appointment.webapp.repository.AppointmentRepository;
import com.appointment.webapp.repository.NotificationLogRepository;
import com.appointment.webapp.whatsapp.config.ClinicClock;
import com.appointment.webapp.whatsapp.service.appointment.QueueStatusService;
import com.appointment.webapp.whatsapp.service.appointment.QueueStatusService.QueueStatus;
import com.appointment.webapp.whatsapp.service.messaging.WhatsAppMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Live queue alerts over WhatsApp, checked every minute while the clinic is open:
 * - "N patients ahead of you" (when 2 or fewer are left)
 * - "It's your turn"
 * - "Your slot time has arrived" / "the doctor is running late" (with a new estimate)
 * - "We missed you" after a no-show
 * Each alert is sent once per appointment (recorded in notification_log, so restarts never repeat it).
 */
@Component
public class WhatsAppQueueNotifier {

    private static final Logger logger = LoggerFactory.getLogger(WhatsAppQueueNotifier.class);
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("hh:mm a");
    private static final int LATE_AFTER_MINUTES = 20;

    static final String NEAR = "QUEUE_NEAR";
    static final String YOUR_TURN = "QUEUE_YOUR_TURN";
    static final String SLOT_TIME = "SLOT_TIME";
    static final String RUNNING_LATE = "RUNNING_LATE";
    static final String MISSED = "MISSED_TURN";

    private final QueueStatusService queueStatus;
    private final NotificationLogRepository log;
    private final AppointmentRepository appointments;
    private final WhatsAppMessageSender sender;
    private final ClinicClock clock;

    public WhatsAppQueueNotifier(QueueStatusService queueStatus, NotificationLogRepository log,
                                 AppointmentRepository appointments, WhatsAppMessageSender sender, ClinicClock clock) {
        this.queueStatus = queueStatus;
        this.log = log;
        this.appointments = appointments;
        this.sender = sender;
        this.clock = clock;
    }

    @Scheduled(cron = "0 * * * * *", zone = "${app.timezone:Asia/Kolkata}")
    public void run() {
        try {
            List<QueueStatus> today = queueStatus.statusesFor(clock.today());
            for (QueueStatus s : today) {
                try {
                    process(s);
                } catch (Exception e) {
                    logger.error("Queue alert failed for appointment {}", s.appointment().getId(), e);
                }
            }
        } catch (Exception e) {
            logger.error("Queue notifier run failed", e);
        }
    }

    void process(QueueStatus s) {
        Appointment a = s.appointment();
        String phone = a.getPatient().getWhatsappPhone();
        if (phone == null || phone.isBlank()) return;

        Set<String> sent = new HashSet<>(log.findKindsByAppointmentId(a.getId()));
        String doctor = "Dr. " + a.getDoctor().getFirstName() + " " + a.getDoctor().getLastName();
        String name = a.getPatient().getFirstName();

        if (a.getStatus() == AppointmentStatus.NOT_ATTENDED) {
            if (!sent.contains(MISSED)) {
                deliver(a, MISSED, phone, "Hi " + name + ", we missed you for your appointment with " + doctor + " today.\n\n" +
                    "No problem - send \"Hi\" anytime to book a new slot.");
            }
            return;
        }
        if (!s.isWaiting()) return;

        boolean alreadyNudged = sent.contains(NEAR) || sent.contains(YOUR_TURN);

        // 1. It's their turn
        if (s.ahead() == 0) {
            if (!sent.contains(YOUR_TURN)) {
                deliver(a, YOUR_TURN, phone, "*It's almost your turn, " + name + "!* 🔔\n\n" +
                    "No one is ahead of you now. Please be ready outside " + doctor + "'s room - you'll be called in shortly.");
            }
            return;
        }

        // 2. Only a couple of patients left
        if (s.ahead() <= 2) {
            if (!sent.contains(NEAR) && !sent.contains(YOUR_TURN)) {
                String ahead = s.ahead() == 1 ? "Just *1 patient* is" : "Only *2 patients* are";
                deliver(a, NEAR, phone, "Hi " + name + ", " + ahead + " ahead of you for " + doctor + ". ⏳\n\n" +
                    "Please make your way to the clinic now. Estimated time: *" + s.estimated().format(TIME) + "*.");
            }
            return;
        }

        if (alreadyNudged) return;

        // 3. Their slot time has arrived but others are still ahead
        if (s.minutesLate() >= LATE_AFTER_MINUTES) {
            if (!sent.contains(RUNNING_LATE)) {
                deliver(a, RUNNING_LATE, phone, "Hi " + name + ", " + doctor + " is running about *" + s.minutesLate() + " minutes* behind schedule today. 🙏\n\n" +
                    "You have " + s.ahead() + " patients ahead of you. New estimate: *" + s.estimated().format(TIME) + "*.\n" +
                    "Send *status* anytime to see where you are in the queue.");
            }
        } else if (s.minutesLate() >= 0 && !a.getScheduledTime().isAfter(clock.now().toLocalTime())) {
            if (!sent.contains(SLOT_TIME) && !sent.contains(RUNNING_LATE)) {
                deliver(a, SLOT_TIME, phone, "Hi " + name + ", it's time for your slot with " + doctor + " (" +
                    a.getScheduledTime().format(TIME) + "). ⏰\n\n" +
                    s.ahead() + " patients are ahead of you - estimated time *" + s.estimated().format(TIME) + "*.");
            }
        }
    }

    private void deliver(Appointment a, String kind, String phone, String text) {
        String result = sender.sendTextMessage(phone, text);
        if (result == null) {
            logger.warn("Could not send {} alert for appointment {} - will retry next minute", kind, a.getId());
            return;
        }
        try {
            NotificationLog row = new NotificationLog();
            row.setAppointment(appointments.getReferenceById(a.getId()));
            row.setKind(kind);
            row.setDetail(text.length() > 250 ? text.substring(0, 250) : text);
            log.save(row);
            logger.info("Sent {} alert for appointment {}", kind, a.getId());
        } catch (DataIntegrityViolationException e) {
            logger.debug("{} alert for appointment {} was already recorded", kind, a.getId());
        }
    }
}
