package com.ezc.aragenPR.webapp.persistance.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ezc.aragenPR.webapp.model.EzMaterialMaster;

public interface MaterialMasterRepo extends JpaRepository<EzMaterialMaster, String>{
	@Modifying
    @Query(
            value = "truncate table EZC_MATERIAL_MASTER",
            nativeQuery = true
    )
    void truncateTable();

}
