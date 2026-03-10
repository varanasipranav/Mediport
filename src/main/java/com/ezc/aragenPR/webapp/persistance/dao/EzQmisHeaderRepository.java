package com.ezc.aragenPR.webapp.persistance.dao;

import com.ezc.aragenPR.webapp.model.EqhDocStatus;
import com.ezc.aragenPR.webapp.model.EzQmisHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface EzQmisHeaderRepository extends JpaRepository<EzQmisHeader, Integer> {
    @Query("SELECT h FROM EzQmisHeader h WHERE h.eqhCurrentParticipant = :roleOsQaCeat AND  (:status IS NULL OR h.eqhStatus = :status)")
    List<EzQmisHeader> findByEqhCurrentParticipant(String roleOsQaCeat, @Param("status") EqhDocStatus status);

    @Query("SELECT h FROM EzQmisHeader h " +
            "WHERE h.eqhCurrentParticipant = :roleOsQaCeat " +
            "AND h.eqhPlant IN :plants " +
            "AND (:status IS NULL OR h.eqhQaStatus = :status)")
    List<EzQmisHeader> findByEqhCurrentParticipantAndEqhPlantInAndEqhStatus(@Param("roleOsQaCeat") String roleOsQaCeat,
                                                                            @Param("plants") List<String> plants, @Param("status") EqhDocStatus status);
    @Query("SELECT h FROM EzQmisHeader h WHERE h.eqhCreatedOn BETWEEN :fromDate AND :toDate " +
            "AND h.eqhVendor = :vendor " +
            "AND (:status IS NULL OR h.eqhVendorStatus = :status)")
    List<EzQmisHeader> findByDateVendorAndOptionalStatus(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("vendor") String vendor,
            @Param("status") EqhDocStatus status);
}
