# 🏗️ WhatsApp Integration - Technical Architecture Deep Dive

**Version:** 1.0  
**Last Updated:** January 31, 2026  
**Architecture Level:** Production-Grade Enterprise

---

## 📐 SYSTEM ARCHITECTURE OVERVIEW

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           PRESENTATION LAYER                                 │
├──────────────────────────────────┬──────────────────────────────────────────┤
│         Web Channel              │         WhatsApp Channel                 │
│                                  │                                          │
│  ┌─────────────────────────┐    │    ┌───────────────────────────────┐    │
│  │   Thymeleaf Templates   │    │    │   WhatsApp Cloud API          │    │
│  │   • Patient Portal      │    │    │   • Webhook Events            │    │
│  │   • Doctor Dashboard    │    │    │   • Message Delivery          │    │
│  │   • Attendant Queue     │    │    │   • Interactive Messages      │    │
│  └────────────┬────────────┘    │    └───────────────┬───────────────┘    │
│               │                  │                    │                     │
└───────────────┼──────────────────┴────────────────────┼─────────────────────┘
                │                                       │
                ▼                                       ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                          CONTROLLER LAYER                                    │
├──────────────────────────────────┬──────────────────────────────────────────┤
│    Web MVC Controllers           │    REST API Controllers                  │
│                                  │                                          │
│  • PatientController             │    • WhatsAppWebhookController           │
│  • DoctorController              │    • WhatsAppFlowController              │
│  • AttendantController           │    • ApiController (existing)            │
│  • AdminController               │                                          │
│                                  │                                          │
└──────────────┬───────────────────┴───────────────────┬──────────────────────┘
               │                                       │
               │    ┌──────────────────────────────────┘
               │    │
               ▼    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                          SERVICE LAYER (Business Logic)                      │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌────────────────────┐  ┌────────────────────┐  ┌────────────────────┐   │
│  │ Core Services      │  │ WhatsApp Services  │  │ Integration Svc    │   │
│  │ (EXISTING)         │  │ (NEW)              │  │ (NEW)              │   │
│  ├────────────────────┤  ├────────────────────┤  ├────────────────────┤   │
│  │ • AppointmentSvc   │  │ • ConversationSvc  │  │ • NotificationSvc  │   │
│  │ • UserService      │  │ • MessageService   │  │ • SchedulerService │   │
│  │ • DoctorSchedule   │  │ • FlowOrchestrator │  │ • EventPublisher   │   │
│  │ • PrescriptionSvc  │  │ • WebhookProcessor │  │                    │   │
│  └────────────────────┘  └────────────────────┘  └────────────────────┘   │
│                                                                              │
└──────────────┬───────────────────────────────────────────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                       REPOSITORY LAYER (Data Access)                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌───────────────────┐  ┌───────────────────┐  ┌─────────────────────┐    │
│  │ Core Repositories │  │ WhatsApp Repos    │  │ Notification Repos  │    │
│  ├───────────────────┤  ├───────────────────┤  ├─────────────────────┤    │
│  │ • UserRepo        │  │ • ConversationRepo│  │ • NotificationQueue │    │
│  │ • AppointmentRepo │  │ • MessageRepo     │  │ • AuditLogRepo      │    │
│  │ • ScheduleRepo    │  │ • SessionRepo     │  │                     │    │
│  │ • PrescriptionRepo│  │                   │  │                     │    │
│  └───────────────────┘  └───────────────────┘  └─────────────────────┘    │
│                                                                              │
└──────────────┬───────────────────────────────────────────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                          PERSISTENCE LAYER                                   │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │                     MySQL Database (Primary)                        │    │
│  ├────────────────────────────────────────────────────────────────────┤    │
│  │  Core Tables:                    │  WhatsApp Tables:                │    │
│  │  • users                         │  • whatsapp_conversations        │    │
│  │  • roles                         │  • whatsapp_messages             │    │
│  │  • appointments                  │  • whatsapp_notifications_queue  │    │
│  │  • doctor_schedules              │  • whatsapp_audit_log            │    │
│  │  • prescriptions                 │                                  │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │         Redis Cache (Optional - for High Performance)               │    │
│  ├────────────────────────────────────────────────────────────────────┤    │
│  │  • Active conversation sessions (TTL: 15 min)                       │    │
│  │  • Temporary context data (booking flow state)                      │    │
│  │  • Rate limiting counters                                           │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 🔄 CONVERSATION STATE MACHINE

