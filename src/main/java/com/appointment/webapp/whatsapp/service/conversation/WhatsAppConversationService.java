package com.appointment.webapp.whatsapp.service.conversation;

import com.appointment.webapp.model.User;
import com.appointment.webapp.whatsapp.dto.ConversationContext;
import com.appointment.webapp.whatsapp.model.WhatsAppConversation;
import com.appointment.webapp.whatsapp.model.enums.ConversationState;
import com.appointment.webapp.whatsapp.repository.WhatsAppConversationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Conversation Service
 * Manages WhatsApp conversation state and context
 */
@Service
@Transactional
public class WhatsAppConversationService {

    private static final Logger logger = LoggerFactory.getLogger(WhatsAppConversationService.class);

    @Autowired
    private WhatsAppConversationRepository conversationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Get or create conversation by WhatsApp phone number
     */
    public WhatsAppConversation getOrCreateConversation(String whatsappPhone, String senderName) {
        logger.debug("Getting or creating conversation for: {}", whatsappPhone);

        Optional<WhatsAppConversation> existingOpt = conversationRepository.findByWhatsappPhone(whatsappPhone);

        if (existingOpt.isPresent()) {
            WhatsAppConversation existing = existingOpt.get();

            // Check if session expired
            if (existing.isSessionExpired()) {
                logger.info("Session expired for {}. Resetting conversation.", whatsappPhone);
                existing.reset();
                existing.setSenderName(senderName);
            } else {
                existing.touch(); // Update last activity
            }

            return conversationRepository.save(existing);
        }

        // Create new conversation
        WhatsAppConversation newConversation = new WhatsAppConversation();
        newConversation.setWhatsappPhone(whatsappPhone);
        newConversation.setSenderName(senderName);
        newConversation.setCurrentState(ConversationState.INITIAL);

        logger.info("Created new conversation for: {} ({})", senderName, whatsappPhone);
        return conversationRepository.save(newConversation);
    }

    /**
     * Update conversation state
     */
    public void updateState(WhatsAppConversation conversation, ConversationState newState) {
        logger.info("State transition for {}: {} → {}",
                   conversation.getWhatsappPhone(),
                   conversation.getCurrentState(),
                   newState);

        conversation.updateState(newState);
        conversationRepository.save(conversation);
    }

    /**
     * Get conversation context (parse JSON)
     */
    public ConversationContext getContext(WhatsAppConversation conversation) {
        String contextJson = conversation.getContextData();

        if (contextJson == null || contextJson.isEmpty()) {
            return new ConversationContext();
        }

        try {
            return objectMapper.readValue(contextJson, ConversationContext.class);
        } catch (JsonProcessingException e) {
            logger.error("Error parsing context JSON for conversation {}", conversation.getId(), e);
            return new ConversationContext();
        }
    }

    /**
     * Save conversation context (serialize to JSON)
     */
    public void saveContext(WhatsAppConversation conversation, ConversationContext context) {
        try {
            String contextJson = objectMapper.writeValueAsString(context);
            conversation.setContextData(contextJson);
            conversationRepository.save(conversation);
            logger.debug("Saved context for {}: {}", conversation.getWhatsappPhone(), context);
        } catch (JsonProcessingException e) {
            logger.error("Error serializing context for conversation {}", conversation.getId(), e);
        }
    }

    /**
     * Link user to conversation
     */
    public void linkUser(WhatsAppConversation conversation, User user) {
        conversation.setUser(user);

        // Update context with user ID
        ConversationContext context = getContext(conversation);
        context.setUserId(user.getId());
        saveContext(conversation, context);

        logger.info("Linked user {} to conversation {}", user.getId(), conversation.getId());
    }

    /** Remember the language the patient chose for this chat. */
    public void saveLanguage(WhatsAppConversation conversation, String languageCode) {
        conversation.setPreferredLanguage(languageCode);
        conversationRepository.save(conversation);
    }

    /**
     * Reset conversation to initial state
     */
    public void resetConversation(WhatsAppConversation conversation) {
        logger.info("Resetting conversation for: {}", conversation.getWhatsappPhone());
        conversation.reset();
        conversationRepository.save(conversation);
    }

    /**
     * Delete expired conversations (cleanup job)
     */
    @Transactional
    public int cleanupExpiredSessions() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(7); // Keep for 7 days
        var expiredSessions = conversationRepository.findExpiredSessions(LocalDateTime.now());

        int count = 0;
        for (WhatsAppConversation conversation : expiredSessions) {
            if (conversation.getCreatedAt().isBefore(cutoffTime)) {
                conversationRepository.delete(conversation);
                count++;
            }
        }

        if (count > 0) {
            logger.info("Cleaned up {} expired conversations", count);
        }

        return count;
    }

    /**
     * Get conversation by phone number
     */
    public Optional<WhatsAppConversation> getConversationByPhone(String whatsappPhone) {
        return conversationRepository.findByWhatsappPhone(whatsappPhone);
    }

    /**
     * Update last activity timestamp
     */
    public void touchConversation(WhatsAppConversation conversation) {
        conversation.touch();
        conversationRepository.save(conversation);
    }
}
