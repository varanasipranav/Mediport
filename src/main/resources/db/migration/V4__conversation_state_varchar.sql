-- The conversation step was a fixed MySQL ENUM, so adding a new step (language picker) was rejected by the database.
-- Plain text columns let new steps be added without a schema change.
ALTER TABLE whatsapp_conversations MODIFY current_state VARCHAR(50) NOT NULL;
ALTER TABLE whatsapp_conversations MODIFY previous_state VARCHAR(50) NULL;
