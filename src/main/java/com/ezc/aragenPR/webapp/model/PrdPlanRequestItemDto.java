package com.ezc.aragenPR.webapp.model;

public class PrdPlanRequestItemDto {

private String plant;
	
private Integer lineNo;

private String category;

private String categoryCode;

private double weight;

private String material;

private String materialDesc;

private double week1Qty;

private double week2Qty;

private double week3Qty;

private double week4Qty;

private double total;

private String outSrcLoc;

private String uom; 
 
private String errFlag;

private String errText;

private String status;

private String storageLocation;

private double totVendAcpQty;
private double totCPTAcpQty;

private double netWt;

private String vendAcpRemarks;
private String cptAcpRemarks;

private String itemIndicator;

private String insertTxt;

private String vendWeekPropQty;
private String vendWeekPropRem;


public String getVendAcpRemarks() {
	return vendAcpRemarks;
}

public void setVendAcpRemarks(String vendAcpRemarks) {
	this.vendAcpRemarks = vendAcpRemarks;
}

public String getCptAcpRemarks() {
	return cptAcpRemarks;
}

public void setCptAcpRemarks(String cptAcpRemarks) {
	this.cptAcpRemarks = cptAcpRemarks;
}

public double getTotVendAcpQty() {
	return totVendAcpQty;
}

public void setTotVendAcpQty(double totVendAcpQty) {
	this.totVendAcpQty = totVendAcpQty;
}

public double getTotCPTAcpQty() {
	return totCPTAcpQty;
}

public void setTotCPTAcpQty(double totCPTAcpQty) {
	this.totCPTAcpQty = totCPTAcpQty;
}

public Integer getLineNo() {
	return lineNo;
}

public void setLineNo(Integer lineNo) {
	this.lineNo = lineNo;
}

public String getMaterial() {
	return material;
}

public void setMaterial(String material) {
	this.material = material;
}

public String getMaterialDesc() {
	return materialDesc;
}

public void setMaterialDesc(String materialDesc) {
	this.materialDesc = materialDesc;
}

public double getWeek1Qty() {
	return week1Qty;
}

public void setWeek1Qty(double week1Qty) {
	this.week1Qty = week1Qty;
}

public double getWeek2Qty() {
	return week2Qty;
}

public void setWeek2Qty(double week2Qty) {
	this.week2Qty = week2Qty;
}

public double getWeek3Qty() {
	return week3Qty;
}

public void setWeek3Qty(double week3Qty) {
	this.week3Qty = week3Qty;
}

public double getWeek4Qty() {
	return week4Qty;
}

public void setWeek4Qty(double week4Qty) {
	this.week4Qty = week4Qty;
}

public String getOutSrcLoc() {
	return outSrcLoc;
}

public void setOutSrcLoc(String outSrcLoc) {
	this.outSrcLoc = outSrcLoc;
}

public String getErrFlag() {
	return errFlag;
}

public void setErrFlag(String errFlag) {
	this.errFlag = errFlag;
}

public String getErrText() {
	return errText;
}

public void setErrText(String errText) {
	this.errText = errText;
}

public String getStatus() {
	return status;
}

public void setStatus(String status) {
	this.status = status;
}

public double getTotal() {
	return total;
}

public void setTotal(double total) {
	this.total = total;
}

public String getUom() {
	return uom;
}

public void setUom(String uom) {
	this.uom = uom;
}

public String getStorageLocation() {
	return storageLocation;
}

public void setStorageLocation(String storageLocation) {
	this.storageLocation = storageLocation;
}

public double getNetWt() {
	return netWt;
}

public void setNetWt(double netWt) {
	this.netWt = netWt;
}

public String getCategory() {
	return category;
}

public void setCategory(String category) {
	this.category = category;
}

public double getWeight() {
	return weight;
}

public void setWeight(double weight) {
	this.weight = weight;
}

public String getCategoryCode() {
	return categoryCode;
}

public void setCategoryCode(String categoryCode) {
	this.categoryCode = categoryCode;
}

public String getPlant() {
	return plant;
}

public void setPlant(String plant) {
	this.plant = plant;
}

public String getItemIndicator() {
	return itemIndicator;
}

public void setItemIndicator(String itemIndicator) {
	this.itemIndicator = itemIndicator;
}

public String getInsertTxt() {
	return insertTxt;
}

public void setInsertTxt(String insertTxt) {
	this.insertTxt = insertTxt;
}

public String getVendWeekPropQty() {
	return vendWeekPropQty;
}

public void setVendWeekPropQty(String vendWeekPropQty) {
	this.vendWeekPropQty = vendWeekPropQty;
}

public String getVendWeekPropRem() {
	return vendWeekPropRem;
}

public void setVendWeekPropRem(String vendWeekPropRem) {
	this.vendWeekPropRem = vendWeekPropRem;
}







}
