package com.ezc.aragenPR.webapp.service;

import java.util.Date;
import java.util.List;

import com.ezc.aragenPR.webapp.model.EzEHSIncident;
import com.ezc.aragenPR.webapp.model.EzUploadDocument;

public interface EhsIncidentService {
	EzEHSIncident createIncident(EzEHSIncident incident);
	 List<EzEHSIncident> getFilteredIncidents(Date fromDate, Date toDate, String status,String vendorId,String typeOfObservation);
	 EzEHSIncident getIncidentById(Long id);
	 void updateIncident(EzEHSIncident incident);
	List<EzUploadDocument> getFilesById(Integer docId);
	List<EzUploadDocument> findFileByDocType(Integer docNo, String docType);
	List<EzEHSIncident> getFilteredIncidents(Date fromDate, Date toDate, String status,String typeOfObservation);
}
