package com.ezc.aragenPR.webapp.persistance.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ezc.aragenPR.webapp.model.EzEHSIncident;

@Repository
public interface EzEhsInventoryRepository extends JpaRepository<EzEHSIncident, Long> {

//	 @Query("SELECT e FROM EzEHSIncident e WHERE "
//	         + "(:fromDate IS NULL OR e.reportingDate >= :fromDate) "
//	         + "AND (:toDate IS NULL OR e.reportingDate <= :toDate) "
//	         + "AND (:status IS NULL OR :status = '' OR e.status = :status) "
//	         + "AND (e.status = 'Submitted' OR e.status = 'Approved')")
//	    List<EzEHSIncident> findByFilters(
//	        @Param("fromDate") Date fromDate,
//	        @Param("toDate") Date toDate,
//	        @Param("status") String status
//	    );
//	 @Query("SELECT e FROM EzEHSIncident e WHERE "
//	         + "(:fromDate IS NULL OR e.reportingDate >= :fromDate) "
//	         + "AND (:toDate IS NULL OR e.reportingDate <= :toDate) "
//	         + "AND (:status IS NULL OR :status = '' OR e.status = :status) "
//	         + "AND (:vendorId IS NULL OR e.vendor = :vendorId)") // ✅ Add this line
//	    List<EzEHSIncident> findByVendorFilters(
//	        @Param("fromDate") Date fromDate,
//	        @Param("toDate") Date toDate,
//	        @Param("status") String status,
//	        @Param("vendorId") String vendorId
//	    );
	
	@Query("SELECT e FROM EzEHSIncident e WHERE "
		     + "(:fromDate IS NULL OR e.reportingDate >= :fromDate) "
		     + "AND (:toDate IS NULL OR e.reportingDate <= :toDate) "
		     + "AND (:status IS NULL OR :status = '' OR e.status = :status) "
		     + "AND (:typeOfObservation IS NULL OR :typeOfObservation = '' OR e.typeOfObservation = :typeOfObservation) "
		     + "AND (e.status = 'Submitted' OR e.status = 'Approved')")
		List<EzEHSIncident> findByFilters(
		    @Param("fromDate") Date fromDate,
		    @Param("toDate") Date toDate,
		    @Param("status") String status,
		    @Param("typeOfObservation") String typeOfObservation
		);

	@Query("SELECT e FROM EzEHSIncident e WHERE "
		     + "(:fromDate IS NULL OR e.reportingDate >= :fromDate) "
		     + "AND (:toDate IS NULL OR e.reportingDate <= :toDate) "
		     + "AND (:status IS NULL OR :status = '' OR e.status = :status) "
		     + "AND (:vendorId IS NULL OR e.vendor = :vendorId) "
		     + "AND (:typeOfObservation IS NULL OR :typeOfObservation = '' OR e.typeOfObservation = :typeOfObservation)")
		List<EzEHSIncident> findByVendorFilters(
		    @Param("fromDate") Date fromDate,
		    @Param("toDate") Date toDate,
		    @Param("status") String status,
		    @Param("vendorId") String vendorId,
		    @Param("typeOfObservation") String typeOfObservation
		);


	
}