```
┌─────────────────────────────────────────────────────────────────────────┐
│                   WhatsApp Conversation Flow State Machine              │
└─────────────────────────────────────────────────────────────────────────┘

                              ┌──────────┐
                              │ NEW_USER │ (First message from unknown number)
                              └────┬─────┘
                                   │
                    ┌──────────────┴─────────────┐
                    │                            │
              Registered?                   Not Registered
                    │                            │
                    ▼                            ▼
            ┌──────────────┐          ┌───────────────────┐
            │  MAIN_MENU   │          │ AWAITING_NAME     │
            └──────┬───────┘          └─────┬─────────────┘
                   │                        │
       ┌───────────┼────────────┐          ▼
       │           │            │   ┌───────────────────┐
       │           │            │   │ AWAITING_GENDER   │
       │           │            │   └─────┬─────────────┘
       │           │            │          │
       │           │            │          ▼
       │           │            │   ┌───────────────────┐
       │           │            │   │ AWAITING_DOB      │
       │           │            │   └─────┬─────────────┘
       │           │            │          ��
       │           │            │          ▼
       │           │            │   ┌───────────────────────┐
       │           │            └──►│ REGISTRATION_COMPLETE │
       │           │                └─────┬───────────���─────┘
       │           │                      │
       │           │                      ���─────►┌──────────────┐
       │           │                             │  MAIN_MENU   │
       │           │                             └──────┬───────┘
       │           │                                    │
       ▼           ▼                                    │
┌───────────┐  ┌──────────────┐                       │
│  View     │  │   Book       │◄──────────────────────┘
│  Appts    │  │ Appointment  │
└───────────┘  └──────┬───────┘
                      │
              ┌───────┼────────┬───────────────┐
              ▼       ▼        ▼               ▼
       ┌─────────┐ ┌──────┐ ┌──────┐   ┌──────────┐
       │ SELECT  │ │SELECT│ │SELECT│   │  CANCEL  │
       │ DOCTOR  │ │ DATE │ │CONFIRM│  │APPOINTMENT│
       └────┬────┘ └──┬───┘ └──┬───┘   └──────────┘
            │         │        │
            └─────────┴────────┴─────► ┌──────────────┐
                                       │ BOOKING_DONE │
                                       └──────┬───────┘
                                              │
                                              ▼
                                       ┌──────────────┐
                                       │  MAIN_MENU   │
                                       └──────────────┘

State Timeouts:
- AWAITING_* states: 5 minutes (then reset to MAIN_MENU)
- MAIN_MENU: 60 minutes (then reset to NEW_USER)
- Idle conversation: 24 hours (then archive)
```

---

## 📦 PACKAGE STRUCTURE (New Additions)

