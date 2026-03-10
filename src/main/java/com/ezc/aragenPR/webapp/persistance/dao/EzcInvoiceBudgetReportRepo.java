package com.ezc.aragenPR.webapp.persistance.dao;

import com.ezc.aragenPR.webapp.model.EzcInvoiceBudgetReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EzcInvoiceBudgetReportRepo extends JpaRepository<EzcInvoiceBudgetReport, Long> {
	
	List<EzcInvoiceBudgetReport> findByYear(Integer year);
    
    List<EzcInvoiceBudgetReport> findByMonthAndYear(Integer month, Integer year);

    List<EzcInvoiceBudgetReport> findByPlant(String plant);
    
    // Add other queries as needed
}
