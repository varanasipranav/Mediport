-- Baseline: schema as of the first Flyway release. Existing databases skip this (baseline-version=1).
SET FOREIGN_KEY_CHECKS=0;
CREATE TABLE `appointments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `actual_end_time` datetime(6) DEFAULT NULL,
  `actual_start_time` datetime(6) DEFAULT NULL,
  `appointment_date` date NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `is_priority` bit(1) DEFAULT NULL,
  `notes` text,
  `queue_number` int NOT NULL,
  `scheduled_time` time NOT NULL,
  `status` varchar(50) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `doctor_id` bigint NOT NULL,
  `patient_id` bigint NOT NULL,
  `schedule_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKkxxmppaju5lo1xvcgpyqha1e3` (`schedule_id`,`queue_number`),
  KEY `FK6u6s6egu60m2cbdjno44jbipa` (`doctor_id`),
  KEY `FKopb2h9yhin1rb4dqote8bws6w` (`patient_id`),
  CONSTRAINT `FK6u6s6egu60m2cbdjno44jbipa` FOREIGN KEY (`doctor_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKa414dbdx50axobch6e5am3dmu` FOREIGN KEY (`schedule_id`) REFERENCES `doctor_schedules` (`id`),
  CONSTRAINT `FKopb2h9yhin1rb4dqote8bws6w` FOREIGN KEY (`patient_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `doctor_schedules` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `end_time` time NOT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `max_patients` int DEFAULT NULL,
  `priority_work_minutes` int NOT NULL,
  `schedule_date` date NOT NULL,
  `shift_type` enum('CUSTOM','EVENING','FULL_DAY','MORNING') DEFAULT NULL,
  `slot_duration_minutes` int NOT NULL,
  `start_time` time NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `doctor_id` bigint NOT NULL,
  `is_override` bit(1) DEFAULT NULL,
  `is_template_generated` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKtnqcy8socii10e41bhldd2pvk` (`doctor_id`,`schedule_date`),
  CONSTRAINT `FK4abj2md08eyas4y9h4rjtucka` FOREIGN KEY (`doctor_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `doctor_weekly_templates` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `day_of_week` int NOT NULL,
  `end_time` time NOT NULL,
  `is_active` bit(1) NOT NULL,
  `max_patients` int DEFAULT NULL,
  `shift_type` enum('CUSTOM','EVENING','FULL_DAY','MORNING') DEFAULT NULL,
  `slot_duration_minutes` int NOT NULL,
  `start_time` time NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `doctor_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKk2ketdmprbox8m5g0l7k8jc9m` (`doctor_id`,`day_of_week`),
  CONSTRAINT `FK30rw78ruvdwek03g9h977wk32` FOREIGN KEY (`doctor_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `prescriptions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `additional_notes` text,
  `created_at` datetime(6) DEFAULT NULL,
  `medicines` text NOT NULL,
  `next_visit_date` date DEFAULT NULL,
  `observations` text,
  `print_count` int DEFAULT NULL,
  `printed_at` datetime(6) DEFAULT NULL,
  `tests` text,
  `updated_at` datetime(6) DEFAULT NULL,
  `appointment_id` bigint NOT NULL,
  `doctor_id` bigint NOT NULL,
  `patient_id` bigint NOT NULL,
  `printed_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKe2fpvlkkcgcd40k4ufyyju2al` (`appointment_id`),
  KEY `FK2hdpvkpjjx3plf21194oxjskt` (`doctor_id`),
  KEY `FK7sia9wnwh9j5hwrta9k8q0rbq` (`patient_id`),
  KEY `FKdwqsi1b654i5seltp8yjjv95c` (`printed_by`),
  CONSTRAINT `FK2hdpvkpjjx3plf21194oxjskt` FOREIGN KEY (`doctor_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FK7sia9wnwh9j5hwrta9k8q0rbq` FOREIGN KEY (`patient_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKdwqsi1b654i5seltp8yjjv95c` FOREIGN KEY (`printed_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FKe2fpvlkkcgcd40k4ufyyju2al` FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `privileges` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(200) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKm2tnonbcaquofx1ccy060ejyc` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `role_privileges` (
  `role_id` bigint NOT NULL,
  `privilege_id` bigint NOT NULL,
  PRIMARY KEY (`role_id`,`privilege_id`),
  KEY `FKbosd0iqvr1efg44c01jcuvdxt` (`privilege_id`),
  CONSTRAINT `FKbosd0iqvr1efg44c01jcuvdxt` FOREIGN KEY (`privilege_id`) REFERENCES `privileges` (`id`),
  CONSTRAINT `FKddceebgln19s0c4tjwj01v1ou` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `roles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(200) DEFAULT NULL,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKofx66keruapi6vyqpv6f2or37` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `user_privileges` (
  `user_id` bigint NOT NULL,
  `privilege_id` bigint NOT NULL,
  PRIMARY KEY (`user_id`,`privilege_id`),
  KEY `FK8ht5860cyq8fsdbceqwnq517e` (`privilege_id`),
  CONSTRAINT `FK4pjq82mghfm4ec8innfnfetrm` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FK8ht5860cyq8fsdbceqwnq517e` FOREIGN KEY (`privilege_id`) REFERENCES `privileges` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `user_roles` (
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  KEY `FKh8ciramu9cc9q3qcqiv4ue8a6` (`role_id`),
  KEY `FKhfh9dx7w3ubf1co1vdev94g3f` (`user_id`),
  CONSTRAINT `FKh8ciramu9cc9q3qcqiv4ue8a6` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`),
  CONSTRAINT `FKhfh9dx7w3ubf1co1vdev94g3f` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `account_non_locked` bit(1) NOT NULL,
  `address` varchar(500) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `date_of_birth` date DEFAULT NULL,
  `department` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `employee_id` varchar(50) DEFAULT NULL,
  `is_enabled` bit(1) NOT NULL,
  `first_name` varchar(100) NOT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `last_name` varchar(100) NOT NULL,
  `license_number` varchar(50) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `patient_id` varchar(50) DEFAULT NULL,
  `phone_number` varchar(20) NOT NULL,
  `specialization` varchar(100) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `updated_date` datetime(6) DEFAULT NULL,
  `username` varchar(50) NOT NULL,
  `age` int DEFAULT NULL,
  `whatsapp_phone` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `whatsapp_conversations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `context_data` json DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `current_state` enum('APPOINTMENT_BOOKED','APPOINTMENT_CANCELLED','APPOINTMENT_RESCHEDULED','ASKING_NEW_OR_EXISTING','CHECKING_PATIENT','COLLECTING_PATIENT_AGE','COLLECTING_PATIENT_DOB','COLLECTING_PATIENT_GENDER','COLLECTING_PATIENT_NAME','CONFIRMING_APPOINTMENT','CONFIRMING_CANCELLATION','CONFIRMING_RESCHEDULE','ENTERING_CUSTOM_DATE','ERROR_STATE','HELP_MODE','INITIAL','MAIN_MENU','RESCHEDULE_SELECTING_DATE','RESCHEDULE_SELECTING_DOCTOR','RESCHEDULE_SELECTING_TIME','SELECTING_APPOINTMENT_TO_CANCEL','SELECTING_APPOINTMENT_TO_RESCHEDULE','SELECTING_DATE','SELECTING_DOCTOR','SELECTING_EXISTING_PATIENT','SELECTING_TIME_SLOT','SESSION_EXPIRED','VIEWING_APPOINTMENTS') NOT NULL,
  `last_message_at` datetime(6) DEFAULT NULL,
  `preferred_language` varchar(10) DEFAULT NULL,
  `previous_state` enum('APPOINTMENT_BOOKED','APPOINTMENT_CANCELLED','APPOINTMENT_RESCHEDULED','ASKING_NEW_OR_EXISTING','CHECKING_PATIENT','COLLECTING_PATIENT_AGE','COLLECTING_PATIENT_DOB','COLLECTING_PATIENT_GENDER','COLLECTING_PATIENT_NAME','CONFIRMING_APPOINTMENT','CONFIRMING_CANCELLATION','CONFIRMING_RESCHEDULE','ENTERING_CUSTOM_DATE','ERROR_STATE','HELP_MODE','INITIAL','MAIN_MENU','RESCHEDULE_SELECTING_DATE','RESCHEDULE_SELECTING_DOCTOR','RESCHEDULE_SELECTING_TIME','SELECTING_APPOINTMENT_TO_CANCEL','SELECTING_APPOINTMENT_TO_RESCHEDULE','SELECTING_DATE','SELECTING_DOCTOR','SELECTING_EXISTING_PATIENT','SELECTING_TIME_SLOT','SESSION_EXPIRED','VIEWING_APPOINTMENTS') DEFAULT NULL,
  `sender_name` varchar(100) DEFAULT NULL,
  `session_expires_at` datetime(6) DEFAULT NULL,
  `session_started_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `whatsapp_phone` varchar(20) NOT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKdycv658k0yxf34a6kyb6who67` (`whatsapp_phone`),
  KEY `FKmh4379n2ykhmuh52cf9jawliu` (`user_id`),
  CONSTRAINT `FKmh4379n2ykhmuh52cf9jawliu` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `whatsapp_messages` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `conversation_id` bigint NOT NULL,
  `message_id` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'WhatsApp message ID (wamid.xxx)',
  `direction` enum('INCOMING','OUTGOING') COLLATE utf8mb4_unicode_ci NOT NULL,
  `message_type` enum('TEXT','BUTTON_REPLY','LIST_REPLY','INTERACTIVE','TEMPLATE') COLLATE utf8mb4_unicode_ci NOT NULL,
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Message content',
  `status` enum('PENDING','SENT','DELIVERED','READ','FAILED') COLLATE utf8mb4_unicode_ci DEFAULT 'PENDING',
  `error_message` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sent_at` timestamp NULL DEFAULT NULL COMMENT 'When sent to WhatsApp API',
  `delivered_at` timestamp NULL DEFAULT NULL,
  `read_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_conversation_id` (`conversation_id`),
  KEY `idx_message_id` (`message_id`),
  KEY `idx_direction` (`direction`),
  KEY `idx_created_at` (`created_at`),
  CONSTRAINT `whatsapp_messages_ibfk_1` FOREIGN KEY (`conversation_id`) REFERENCES `whatsapp_conversations` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
SET FOREIGN_KEY_CHECKS=1;
