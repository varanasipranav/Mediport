package com.ezc.aragenPR.webapp.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

//import com.ezc.aragenPR.webapp.dto.EzcActionDTO;
import com.ezc.aragenPR.webapp.model.EzcEHSCAPA;
import com.ezc.aragenPR.webapp.model.EzcEhsActionTable;

public interface EzcEHSActionService {

//	    void saveActions(List<EzcActionDTO> actions, EzcEHSCAPA capa,String userId);

    	List<EzcEhsActionTable> getAllActions();

		Optional<EzcEhsActionTable> getActionById(Long actionId);
		
		void updateAction(EzcEhsActionTable action);

		List<EzcEhsActionTable> getFilteredActions(Date fromDate, Date toDate, String status);
		
		int getPendingActionCount();
	 
	
}
