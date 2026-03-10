package com.ezc.aragenPR.webapp.persistance.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ezc.aragenPR.webapp.model.EzcEhsInitiative;

import jakarta.transaction.Transactional;

@Repository
public interface EzcEhsInitiativeRepository extends JpaRepository<EzcEhsInitiative, Integer> {
	
	 @Query("SELECT e FROM EzcEhsInitiative e WHERE e.header.docId = :docId")
     List<EzcEhsInitiative> findByHeaderDocId(@Param("docId") Integer docId);
	 
	@Transactional
	void deleteByHeaderDocId(Integer  docId);
}

