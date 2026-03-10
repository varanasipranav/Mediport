package com.ezc.aragenPR.webapp.persistance.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ezc.aragenPR.webapp.model.EzPriceSeqMaster;

public interface PriceSeqMasterRepository extends JpaRepository<EzPriceSeqMaster, Integer> {

	@Query(value = "select a from EzPriceSeqMaster a where a.id in (select b.seqId from EzConditionSeqMap b where b.conditionType=:condtype and b.userId=:userId)")
    List<EzPriceSeqMaster> getByCondType(@Param("condtype") String condtype,@Param("userId") String userId);
}
