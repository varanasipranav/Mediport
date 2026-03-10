package com.ezc.aragenPR.webapp.persistance.dao;


import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ezc.aragenPR.webapp.model.EzPurchaseOrder;

public interface PurchaseOrderRepository extends JpaRepository<EzPurchaseOrder, String> {
//    @Query("SELECT po FROM EzPurchaseOrder po WHERE po.docDate BETWEEN :fromDate AND :toDate")
//    List<EzPurchaseOrder> findByUserIdAndDateRange(
//        @Param("fromDate") String fromDate,
//        @Param("toDate") String toDate
//    );
    
//	if(vendor == "CPTTEAM") {
//		List<EzPurchaseOrder> findAll();
//	}
//	else {
		List<EzPurchaseOrder> findByUserId(String vendor);
		
		
		List<EzPurchaseOrder> findByUserIdAndDocDateGreaterThanEqualAndDocDateLessThanEqual(String vendor,Date startDate,Date endDate);
		
		List<EzPurchaseOrder> findByDocDateGreaterThanEqualAndDocDateLessThanEqual(Date startDate,Date endDate);
//	}
}

// Created JPA repository which we will import in interface (service)
