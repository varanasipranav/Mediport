package com.appointment.webapp.repository;

import com.appointment.webapp.model.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {

    boolean existsByAppointmentIdAndKind(Long appointmentId, String kind);

    @Query("SELECT n.kind FROM NotificationLog n WHERE n.appointment.id = :appointmentId")
    List<String> findKindsByAppointmentId(@Param("appointmentId") Long appointmentId);
}
