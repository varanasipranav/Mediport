package com.ezc.aragenPR.webapp.persistance.dao;

import com.ezc.aragenPR.webapp.model.EzEhsSafetyMaterial;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EzEhsSafetyMaterialRepository extends JpaRepository<EzEhsSafetyMaterial, Integer> {
	//List<EzEhsSafetyMaterial> findByStatus(EzEhsSafetyMaterial.Status status);
	List<EzEhsSafetyMaterial> findByStatus(EzEhsSafetyMaterial.Status status);

	EzEhsSafetyMaterial findByKey(String key);

}
