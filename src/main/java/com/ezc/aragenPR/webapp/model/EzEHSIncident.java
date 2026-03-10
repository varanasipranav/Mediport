package com.ezc.aragenPR.webapp.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Entity
@Table(name = "EZC_EHS_INCIDENT")
@Data
public class EzEHSIncident {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
    @Temporal(TemporalType.DATE)
	@Column(name = "EEI_REPORTING_DATE" ,length = 10, nullable = false)
	private Date reportingDate;
	
	@Column(name = "EEI_REPORTING_PERSON",length = 25, nullable = false)
	private String reportingPerson;
	
	@Column(name = "EEI_OBSERVATION" , nullable = false)
	private String typeOfObservation;
	
	@Column(name = "EEI_Description" , nullable = false)
	private String Description;
	
	@Column(name = "EEI_SUGGESTED_ACTION",length = 255, nullable = false)
	private String suggestedAction;
	
	@Column(name = "EEI_RESPOSIBLITY",length = 25, nullable = false)
	private String responsiblity;
	
	@Column(name = "EEI_STATUS")
	private String status;
	
	@Column(name = "EEI_COMMENTS", length = 500)  
    private String comments;
	
	@Column(name = "EEI_VENDOR", length = 100) 
	private String vendor;

	// Getter and Setter
	public String getVendor() {
	    return vendor;
	}

	public void setVendor(String vendor) {
	    this.vendor = vendor;
	}
	public String getDescription() {
		return Description;
	}
	public void setDescription(String description) {
		Description = description;
	}

	public Date getReportingDate() {
		return reportingDate;
	}
	public void setReportingDate(Date reportingDate) {
		this.reportingDate = reportingDate;
	}
	public String getReportingPerson() {
		return reportingPerson;
	}
	public void setReportingPerson(String reportingPerson) {
		this.reportingPerson = reportingPerson;
	}
	public String getTypeOfObservation() {
		return typeOfObservation;
	}
	public void setTypeOfObservation(String typeOfObservation) {
		this.typeOfObservation = typeOfObservation;
	}
	public String getSuggestedAction() {
		return suggestedAction;
	}
	public void setSuggestedAction(String suggestedAction) {
		this.suggestedAction = suggestedAction;
	}
	public String getResponsiblity() {
		return responsiblity;
	}
	public void setResponsiblity(String responsiblity) {
		this.responsiblity = responsiblity;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}

}