```
src/main/java/com/appointment/webapp/
│
├── whatsapp/                           ⭐ NEW PACKAGE
│   │
│   ├── config/
│   │   ├── WhatsAppConfig.java         // API credentials, URLs
│   │   ├── WhatsAppRestConfig.java     // RestTemplate for WhatsApp API
│   │   └── ConversationConfig.java     // Timeouts, limits
│   │
│   ├── controller/
│   │   ├── WhatsAppWebhookController.java    // Main webhook endpoint
│   │   └── WhatsAppAdminController.java      // Admin panel (broadcast, stats)
│   │
│   ├── service/
│   │   ├── conversation/
│   │   │   ├── WhatsAppConversationService.java    // Session management
│   │   │   ├── ConversationStateManager.java       // State transitions
│   │   │   └── ConversationContextService.java     // Context data (temp storage)
│   │   │
│   │   ├── messaging/
│   │   │   ├── WhatsAppMessageService.java         // Send messages
│   │   │   ├── TemplateMessageService.java         // Template messages
│   │   │   └── InteractiveMessageService.java      // Buttons, lists
│   │   │
│   │   ├── flow/
│   │   │   ├── WhatsAppFlowOrchestrator.java       // Main conversation router
│   │   │   ├── RegistrationFlowHandler.java        // Registration logic
│   │   │   ├── BookingFlowHandler.java             // Booking logic
│   │   │   ├── ViewAppointmentsHandler.java        // View appointments
│   │   │   ├── QueueStatusHandler.java             // Queue queries
│   │   │   └── PrescriptionViewHandler.java        // Prescription view
│   │   │
│   │   ├── notification/
│   │   │   ├── WhatsAppNotificationService.java    // Scheduled notifications
│   │   │   ├── AppointmentReminderService.java     // Reminders (24h, 2h)
│   │   │   ├── QueueUpdateNotifier.java            // Real-time queue updates
│   │   │   └── NotificationScheduler.java          // Background processor
│   │   │
│   │   └── webhook/
│   │       ├── WhatsAppWebhookProcessor.java       // Process incoming webhooks
│   │       ├── WebhookSignatureValidator.java      // Security verification
│   │       └── MessageStatusProcessor.java         // Handle delivery receipts
│   │
│   ├── model/
│   │   ├── entity/
│   │   │   ├── WhatsAppConversation.java           // JPA entity
│   │   │   ├── WhatsAppMessage.java                // JPA entity
│   │   │   └── WhatsAppNotification.java           // JPA entity
│   │   │
│   │   ├── enums/
│   │   │   ├── ConversationState.java              // State machine states
│   │   │   ├── MessageDirection.java               // INCOMING, OUTGOING
│   │   │   ├── MessageType.java                    // TEXT, TEMPLATE, INTERACTIVE
│   │   │   └── NotificationStatus.java             // PENDING, SENT, FAILED
│   │   │
│   │   └── dto/
│   │       ├── WebhookEvent.java                   // Incoming webhook DTO
│   │       ├── WhatsAppTextMessage.java            // Outgoing text DTO
│   │       ├── InteractiveButton.java              // Button DTO
│   │       └── TemplateMessage.java                // Template DTO
│   │
│   ├── repository/
│   ��   ├── WhatsAppConversationRepository.java
│   │   ├── WhatsAppMessageRepository.java
│   │   └── WhatsAppNotificationRepository.java
│   │
│   └── util/
│       ├── PhoneNumberUtil.java                    // E.164 formatting, validation
│       ├── MessageFormatter.java                   // Format messages for WhatsApp
│       └── DateTimeFormatter.java                  // Human-readable dates
│
└── (existing packages remain unchanged)
    ├── controller/
    ├── service/
    ├── model/
    ├── repository/
    ├── config/
    └── security/
```

---

## 🗄️ DATABASE SCHEMA - DETAILED

### **New Table 1: whatsapp_conversations**

```sql
CREATE TABLE whatsapp_conversations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- Identity
    whatsapp_phone VARCHAR(20) NOT NULL UNIQUE COMMENT 'E.164 format: +919876543210',
    user_id BIGINT COMMENT 'Linked patient user (NULL until registration)',
    
    -- State Machine
    current_state VARCHAR(50) NOT NULL DEFAULT 'NEW_USER',
    previous_state VARCHAR(50),
    
    -- Context Data (JSON)
    context_data JSON COMMENT 'Temporary data: {doctorId: 5, selectedDate: "2026-02-01"}',
    
    -- Session Management
    session_started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_message_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    session_expires_at TIMESTAMP,
    
    -- Metadata
    preferred_language VARCHAR(10) DEFAULT 'en' COMMENT 'en, hi, ta, te, etc.',
    timezone VARCHAR(50) DEFAULT 'Asia/Kolkata',
    
    -- Audit
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign Keys
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    
    -- Indexes
    INDEX idx_whatsapp_phone (whatsapp_phone),
    INDEX idx_user_id (user_id),
    INDEX idx_current_state (current_state),
    INDEX idx_last_message_at (last_message_at),
    INDEX idx_session_expires (session_expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

**Rationale:**
- `whatsapp_phone` is the primary identifier (before user registration)
- `context_data` stores temporary state (e.g., selected doctor during booking flow)
- `session_expires_at` enables automatic cleanup of idle conversations
- `user_id` links to existing `users` table after registration

---

### **New Table 2: whatsapp_messages**

```sql
CREATE TABLE whatsapp_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- Relationships
    conversation_id BIGINT NOT NULL,
    
    -- Message Identity
    message_id VARCHAR(100) COMMENT 'WhatsApp unique message ID (wamid.xxx)',
    
    -- Message Details
    direction ENUM('INCOMING', 'OUTGOING') NOT NULL,
    message_type ENUM('TEXT', 'TEMPLATE', 'INTERACTIVE', 'IMAGE', 'DOCUMENT') NOT NULL,
    content TEXT NOT NULL COMMENT 'Message content or payload JSON',
    
    -- Delivery Status
    status ENUM('PENDING', 'SENT', 'DELIVERED', 'READ', 'FAILED') DEFAULT 'PENDING',
    error_message VARCHAR(500) COMMENT 'Error details if FAILED',
    
    -- Timestamps
    sent_at TIMESTAMP COMMENT 'When message was sent to WhatsApp API',
    delivered_at TIMESTAMP COMMENT 'When WhatsApp confirmed delivery',
    read_at TIMESTAMP COMMENT 'When user read the message',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign Keys
    FOREIGN KEY (conversation_id) REFERENCES whatsapp_conversations(id) ON DELETE CASCADE,
    
    -- Indexes
    INDEX idx_conversation_id (conversation_id),
    INDEX idx_message_id (message_id),
    INDEX idx_direction (direction),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

