package com.ezc.aragenPR.webapp.persistance.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ezc.aragenPR.webapp.model.EzEhsEnvironmentMaterial;

@Repository
public interface EzEhsEnvironmentMaterialRepository extends JpaRepository<EzEhsEnvironmentMaterial, Integer> {
    
    // Find materials by status
    List<EzEhsEnvironmentMaterial> findByStatus(EzEhsEnvironmentMaterial.Status status);

    // Find material by key
    EzEhsEnvironmentMaterial findByKey(String key);
}
