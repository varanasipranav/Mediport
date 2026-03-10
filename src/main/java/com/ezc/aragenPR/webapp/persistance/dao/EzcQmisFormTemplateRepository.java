package com.ezc.aragenPR.webapp.persistance.dao;

import com.ezc.aragenPR.webapp.model.EzcQmisFormTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface EzcQmisFormTemplateRepository extends JpaRepository<EzcQmisFormTemplate, Integer> {
    List<EzcQmisFormTemplate> findByEqftType(String qmisType);
    List<EzcQmisFormTemplate> findByEqftMaterialTypeAndEqftType(String materialType, String type);
    List<EzcQmisFormTemplate> findByEqftMaterialTypeAndEqftTypeAndEqftQmisType(String materialType, String type, String qmisType);
    @Query("SELECT t.eqftQmisType FROM EzcQmisFormTemplate t WHERE t.eqftMaterialType = :materialType AND t.eqftVendor = :vendor")
    Set<String> findQmisTypeByMaterialType(@Param("materialType") String materialType, @Param("vendor") String vendor);

    List<EzcQmisFormTemplate> findByEqftMaterialType(String type1);
    List<EzcQmisFormTemplate> findByEqftMaterialTypeAndEqftVendor(String type1, String vendor);

    List<EzcQmisFormTemplate> findByEqftMaterialTypeAndEqftQmisType(String type1, String paramType);
}