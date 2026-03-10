package com.ezc.aragenPR.webapp.persistance.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ezc.aragenPR.webapp.model.EzMaterialLimitKey;
import com.ezc.aragenPR.webapp.model.EzMaterialLimits;
import com.ezc.aragenPR.webapp.model.EzMaterialSalesOrgData;
import com.ezc.aragenPR.webapp.model.EzMaterialSalesOrgDataKey;

public interface MaterialLimitRepo extends JpaRepository<EzMaterialLimits,EzMaterialLimitKey> {
	
	@Query(
            value = "SELECT EML_DIST_CHNL,EML_BRAND,EML_LEVEL,EML_LOWER,EML_UPPER FROM EZC_MATERIAL_LIMITS",
            nativeQuery = true
    )
	List<Object[]> getAllMaterialLimits();
	
	
	/*
	 * @Query( value =
	 * "SELECT * FROM EZC_MATERIAL_LIMITS A WHERE EML_LEVEL=:curLevel AND TRIM(A.EML_MATERIAL) IN (SELECT EPRI_MATERIAL FROM EZC_PRICE_REQ_ITEMS WHERE EPRI_DOC_ID=:requestNo)"
	 * , nativeQuery = true ) List<EzMaterialLimits> findReqMatLimits(String
	 * requestNo,Integer curLevel);
	 * 
	 * 
	 * @Query( value =
	 * "SELECT * FROM EZC_MATERIAL_LIMITS A WHERE A.EML_MATERIAL IN :matList",
	 * nativeQuery = true ) List<EzMaterialLimits>
	 * findReqMatLimitsByMaterial(List<String> matList);
	 */
	
	@Query(
            value = "SELECT * FROM EZC_MATERIAL_LIMITS A WHERE EML_DIST_CHNL=:distChnl and EML_BRAND=:brand",
            nativeQuery = true
    )
	List<EzMaterialLimits> getLimitsByDcBrand(String distChnl,String brand);
	
}
