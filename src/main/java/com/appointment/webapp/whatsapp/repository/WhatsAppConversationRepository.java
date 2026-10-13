package com.appointment.webapp.whatsapp.repository;

import com.appointment.webapp.model.User;
import com.appointment.webapp.whatsapp.model.WhatsAppConversation;
import com.appointment.webapp.whatsapp.model.enums.ConversationState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for WhatsApp Conversation management
 */
@Repository
public interface WhatsAppConversationRepository extends JpaRepository<WhatsAppConversation, Long> {

    /**
     * Find conversation by WhatsApp phone number
     */
    Optional<WhatsAppConversation> findByWhatsappPhone(String whatsappPhone);

    /**
     * Find conversation by linked user
     */
    Optional<WhatsAppConversation> findByUser(User user);

    /**
     * Find conversations by current state
     */
    List<WhatsAppConversation> findByCurrentState(ConversationState state);

    /**
     * Find expired sessions
     */
    @Query("SELECT c FROM WhatsAppConversation c WHERE c.sessionExpiresAt < :now")
    List<WhatsAppConversation> findExpiredSessions(@Param("now") LocalDateTime now);

    /**
     * Find active conversations (not expired)
     */
    @Query("SELECT c FROM WhatsAppConversation c WHERE c.sessionExpiresAt > :now")
    List<WhatsAppConversation> findActiveConversations(@Param("now") LocalDateTime now);

    /**
     * Find conversations in a specific state for a long time (stuck)
     */
    @Query("SELECT c FROM WhatsAppConversation c WHERE c.currentState = :state AND c.lastMessageAt < :cutoffTime")
    List<WhatsAppConversation> findStuckConversations(
        @Param("state") ConversationState state,
        @Param("cutoffTime") LocalDateTime cutoffTime
    );

    /**
     * Count active conversations
     */
    @Query("SELECT COUNT(c) FROM WhatsAppConversation c WHERE c.sessionExpiresAt > :now")
    Long countActiveConversations(@Param("now") LocalDateTime now);

    /**
     * Delete conversations older than specified date
     */
    void deleteByCreatedAtBefore(LocalDateTime cutoffDate);
}
