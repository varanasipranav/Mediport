package com.ezc.aragenPR.webapp.persistance.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ezc.aragenPR.webapp.model.EzcEhsFormFieldMaster;

@Repository
public interface EzcEhsFormFieldMasterRepository extends JpaRepository<EzcEhsFormFieldMaster, Long> {
    // Add custom query methods if needed later
}
