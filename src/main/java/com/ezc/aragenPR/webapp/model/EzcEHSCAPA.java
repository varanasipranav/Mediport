package com.ezc.aragenPR.webapp.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "EZC_EHS_CAPA")
public class EzcEHSCAPA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ECC_CAPA_ID")
    private Long capaId; 
    
	@Column(name = "ECC_INCIDENT_ID")
    private Long incidentId;

    @Column(name = "EEC_INCIDENT_TYPE")
    private String incidentType;

    @Column(name = "EEC_CASE")
    private String caseType;

    @Column(name = "ECC_INJURED_BODY_PART")
    private String injuredBodyPart;
    
    @Column(name = "ECC_TYPE_OF_INJURY")
    private String typeOfInjury;

    @Column(name = "ECC_PLANT_NAME")
    private String plantName;

    @Column(name = "ECC_INJURED_PERSON_NAME")
    private String injuredPersonName;

    @Column(name = "ECC_EMPLOYMENT_TYPE")
    private String employmentType; 

    @Column(name = "ECC_INCIDENT_DESCRIPTION")
    private String incidentDescription;

    @Column(name = "ECC_HAZARD_IDENTIFIED")
    private String hazardIdentified;  

    @Column(name = "ECC_HIRA_AVAILABLE")
    private String hiraAvailable; 
    
    @Column(name = "ECC_UNSAFE_CONDITION")
    private String unsafeCondition;
    
    @Column(name = "ECC_UNSAFE_ACTION")
    private String unsafeAction;

    @Column(name = "ECC_REPORTED_BACK_DATE")
    private LocalDate reportedBackDate;

    @Column(name = "ECC_WITNESS_NAME")
    private String witnessName;
    
    @Column(name = "ECC_INVESTIGATED_BY")
    private String investigatedBy;

	@Column(name = "ECC_DATE_TIME")
    private LocalDateTime dateTime;

    @Column(name = "ECC_SHIFT")
    private String shift;

    @Column(name = "ECC_EMPLOYEE_CODE")
    private String employeeCode;

    @Column(name = "ECC_POSSIBLE_IMMEDIATE_CAUSES")
    private String possibleImmediateCauses;  

    @Column(name = "ECC_POSSIBLE_SYSTEM_CAUSES")
    private String possibleSystemCauses;  

    @Column(name = "ECC_ROOT_CAUSES")
    private String rootCauses;  
    
    @Column(name = "ECC_CAPA_STATUS")
    private String capaStatus;
    
    @Column(name = "ECC_ACTIVITY_TYPE")
    private String activityType;
    
    @Column(name = "ECC_DEPARTMENT")
    private String department;
    
    @Column(name = "ECC_MACHINE")
    private String machine;



    @OneToMany(mappedBy = "capa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EzcEhsActionTable> actions = new ArrayList<EzcEhsActionTable>();

    // Getters, and Setters
    
    public Long getIncidentId() {
		return incidentId;
	}


	public void setIncidentId(Long incidentId) {
		this.incidentId = incidentId;
	}
    
	public Long getCapaId() {
		return capaId;
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

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
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

	public List<EzcEhsActionTable> getActions() {
		return actions;
	}

	public void setActions(List<EzcEhsActionTable> actions) {
		this.actions = actions;
	}

	public String getCapaStatus() {
		return capaStatus;
	}

	public void setCapaStatus(String capaStatus) {
		this.capaStatus = capaStatus;
	}

   
}

