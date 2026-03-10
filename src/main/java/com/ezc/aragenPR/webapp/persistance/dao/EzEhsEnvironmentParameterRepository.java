package com.ezc.aragenPR.webapp.persistance.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ezc.aragenPR.webapp.model.EzEhsEnvironmentParameter;
import com.ezc.aragenPR.webapp.model.EzcEhsHeader;


@Repository
public interface EzEhsEnvironmentParameterRepository extends JpaRepository<EzEhsEnvironmentParameter, Integer> {
	
	@Query("SELECT p FROM EzEhsEnvironmentParameter p WHERE p.header.docId = :docId")
	List<EzEhsEnvironmentParameter> findByHeaderDocId(@Param("docId") Integer docId);
	
	@Query("SELECT p FROM EzEhsEnvironmentParameter p WHERE p.header IN :headers")
	List<EzEhsEnvironmentParameter> findByHeaderIn(@Param("headers") List<EzcEhsHeader> headers);



}