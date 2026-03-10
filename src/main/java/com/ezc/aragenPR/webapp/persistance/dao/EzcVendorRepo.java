package com.ezc.aragenPR.webapp.persistance.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ezc.aragenPR.webapp.model.EzcVendor;

public interface EzcVendorRepo extends JpaRepository<EzcVendor, Long> {
    // Add custom query methods here if needed
}
