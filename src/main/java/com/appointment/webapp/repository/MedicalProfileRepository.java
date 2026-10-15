package com.appointment.webapp.repository;

import com.appointment.webapp.model.MedicalProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalProfileRepository extends JpaRepository<MedicalProfile, Long> {
}