**Rationale:**
- Complete message history for debugging and analytics
- Track delivery status (SENT → DELIVERED → READ)
- `message_id` from WhatsApp enables status update correlation

---

### **New Table 3: whatsapp_notifications_queue**

```sql
CREATE TABLE whatsapp_notifications_queue (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- Target
    user_id BIGINT NOT NULL,
    whatsapp_phone VARCHAR(20) NOT NULL COMMENT 'E.164 format',
    
    -- Notification Details
    notification_type VARCHAR(50) NOT NULL COMMENT 'APPOINTMENT_REMINDER, QUEUE_UPDATE, etc.',
    template_name VARCHAR(100) COMMENT 'WhatsApp template name (for approved templates)',
    
    -- Payload
    payload JSON NOT NULL COMMENT 'Data for message generation: {appointmentId: 123, queueNumber: 5}',
    
    -- Scheduling
    scheduled_at TIMESTAMP NOT NULL COMMENT 'When to send (NULL = immediate)',
    sent_at TIMESTAMP COMMENT 'When actually sent',
    
    -- Status & Retry
    status ENUM('PENDING', 'PROCESSING', 'SENT', 'FAILED', 'CANCELLED') DEFAULT 'PENDING',
    retry_count INT DEFAULT 0,
    max_retries INT DEFAULT 3,
    next_retry_at TIMESTAMP,
    
    -- Metadata
    error_message VARCHAR(500),
    whatsapp_message_id VARCHAR(100) COMMENT 'Response from WhatsApp API',
    
    -- Audit
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign Keys
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    -- Indexes
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_scheduled_at (scheduled_at),
    INDEX idx_notification_type (notification_type),
    INDEX idx_next_retry (next_retry_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

**Rationale:**
- Decoupled notification sending (async queue)
- Retry mechanism for failed deliveries
- Scheduled notifications (e.g., reminder 24h before appointment)
- Can process in batches using background scheduler

---

### **Modified Table: users** (Add columns)

```sql
ALTER TABLE users
ADD COLUMN whatsapp_phone VARCHAR(20) COMMENT 'E.164 format, can differ from phone_number',
ADD COLUMN whatsapp_opt_in BOOLEAN DEFAULT TRUE COMMENT 'User consent for WhatsApp messages',
ADD COLUMN preferred_language VARCHAR(10) DEFAULT 'en' COMMENT 'Language preference',
ADD INDEX idx_whatsapp_phone (whatsapp_phone);
```

**Rationale:**
- `whatsapp_phone` may differ from `phone_number` (international format)
- `whatsapp_opt_in` for GDPR/privacy compliance
- `preferred_language` for multi-lingual support

---

## 🔐 SECURITY IMPLEMENTATION

### **1. Webhook Signature Verification**

```java
@Component
public class WebhookSignatureValidator {
    
    @Value("${whatsapp.webhook.app-secret}")
    private String appSecret;
    
    /**
     * Verify incoming webhook signature
     * https://developers.facebook.com/docs/graph-api/webhooks/getting-started#verification-requests
     */
    public boolean validateSignature(String payload, String signature) {
        if (signature == null || !signature.startsWith("sha256=")) {
            return false;
        }
        
        try {
            String expectedSignature = "sha256=" + computeHmacSha256(payload, appSecret);
            return MessageDigest.isEqual(
                signature.getBytes(StandardCharsets.UTF_8),
                expectedSignature.getBytes(StandardCharsets.UTF_8)
            );
        } catch (Exception e) {
            logger.error("Error validating webhook signature", e);
            return false;
        }
    }
    
    private String computeHmacSha256(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKey);
        byte[] hmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hmac);
    }
    
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
```

---

### **2. Rate Limiting**

```java
@Component
public class WhatsAppRateLimiter {
    
