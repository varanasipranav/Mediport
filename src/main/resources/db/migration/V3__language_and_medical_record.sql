-- Preferred WhatsApp language per patient (en, hi, te, ta ...) so reminders and alerts use it too
ALTER TABLE users ADD COLUMN preferred_language VARCHAR(5) NOT NULL DEFAULT 'en';

-- Long-lived medical facts about a patient (one row per patient)
CREATE TABLE medical_profiles (
    user_id            BIGINT      NOT NULL,
    blood_group        VARCHAR(5)  NULL,
    allergies          TEXT        NULL,
    chronic_conditions TEXT        NULL,
    notes              TEXT        NULL,
    updated_by         VARCHAR(50) NULL,
    updated_at         DATETIME(6) NULL,
    PRIMARY KEY (user_id),
    CONSTRAINT fk_medical_profile_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Measurements taken at a visit (one set per appointment)
CREATE TABLE visit_vitals (
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    appointment_id BIGINT       NOT NULL,
    bp_systolic    INT          NULL,
    bp_diastolic   INT          NULL,
    pulse          INT          NULL,
    temperature_c  DECIMAL(4,1) NULL,
    weight_kg      DECIMAL(5,1) NULL,
    height_cm      DECIMAL(5,1) NULL,
    spo2           INT          NULL,
    recorded_by    VARCHAR(50)  NULL,
    recorded_at    DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_vitals_appointment (appointment_id),
    CONSTRAINT fk_vitals_appointment FOREIGN KEY (appointment_id) REFERENCES appointments (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Who opened whose medical record, and when (privacy audit trail)
CREATE TABLE record_access_log (
    id          BIGINT      NOT NULL AUTO_INCREMENT,
    patient_id  BIGINT      NOT NULL,
    accessed_by VARCHAR(50) NOT NULL,
    action      VARCHAR(40) NOT NULL,
    accessed_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_access_patient (patient_id, accessed_at),
    CONSTRAINT fk_access_patient FOREIGN KEY (patient_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
