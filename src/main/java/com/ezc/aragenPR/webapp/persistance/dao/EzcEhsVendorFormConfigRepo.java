package com.ezc.aragenPR.webapp.persistance.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ezc.aragenPR.webapp.model.EzcEhsVendorFormConfig;

@Repository
public interface EzcEhsVendorFormConfigRepo extends JpaRepository<EzcEhsVendorFormConfig, Long> {
	
//	Optional<EzcEhsVendorFormConfig> findByEvfmVendorId(String evfmVendorId);
	List<EzcEhsVendorFormConfig> findAllByEvfmVendorId(String vendorId);

	
}
