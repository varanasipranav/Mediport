-- ==========================================
-- Weekly Schedule Templates
-- Allows doctors to set recurring weekly schedules
-- ==========================================

-- Doctor Weekly Schedule Templates (DEFAULT SCHEDULES)
CREATE TABLE doctor_weekly_templates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    doctor_id BIGINT NOT NULL,

    -- Day of week (1=Monday, 2=Tuesday... 7=Sunday)
    day_of_week INT NOT NULL,

    -- Is this day active? (false = day off)
    is_active BOOLEAN DEFAULT TRUE,

    -- Shift details
    shift_type VARCHAR(20), -- 'MORNING', 'EVENING', 'FULL_DAY', 'CUSTOM'
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,

    -- Appointment settings
    slot_duration_minutes INT NOT NULL DEFAULT 15,
    max_patients INT,

    -- Metadata
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),

    -- Constraints
    UNIQUE(doctor_id, day_of_week),
    CHECK (day_of_week BETWEEN 1 AND 7),
    CHECK (start_time < end_time),
    CHECK (slot_duration_minutes > 0),

    -- Foreign Key
    FOREIGN KEY (doctor_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Indexes for performance
CREATE INDEX idx_weekly_template_doctor ON doctor_weekly_templates(doctor_id);
CREATE INDEX idx_weekly_template_day ON doctor_weekly_templates(day_of_week);
CREATE INDEX idx_weekly_template_active ON doctor_weekly_templates(is_active);

-- Add a column to doctor_schedules to indicate if it's auto-generated from template
ALTER TABLE doctor_schedules
ADD COLUMN is_template_generated BOOLEAN DEFAULT FALSE COMMENT 'Auto-generated from weekly template';

-- Add a column to track overrides
ALTER TABLE doctor_schedules
ADD COLUMN is_override BOOLEAN DEFAULT FALSE COMMENT 'Manual override of weekly template';

-- Sample data: Create default weekly template for a doctor (excluding Sat & Sun)
-- This is just an example - doctors will set their own templates via UI
/*
INSERT INTO doctor_weekly_templates (doctor_id, day_of_week, is_active, shift_type, start_time, end_time, slot_duration_minutes)
VALUES
-- Monday to Friday - Morning shift
(1, 1, TRUE, 'MORNING', '09:00:00', '13:00:00', 15),
(1, 2, TRUE, 'MORNING', '09:00:00', '13:00:00', 15),
(1, 3, TRUE, 'MORNING', '09:00:00', '13:00:00', 15),
(1, 4, TRUE, 'MORNING', '09:00:00', '13:00:00', 15),
(1, 5, TRUE, 'MORNING', '09:00:00', '13:00:00', 15),
-- Saturday & Sunday - OFF by default
(1, 6, FALSE, NULL, '09:00:00', '13:00:00', 15),
(1, 7, FALSE, NULL, '09:00:00', '13:00:00', 15);
*/

-- Create a stored procedure to auto-generate daily schedules from templates
DELIMITER //

CREATE PROCEDURE generate_schedules_from_templates(
    IN p_doctor_id BIGINT,
    IN p_start_date DATE,
    IN p_end_date DATE
)
BEGIN
    DECLARE v_current_date DATE;
    DECLARE v_day_of_week INT;
    DECLARE done INT DEFAULT 0;

    SET v_current_date = p_start_date;

    WHILE v_current_date <= p_end_date DO
        -- Get day of week (1=Monday, 7=Sunday)
        SET v_day_of_week = DAYOFWEEK(v_current_date);
        -- MySQL DAYOFWEEK: 1=Sunday, 2=Monday... 7=Saturday
        -- Convert to: 1=Monday, 7=Sunday
        SET v_day_of_week = IF(v_day_of_week = 1, 7, v_day_of_week - 1);

        -- Insert schedule if template exists and is active, and no manual schedule exists
        INSERT INTO doctor_schedules (
            doctor_id, schedule_date, shift_type,
            start_time, end_time, slot_duration_minutes, max_patients,
            is_active, is_template_generated, created_by
        )
        SELECT
            wt.doctor_id,
            v_current_date,
            wt.shift_type,
            wt.start_time,
            wt.end_time,
            wt.slot_duration_minutes,
            wt.max_patients,
            TRUE,
            TRUE,
            'system-auto'
        FROM doctor_weekly_templates wt
        WHERE wt.doctor_id = p_doctor_id
          AND wt.day_of_week = v_day_of_week
          AND wt.is_active = TRUE
          AND NOT EXISTS (
              SELECT 1 FROM doctor_schedules ds
              WHERE ds.doctor_id = p_doctor_id
                AND ds.schedule_date = v_current_date
          );

        SET v_current_date = DATE_ADD(v_current_date, INTERVAL 1 DAY);
    END WHILE;
END //

DELIMITER ;

-- Usage example:
-- CALL generate_schedules_from_templates(1, '2026-02-01', '2026-02-28');
-- This will create daily schedules for doctor_id=1 for the entire February 2026

