package com.ezc.aragenPR.webapp.persistance.dao;

import com.ezc.aragenPR.webapp.model.EzQmisActualData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface QmisActualDataRepository extends JpaRepository<EzQmisActualData, Long> {

    EzQmisActualData findByEqadType1AndEqadType2AndEqadType3AndEqadMonthAndEqadYearAndEqadVendor(String eqadType1, String eqadType2, String eqadType3, int currentMonth, int currentYear, String eqadVendor);
    List<EzQmisActualData> findByEqadType1AndEqadType2AndEqadType3OrderByEqadMonthAsc(String eqadType1, String eqadType2, String eqadType3);


    List<EzQmisActualData> findByEqadType1AndEqadType2AndEqadType3(String type1, String type2, String type3);


    Set<String> findEqadType3ByEqadType1AndEqadType2(String type1, String type2);

    @Query("SELECT DISTINCT e.eqadType3 FROM EzQmisActualData e WHERE e.eqadType1 = :eqadType1 AND e.eqadType2 = :eqadType2")
    Set<String> findDistinctEqadType3ByEqadType1AndEqadType2(@Param("eqadType1") String eqadType1, @Param("eqadType2") String eqadType2);
    @Query("SELECT DISTINCT e.eqadType3 FROM EzQmisActualData e WHERE e.eqadType1 = :eqadType1")
    Set<String> findDistinctEqadType3ByEqadType1(String eqadType1);

    List<EzQmisActualData> findByEqadType1AndEqadType3AndEqadVendor(String eqadType1, String eqadType3, String eqadVendor);
}