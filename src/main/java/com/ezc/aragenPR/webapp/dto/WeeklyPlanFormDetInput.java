package com.ezc.aragenPR.webapp.dto;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import com.ezc.aragenPR.webapp.model.EzcPoPlanItems;
import com.ezc.aragenPR.webapp.model.EzcPoWeekPlan;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class WeeklyPlanFormDetInput {

	private String docId;
	private String monthYear;
	private String supplier;
	private String purchasingGroup;
	private String outSrcLoc;
	private String po;
	private String nextPart;
	private String currentStep;
	private String status;
	private String week;
	
	private boolean showSubmitToVendor=false;
	private boolean showSubmitToCPT=false;
	private boolean showAcceptCPT=false;
	private boolean showAmmendCPT=false;

	HashMap<Integer, BigDecimal> monTotQty=new HashMap<Integer, BigDecimal>(); 
 
	
	public HashMap<Integer, BigDecimal> getMonTotQty() {
		return monTotQty;
	}


	public void setMonTotQty(HashMap<Integer, BigDecimal> monTotQty) {
		this.monTotQty = monTotQty;
	}


	public boolean isShowSubmitToVendor() {
		return showSubmitToVendor;
	}


	public void setShowSubmitToVendor(boolean showSubmitToVendor) {
		this.showSubmitToVendor = showSubmitToVendor;
	}


	public boolean isShowSubmitToCPT() {
		return showSubmitToCPT;
	}


	public void setShowSubmitToCPT(boolean showSubmitToCPT) {
		this.showSubmitToCPT = showSubmitToCPT;
	}


	public boolean isShowAcceptCPT() {
		return showAcceptCPT;
	}


	public void setShowAcceptCPT(boolean showAcceptCPT) {
		this.showAcceptCPT = showAcceptCPT;
	}


	private List<EzcPoPlanItems> ezcPoPlanItems;
	
	
	
	public String getPo() {
		return po;
	}


	public void setPo(String po) {
		this.po = po;
	}


	public String getNextPart() {
		return nextPart;
	}


	public void setNextPart(String nextPart) {
		this.nextPart = nextPart;
	}


	public String getCurrentStep() {
		return currentStep;
	}


	public void setCurrentStep(String currentStep) {
		this.currentStep = currentStep;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public String getWeek() {
		return week;
	}


	public void setWeek(String week) {
		this.week = week;
	}


	public List<EzcPoPlanItems> getEzcPoPlanItems() {
		return ezcPoPlanItems;
	}


	public void setEzcPoPlanItems(List<EzcPoPlanItems> ezcPoPlanItems) {
		this.ezcPoPlanItems = ezcPoPlanItems;
	}





	


	public String getDocId() {
		return docId;
	}


	public void setDocId(String docId) {
		this.docId = docId;
	}


	public String getMonthYear() {
		return monthYear;
	}


	public void setMonthYear(String monthYear) {
		this.monthYear = monthYear;
	}


	public String getSupplier() {
		return supplier;
	}


	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}


	public String getPurchasingGroup() {
		return purchasingGroup;
	}


	public void setPurchasingGroup(String purchasingGroup) {
		this.purchasingGroup = purchasingGroup;
	}


	public String getOutSrcLoc() {
		return outSrcLoc;
	}


	public void setOutSrcLoc(String outSrcLoc) {
		this.outSrcLoc = outSrcLoc;
	}


	public boolean isShowAmmendCPT() {
		return showAmmendCPT;
	}


	public void setShowAmmendCPT(boolean showAmmendCPT) {
		this.showAmmendCPT = showAmmendCPT;
	}


	
}
