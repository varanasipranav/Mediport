-- ==========================================
-- WhatsApp Conversation Management Schema
-- Phase 2: State Machine & Session Tracking
-- ==========================================

-- WhatsApp Conversations (Session Management)
CREATE TABLE IF NOT EXISTS whatsapp_conversations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    -- Identity
    whatsapp_phone VARCHAR(20) NOT NULL UNIQUE COMMENT 'E.164 format: +919876543210',
    user_id BIGINT COMMENT 'Linked patient user (NULL until registration)',

    -- State Machine
    current_state VARCHAR(50) NOT NULL DEFAULT 'INITIAL',
    previous_state VARCHAR(50),

    -- Context Data (JSON)
    context_data JSON COMMENT 'Temporary data: {selectedDoctorId: 5, selectedDate: "2026-02-01", patientName: "John"}',

    -- Session Management
    session_started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_message_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    session_expires_at TIMESTAMP,

    -- Metadata
    sender_name VARCHAR(100) COMMENT 'WhatsApp profile name',
    preferred_language VARCHAR(10) DEFAULT 'en',

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

-- WhatsApp Messages Log
CREATE TABLE IF NOT EXISTS whatsapp_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    -- Relationships
    conversation_id BIGINT NOT NULL,

    -- Message Identity
    message_id VARCHAR(100) COMMENT 'WhatsApp message ID (wamid.xxx)',

    -- Message Details
    direction ENUM('INCOMING', 'OUTGOING') NOT NULL,
    message_type ENUM('TEXT', 'BUTTON_REPLY', 'LIST_REPLY', 'INTERACTIVE', 'TEMPLATE') NOT NULL,
    content TEXT NOT NULL COMMENT 'Message content',

    -- Delivery Status
    status ENUM('PENDING', 'SENT', 'DELIVERED', 'READ', 'FAILED') DEFAULT 'PENDING',
    error_message VARCHAR(500),

    -- Timestamps
    sent_at TIMESTAMP COMMENT 'When sent to WhatsApp API',
    delivered_at TIMESTAMP,
    read_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Keys
    FOREIGN KEY (conversation_id) REFERENCES whatsapp_conversations(id) ON DELETE CASCADE,

    -- Indexes
    INDEX idx_conversation_id (conversation_id),
    INDEX idx_message_id (message_id),
    INDEX idx_direction (direction),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Add whatsapp_phone column to users table (if not exists)
ALTER TABLE users
ADD COLUMN IF NOT EXISTS whatsapp_phone VARCHAR(20) COMMENT 'WhatsApp number in E.164 format',
ADD INDEX IF NOT EXISTS idx_whatsapp_phone (whatsapp_phone);

-- Sample test data (optional - for development)
/*
INSERT INTO whatsapp_conversations (whatsapp_phone, current_state, sender_name) VALUES
('+919876543210', 'INITIAL', 'John Doe');
*/
