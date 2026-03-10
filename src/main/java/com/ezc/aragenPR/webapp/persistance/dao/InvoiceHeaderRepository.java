package com.ezc.aragenPR.webapp.persistance.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ezc.aragenPR.webapp.model.EzInvoiceHeader;
import com.ezc.aragenPR.webapp.model.EzPurchaseOrder;

public interface InvoiceHeaderRepository extends JpaRepository<EzInvoiceHeader, Long> {

	List<EzInvoiceHeader> findByUserIdAndInvoiceDateGreaterThanEqualAndInvoiceDateLessThanEqual(String vendor,Date startDate,Date endDate);
	
	List<EzInvoiceHeader> findByInvoiceDateGreaterThanEqualAndInvoiceDateLessThanEqual(Date startDate,Date endDate);
	
    @Query("SELECT e.invoiceNo FROM EzInvoiceHeader e")
    List<String> findAllInvoiceNos();

}
