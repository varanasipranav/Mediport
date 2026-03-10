package com.ezc.aragenPR.webapp.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "EZC_EHS_ACTION_TABLE")
public class EzcEhsActionTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EEAT_ACTION_ID")
    private Long actionId;

    @ManyToOne
    @JoinColumn(name = "ECC_CAPA_ID", nullable = false)
    private EzcEHSCAPA capa;  // Foreign Key to CAPA table

    @Column(name = "EEAT_ACTION")
    private String action;

    @Column(name = "EEAT_RESPONSIBILITY")
    private String responsibility;

    @Column(name = "EEAT_COMPLETION_DATE")
    private LocalDate completionDate;

    @Column(name = "EEAT_STATUS")
    private String status;
    

    @Column(name = "EEAT_COMMENTS")
    private String comments;
    
    @Column(name = "EEAT_SUBMITTED_DATE")
    private LocalDate submittedDate;
    
    @Column(name = "EEAT_CREATED_BY")
    private String createdBy;

    @Column(name = "EEAT_PLANT")
    private String plant;

  
    // Getters, and Setters

     
	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getPlant() {
		return plant;
	}

	public void setPlant(String plant) {
		this.plant = plant;
	}

	public LocalDate getSubmittedDate() {
		return submittedDate;
	}

	public void setSubmittedDate(LocalDate submittedDate) {
		this.submittedDate = submittedDate;
	}

	public Long getActionId() {
		return actionId;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public void setActionId(Long actionId) {
		this.actionId = actionId;
	}

	public EzcEHSCAPA getCapa() {
		return capa;
	}

	public void setCapa(EzcEHSCAPA capa) {
		this.capa = capa;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getResponsibility() {
		return responsibility;
	}

	public void setResponsibility(String responsibility) {
		this.responsibility = responsibility;
	}

	public LocalDate getCompletionDate() {
		return completionDate;
	}

	public void setCompletionDate(LocalDate completionDate) {
		this.completionDate = completionDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	} 
    
    
    
}
