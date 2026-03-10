package com.ezc.aragenPR.webapp.persistance.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ezc.aragenPR.webapp.model.EzMaterialSalesOrgData;
import com.ezc.aragenPR.webapp.model.EzMaterialSalesOrgDataKey;

public interface MaterialSalesOrgDataRepo extends JpaRepository<EzMaterialSalesOrgData,EzMaterialSalesOrgDataKey> {
	@Modifying
    @Query(
            value = "truncate table EZC_MATERIAL_SALES_DATA",
            nativeQuery = true
    )
    void truncateTable();

	@Query(
            value = "SELECT DISTINCT A.EMM_MAT_NO,A.EMM_MAT_DESC,B.EMSD_BRAND,C.EVM_VALUE2 FROM EZC_MATERIAL_MASTER A, EZC_MATERIAL_SALES_DATA B,EZC_VALUE_MAPPING C WHERE A.EMM_MAT_NO=B.EMSD_MAT_NO AND B.EMSD_BRAND=C.EVM_VALUE1 AND C.EVM_MAP_KEY='BRAND' AND B.EMSD_BRAND <> ''",
            nativeQuery = true
    )
	List<Object[]> getAllMaterialList();
	
}
