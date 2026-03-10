package com.ezc.aragenPR.webapp.persistance.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ezc.aragenPR.webapp.model.Work_Groups;

import java.util.List;

public interface GroupRepository extends JpaRepository<Work_Groups, Long> {

	@Query("SELECT wg FROM Work_Groups wg WHERE wg.name = ?1")	
	Work_Groups findByGroup(String name);
	
	@Query("SELECT wg FROM Work_Groups wg WHERE wg.role = ?1")	
	List<Work_Groups> findByRole(String role);

} 
