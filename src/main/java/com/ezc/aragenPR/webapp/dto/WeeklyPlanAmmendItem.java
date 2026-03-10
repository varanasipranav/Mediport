package com.ezc.aragenPR.webapp.dto;

public class WeeklyPlanAmmendItem {
	
	private String lineNo;
	private String material;
	private String description;
	private String uom;
	private String quantity;
	private String location;
	private String status;
	 
	    private String cptWeekPropQty;
	    private String vendWeekPropQty;
	   private String cptWeekAccQty;
	   private String remarks;
	public String getLineNo() {
		return lineNo;
	}
	public void setLineNo(String lineNo) {
		this.lineNo = lineNo;
	}
	public String getMaterial() {
		return material;
	}
	public void setMaterial(String material) {
		this.material = material;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUom() {
		return uom;
	}
	public void setUom(String uom) {
		this.uom = uom;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCptWeekPropQty() {
		return cptWeekPropQty;
	}
	public void setCptWeekPropQty(String cptWeekPropQty) {
		this.cptWeekPropQty = cptWeekPropQty;
	}
	public String getVendWeekPropQty() {
		return vendWeekPropQty;
	}
	public void setVendWeekPropQty(String vendWeekPropQty) {
		this.vendWeekPropQty = vendWeekPropQty;
	}
	public String getCptWeekAccQty() {
		return cptWeekAccQty;
	}
	public void setCptWeekAccQty(String cptWeekAccQty) {
		this.cptWeekAccQty = cptWeekAccQty;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	   
		
}
