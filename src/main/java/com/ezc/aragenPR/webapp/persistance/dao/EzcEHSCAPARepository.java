package com.ezc.aragenPR.webapp.persistance.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ezc.aragenPR.webapp.model.EzcEHSCAPA;

@Repository
public interface EzcEHSCAPARepository extends JpaRepository<EzcEHSCAPA, Long> {
	Optional<EzcEHSCAPA> findByIncidentId(Long incidentId);

}
