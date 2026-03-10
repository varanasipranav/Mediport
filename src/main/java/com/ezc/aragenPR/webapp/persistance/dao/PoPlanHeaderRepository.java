package com.ezc.aragenPR.webapp.persistance.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ezc.aragenPR.webapp.model.EzcPoPlanHeader;
import java.util.List;
import java.util.Date;

public interface PoPlanHeaderRepository extends JpaRepository<EzcPoPlanHeader, String> {

	
	@Query(nativeQuery = true,value="SELECT A.EPPH_DOC_ID,A.EPPH_MONTH_YR,A.EPPH_SUPPLIER,B.EPWP_WEEK,B.EPWP_STATUS,A.EPPH_CREATED_ON,A.EPPH_OUT_LOC FROM EZC_PO_PLAN_HEADER A, EZC_PO_WEEK_PLAN B WHERE A.EPPH_DOC_ID=B.EPWP_DOC_ID AND A.EPPH_STATUS='APPROVED' AND (A.EPPH_CREATED_ON >= :fromDate AND A.EPPH_CREATED_ON <= :toDate)")
	List<Object[]> getWeeklyPlanListDate(Date fromDate,Date toDate);
	
	@Query(nativeQuery = true,value="SELECT A.EPPH_DOC_ID,A.EPPH_MONTH_YR,A.EPPH_OUT_LOC FROM EZC_PO_PLAN_HEADER A WHERE A.EPPH_PLAN_ID=:planId")
	List<Object[]> getOtherMonthlyPlanId(String planId);
	
	@Query(
			  nativeQuery = true,
			  value = "SELECT A.EPPH_DOC_ID, A.EPPH_MONTH_YR, A.EPPH_SUPPLIER, B.EPWP_WEEK, B.EPWP_STATUS, A.EPPH_CREATED_ON, A.EPPH_OUT_LOC " +
			          "FROM EZC_PO_PLAN_HEADER A, EZC_PO_WEEK_PLAN B " +
			          "WHERE A.EPPH_DOC_ID = B.EPWP_DOC_ID " +
			          "AND A.EPPH_STATUS = 'APPROVED' " +
			          "AND A.EPPH_SUPPLIER = :supplier " +
			          "AND (A.EPPH_CREATED_ON >= :fromDate AND A.EPPH_CREATED_ON <= :toDate)"
			)
			List<Object[]> getWeeklyPlanListBySupplierAndDate(
			    @Param("supplier") String supplier,
			    @Param("fromDate") Date fromDate,
			    @Param("toDate") Date toDate
			);

	@Query("SELECT e FROM EzcPoPlanHeader e WHERE e.status = 'SUBMITTED' AND e.nextPart = :role AND e.createdOn BETWEEN :fromDate AND :toDate")
	List<EzcPoPlanHeader> findSubmittedForRoleVend(@Param("role") String role,
												   @Param("fromDate") Date fromDate,
												   @Param("toDate") Date toDate);

	@Query("SELECT p FROM EzcPoPlanHeader p " +
			"WHERE p.nextPart = :role " +
			"AND p.supplier = :userId " +
			"AND p.createdOn BETWEEN :fromDate AND :toDate " +
			"AND p.status = 'SUBMITTED'")
	List<EzcPoPlanHeader> findSubmittedPlansByRoleAndUserIdAndDateRange(
			@Param("role") String role,
			@Param("userId") String userId,
			@Param("fromDate") Date fromDate,
			@Param("toDate") Date toDate
	);
}
