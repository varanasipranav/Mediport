package com.ezc.aragenPR.webapp.service;

//import com.ezc.aragenPR.webapp.dto.EzcCAPADTO;
import com.ezc.aragenPR.webapp.model.EzcEHSCAPA;

public interface EzcEHSCAPAService {

	 // Save or update CAPA data
	//EzcEHSCAPA saveCapa(EzcCAPADTO capaDTO);
	//EzcCAPADTO getCAPAByIncidentId(Long incidentId);
	String getCAPAStatusByIncidentId(Long incidentId);
	void updateCAPAStatus(Long capaId, String status);


}
