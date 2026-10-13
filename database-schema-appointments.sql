-- ==========================================
-- Hospital Appointment & Prescription System
-- Core Schema - Phase 1
-- ==========================================

-- Doctor Schedules (Source of Truth for Availability)
CREATE TABLE doctor_schedules (
    id BIGSERIAL PRIMARY KEY,
    doctor_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    schedule_date DATE NOT NULL,
    shift_type VARCHAR(20), -- 'MORNING', 'EVENING', 'CUSTOM'
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    slot_duration_minutes INT NOT NULL DEFAULT 15,
    max_patients INT, -- NULL = auto-calculate from time slots
    priority_work_minutes INT NOT NULL DEFAULT 0, -- Emergency/admin work delays
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    UNIQUE(doctor_id, schedule_date),
    CHECK (start_time < end_time),
    CHECK (slot_duration_minutes > 0),
    CHECK (priority_work_minutes >= 0)
);

-- Appointments (The Queue)
CREATE TABLE appointments (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    doctor_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    schedule_id BIGINT NOT NULL REFERENCES doctor_schedules(id) ON DELETE CASCADE,
    appointment_date DATE NOT NULL,
    queue_number INT NOT NULL, -- 1, 2, 3... (CRITICAL - This is the ordering)
    scheduled_time TIME NOT NULL, -- Calculated from queue position
    actual_start_time TIMESTAMP,
    actual_end_time TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'WAITING',
    -- 'WAITING', 'IN_CONSULTATION', 'COMPLETED', 'NOT_ATTENDED', 'DELAYED', 'CANCELLED'
    is_priority BOOLEAN DEFAULT FALSE,
    notes TEXT, -- Attendant can add notes
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    UNIQUE(schedule_id, queue_number),
    CHECK (queue_number > 0)
);

-- Prescriptions
CREATE TABLE prescriptions (
    id BIGSERIAL PRIMARY KEY,
    appointment_id BIGINT NOT NULL REFERENCES appointments(id) ON DELETE CASCADE,
    patient_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    doctor_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    observations TEXT, -- Doctor's notes/diagnosis
    medicines TEXT NOT NULL, -- Prescribed medicines (JSON or text)
    tests TEXT, -- Lab tests recommended
    additional_notes TEXT, -- Follow-up instructions
    next_visit_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    printed_at TIMESTAMP,
    printed_by BIGINT REFERENCES users(id),
    print_count INT DEFAULT 0
);

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

-- Triggers for updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_doctor_schedules_updated_at BEFORE UPDATE ON doctor_schedules
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_appointments_updated_at BEFORE UPDATE ON appointments
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_prescriptions_updated_at BEFORE UPDATE ON prescriptions
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
