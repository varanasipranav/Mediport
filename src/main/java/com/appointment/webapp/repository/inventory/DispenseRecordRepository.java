package com.appointment.webapp.repository.inventory;

import com.appointment.webapp.model.inventory.DispenseRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DispenseRecordRepository extends JpaRepository<DispenseRecord, Long> {

    Optional<DispenseRecord> findByPrescriptionId(Long prescriptionId);

    @Query("SELECT d FROM DispenseRecord d JOIN FETCH d.prescription p JOIN FETCH p.patient ORDER BY d.dispensedAt DESC LIMIT 20")
    List<DispenseRecord> findRecent();
}