    private final ConcurrentHashMap<String, Deque<Instant>> requestHistory = new ConcurrentHashMap<>();
    private static final int MAX_REQUESTS_PER_MINUTE = 60;
    
    public boolean allowRequest(String phoneNumber) {
        requestHistory.putIfAbsent(phoneNumber, new ConcurrentLinkedDeque<>());
        Deque<Instant> history = requestHistory.get(phoneNumber);
        
        Instant now = Instant.now();
        Instant oneMinuteAgo = now.minus(1, ChronoUnit.MINUTES);
        
        // Remove old requests
        while (!history.isEmpty() && history.peekFirst().isBefore(oneMinuteAgo)) {
            history.pollFirst();
        }
        
        // Check limit
        if (history.size() >= MAX_REQUESTS_PER_MINUTE) {
            return false; // Rate limit exceeded
        }
        
        history.addLast(now);
        return true;
    }
}
```

**Usage in Controller:**
```java
@PostMapping("/webhook")
public ResponseEntity<String> receiveWebhook(@RequestBody String payload, 
                                              @RequestHeader("X-Hub-Signature-256") String signature) {
    
    // Verify signature
    if (!signatureValidator.validateSignature(payload, signature)) {
        logger.warn("Invalid webhook signature");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature");
    }
    
    WebhookEvent event = parseWebhook(payload);
    String phoneNumber = event.getFrom();
    
    // Rate limiting
    if (!rateLimiter.allowRequest(phoneNumber)) {
        logger.warn("Rate limit exceeded for {}", phoneNumber);
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Rate limit exceeded");
    }
    
    // Process webhook asynchronously
    webhookProcessor.processAsync(event);
    
    return ResponseEntity.ok("OK");
}
```

---

### **3. Environment Variables (AWS Secrets Manager)**

**Never hardcode credentials in application.properties!**

```yaml
# application.properties (AWS Elastic Beanstalk)
whatsapp.api.base-url=https://graph.facebook.com/v18.0
whatsapp.api.phone-number-id=${WHATSAPP_PHONE_NUMBER_ID}
whatsapp.api.access-token=${WHATSAPP_ACCESS_TOKEN}
whatsapp.webhook.verify-token=${WHATSAPP_VERIFY_TOKEN}
whatsapp.webhook.app-secret=${WHATSAPP_APP_SECRET}
```

**Set in AWS Elastic Beanstalk:**
```bash
eb setenv \
  WHATSAPP_PHONE_NUMBER_ID=123456789 \
  WHATSAPP_ACCESS_TOKEN=EAAxxxxxxxxxxxx \
  WHATSAPP_VERIFY_TOKEN=my_secure_random_token_12345 \
  WHATSAPP_APP_SECRET=abcdef1234567890
```

---

## 🔄 MESSAGE FLOW SEQUENCE DIAGRAMS

### **Flow 1: User Sends Message**

```
┌─────────┐           ┌──────────┐           ┌──────────────┐           ┌─────────┐
│ Patient │           │ WhatsApp │           │ Spring Boot  │           │ MySQL   │
│         │           │   API    │           │  Application │           │   DB    │
└────┬────┘           └────┬─────┘           └──────┬───────┘           └────┬────┘
     │                     │                        │                        │
     │ 1. Send "Hi"        │                        │                        │
     │────────────────────>│                        │                        │
     │                     │                        │                        │
     │                     │ 2. POST /webhook       │                        │
     │                     │   (webhook event)      │                        │
     │                     │───────────────────────>│                        │
     │                     │                        │                        │
     │                     │                        │ 3. Verify signature    │
     │                     │                        │────────┐               │
     │                     │                        │        │               │
     │                     │                        │<───────┘               │
     │                     │                        │                        │
     │                     │                        │ 4. Get/Create          │
     │                     │                        │    Conversation        │
     │                     │                        │───────────────────────>│
     │                     │                        │                        │
     │                     │                        │<───────────────────────│
     │                     │                        │   (conversation data)  │
     │                     │                        │                        │
     │                     │                        │ 5. Route to handler    │
     │                     │                        │    based on state      │
     │                     │                        │────────┐               │
     │                     │                        │        │               │
     │                     │                        │<───────┘               │
     │                     │                        │                        │
     │                     │                        │ 6. Generate response   │
     │                     │                        │────────┐               │
     │                     │                        │        │               │
     │                     │                        │<───────┘               │
     │                     │                        │                        │
     │                     │                        │ 7. Save message        │
     │                     │                        │───────────────────────>│
     │                     │                        │                        │
     │                     │                        │ 8. Update state        │
     │                     │                        │───────────────────────>│
     │                     │                        │                        │
     │                     │ 9. POST /messages      │                        │
     │                     │    (send reply)        │                        │
     │                     │<───────────────────────│                        │
     │                     │                        │                        │
     │                     │ 10. Return 200 OK      │                        │
     │                     │───────────────────────>│                        │
     │                     │                        │                        │
     │ 11. Receive reply   │                        │                        │
     │<────────────────────│                        │                        │
     │                     │                        │                        │
