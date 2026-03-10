package com.ezc.aragenPR.webapp.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ezc.aragenPR.webapp.model.EzEHSIncident;
import com.ezc.aragenPR.webapp.model.EzUploadDocument;
import com.ezc.aragenPR.webapp.persistance.dao.EzEhsInventoryRepository;
import com.ezc.aragenPR.webapp.persistance.dao.EzUploadDocumentRepo;

@Service
public class EhsIncidentServiceImpl implements EhsIncidentService {

	@Autowired
	private EzEhsInventoryRepository incidentRepository;
	
	@Autowired
	private EzUploadDocumentRepo ezUploadDocumentRepo;

	@Override
	public EzEHSIncident createIncident(EzEHSIncident incident) {
				
		return incidentRepository.save(incident);
		
	}

	@Override
	public List<EzEHSIncident> getFilteredIncidents(Date fromDate, Date toDate, String status,String typeOfObservation) {
		
	    return incidentRepository.findByFilters(fromDate, toDate, status,typeOfObservation);
	}
	
	@Override
	public List<EzEHSIncident> getFilteredIncidents(Date fromDate, Date toDate, String status, String vendorId,String typeOfObservation) {
	  
	    return incidentRepository.findByVendorFilters(fromDate, toDate, status, vendorId,typeOfObservation);
	}


	@Override
	public EzEHSIncident getIncidentById(Long id) {
		 return incidentRepository.findById(id)
			        .orElseThrow(() -> new RuntimeException("Incident not found with ID: " + id));
	}

	@Override
	public void updateIncident(EzEHSIncident incident) {
		 incidentRepository.save(incident); 
	}
	
	@Override
	public List<EzUploadDocument> getFilesById(Integer docId) {
		// TODO Auto-generated method stub
		List<EzUploadDocument> ezUploadDocumentList= ezUploadDocumentRepo.getFilesById(docId);
		return ezUploadDocumentList;
	}
	
	@Override
	public List<EzUploadDocument> findFileByDocType(Integer docNo, String docType) {
		// TODO Auto-generated method stub
		List<EzUploadDocument> ezUploadDocumentList= ezUploadDocumentRepo.findFileByDocType(docNo,docType);
		return ezUploadDocumentList;
	}

	

}
