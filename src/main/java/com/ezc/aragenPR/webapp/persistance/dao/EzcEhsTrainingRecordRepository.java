package com.ezc.aragenPR.webapp.persistance.dao;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ezc.aragenPR.webapp.model.EzcEhsTrainingRecord;


@Repository
public interface EzcEhsTrainingRecordRepository extends JpaRepository<EzcEhsTrainingRecord, Integer> {

	
	List<EzcEhsTrainingRecord> findByHeaderDocId(Integer docId);
}
