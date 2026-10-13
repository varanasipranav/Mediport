-- WhatsApp queue notifications: one row per (appointment, kind) so each alert is sent exactly once, even across restarts
CREATE TABLE notification_log (
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    appointment_id BIGINT       NOT NULL,
    kind           VARCHAR(40)  NOT NULL,
    sent_at        DATETIME(6)  NOT NULL,
    detail         VARCHAR(255) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_notification (appointment_id, kind),
    CONSTRAINT fk_notification_appointment FOREIGN KEY (appointment_id) REFERENCES appointments (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Pharmacy / clinic inventory
CREATE TABLE medicines (
    id            BIGINT        NOT NULL AUTO_INCREMENT,
    name          VARCHAR(150)  NOT NULL,
    generic_name  VARCHAR(150)  NULL,
    strength      VARCHAR(50)   NOT NULL DEFAULT '',
    form          VARCHAR(30)   NOT NULL DEFAULT 'Tablet',
    unit          VARCHAR(20)   NOT NULL DEFAULT 'unit',
    reorder_level INT           NOT NULL DEFAULT 10,
    default_price DECIMAL(10,2) NULL,
    active        BIT(1)        NOT NULL DEFAULT b'1',
    created_at    DATETIME(6)   NULL,
    updated_at    DATETIME(6)   NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_medicine (name, strength, form)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE stock_batches (
    id                 BIGINT        NOT NULL AUTO_INCREMENT,
    medicine_id        BIGINT        NOT NULL,
    batch_no           VARCHAR(60)   NOT NULL,
    expiry_date        DATE          NOT NULL,
    quantity_received  INT           NOT NULL,
    quantity_available INT           NOT NULL,
    unit_cost          DECIMAL(10,2) NULL,
    unit_price         DECIMAL(10,2) NULL,
    supplier           VARCHAR(120)  NULL,
    received_at        DATETIME(6)   NOT NULL,
    received_by        VARCHAR(50)   NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_batch (medicine_id, batch_no),
    KEY idx_batch_expiry (expiry_date),
    CONSTRAINT fk_batch_medicine FOREIGN KEY (medicine_id) REFERENCES medicines (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE stock_movements (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    medicine_id     BIGINT       NOT NULL,
    batch_id        BIGINT       NULL,
    movement_type   VARCHAR(20)  NOT NULL,
    quantity        INT          NOT NULL,
    prescription_id BIGINT       NULL,
    note            VARCHAR(255) NULL,
    created_by      VARCHAR(50)  NULL,
    created_at      DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    KEY idx_movement_medicine (medicine_id, created_at),
    CONSTRAINT fk_movement_medicine FOREIGN KEY (medicine_id) REFERENCES medicines (id),
    CONSTRAINT fk_movement_batch FOREIGN KEY (batch_id) REFERENCES stock_batches (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE dispense_records (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    prescription_id BIGINT       NOT NULL,
    status          VARCHAR(20)  NOT NULL,
    dispensed_by    VARCHAR(50)  NULL,
    dispensed_at    DATETIME(6)  NOT NULL,
    notes           VARCHAR(500) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_dispense_prescription (prescription_id),
    CONSTRAINT fk_dispense_prescription FOREIGN KEY (prescription_id) REFERENCES prescriptions (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
