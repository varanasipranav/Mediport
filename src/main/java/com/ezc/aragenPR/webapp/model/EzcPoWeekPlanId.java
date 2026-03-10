package com.ezc.aragenPR.webapp.model;
import java.io.Serializable;
import java.util.Objects;

public class EzcPoWeekPlanId implements Serializable {
	private EzcPoPlanHeader poPlanHeader;
    private Integer week;
	public EzcPoWeekPlanId() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
	public int hashCode() {
		return Objects.hash(poPlanHeader, week);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EzcPoWeekPlanId other = (EzcPoWeekPlanId) obj;
		return Objects.equals(poPlanHeader, other.poPlanHeader) && Objects.equals(week, other.week);
	}
	public EzcPoWeekPlanId(EzcPoPlanHeader poPlanHeader, Integer week) {
		super();
		this.poPlanHeader = poPlanHeader;
		this.week = week;
	}
	   
    
}