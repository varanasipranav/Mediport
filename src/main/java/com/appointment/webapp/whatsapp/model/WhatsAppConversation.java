package com.appointment.webapp.whatsapp.model;

import com.appointment.webapp.model.User;
import com.appointment.webapp.whatsapp.model.enums.ConversationState;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * WhatsApp Conversation Entity
 * Tracks user conversation state and session data
 */
@Entity
@Table(name = "whatsapp_conversations")
public class WhatsAppConversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "whatsapp_phone", unique = true, nullable = false, length = 20)
    private String whatsappPhone; // E.164 format: +919876543210

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // Linked patient (null until registration complete)

    @Enumerated(EnumType.STRING)
    @Column(name = "current_state", nullable = false, length = 50)
    private ConversationState currentState = ConversationState.INITIAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_state", length = 50)
    private ConversationState previousState;

    @Column(name = "context_data", columnDefinition = "JSON")
    private String contextData; // Store as JSON string

    @Column(name = "session_started_at")
    private LocalDateTime sessionStartedAt;

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    @Column(name = "session_expires_at")
    private LocalDateTime sessionExpiresAt;

    @Column(name = "sender_name", length = 100)
    private String senderName;

    @Column(name = "preferred_language", length = 10)
    private String preferredLanguage = "en";

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public WhatsAppConversation() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.sessionStartedAt = LocalDateTime.now();
        this.lastMessageAt = LocalDateTime.now();
        this.sessionExpiresAt = LocalDateTime.now().plusMinutes(30);
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (sessionStartedAt == null) {
            sessionStartedAt = LocalDateTime.now();
        }
        if (lastMessageAt == null) {
            lastMessageAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Business Methods

    /**
     * Update state and save previous state
     */
    public void updateState(ConversationState newState) {
        this.previousState = this.currentState;
        this.currentState = newState;
        this.lastMessageAt = LocalDateTime.now();
        this.sessionExpiresAt = LocalDateTime.now().plusMinutes(30);
    }

    /**
     * Check if session has expired
     */
    public boolean isSessionExpired() {
        return LocalDateTime.now().isAfter(sessionExpiresAt);
    }

    /**
     * Reset conversation to initial state
     */
    public void reset() {
        this.currentState = ConversationState.INITIAL;
        this.previousState = null;
        this.contextData = null;
        this.sessionStartedAt = LocalDateTime.now();
        this.lastMessageAt = LocalDateTime.now();
        this.sessionExpiresAt = LocalDateTime.now().plusMinutes(30);
    }

    /**
     * Update last activity timestamp
     */
    public void touch() {
        this.lastMessageAt = LocalDateTime.now();
        this.sessionExpiresAt = LocalDateTime.now().plusMinutes(30);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWhatsappPhone() {
        return whatsappPhone;
    }

    public void setWhatsappPhone(String whatsappPhone) {
        this.whatsappPhone = whatsappPhone;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ConversationState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(ConversationState currentState) {
        this.currentState = currentState;
    }

    public ConversationState getPreviousState() {
        return previousState;
    }

    public void setPreviousState(ConversationState previousState) {
        this.previousState = previousState;
    }

    public String getContextData() {
        return contextData;
    }

    public void setContextData(String contextData) {
        this.contextData = contextData;
    }

    public LocalDateTime getSessionStartedAt() {
        return sessionStartedAt;
    }

    public void setSessionStartedAt(LocalDateTime sessionStartedAt) {
        this.sessionStartedAt = sessionStartedAt;
    }

    public LocalDateTime getLastMessageAt() {
        return lastMessageAt;
    }

    public void setLastMessageAt(LocalDateTime lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
    }

    public LocalDateTime getSessionExpiresAt() {
        return sessionExpiresAt;
    }

    public void setSessionExpiresAt(LocalDateTime sessionExpiresAt) {
        this.sessionExpiresAt = sessionExpiresAt;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "WhatsAppConversation{" +
                "id=" + id +
                ", whatsappPhone='" + whatsappPhone + '\'' +
                ", currentState=" + currentState +
                ", senderName='" + senderName + '\'' +
                '}';
    }
}
