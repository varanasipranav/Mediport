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
 * Tells the patient on WhatsApp the moment the doctor saves their prescription, with a PDF link
 * that stays valid for a week. Runs after the save has committed and off the doctor's request thread.
 */
@Component
public class WhatsAppPrescriptionNotifier {

    private static final Logger logger = LoggerFactory.getLogger(WhatsAppPrescriptionNotifier.class);

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
        CompletableFuture.runAsync(() -> send(event.prescriptionId()));
    }

    private void send(Long prescriptionId) {
        try {
            Prescription p = prescriptions.getPrescriptionById(prescriptionId);
            String phone = p.getPatient().getWhatsappPhone();
            if (phone == null || phone.isBlank()) return;

            String text = "Hi " + p.getPatient().getFirstName() + ", your prescription from *Dr. " +
                p.getDoctor().getFirstName() + " " + p.getDoctor().getLastName() + "* is ready. \uD83D\uDCC4\n\n" +
                "Prescription no: *" + documents.rxNumber(p) + "*\n" +
                "Download (PDF, valid 7 days):\n" + documents.signedPdfUrl(p.getId()) + "\n\n" +
                "Show it at the pharmacy or collect your medicines there. Get well soon!";
            if (sender.sendTextMessage(phone, text) == null) {
                logger.warn("Prescription {} was saved but the WhatsApp message could not be sent", prescriptionId);
            }
        } catch (Exception e) {
            logger.error("Could not notify patient about prescription {}", prescriptionId, e);
        }
    }
}
