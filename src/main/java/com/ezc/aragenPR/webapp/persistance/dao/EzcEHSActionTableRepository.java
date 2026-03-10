package com.ezc.aragenPR.webapp.persistance.dao;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ezc.aragenPR.webapp.model.EzcEhsActionTable;

@Repository
public interface EzcEHSActionTableRepository extends JpaRepository<EzcEhsActionTable, Long> {

    // Fetch all actions for a specific CAPA ID
    List<EzcEhsActionTable> findByCapa_CapaId(Long capaId);
    
    List<EzcEhsActionTable> findByCompletionDateBetween(LocalDate from, LocalDate to);

    List<EzcEhsActionTable> findByCompletionDateBetweenAndStatus(LocalDate from, LocalDate to, String status);

    List<EzcEhsActionTable> findByStatus(String status);


}