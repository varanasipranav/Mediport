package com.ezc.aragenPR.webapp.persistance.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ezc.aragenPR.webapp.model.EzConditionValueMap;
import com.ezc.aragenPR.webapp.model.EzConditionValueMapKey;

public interface ConditionValueMapRepo extends JpaRepository<EzConditionValueMap, EzConditionValueMapKey>{

	@Query(
            value = "SELECT ECVM_CONDITION_TYPE,ECVM_BRAND,ECVM_DIST_CATEGORY,ECVM_VALUE FROM EZC_CONDITION_VALUE_MAP",
            nativeQuery = true
    )
	List<Object[]> getAllMaterialDiscounts();
}
