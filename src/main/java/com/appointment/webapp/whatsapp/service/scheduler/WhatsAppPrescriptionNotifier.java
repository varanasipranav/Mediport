package com.appointment.webapp.whatsapp.service.scheduler;

import com.appointment.webapp.model.Prescription;
import com.appointment.webapp.service.PrescriptionCreatedEvent;
import com.appointment.webapp.service.PrescriptionService;
import com.appointment.webapp.service.RxDocumentService;
import com.appointment.webapp.whatsapp.service.messaging.WhatsAppMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.concurrent.CompletableFuture;

/**
 * Tells the patient on WhatsApp when their prescription is ready, with a PDF link that stays valid for a week.
 * Sent automatically after the doctor saves, and on demand from the "Send on WhatsApp" button.
 */
@Component
public class WhatsAppPrescriptionNotifier {

    private static final Logger logger = LoggerFactory.getLogger(WhatsAppPrescriptionNotifier.class);

    public enum Outcome { SENT, NO_NUMBER, REJECTED }

    private final PrescriptionService prescriptions;
    private final RxDocumentService documents;
    private final WhatsAppMessageSender sender;

    public WhatsAppPrescriptionNotifier(PrescriptionService prescriptions, RxDocumentService documents,
                                        WhatsAppMessageSender sender) {
        this.prescriptions = prescriptions;
        this.documents = documents;
        this.sender = sender;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCreated(PrescriptionCreatedEvent event) {
        CompletableFuture.runAsync(() -> sendNow(event.prescriptionId()));
    }

    /** Sends the message now and says what happened. */
    public Outcome sendNow(Long prescriptionId) {
        try {
            Prescription p = prescriptions.getPrescriptionById(prescriptionId);
            String phone = p.getPatient().getWhatsappPhone();
            if (phone == null || phone.isBlank()) {
                logger.warn("Prescription {}: patient {} has no WhatsApp number - nothing sent",
                    prescriptionId, p.getPatient().getUsername());
                return Outcome.NO_NUMBER;
            }

            String text = "Hi " + p.getPatient().getFirstName() + ", your prescription from *Dr. " +
                p.getDoctor().getFirstName() + " " + p.getDoctor().getLastName() + "* is ready. \uD83D\uDCC4\n\n" +
                "Prescription no: *" + documents.rxNumber(p) + "*\n" +
                "Download (PDF, valid 7 days):\n" + documents.signedPdfUrl(p.getId()) + "\n\n" +
                "Show it at the pharmacy or collect your medicines there. Get well soon!";
            if (sender.sendTextMessage(phone, text) == null) {
                logger.warn("Prescription {}: WhatsApp did not accept the message to {}", prescriptionId, phone);
                return Outcome.REJECTED;
            }
            logger.info("Prescription {}: WhatsApp message sent to {}", prescriptionId, phone);
            return Outcome.SENT;
        } catch (Exception e) {
            logger.error("Could not notify patient about prescription {}", prescriptionId, e);
            return Outcome.REJECTED;
        }
    }
}
