/*
package com.ezc.aragenPR.webapp.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ezc.aragenPR.webapp.dto.EzcActionDTO;
import com.ezc.aragenPR.webapp.model.EzcEHSCAPA;
import com.ezc.aragenPR.webapp.model.EzcEhsActionTable;
import com.ezc.aragenPR.webapp.persistance.dao.EzcEHSActionTableRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class EzcEHSActionServiceImpl implements EzcEHSActionService{

	 @Autowired
	    private EzcEHSActionTableRepository actionTableRepository;

	 @Override
	 public void saveActions(List<EzcActionDTO> actionDTOs, EzcEHSCAPA capa,String userId) {
		 List<EzcEhsActionTable> updatedActions = new ArrayList<>();

		 for (EzcActionDTO dto : actionDTOs) {
		     EzcEhsActionTable action;

		     if (dto.getActionId() != null) {
		         // Fetch existing action
		         action = actionTableRepository.findById(dto.getActionId()).orElse(new EzcEhsActionTable());
		     } else {
		         action = new EzcEhsActionTable();
		     }

		     action.setCapa(capa);
		     action.setAction(dto.getAction());
		     action.setResponsibility(dto.getResponsibility());
		     action.setCompletionDate(dto.getCompletionDate());

		     // Optional fields
		     if (dto.getStatus() != null) action.setStatus(dto.getStatus());
		     if (dto.getSubmittedDate() != null) action.setSubmittedDate(dto.getSubmittedDate());
		     if (dto.getComments() != null) action.setComments(dto.getComments());

		     // New fields
		     action.setCreatedBy(userId); // Set from logged-in user
		     action.setPlant(capa.getPlantName()); // Assuming plant is in capa

		     updatedActions.add(action);
		 }

		 actionTableRepository.saveAll(updatedActions);

	 }


		@Override
		public List<EzcEhsActionTable> getAllActions() {
			// TODO Auto-generated method stub
			return actionTableRepository.findAll();
		}

		@Override
		public Optional<EzcEhsActionTable> getActionById(Long actionId) {
			 return actionTableRepository.findById(actionId);
		}

		@Override
		public void updateAction(EzcEhsActionTable action) {
			if (actionTableRepository.existsById(action.getActionId())) {
				actionTableRepository.save(action);
	        } else {
	            throw new EntityNotFoundException("Action with ID " + action.getActionId() + " not found.");
	        }
		}

		
		@Override
		public List<EzcEhsActionTable> getFilteredActions(Date fromDate, Date toDate, String status) {
		    LocalDate from = fromDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		    LocalDate to = toDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		    if (status == null || status.isEmpty()) {
		        return actionTableRepository.findByCompletionDateBetween(from, to);
		    }
		    return actionTableRepository.findByCompletionDateBetweenAndStatus(from, to, status);
		}
		
		@Override
		public int getPendingActionCount() {
		    List<EzcEhsActionTable> openedActions = actionTableRepository.findByStatus("Opened");
		    return openedActions.size();
		}


	  
}
*/
