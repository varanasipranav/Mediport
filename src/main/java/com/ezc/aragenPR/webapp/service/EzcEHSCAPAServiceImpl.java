/*
package com.ezc.aragenPR.webapp.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ezc.aragenPR.webapp.dto.EzcActionDTO;
import com.ezc.aragenPR.webapp.dto.EzcCAPADTO;
import com.ezc.aragenPR.webapp.model.EzcEHSCAPA;
import com.ezc.aragenPR.webapp.model.EzcEhsActionTable;
import com.ezc.aragenPR.webapp.persistance.dao.EzcEHSActionTableRepository;
import com.ezc.aragenPR.webapp.persistance.dao.EzcEHSCAPARepository;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class EzcEHSCAPAServiceImpl implements EzcEHSCAPAService {


  
	 @Autowired
	    private EzcEHSCAPARepository capaRepository;
	 
	 @Autowired
	 	private EzcEHSActionTableRepository actionTableRepository;

	 @Override
	 @Transactional
	 public EzcEHSCAPA saveCapa(EzcCAPADTO capaDTO) {
	     EzcEHSCAPA capa = new EzcEHSCAPA();
	     
	     capa.setCapaId(capaDTO.getCapaId());
	     capa.setIncidentId(capaDTO.getIncidentId());
	     capa.setIncidentType(capaDTO.getIncidentType());
	     capa.setCaseType(capaDTO.getCaseType());
	     capa.setInjuredBodyPart(capaDTO.getInjuredBodyPart());
	     capa.setPlantName(capaDTO.getPlantName());
	     capa.setInjuredPersonName(capaDTO.getInjuredPersonName());
	     capa.setEmploymentType(capaDTO.getEmploymentType());
	     capa.setIncidentDescription(capaDTO.getIncidentDescription());
	     capa.setHazardIdentified(capaDTO.getHazardIdentified());
	     capa.setHiraAvailable(capaDTO.getHiraAvailable());
	     capa.setReportedBackDate(capaDTO.getReportedBackDate());
	     capa.setWitnessName(capaDTO.getWitnessName());
	     capa.setInvestigatedBy(capaDTO.getInvestigatedBy());
	     capa.setShift(capaDTO.getShift());
	     capa.setEmployeeCode(capaDTO.getEmployeeCode());
	     capa.setPossibleImmediateCauses(capaDTO.getPossibleImmediateCauses());
	     capa.setPossibleSystemCauses(capaDTO.getPossibleSystemCauses());
	     capa.setRootCauses(capaDTO.getRootCauses());
	     capa.setCapaStatus(capaDTO.getCapaStatus());
	     capa.setTypeOfInjury(capaDTO.getTypeOfInjury());
	     capa.setUnsafeAction(capaDTO.getUnsafeAction());
	     capa.setUnsafeCondition(capaDTO.getUnsafeCondition());
	     capa.setActivityType(capaDTO.getActivityType());
	     capa.setDepartment(capaDTO.getDepartment());
	     capa.setMachine(capaDTO.getMachine());

	     if (capaDTO.getIncidentDate() != null && capaDTO.getIncidentTime() != null) {
	         LocalTime time = LocalTime.parse(capaDTO.getIncidentTime(), DateTimeFormatter.ofPattern("HH:mm"));
	         capa.setDateTime(LocalDateTime.of(capaDTO.getIncidentDate(), time));
	     }
	     
	     return capaRepository.save(capa); // 🔁 Return the entity
	 }

	@Override
	public EzcCAPADTO getCAPAByIncidentId(Long incidentId) {
		  Optional<EzcEHSCAPA> capaOpt = capaRepository.findByIncidentId(incidentId);
	    if (capaOpt.isPresent()) {
	        EzcEHSCAPA capa = capaOpt.get();
	        EzcCAPADTO dto = new EzcCAPADTO();

	        dto.setCapaId(capa.getCapaId());
	        dto.setIncidentId(capa.getIncidentId());
	        dto.setIncidentType(capa.getIncidentType());
	        dto.setCaseType(capa.getCaseType());
	        dto.setInjuredBodyPart(capa.getInjuredBodyPart());
	        dto.setPlantName(capa.getPlantName());
	        dto.setInjuredPersonName(capa.getInjuredPersonName());
	        dto.setEmploymentType(capa.getEmploymentType());
	        dto.setIncidentDescription(capa.getIncidentDescription());
	        dto.setHazardIdentified(capa.getHazardIdentified());
	        dto.setHiraAvailable(capa.getHiraAvailable());
	        dto.setReportedBackDate(capa.getReportedBackDate());
	        dto.setWitnessName(capa.getWitnessName());
	        dto.setInvestigatedBy(capa.getInvestigatedBy());
	        dto.setShift(capa.getShift());
	        dto.setEmployeeCode(capa.getEmployeeCode());
	        dto.setPossibleImmediateCauses(capa.getPossibleImmediateCauses());
	        dto.setPossibleSystemCauses(capa.getPossibleSystemCauses());
	        dto.setRootCauses(capa.getRootCauses());
	        dto.setTypeOfInjury(capa.getTypeOfInjury());
	        dto.setUnsafeAction(capa.getUnsafeAction());
	        dto.setUnsafeCondition(capa.getUnsafeCondition());
	        dto.setActivityType(capa.getActivityType());
	        dto.setDepartment(capa.getDepartment());
	        dto.setMachine(capa.getMachine());

	        if (capa.getDateTime() != null) {
	            LocalDate datePart = capa.getDateTime().toLocalDate();
	            LocalTime timePart = capa.getDateTime().toLocalTime();

	            if (datePart != null) {
	                // Format the date as yyyy-MM-dd
	                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	                String formattedDate = datePart.format(dateFormatter);
	                System.out.println("formattedDate " + LocalDate.parse(formattedDate));
	                dto.setIncidentDate(LocalDate.parse(formattedDate)); // You can also store it as a String if your DTO uses String
	            }

	            if (timePart != null) {
	                dto.setIncidentTime(timePart.format(DateTimeFormatter.ofPattern("HH:mm")));
	            }
	        }
	        
	        List<EzcEhsActionTable> actions = actionTableRepository.findByCapa_CapaId(capa.getCapaId());

	        List<EzcActionDTO> actionDTOs = actions.stream().map(action -> {
	            EzcActionDTO actionDTO = new EzcActionDTO();
	            actionDTO.setAction(action.getAction());
	            actionDTO.setResponsibility(action.getResponsibility());
	            actionDTO.setCompletionDate(action.getCompletionDate());
	            actionDTO.setStatus(action.getStatus());
	            actionDTO.setSubmittedDate(action.getSubmittedDate());
	            actionDTO.setComments(action.getComments());
	            return actionDTO;
	        }).collect(Collectors.toList());

	        dto.setActions(actionDTOs);


	        return dto;
	    }
	    return null;
	}

	@Override
	public String getCAPAStatusByIncidentId(Long incidentId) {
		 Optional<EzcEHSCAPA> capa = capaRepository.findByIncidentId(incidentId); // Correct method
		    String status = capa.map(EzcEHSCAPA::getCapaStatus).orElse("Not Completed");
		    System.out.println("Fetched CAPA Status from DB: " + status);
		    return status;
	}

	@Override
	public void updateCAPAStatus(Long capaId, String status) {
		 EzcEHSCAPA capa = capaRepository.findById(capaId).orElse(null);
		    if (capa != null) {
		        capa.setCapaStatus(status);
		        capaRepository.save(capa);
		    }
	}


}
*/
