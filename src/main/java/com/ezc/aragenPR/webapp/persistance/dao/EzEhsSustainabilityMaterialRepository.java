package com.ezc.aragenPR.webapp.persistance.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ezc.aragenPR.webapp.model.EzEhsSustainabilityMaterial;

@Repository
public interface EzEhsSustainabilityMaterialRepository extends JpaRepository<EzEhsSustainabilityMaterial, String> {
    EzEhsSustainabilityMaterial findByKey(String key);
    List<EzEhsSustainabilityMaterial> findByStatus(EzEhsSustainabilityMaterial.Status status);
}
