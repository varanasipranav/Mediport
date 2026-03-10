package com.ezc.aragenPR.webapp.persistance.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ezc.aragenPR.webapp.model.EzComments;

public interface CommentsRepo extends JpaRepository<EzComments, Integer> {
	
	List<EzComments> findByDocIdAndDocType(String docId,String docType);

}