```

---

### **Flow 2: Book Appointment**

```
┌─────────┐      ┌──────────────┐      ┌──────────────┐      ┌─────────┐
│ Patient │      │ FlowHandler  │      │ Appointment  │      │ MySQL   │
│         │      │              │      │   Service    │      │   DB    │
└────┬────┘      └──────┬───────┘      └──────┬───────┘      └────┬────┘
     │                  │                     │                   │
     │ "1" (Book Appt)  │                     │                   │
     │─────────────────>│                     │                   │
     │                  │                     │                   │
     │                  │ 1. Update state     │                   │
     │                  │    BOOKING_SELECT_DOCTOR                │
     │                  │───────────────────────────────────���────>│
     │                  │                     │                   │
     │                  │ 2. Get available    │                   │
     │                  │    doctors          │                   │
     │                  │────────────────────>│                   │
     │                  │                     │                   │
     │                  │                     │ Query schedules   │
     │                  │                     │──────────────────>│
     │                  │                     │                   │
     │                  │                     │<──────────────────│
     │                  │                     │   (doctor list)   │
     │                  │<────────────────────│                   │
     │                  │                     │                   │
     │                  │ 3. Format message   │                   │
     │                  │    with doctor list │                   │
     │                  │────────┐            │                   │
     │                  │        │            │                   │
     │                  │<───────┘            │                   │
     │                  │                     │                   │
     │ 4. Send list     │                     │                   │
     │<─────────────────│                     │                   │
     │                  │                     │                   │
     │ "2" (Dr. Brown)  │                     │                   │
     │─────────────────>│                     │                   │
     │                  │                     │                   │
     │                  │ 5. Save context     │                   │
     │                  │    {doctorId: 2}    │                   │
     │                  │────────────────────────────────────────>│
     │                  │                     │                   │
     │                  │ 6. Check slots      │                   │
     │                  │────────────────────>│                   │
     │                  │                     │                   │
     │                  │<────────────────────│                   │
     │                  │   (slot info)       │                   │
     │                  │                     │                   │
     │ 7. Show slots    │                     │                   │
     │<─────────────────│                     │                   │
     │                  │                     │                   │
     │ "1" (Confirm)    │                     │                   │
     │─────────────────>│                     │                   │
     │                  │                     │                   │
     │                  │ 8. Book appointment │                   │
     │                  │────────────────────>│                   │
     │                  │                     │                   │
     │                  │                     │ Create appointment│
     │                  │                     │──────────────────>│
     │                  │                     │                   │
     │                  │                     │<──────────────────│
     │                  │                     │   (appointment)   │
     │                  │<────────────────────│                   │
     │                  │                     │                   │
     │                  │ 9. Clear context    │                   │
     │                  │────────────────────────────────────────>│
     │                  │                     │                   │
     │                  │10. Schedule reminder│                   │
     │                  │────────────────────────────────────────>│
     │                  │                     │                   │
     │ 11. Confirmation │                     │                   │
     │<─────────────────│                     │                   │
     │  "✅ Booked!     │                     │                   │
     │   Queue #6"      │                     │                   │
     │                  │                     │                   │
