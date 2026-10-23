package com.appointment.webapp.whatsapp.service.scheduler;

import com.appointment.webapp.model.Appointment;
import com.appointment.webapp.model.NotificationLog;
import com.appointment.webapp.model.enums.AppointmentStatus;
import com.appointment.webapp.repository.AppointmentRepository;
import com.appointment.webapp.repository.NotificationLogRepository;
import com.appointment.webapp.whatsapp.config.ClinicClock;
import com.appointment.webapp.whatsapp.i18n.Lang;
import com.appointment.webapp.whatsapp.i18n.LanguageService;
import com.appointment.webapp.whatsapp.i18n.MessageCatalog;
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
    /** Beyond this the booked time is clearly stale (old data, clinic closed) - say nothing rather than something silly. */
    private static final int MAX_LATE_MINUTES = 240;

    static final String NEAR = "QUEUE_NEAR";
    static final String YOUR_TURN = "QUEUE_YOUR_TURN";
    static final String SLOT_TIME = "SLOT_TIME";
    static final String RUNNING_LATE = "RUNNING_LATE";
    static final String MISSED = "MISSED_TURN";

    @org.springframework.beans.factory.annotation.Autowired
    private com.appointment.webapp.tenancy.TenantRunner tenantRunner;

    private final QueueStatusService queueStatus;
    private final NotificationLogRepository log;
    private final AppointmentRepository appointments;
    private final WhatsAppMessageSender sender;
    private final ClinicClock clock;
    private final MessageCatalog messages;
    private final LanguageService languages;

    public WhatsAppQueueNotifier(QueueStatusService queueStatus, NotificationLogRepository log,
                                 AppointmentRepository appointments, WhatsAppMessageSender sender, ClinicClock clock,
                                 MessageCatalog messages, LanguageService languages) {
        this.messages = messages;
        this.languages = languages;
        this.queueStatus = queueStatus;
        this.log = log;
        this.appointments = appointments;
        this.sender = sender;
        this.clock = clock;
    }

    @Scheduled(cron = "0 * * * * *", zone = "${app.timezone:Asia/Kolkata}")
    public void run() {
        tenantRunner.forEachTenant("queueAlerts", this::runForCurrentClinic);
    }

    private void runForCurrentClinic() {
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
        Lang lang = languages.of(a.getPatient());
        String doctor = "Dr. " + a.getDoctor().getFirstName() + " " + a.getDoctor().getLastName();
        String name = a.getPatient().getFirstName();

        if (a.getStatus() == AppointmentStatus.NOT_ATTENDED) {
            if (!sent.contains(MISSED)) {
                deliver(a, MISSED, phone, messages.t(lang, "q.missed", name, doctor));
            }
            return;
        }
        if (!s.isWaiting()) return;

        boolean alreadyNudged = sent.contains(NEAR) || sent.contains(YOUR_TURN);

        // 1. It's their turn
        if (s.ahead() == 0) {
            if (!sent.contains(YOUR_TURN)) {
                deliver(a, YOUR_TURN, phone, messages.t(lang, "q.turn", name, doctor));
            }
            return;
        }

        // 2. Only a couple of patients left
        if (s.ahead() <= 2) {
            if (!sent.contains(NEAR) && !sent.contains(YOUR_TURN)) {
                deliver(a, NEAR, phone, messages.t(lang, s.ahead() == 1 ? "q.near.1" : "q.near.2",
                    name, doctor, messages.time(s.estimated())));
            }
            return;
        }

        if (alreadyNudged) return;

        // 3. Their slot time has arrived but others are still ahead
        if (s.minutesLate() >= LATE_AFTER_MINUTES && s.minutesLate() <= MAX_LATE_MINUTES) {
            if (!sent.contains(RUNNING_LATE)) {
                deliver(a, RUNNING_LATE, phone, messages.t(lang, "q.late",
                    name, doctor, s.minutesLate(), s.ahead(), messages.time(s.estimated())));
            }
        } else if (s.minutesLate() >= 0 && s.minutesLate() < LATE_AFTER_MINUTES && !a.getScheduledTime().isAfter(clock.now().toLocalTime())) {
            if (!sent.contains(SLOT_TIME) && !sent.contains(RUNNING_LATE)) {
                deliver(a, SLOT_TIME, phone, messages.t(lang, "q.slot",
                    name, doctor, messages.time(a.getScheduledTime()), s.ahead(), messages.time(s.estimated())));
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
