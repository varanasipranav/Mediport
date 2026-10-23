-- Platform (master) database: one row per clinic, who can log in where, demo requests, platform operators.

CREATE TABLE tenants (
    site_id           VARCHAR(30)  NOT NULL PRIMARY KEY,
    schema_name       VARCHAR(64)  NOT NULL UNIQUE,
    display_name      VARCHAR(150) NOT NULL,
    status            VARCHAR(20)  NOT NULL,
    timezone          VARCHAR(60)  NOT NULL DEFAULT 'Asia/Kolkata',
    default_region    VARCHAR(5)   NOT NULL DEFAULT 'IN',
    rx_signing_key    VARCHAR(100) NOT NULL,
    tagline           VARCHAR(200) NULL,
    address           VARCHAR(300) NULL,
    phone             VARCHAR(40)  NULL,
    email             VARCHAR(150) NULL,
    registration_no   VARCHAR(80)  NULL,
    wa_phone_number_id VARCHAR(40) NULL UNIQUE,
    wa_access_token   VARCHAR(600) NULL,
    wa_app_secret     VARCHAR(100) NULL,
    schema_version    VARCHAR(20)  NULL,
    last_error        VARCHAR(500) NULL,
    created_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    provisioned_at    DATETIME     NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Which clinic a login belongs to. "admin@sunrise" picks the row directly; a bare "admin" works if only one clinic has it.
CREATE TABLE login_directory (
    username  VARCHAR(100) NOT NULL,
    site_id   VARCHAR(30)  NOT NULL,
    PRIMARY KEY (username, site_id),
    CONSTRAINT fk_login_site FOREIGN KEY (site_id) REFERENCES tenants (site_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE demo_requests (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    clinic_name    VARCHAR(150) NOT NULL,
    contact_name   VARCHAR(100) NOT NULL,
    email          VARCHAR(150) NOT NULL,
    phone          VARCHAR(40)  NOT NULL,
    city           VARCHAR(80)  NULL,
    doctors_count  VARCHAR(20)  NULL,
    requested_code VARCHAR(30)  NULL,
    message        VARCHAR(1000) NULL,
    status         VARCHAR(20)  NOT NULL DEFAULT 'NEW',
    site_id        VARCHAR(30)  NULL,
    ip             VARCHAR(60)  NULL,
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    decided_at     DATETIME     NULL,
    decided_by     VARCHAR(100) NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE platform_users (
    username      VARCHAR(80)  NOT NULL PRIMARY KEY,
    password_hash VARCHAR(100) NOT NULL,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
