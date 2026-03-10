/*
package com.ezc.aragenPR.webapp.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.ezc.aragenPR.webapp.model.EzcEHSCAPA;

public class EzcCAPADTO {

	private Long capaId;
	private Long incidentId;
	private String incidentType;
	private String caseType;
	private String injuredBodyPart;
	private String plantName;
	private String injuredPersonName;
	private String employmentType;
	private String incidentDescription;
	private String hazardIdentified;
	private String hiraAvailable;
	private LocalDate reportedBackDate;
	private String witnessName;
	private String investigatedBy;
	private LocalDate incidentDate;
	private String incidentTime;
	private String shift;
	private String employeeCode;
	private String possibleImmediateCauses;
	private String possibleSystemCauses;
	private String rootCauses;
	private String capaStatus;
	private String typeOfInjury;
	private String unsafeCondition;
	private String unsafeAction;
	private String activityType;
	private String department;
	 private String machine;

	// private List<EzcActionDTO> actions;
	//private List<EzcActionDTO> actions = new ArrayList<>();
	// Getters, and Setters

	public EzcCAPADTO(EzcEHSCAPA capa) {
		this.capaId = capa.getCapaId();
		this.incidentId = capa.getIncidentId();
		this.incidentType = capa.getIncidentType();
		this.caseType = capa.getCaseType();
		this.injuredBodyPart = capa.getInjuredBodyPart();
		this.plantName = capa.getPlantName();
		this.injuredPersonName = capa.getInjuredPersonName();
		this.employmentType = capa.getEmploymentType();
		this.incidentDescription = capa.getIncidentDescription();
		this.hazardIdentified = capa.getHazardIdentified();
		this.hiraAvailable = capa.getHiraAvailable();
		this.reportedBackDate = capa.getReportedBackDate();
		this.witnessName = capa.getWitnessName();
		this.investigatedBy = capa.getInvestigatedBy();
		this.shift = capa.getShift();
		this.employeeCode = capa.getEmployeeCode();
		this.possibleImmediateCauses = capa.getPossibleImmediateCauses();
		this.possibleSystemCauses = capa.getPossibleSystemCauses();
		this.rootCauses = capa.getRootCauses();
		this.capaStatus = capa.getCapaStatus();
		this.typeOfInjury = capa.getTypeOfInjury();
		this.unsafeAction = capa.getUnsafeAction();
		this.unsafeCondition = capa.getUnsafeCondition();
		this.activityType = capa.getActivityType();
		this.department = capa.getDepartment();
		this.machine = capa.getMachine();

		// ✅ Convert LocalDateTime to separate Date & Time
		if (capa.getDateTime() != null) {
			this.incidentDate = capa.getDateTime().toLocalDate();
			this.incidentTime = capa.getDateTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
		}

		// ✅ Convert Action Entities to DTOs
		*/
