package com.ezc.aragenPR.webapp.dto;

import java.util.Date;

public class ReportIncidentDTO {
	
	private Date reportingDate;
	private String reportingPerson;
	private String typeOfObservation;
	private String Description;
	private String suggestedAction;
	private String responsiblity;
	private String status;
	private String comments;
	private String uuid;
	private String vendor;

	// Getter and Setter
	public String getVendor() {
	    return vendor;
	}

	public void setVendor(String vendor) {
	    this.vendor = vendor;
	}
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
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

}