```

---

## 🚀 DEPLOYMENT ARCHITECTURE (AWS)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              AWS CLOUD                                       │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────────┐ │
│  │                         Route 53 (DNS)                                 │ │
│  │                   hospital.example.com                                 │ │
│  └──────────────────────────────┬─────────────────────────────────────────┘ │
│                                 │                                            │
│  ┌──────────────────────────────▼─────────────────────────────────────────┐ │
│  │                  Application Load Balancer (ALB)                       │ │
│  │              HTTPS (SSL/TLS Certificate from ACM)                      │ │
│  └──────────────────┬──────────────────────┬──────────────────────────────┘ │
│                     │                      │                                 │
│          ┌──────────▼──────────┐  ┌────────▼─────────┐                      │
│          │   Target Group 1    │  │  Target Group 2  │                      │
│          │  /api/whatsapp/*    │  │     /*           │                      │
│          └──────────┬──────────┘  └────────┬─────────┘                      │
│                     │                      │                                 │
│  ┌──────────────────▼──────────────────────▼──────────────────────────────┐ │
│  │              Elastic Beanstalk Environment                             │ │
│  │                                                                         │ │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                    │ │
│  │  │   EC2       │  │   EC2       │  │   EC2       │                    │ │
│  │  │ Instance 1  │  │ Instance 2  │  │ Instance 3  │                    │ │
│  │  │             │  │             │  │             │                    │ │
│  │  │ Spring Boot │  │ Spring Boot │  │ Spring Boot │                    │ │
│  │  │   App       │  │   App       │  │   App       │                    │ │
│  │  └─────────────┘  └─────────────┘  └─────────────┘                    │ │
│  │                                                                         │ │
│  │  Auto Scaling Group (Min: 2, Max: 10)                                  │ │
│  └─────────────────────────┬───────────────────────────────────────────────┘ │
│                            │                                                 │
│  ┌─────────────────────────▼───────────────────────────────────────────────┐ │
│  │                     RDS MySQL (Multi-AZ)                                │ │
│  │                                                                         │ │
│  │  ┌──────────────────┐        ┌──────────────────┐                      │ │
│  │  │   Primary DB     │        │   Standby DB     │                      │ │
│  │  │  (AZ-1a)         │◄──────►│   (AZ-1b)        │                      │ │
│  │  │                  │        │  (Auto Failover) │                      │ │
│  │  └──────────────────┘        └──────────────────┘                      │ │
│  │                                                                         │ │
│  │  Automated Backups (7 days retention)                                  │ │
│  └─────────────────────────────────────────────────────────────────────────┘ │
│                                                                              │
│  ┌─────────────────────────────────────────────────────────────────────────┐ │
│  │                ElastiCache Redis (Optional)                             │ │
│  │                                                                         │ │
│  │  ┌──────────────────┐        ┌──────────────────┐                      │ │
│  │  │  Primary Cache   │◄──────►│ Replica Cache    │                      │ │
│  │  │   (AZ-1a)        │        │   (AZ-1b)        │                      │ │
│  │  └──────────────────┘        └──────────────────┘                      │ │
│  │                                                                         │ │
│  │  Use case: Session management, rate limiting counters                  │ │
│  └─────────────────────────────────────────────────────────────────────────┘ │
│                                                                              │
│  ┌─────────────────────────────────────────────────────────────────────────┐ │
│  │                    CloudWatch (Monitoring)                              │ │
│  │                                                                         │ │
│  │  • Application logs                                                     │ │
│  │  • Webhook request metrics                                              │ │
│  │  • Notification delivery rates                                          │ │
│  │  • Error rate alarms                                                    │ │
│  │  • Auto-scaling triggers                                                │ │
│  └─────────────────────────────────────────────────────────────────────────┘ │
│                                                                              │
│  ┌─────────────────────────────────────────────────────────────────────────┐ │
│  │                   Secrets Manager                                       │ │
│  │                                                                         │ │
│  │  • WhatsApp API Access Token                                            │ │
│  │  • WhatsApp App Secret                                                  │ │
│  │  • Database credentials                                                 │ │
│  │  • Webhook verify token                                                 │ │
│  └─────────────────────────────────────────────────────────────────────────┘ │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘

External API:
┌─────────────────────────────────────┐
│    WhatsApp Cloud API               │
│    (Meta/Facebook)                  │
│                                     │
│  • Send messages                    │
│  • Deliver webhooks                 │
│  • Message status updates           │
└─────────────────────────────────────┘
```

---

## 📊 MONITORING & OBSERVABILITY

### **CloudWatch Metrics to Track:**

1. **Webhook Metrics:**
   - `whatsapp.webhook.requests.total` - Total webhook calls
   - `whatsapp.webhook.requests.invalid_signature` - Security violations
   - `whatsapp.webhook.processing.duration` - Processing time

2. **Conversation Metrics:**
   - `whatsapp.conversations.active` - Active conversations
   - `whatsapp.conversations.new` - New conversations (daily)
   - `whatsapp.conversations.state_transitions` - State changes

