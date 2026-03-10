package com.ezc.aragenPR.webapp.persistance.dao;
import com.ezc.aragenPR.webapp.model.EzcSESHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface SESRepo extends JpaRepository<EzcSESHeader, String> {

    boolean existsBySesNumber(String sesNumber);
    List<EzcSESHeader> findByEntryDateAfterAndCreatedByOrderByEntryDateDesc(LocalDate entryDate,String createdBy);
    EzcSESHeader findBySesNumber(String sesNumber);
    @Modifying
    @Transactional
    @Query("UPDATE EzcSESHeader h SET h.status = 'APPROVED' WHERE h.sesNumber = :sesNumber")
    int updateStatusToApproved(@Param("sesNumber") String sesNumber);
    long countByEntryDateAfterAndStatusAndCreatedBy(LocalDate entryDate, String status, String createdBy);


}
