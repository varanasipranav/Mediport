package com.ezc.aragenPR.webapp.persistance.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ezc.aragenPR.webapp.model.EzEhsSustainabilityParameter;
import com.ezc.aragenPR.webapp.model.EzcEhsHeader;

@Repository
public interface EzEhsSustainabilityParameterRepository extends JpaRepository<EzEhsSustainabilityParameter, Long> {

    @Query("SELECT p FROM EzEhsSustainabilityParameter p WHERE p.header.docId = :docId")
    List<EzEhsSustainabilityParameter> findByHeaderDocId(@Param("docId") Integer docId);
    

    @Query("SELECT p FROM EzEhsSustainabilityParameter p WHERE p.header IN :headers")
    List<EzEhsSustainabilityParameter> findByHeaderIn(@Param("headers") List<EzcEhsHeader> headers);
}