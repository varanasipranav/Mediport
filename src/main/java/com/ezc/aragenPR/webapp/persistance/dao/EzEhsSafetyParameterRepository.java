package com.ezc.aragenPR.webapp.persistance.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.ezc.aragenPR.webapp.model.EzEhsSafetyParameter;
import com.ezc.aragenPR.webapp.model.EzcEhsHeader;

@Repository
public interface EzEhsSafetyParameterRepository extends JpaRepository<EzEhsSafetyParameter, Integer> {
	
	@Query("SELECT p FROM EzEhsSafetyParameter p WHERE p.header.docId = :docId")
	List<EzEhsSafetyParameter> findByHeaderDocId(@Param("docId") Integer docId);
	
	@Query("SELECT p FROM EzEhsSafetyParameter p WHERE p.header IN :headers")
	List<EzEhsSafetyParameter> findByHeaderIn(@Param("headers") List<EzcEhsHeader> headers);


	
}
