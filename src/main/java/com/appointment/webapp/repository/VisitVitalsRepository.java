package com.appointment.webapp.repository;

import com.appointment.webapp.model.VisitVitals;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VisitVitalsRepository extends JpaRepository<VisitVitals, Long> {

    Optional<VisitVitals> findByAppointmentId(Long appointmentId);

    @Query("SELECT v FROM VisitVitals v WHERE v.appointment.patient.id = :patientId ORDER BY v.recordedAt DESC")
    List<VisitVitals> findForPatient(@Param("patientId") Long patientId);
}
