package com.appointment.webapp.repository;

import com.appointment.webapp.model.RecordAccessLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordAccessLogRepository extends JpaRepository<RecordAccessLog, Long> {
}
