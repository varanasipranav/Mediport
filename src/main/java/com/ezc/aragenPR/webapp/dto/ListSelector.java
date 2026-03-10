package com.ezc.aragenPR.webapp.dto;

import java.util.ArrayList;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

public class ListSelector {

	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date fromDate;
	
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date toDate;
	
	private String status;
	private String type;
	
	
	private ArrayList<String> user;
	
	private String nextPart;
	
	private String wfUser;
	
	private String step;
	
	private String role;
	
	private String vendor;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	public Date getFromDate() {
		return fromDate;
	}

	

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ArrayList<String> getUser() {
		return user;
	}

	public void setUser(ArrayList<String> user) {
		this.user = user;
	}

	
	public String getNextPart() {
		return nextPart;
	}

	public void setNextPart(String nextPart) {
		this.nextPart = nextPart;
	}

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	/**
	 * @return the wfUser
	 */
	public String getWfUser() {
		return wfUser;
	}

	/**
	 * @param wfUser the wfUser to set
	 */
	public void setWfUser(String wfUser) {
		this.wfUser = wfUser;
	}

	/**
	 * @return the role
	 */
	public String getRole() {
		return role;
	}

	/**
	 * @param role the role to set
	 */
	public void setRole(String role) {
		this.role = role;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	
	
}