/*if (capa.getActions() != null) {
			this.actions = capa.getActions().stream().map(EzcActionDTO::new).collect(Collectors.toList());
		} else {
			this.actions = List.of();
		}*//*

	}
	

	
	
	public Long getIncidentId() {
		return incidentId;
	}




	public void setIncidentId(Long incidentId) {
		this.incidentId = incidentId;
	}




	public String getMachine() {
		return machine;
	}


	public void setMachine(String machine) {
		this.machine = machine;
	}


	public String getDepartment() {
		return department;
	}


	public void setDepartment(String department) {
		this.department = department;
	}


	public String getActivityType() {
		return activityType;
	}

	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}

	public String getUnsafeCondition() {
		return unsafeCondition;
	}

	public void setUnsafeCondition(String unsafeCondition) {
		this.unsafeCondition = unsafeCondition;
	}

	public String getUnsafeAction() {
		return unsafeAction;
	}

	public void setUnsafeAction(String unsafeAction) {
		this.unsafeAction = unsafeAction;
	}

	public String getTypeOfInjury() {
		return typeOfInjury;
	}

	public void setTypeOfInjury(String typeOfInjury) {
		this.typeOfInjury = typeOfInjury;
	}

	public String getCapaStatus() {
		return capaStatus;
	}

	public void setCapaStatus(String capaStatus) {
		this.capaStatus = capaStatus;
	}

	public EzcCAPADTO() {
		this.reportedBackDate = LocalDate.now(); // Default to today’s date
		this.actions = new ArrayList<>();
	}

	public Long getCapaId() {
		return capaId;
	}

	public void setCapaId(Long capaId) {
		this.capaId = capaId;
	}

	public String getIncidentType() {
		return incidentType;
	}

	public void setIncidentType(String incidentType) {
		this.incidentType = incidentType;
	}

	public String getCaseType() {
		return caseType;
	}

	public void setCaseType(String caseType) {
		this.caseType = caseType;
	}

	public String getInjuredBodyPart() {
		return injuredBodyPart;
	}

	public void setInjuredBodyPart(String injuredBodyPart) {
		this.injuredBodyPart = injuredBodyPart;
	}

	public String getPlantName() {
		return plantName;
	}

	public void setPlantName(String plantName) {
		this.plantName = plantName;
	}

	public String getInjuredPersonName() {
		return injuredPersonName;
	}

	public void setInjuredPersonName(String injuredPersonName) {
		this.injuredPersonName = injuredPersonName;
	}

	public String getEmploymentType() {
		return employmentType;
	}

	public void setEmploymentType(String employmentType) {
		this.employmentType = employmentType;
	}

	public String getIncidentDescription() {
		return incidentDescription;
	}

	public void setIncidentDescription(String incidentDescription) {
		this.incidentDescription = incidentDescription;
	}

	public String getHazardIdentified() {
		return hazardIdentified;
	}

	public void setHazardIdentified(String hazardIdentified) {
		this.hazardIdentified = hazardIdentified;
	}

	public String getHiraAvailable() {
		return hiraAvailable;
	}

	public void setHiraAvailable(String hiraAvailable) {
		this.hiraAvailable = hiraAvailable;
	}

	public LocalDate getReportedBackDate() {
		return reportedBackDate;
	}

	public void setReportedBackDate(LocalDate reportedBackDate) {
		this.reportedBackDate = reportedBackDate;
	}

	public String getWitnessName() {
		return witnessName;
	}

	public void setWitnessName(String witnessName) {
		this.witnessName = witnessName;
	}

	public String getInvestigatedBy() {
		return investigatedBy;
	}

	public void setInvestigatedBy(String investigatedBy) {
		this.investigatedBy = investigatedBy;
	}

	public String getShift() {
		return shift;
	}

	public void setShift(String shift) {
		this.shift = shift;
	}

	public String getEmployeeCode() {
		return employeeCode;
	}

	public void setEmployeeCode(String employeeCode) {
		this.employeeCode = employeeCode;
	}

	public String getPossibleImmediateCauses() {
		return possibleImmediateCauses;
	}

	public void setPossibleImmediateCauses(String possibleImmediateCauses) {
		this.possibleImmediateCauses = possibleImmediateCauses;
	}

	public String getPossibleSystemCauses() {
		return possibleSystemCauses;
	}

	public void setPossibleSystemCauses(String possibleSystemCauses) {
		this.possibleSystemCauses = possibleSystemCauses;
	}

	public String getRootCauses() {
		return rootCauses;
	}

	public void setRootCauses(String rootCauses) {
		this.rootCauses = rootCauses;
	}

	public List<EzcActionDTO> getActions() {
		if (this.actions == null) {
			this.actions = new ArrayList<>();
		}
		return actions;
	}

	*/
/*public void setActions(List<EzcActionDTO> actions) {
		this.actions = actions;
	}*//*


	// ✅ Updated Getter & Setter for `incidentTime`
	public String getIncidentTime() {
		return incidentTime;
	}

	public void setIncidentTime(String incidentTime) {
		this.incidentTime = incidentTime;
	}

	// ✅ Getter & Setter for `incidentDate`
	public LocalDate getIncidentDate() {
		return incidentDate;
	}

	public void setIncidentDate(LocalDate incidentDate) {
		this.incidentDate = incidentDate;
	}

}
*/
