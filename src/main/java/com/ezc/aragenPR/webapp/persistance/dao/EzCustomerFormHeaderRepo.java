package com.ezc.aragenPR.webapp.persistance.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ezc.aragenPR.webapp.dto.ListSelector;
import com.ezc.aragenPR.webapp.model.EzCustomerFormHeader;



public interface EzCustomerFormHeaderRepo  extends JpaRepository<EzCustomerFormHeader, Integer> { 
	
	// Base query (no USERROLE filter)
	@Query(nativeQuery = true, value = """
	    SELECT * FROM ezc_customer_form_header 
	    WHERE ECFH_CREATED_ON BETWEEN :fromDate AND DATE_ADD(:toDate, INTERVAL 1 DAY) 
	    AND (:status IS NULL OR :status = '' OR ECFH_STATUS = :status) 
	    AND ECFH_SUP_PLANT IN (:plantCodeList)
	    """)
	List<EzCustomerFormHeader> findDocumentWithoutRole(Date fromDate, Date toDate, String status, List<String> plantCodeList);

	// Extended query (with USERROLE filter)
	@Query(nativeQuery = true, value = """
	    SELECT * FROM ezc_customer_form_header 
	    WHERE ECFH_CREATED_ON BETWEEN :fromDate AND DATE_ADD(:toDate, INTERVAL 1 DAY) 
	    AND (:status IS NULL OR :status = '' OR ECFH_STATUS = :status) 
	    AND ECFH_SUP_PLANT IN (:plantCodeList)
	    AND ECFH_NEXT_PARTICIPANT = :userRole
	    """)
	List<EzCustomerFormHeader> findDocumentWithRole(Date fromDate, Date toDate, String status, List<String> plantCodeList, String userRole);

	

}
