package com.ezc.aragenPR.webapp.dto;

import java.io.Serializable;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class TPMMeetDto implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String meetId;
	@NotNull 
	@Min(value = 1)
	public Integer noOfAttendees;
	public String retailer;
	public String instructions;
	public String getMeetId() {
		return meetId;
	}
	public void setMeetId(String meetId) {
		this.meetId = meetId;
	}
	public Integer getNoOfAttendees() {
		return noOfAttendees;
	}
	public void setNoOfAttendees(Integer noOfAttendees) {
		this.noOfAttendees = noOfAttendees;
	}
	public String getRetailer() {
		return retailer;
	}
	public void setRetailer(String retailer) {
		this.retailer = retailer;
	}
	public String getInstructions() {
		return instructions;
	}
	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}
	
	
	
}
