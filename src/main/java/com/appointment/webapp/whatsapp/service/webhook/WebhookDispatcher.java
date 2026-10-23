package com.appointment.webapp.whatsapp.service.webhook;

import com.appointment.webapp.whatsapp.dto.webhook.ParsedMessage;
import com.appointment.webapp.whatsapp.service.flow.WhatsAppMessageProcessor;
import jakarta.annotation.PreDestroy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/**
 * Processes WhatsApp messages off the webhook request thread.
 *
 * - The webhook can return 200 immediately (Meta retries if it takes too long).
 * - Messages from the same sender always land on the same single-thread lane, so a
 *   conversation is processed in order while different senders run in parallel.
 * - Message IDs already seen are skipped, so Meta's retries don't double-process a message.
 */
@Service
public class WebhookDispatcher {

    private static final Logger logger = LoggerFactory.getLogger(WebhookDispatcher.class);

    private static final int LANES = 8;
    private static final long DEDUP_TTL_MS = 60 * 60 * 1000L;

    private final ExecutorService[] lanes = new ExecutorService[LANES];
    private final Map<String, Long> seenMessageIds = new ConcurrentHashMap<>();

    @Autowired
    private WhatsAppMessageProcessor messageProcessor;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private java.util.Optional<com.appointment.webapp.tenancy.TenantRegistry> tenantRegistry;

    public WebhookDispatcher() {
        for (int i = 0; i < LANES; i++) {
            lanes[i] = Executors.newSingleThreadExecutor(Thread.ofPlatform().name("whatsapp-lane-" + i).factory());
        }
    }

    public void dispatch(ParsedMessage message) {
        if (isDuplicate(message.getMessageId())) {
            logger.info("Skipping duplicate WhatsApp message {}", message.getMessageId());
            return;
        }

        String sender = (message.getSiteId() != null ? message.getSiteId() + ":" : "") + (message.getFrom() != null ? message.getFrom() : "");
        ExecutorService lane = lanes[Math.floorMod(sender.hashCode(), LANES)];
        // The clinic is looked up before the task runs; on the lane thread it is set only for this message and always cleared
        var tenant = message.getSiteId() == null ? null : tenantRegistry.map(r -> r.find(message.getSiteId()).orElse(null)).orElse(null);
        if (message.getSiteId() != null && tenant == null) {
            logger.warn("Dropping WhatsApp message {}: unknown clinic {}", message.getMessageId(), message.getSiteId());
            return;
        }
        try {
            lane.execute(() -> {
                if (tenant == null) {
                    processWithOpenSession(message);
                } else {
                    com.appointment.webapp.tenancy.TenantContext.runAs(tenant, () -> processWithOpenSession(message));
                }
            });
        } catch (RejectedExecutionException e) {
            logger.error("WhatsApp dispatcher is shut down; dropping message {}", message.getMessageId());
        }
    }

    /**
     * The flow lazy-loads patients/doctors/appointments. On a web request Spring keeps a JPA session open
     * for the whole request ("open session in view"); a background thread has no such session, so bind one
     * here for the duration of the message - same behaviour as before the webhook went async.
     */
    private void processWithOpenSession(ParsedMessage message) {
        EntityManager em = entityManagerFactory.createEntityManager();
        TransactionSynchronizationManager.bindResource(entityManagerFactory, new EntityManagerHolder(em));
        try {
            messageProcessor.processMessage(message);
        } catch (Exception e) {
            logger.error("Error processing WhatsApp message {}", message.getMessageId(), e);
        } finally {
            TransactionSynchronizationManager.unbindResource(entityManagerFactory);
            EntityManagerFactoryUtils.closeEntityManager(em);
        }
    }

    private boolean isDuplicate(String messageId) {
        if (messageId == null || messageId.isBlank()) {
            return false;
        }
        long now = System.currentTimeMillis();
        if (seenMessageIds.size() > 5000) {
            seenMessageIds.values().removeIf(ts -> now - ts > DEDUP_TTL_MS);
        }
        return seenMessageIds.putIfAbsent(messageId, now) != null;
    }

    @PreDestroy
    void shutdown() {
        for (ExecutorService lane : lanes) {
            lane.shutdown();
        }
    }
}
