package com.ezc.aragenPR.webapp.persistance.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ezc.aragenPR.webapp.model.EzEnvironmentReport;
import com.ezc.aragenPR.webapp.model.EzcEhsVendorFormConfig;

import java.util.List;
import java.util.Optional;

@Repository
public interface EzcEhsVendorFormConfigRepository extends JpaRepository<EzcEhsVendorFormConfig, Long> {
	List<EzcEhsVendorFormConfig> findByEvfmVendorId(String vendorId);
    
    @Query("SELECT e FROM EzcEhsVendorFormConfig e " +
            "JOIN FETCH e.fields f " +
            "WHERE e.evfmVendorId = :vendorId")
    List<EzcEhsVendorFormConfig> findByEvfmVendorIdWithFields(String vendorId);
    
  


}
