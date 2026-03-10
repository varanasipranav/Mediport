package com.ezc.aragenPR.webapp.persistance.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ezc.aragenPR.webapp.model.EzEnvironmentReport;

import java.util.List;
import java.util.Optional;

@Repository
public interface EzEnvironmentReportRepository extends JpaRepository<EzEnvironmentReport, Long> {
    List<EzEnvironmentReport> findByDocId(Long docId);
    void deleteByDocId(Long docId);
    List<EzEnvironmentReport> findByDocIdAndVendorIdAndReportMonth(Long docId, Long vendorId, String reportMonth);
    Optional<EzEnvironmentReport> findByDocIdAndVendorIdAndFieldIdAndReportMonth(
	    Long docId, Long vendorId, Long fieldId, String reportMonth
	);

}