3. **Message Metrics:**
   - `whatsapp.messages.sent.total` - Outgoing messages
   - `whatsapp.messages.failed` - Failed deliveries
   - `whatsapp.messages.delivery_rate` - % delivered successfully

4. **Notification Metrics:**
   - `whatsapp.notifications.scheduled` - Scheduled notifications
   - `whatsapp.notifications.sent` - Successfully sent
   - `whatsapp.notifications.retry_count` - Retry attempts

5. **Business Metrics:**
   - `whatsapp.bookings.completed` - Appointments booked via WhatsApp
   - `whatsapp.registrations.completed` - New patient registrations
   - `whatsapp.user_engagement` - Active users (daily/weekly)

---

## 🧪 TESTING STRATEGY

### **Unit Tests:**
```java
@SpringBootTest
public class WhatsAppConversationServiceTest {
    
    @Test
    void testStateTransition_NewUser_To_AwaitingName() {
        WhatsAppConversation conv = new WhatsAppConversation();
        conv.setCurrentState(ConversationState.NEW_USER);
        
        conversationService.updateState(conv, ConversationState.AWAITING_NAME);
        
        assertEquals(ConversationState.AWAITING_NAME, conv.getCurrentState());
        assertEquals(ConversationState.NEW_USER, conv.getPreviousState());
    }
}
```

### **Integration Tests:**
```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class WhatsAppWebhookControllerIntegrationTest {
    
    @Test
    void testWebhookValidSignature() throws Exception {
        String payload = "{\"object\":\"whatsapp_business_account\"}";
        String signature = generateSignature(payload);
        
        mockMvc.perform(post("/api/whatsapp/webhook")
                .content(payload)
                .header("X-Hub-Signature-256", signature))
                .andExpect(status().isOk());
    }
}
```

### **End-to-End Tests (Simulated):**
```java
@Test
void testFullBookingFlow() {
    // Simulate user messages
    sendMessage("+919876543210", "Hi");
    assertResponse("Welcome! Are you registered?");
    
    sendMessage("+919876543210", "1"); // Yes
    assertResponse("What would you like to do?");
    
    sendMessage("+919876543210", "1"); // Book appointment
    assertResponse("Available doctors:");
    
    sendMessage("+919876543210", "2"); // Select Dr. Brown
    assertResponse("Confirm booking?");
    
    sendMessage("+919876543210", "1"); // Confirm
    assertResponse("✅ Appointment Booked!");
    
    // Verify database
    Appointment apt = appointmentRepository.findLatestByPatient(patient);
    assertNotNull(apt);
    assertEquals(doctor.getId(), apt.getDoctor().getId());
}
```

---

## 📚 DEPENDENCIES TO ADD (pom.xml)

```xml
<!-- WhatsApp Integration Dependencies -->
<dependencies>
    <!-- Existing dependencies... -->
    
    <!-- HTTP Client for WhatsApp API -->
    <dependency>
        <groupId>org.apache.httpcomponents.client5</groupId>
        <artifactId>httpclient5</artifactId>
    </dependency>
    
    <!-- JSON Processing -->
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
    </dependency>
    
    <!-- Redis (Optional - for session management) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
    <dependency>
        <groupId>redis.clients</groupId>
        <artifactId>jedis</artifactId>
    </dependency>
    
    <!-- Async Task Execution -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-task</artifactId>
    </dependency>
    
    <!-- Phone Number Validation -->
    <dependency>
        <groupId>com.googlecode.libphonenumber</groupId>
        <artifactId>libphonenumber</artifactId>
        <version>8.13.27</version>
    </dependency>
    
    <!-- Testing -->
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

---

## 🎯 SUCCESS METRICS (KPIs)

**Track these after implementation:**

1. **Adoption Metrics:**
   - % of patients using WhatsApp vs Web portal
   - New patient registrations via WhatsApp
   - Daily active WhatsApp users

2. **Operational Metrics:**
   - Appointment booking success rate
   - Average conversation completion time
   - Notification delivery rate (>99% target)

3. **Performance Metrics:**
   - Webhook processing time (<500ms target)
   - Message delivery latency (<2s target)
   - System uptime (>99.9% target)

4. **Business Metrics:**
   - No-show rate reduction (expect 20-30% drop with reminders)
   - Patient satisfaction score
   - Reception workload reduction (hours saved)

---

**Document Version:** 1.0  
**Last Updated:** January 31, 2026  
**Next Review:** After Phase 1 Completion  

🚀 **Ready for Implementation!**
