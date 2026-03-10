package com.ezc.aragenPR.webapp.persistance.dao;

import com.ezc.aragenPR.webapp.model.EzcMasterDataId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ezc.aragenPR.webapp.model.EzcMasterData;

import java.util.List;


@Repository
    public interface EzcMasterDataRepo extends JpaRepository<EzcMasterData, EzcMasterDataId> {

        List<EzcMasterData> findByEmdKey(String emdKey);
        List<EzcMasterData> findByEmdKeyAndEmdValue3(String emdKey, String emdValue3);
    @Transactional
    void deleteByEmdKey(String emdKey);

}


