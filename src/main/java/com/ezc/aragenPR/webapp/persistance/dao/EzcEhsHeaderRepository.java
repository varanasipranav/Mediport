package com.ezc.aragenPR.webapp.persistance.dao;

import com.ezc.aragenPR.webapp.model.EzcEhsHeader;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface EzcEhsHeaderRepository extends JpaRepository<EzcEhsHeader, Integer> {

	 EzcEhsHeader findByDocId(Integer docId);
	 
	 @Query("SELECT h FROM EzcEhsHeader h WHERE "
	         + "(:fromDate IS NULL OR h.initiatedDate >= :fromDate) "
	         + "AND (:toDate IS NULL OR h.initiatedDate <= :toDate) "
	         + "AND (:status IS NULL OR :status = '' OR h.status = :status) "
	         + "AND (:vendorId IS NULL OR h.vendor = :vendorId)")
	    List<EzcEhsHeader> findByVendorFilters(
	        @Param("fromDate") Date fromDate,
	        @Param("toDate") Date toDate,
	        @Param("status") String status,
	        @Param("vendorId") String vendorId
	    );

	 @Query("SELECT COUNT(h) FROM EzcEhsHeader h WHERE h.status = 'NEW' AND h.vendor = :vendorId")
	 int countNewByVendorId(@Param("vendorId") String vendorId);

	
	@Query("SELECT h FROM EzcEhsHeader h WHERE (h.status = 'SUBMITTED' OR h.status = 'APPROVED') AND h.initiatedDate BETWEEN :fromDate AND :toDate")
	List<EzcEhsHeader> findSubmittedBetweenDates(@Param("fromDate") Date fromDate,
	                                             @Param("toDate") Date toDate);
	
	@Query("SELECT h FROM EzcEhsHeader h WHERE h.monthYear IN :monthYears AND h.vendor = :vendor")
	List<EzcEhsHeader> findByMonthYearInAndVendor(@Param("monthYears") List<String> monthYears,
	                                              @Param("vendor") String vendor);

	@Query("SELECT DISTINCT h.vendor FROM EzcEhsHeader h")
	List<String> findDistinctVendors();
	
	@Query("SELECT h FROM EzcEhsHeader h WHERE h.monthYear IN :monthYears")
	List<EzcEhsHeader> findByMonthYearIn(@Param("monthYears") List<String> monthYears);


}
