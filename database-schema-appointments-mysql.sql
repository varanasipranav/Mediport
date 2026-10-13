-- ==========================================
-- Hospital Appointment & Prescription System
-- MySQL Schema - Phase 1
-- ==========================================

-- Doctor Schedules (Source of Truth for Availability)
CREATE TABLE IF NOT EXISTS doctor_schedules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    doctor_id BIGINT NOT NULL,
    schedule_date DATE NOT NULL,
    shift_type VARCHAR(20), -- 'MORNING', 'EVENING', 'CUSTOM'
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    slot_duration_minutes INT NOT NULL DEFAULT 15,
    max_patients INT, -- NULL = auto-calculate from time slots
    priority_work_minutes INT NOT NULL DEFAULT 0, -- Emergency/admin work delays
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    UNIQUE KEY unique_doctor_date (doctor_id, schedule_date),
    FOREIGN KEY (doctor_id) REFERENCES users(id) ON DELETE CASCADE,
    CHECK (slot_duration_minutes > 0),
    CHECK (priority_work_minutes >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Appointments (The Queue)
CREATE TABLE IF NOT EXISTS appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    schedule_id BIGINT NOT NULL,
    appointment_date DATE NOT NULL,
    queue_number INT NOT NULL, -- 1, 2, 3... (CRITICAL - This is the ordering)
    scheduled_time TIME NOT NULL, -- Calculated from queue position
    actual_start_time TIMESTAMP NULL,
    actual_end_time TIMESTAMP NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'WAITING',
    -- 'WAITING', 'IN_CONSULTATION', 'COMPLETED', 'NOT_ATTENDED', 'DELAYED', 'CANCELLED'
    is_priority BOOLEAN DEFAULT FALSE,
    notes TEXT, -- Attendant can add notes
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    UNIQUE KEY unique_schedule_queue (schedule_id, queue_number),
    FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (schedule_id) REFERENCES doctor_schedules(id) ON DELETE CASCADE,
    CHECK (queue_number > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Prescriptions
CREATE TABLE IF NOT EXISTS prescriptions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    appointment_id BIGINT NOT NULL,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    observations TEXT, -- Doctor's notes/diagnosis
    medicines TEXT NOT NULL, -- Prescribed medicines (JSON or text)
    tests TEXT, -- Lab tests recommended
    additional_notes TEXT, -- Follow-up instructions
    next_visit_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    printed_at TIMESTAMP NULL,
    printed_by BIGINT,
    print_count INT DEFAULT 0,
    FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE CASCADE,
    FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (printed_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Indexes for Performance
CREATE INDEX idx_doctor_schedules_doctor_date ON doctor_schedules(doctor_id, schedule_date);
CREATE INDEX idx_doctor_schedules_date ON doctor_schedules(schedule_date);
CREATE INDEX idx_appointments_patient ON appointments(patient_id);
CREATE INDEX idx_appointments_doctor ON appointments(doctor_id);
CREATE INDEX idx_appointments_schedule ON appointments(schedule_id);
CREATE INDEX idx_appointments_date ON appointments(appointment_date);
CREATE INDEX idx_appointments_status ON appointments(status);
CREATE INDEX idx_appointments_queue ON appointments(schedule_id, queue_number);
CREATE INDEX idx_prescriptions_patient ON prescriptions(patient_id);
CREATE INDEX idx_prescriptions_doctor ON prescriptions(doctor_id);
CREATE INDEX idx_prescriptions_appointment ON prescriptions(appointment_id);
