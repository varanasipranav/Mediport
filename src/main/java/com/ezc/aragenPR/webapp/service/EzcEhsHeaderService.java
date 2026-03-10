package com.ezc.aragenPR.webapp.service;

import com.ezc.aragenPR.webapp.model.EzComments;
import com.ezc.aragenPR.webapp.model.EzcEhsHeader;

import java.util.Date;
import java.util.List;

public interface EzcEhsHeaderService {
    
	List<EzcEhsHeader> getAllHeaders(); 
	EzcEhsHeader findByDocId(Integer docId);
	 EzcEhsHeader save(EzcEhsHeader header);
	List<EzcEhsHeader> getFilteredHeaders(Date fromDate, Date toDate, String status, String userId);
	int getPendingReview();
	void saveComments(String comments, Integer docId, String userId, String string); 
	 List<EzComments> getCommetnsById(String docId, String docType);
	List<EzcEhsHeader> getFilteredHeaders(Date fromDate, Date toDate, String status);
}
