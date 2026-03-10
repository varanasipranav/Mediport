package com.ezc.aragenPR.webapp.persistance.dao;

import com.ezc.aragenPR.webapp.model.EzPurchaseRequisitionHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface EzcPRHeaderRepository extends JpaRepository<EzPurchaseRequisitionHeader, String> {

    // Find header by request number (primary key)
    List<EzPurchaseRequisitionHeader> findByReqNumber(String reqNumber);

    // Fetch header along with items (avoids LazyInitializationException)
    @Query("SELECT h FROM EzPurchaseRequisitionHeader h LEFT JOIN FETCH h.items WHERE h.reqNumber = :reqNumber")
   List<EzPurchaseRequisitionHeader>  findByReqNumberWithItems(@Param("reqNumber") String reqNumber);

    // Filter headers by dates, status, and createdBy (vendor)
    @Query("SELECT h FROM EzPurchaseRequisitionHeader h WHERE "
            + "(:fromDate IS NULL OR h.createdOn >= :fromDate) "
            + "AND (:toDate IS NULL OR h.createdOn <= :toDate) "
            + "AND (:status IS NULL OR h.status = :status)"
            + "AND (:vendorId IS NULL OR h.createdBy = :vendorId)")
    List<EzPurchaseRequisitionHeader> findByVendorFilters(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("status") String status,
            @Param("vendorId") String vendorId
    );
    // Filter headers by dates, status, and createdBy (vendor)
    @Query("SELECT h FROM EzPurchaseRequisitionHeader h WHERE "
            + "(:fromDate IS NULL OR h.createdOn >= :fromDate) "
            + "AND (:toDate IS NULL OR h.createdOn <= :toDate) "
            + "AND (:status IS NULL OR h.status = :status)")

    List<EzPurchaseRequisitionHeader> findByVendorFilters(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("status") String status

    );
    // Count new PRs for a vendor
    @Query("SELECT COUNT(h) FROM EzPurchaseRequisitionHeader h WHERE h.status = 'NEW' AND h.createdBy = :vendorId")
    int countNewByVendorId(@Param("vendorId") String vendorId);

    // Fetch submitted or approved headers between dates
    @Query("SELECT h FROM EzPurchaseRequisitionHeader h WHERE (h.status = 'SUBMITTED' OR h.status = 'APPROVED') AND h.createdOn BETWEEN :fromDate AND :toDate")
    List<EzPurchaseRequisitionHeader> findSubmittedBetweenDates(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate
    );

    // Fetch headers created by specific vendors
    @Query("SELECT DISTINCT h.createdBy FROM EzPurchaseRequisitionHeader h")
    List<String> findDistinctVendors();

    // Optional: Fetch all headers along with items
    @Query("SELECT DISTINCT h FROM EzPurchaseRequisitionHeader h LEFT JOIN FETCH h.items")
    List<EzPurchaseRequisitionHeader> findAllWithItems();

    @Query("SELECT h FROM EzPurchaseRequisitionHeader h WHERE "
            + "(:fromDate IS NULL OR h.createdOn >= :fromDate) "
            + "AND (:toDate IS NULL OR h.createdOn <= :toDate) "
            + "AND (:createdBY IS NULL OR h.createdBy = :createdBY)")
    List<EzPurchaseRequisitionHeader> getCreatedByList(LocalDateTime fromDate, LocalDateTime toDate, String createdBY);

    @Transactional
    @Modifying
    @Query("UPDATE EzPurchaseRequisitionHeader h " +
            "SET h.status = :status, h.nextApprover = :nextApprover, h.modifiedOn = CURRENT_TIMESTAMP " +
            "WHERE h.reqNumber = :prNumber")
    int updateStatusAndApprover(
            @Param("prNumber") String prNumber,
            @Param("status") String status,
            @Param("nextApprover") String nextApprover
    );
}
