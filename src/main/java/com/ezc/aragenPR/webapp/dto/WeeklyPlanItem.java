package com.ezc.aragenPR.webapp.dto;

import java.util.Date;

public class WeeklyPlanItem {
	
	public WeeklyPlanItem() {
		super();
		// TODO Auto-generated constructor stub
	}
	private String planId;
	private String monthYr;
	private String vendor;
	private String week1Sts;
	private String week2Sts;
	private String week3Sts;
	private String week4Sts;
	private String plant;
	private Date createdOn;
	public String getPlanId() {
		return planId;
	}
	public void setPlanId(String planId) {
		this.planId = planId;
	}
	public String getMonthYr() {
		return monthYr;
	}
	public void setMonthYr(String monthYr) {
		this.monthYr = monthYr;
	}
	public String getVendor() {
		return vendor;
	}
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	public String getWeek1Sts() {
		return week1Sts;
	}
	public void setWeek1Sts(String week1Sts) {
		this.week1Sts = week1Sts;
	}
	public String getWeek2Sts() {
		return week2Sts;
	}
	public void setWeek2Sts(String week2Sts) {
		this.week2Sts = week2Sts;
	}
	public String getWeek3Sts() {
		return week3Sts;
	}
	public void setWeek3Sts(String week3Sts) {
		this.week3Sts = week3Sts;
	}
	public String getWeek4Sts() {
		return week4Sts;
	}
	public void setWeek4Sts(String week4Sts) {
		this.week4Sts = week4Sts;
	}
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	public String getPlant() {
		return plant;
	}
	public void setPlant(String plant) {
		this.plant = plant;
	}
	
	
	
}
