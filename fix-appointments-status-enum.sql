-- ==========================================
-- Fix: Update appointments table status enum
-- Issue: Hibernate stuck on ALTER TABLE
-- Solution: Manual migration
-- ==========================================

-- Check current status column definition
SELECT COLUMN_TYPE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'hospital'
  AND TABLE_NAME = 'appointments'
  AND COLUMN_NAME = 'status';

-- Backup appointments table (optional but recommended)
-- CREATE TABLE appointments_backup AS SELECT * FROM appointments;

-- Update status column with correct enum values
ALTER TABLE appointments
MODIFY COLUMN status VARCHAR(50) NOT NULL;

-- OR if you want to keep enum type:
-- ALTER TABLE appointments
-- MODIFY COLUMN status ENUM(
--   'WAITING',
--   'IN_CONSULTATION',
--   'COMPLETED',
--   'CANCELLED',
--   'DELAYED',
--   'NOT_ATTENDED'
-- ) NOT NULL DEFAULT 'WAITING';

-- Verify the change
SELECT COLUMN_TYPE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'hospital'
  AND TABLE_NAME = 'appointments'
  AND COLUMN_NAME = 'status';
