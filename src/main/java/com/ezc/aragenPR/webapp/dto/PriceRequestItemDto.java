package com.ezc.aragenPR.webapp.dto;

import java.util.Date;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;

public class PriceRequestItemDto {

	
private Integer lineNo;

private String salesOrg;

private String salesDocType;

private String customer;

private String material;

private String brand;

private String plant;

private String distChnl;

private String mainChnl;

private String subChnl;

private String custType;

private String chainCode;

private String prdType;

private String distCat;

private String division;

private String salesOffice;


@DateTimeFormat (pattern="dd/MM/yyyy")
private Date validFrom;

@DateTimeFormat (pattern="dd/MM/yyyy")
private Date validTo;

private double value;

private double mrp;

private double vprs;

private double lprice;

private double subtot;

private double marAftDis;

private double marBefDis;

private String matBrand;

private String itemWFKey;

private String errFlag;

private String errText;

private String status;

private String conditions;

private String condBefTotal;

private double condTot;

private String condDispStr;

private double trdDis;

private double varOHDisPer;

private double varOHDisVal;

private double gstPer;

private double gstVal;

private double tvc;

private double gcAmount;

private double budgetGC;




public double getVarOHDisPer() {
	return varOHDisPer;
}

public void setVarOHDisPer(double varOHDisPer) {
	this.varOHDisPer = varOHDisPer;
}

public double getVarOHDisVal() {
	return varOHDisVal;
}

public void setVarOHDisVal(double varOHDisVal) {
	this.varOHDisVal = varOHDisVal;
}

public double getGstPer() {
	return gstPer;
}

public void setGstPer(double gstPer) {
	this.gstPer = gstPer;
}

public double getGstVal() {
	return gstVal;
}

public void setGstVal(double gstVal) {
	this.gstVal = gstVal;
}

public double getTvc() {
	return tvc;
}

public void setTvc(double tvc) {
	this.tvc = tvc;
}

public double getGcAmount() {
	return gcAmount;
}

public void setGcAmount(double gcAmount) {
	this.gcAmount = gcAmount;
}

public double getBudgetGC() {
	return budgetGC;
}

public void setBudgetGC(double budgetGC) {
	this.budgetGC = budgetGC;
}

private Map<String,Double> hmCondMap=null;


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

public String getMatBrand() {
	return matBrand;
}

public void setMatBrand(String matBrand) {
	this.matBrand = matBrand;
}

public String getItemWFKey() {
	return itemWFKey;
}

public void setItemWFKey(String itemWFKey) {
	this.itemWFKey = itemWFKey;
}

public Integer getLineNo() {
	return lineNo;
}

public void setLineNo(Integer lineNo) {
	this.lineNo = lineNo;
}

public String getSalesOrg() {
	return salesOrg;
}

public void setSalesOrg(String salesOrg) {
	this.salesOrg = salesOrg;
}

public String getSalesDocType() {
	return salesDocType;
}

public void setSalesDocType(String salesDocType) {
	this.salesDocType = salesDocType;
}

public String getCustomer() {
	return customer;
}

public void setCustomer(String customer) {
	this.customer = customer;
}

public String getMaterial() {
	return material;
}

public void setMaterial(String material) {
	this.material = material;
}

public String getBrand() {
	return brand;
}

public void setBrand(String brand) {
	this.brand = brand;
}

public String getPlant() {
	return plant;
}

public void setPlant(String plant) {
	this.plant = plant;
}

public String getDistChnl() {
	return distChnl;
}

public void setDistChnl(String distChnl) {
	this.distChnl = distChnl;
}

public String getMainChnl() {
	return mainChnl;
}

public void setMainChnl(String mainChnl) {
	this.mainChnl = mainChnl;
}

public String getSubChnl() {
	return subChnl;
}

public void setSubChnl(String subChnl) {
	this.subChnl = subChnl;
}

public String getCustType() {
	return custType;
}

public void setCustType(String custType) {
	this.custType = custType;
}

public String getChainCode() {
	return chainCode;
}

public void setChainCode(String chainCode) {
	this.chainCode = chainCode;
}

public String getPrdType() {
	return prdType;
}

public void setPrdType(String prdType) {
	this.prdType = prdType;
}

public String getDistCat() {
	return distCat;
}

public void setDistCat(String distCat) {
	this.distCat = distCat;
}

public String getDivision() {
	return division;
}

public void setDivision(String division) {
	this.division = division;
}

public String getSalesOffice() {
	return salesOffice;
}

public void setSalesOffice(String salesOffice) {
	this.salesOffice = salesOffice;
}

public Date getValidFrom() {
	return validFrom;
}

public void setValidFrom(Date validFrom) {
	this.validFrom = validFrom;
}

public Date getValidTo() {
	return validTo;
}

public void setValidTo(Date validTo) {
	this.validTo = validTo;
}

public double getValue() {
	return value;
}

public void setValue(double value) {
	this.value = value;
}

public double getMrp() {
	return mrp;
}

public void setMrp(double mrp) {
	this.mrp = mrp;
}

public double getVprs() {
	return vprs;
}

public void setVprs(double vprs) {
	this.vprs = vprs;
}

public double getLprice() {
	return lprice;
}

public void setLprice(double lprice) {
	this.lprice = lprice;
}

public double getSubtot() {
	return subtot;
}

public void setSubtot(double subtot) {
	this.subtot = subtot;
}

public double getMarAftDis() {
	return marAftDis;
}

public void setMarAftDis(double marAftDis) {
	this.marAftDis = marAftDis;
}

public double getMarBefDis() {
	return marBefDis;
}

public void setMarBefDis(double marBefDis) {
	this.marBefDis = marBefDis;
}

/**
 * @return the status
 */
public String getStatus() {
	return status;
}

/**
 * @param status the status to set
 */
public void setStatus(String status) {
	this.status = status;
}

public Map<String,Double> getHmCondMap() {
	return hmCondMap;
}

public void setHmCondMap(Map<String,Double> hmCondMap) {
	this.hmCondMap = hmCondMap;
}

public String getConditions() {
	return conditions;
}

public void setConditions(String conditions) {
	this.conditions = conditions;
}

public double getCondTot() {
	return condTot;
}

public void setCondTot(double condTot) {
	this.condTot = condTot;
}

public String getCondBefTotal() {
	return condBefTotal;
}

public void setCondBefTotal(String condBefTotal) {
	this.condBefTotal = condBefTotal;
}

public String getCondDispStr() {
	return condDispStr;
}

public void setCondDispStr(String condDispStr) {
	this.condDispStr = condDispStr;
}

public double getTrdDis() {
	return trdDis;
}

public void setTrdDis(double trdDis) {
	this.trdDis = trdDis;
}



}
