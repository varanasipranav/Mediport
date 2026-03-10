/*
package com.ezc.aragenPR.webapp.persistance.dao;


import com.ezc.aragenPR.webapp.model.EzQmisActualData;
import com.ezc.aragenPR.webapp.model.EzQmisForecastData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QmisForecastDataRepository extends JpaRepository<EzQmisForecastData, Long> {
    List<EzQmisForecastData> findByEqfdType3OrderByEqfdMonthAsc(String eqfdType3);
    List<EzQmisForecastData> findByEqfdType1AndEqfdType2AndEqfdType3OrderByEqfdMonthAsc(String eqfdType1, String eqfdType2, String eqfdType3);


    List<EzQmisForecastData> findByEqfdType1AndEqfdType2AndEqfdType3(String type1, String type2, String type3);

    List<EzQmisForecastData> findByEqfdType1AndEqfdType3(String type1, String type3);
    List<EzQmisForecastData> findByEqfdType1AndEqfdType3AndEqfdVendor(String type1, String type3, String vendor);

    Optional<EzQmisForecastData> findByEqfdYearAndEqfdMonthAndEqfdType1AndEqfdType3AndEqfdVendor(int year, int month, String type, String s, String vendor);
}*/
