package com.ezc.aragenPR.webapp.model;

import java.util.ArrayList;
import java.util.List;

public class PrdPlanFormDetInput {

	private List<PrdPlanRequestItemDto> prdPlanRequestItems = new ArrayList<PrdPlanRequestItemDto>();
	private String selVendor;
	private String selOutPlant;
	private String selMonthYr;
	private String selPurGrp;
	private String selWeek;
	private String planId;
	private EzcPoWeekPlan poSelWeekPlan;
	private EzcPoWeekPlan poNxtWeekPlan;
	boolean showApprove=false;
	boolean showSubmit=false;
	boolean showReject=false;
	boolean postToSAP=false;
	private String status;
	private String createdBy;
	private String currentStep;
	private String nextPart;
	private String docId;
	private Integer maxLineNo;
	
	private String w1PO;
	private String w2PO;
	private String w3PO;
	private String w4PO;
	
	private String errFlag;
	
	private boolean showSubmitToVendor=false;
	private boolean showSubmitToCPT=false;
	private boolean showAcceptCPT=false;
	private boolean showAmmendCPT=false;
	
	
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
	public boolean isShowAmmendCPT() {
		return showAmmendCPT;
	}
	public void setShowAmmendCPT(boolean showAmmendCPT) {
		this.showAmmendCPT = showAmmendCPT;
	}
	public String getErrFlag() {
		return errFlag;
	}
	public void setErrFlag(String errFlag) {
		this.errFlag = errFlag;
	}
	public String getW1PO() {
		return w1PO;
	}
	public void setW1PO(String w1po) {
		w1PO = w1po;
	}
	public String getW2PO() {
		return w2PO;
	}
	public void setW2PO(String w2po) {
		w2PO = w2po;
	}
	public String getW3PO() {
		return w3PO;
	}
	public void setW3PO(String w3po) {
		w3PO = w3po;
	}
	public String getW4PO() {
		return w4PO;
	}
	public void setW4PO(String w4po) {
		w4PO = w4po;
	}
	public boolean isShowApprove() {
		return showApprove;
	}
	public void setShowApprove(boolean showApprove) {
		this.showApprove = showApprove;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getCurrentStep() {
		return currentStep;
	}
	public void setCurrentStep(String currentStep) {
		this.currentStep = currentStep;
	}
	public String getNextPart() {
		return nextPart;
	}
	public void setNextPart(String nextPart) {
		this.nextPart = nextPart;
	}
	public boolean isShowSubmit() {
		return showSubmit;
	}
	public void setShowSubmit(boolean showSubmit) {
		this.showSubmit = showSubmit;
	}
	public boolean isShowReject() {
		return showReject;
	}
	public void setShowReject(boolean showReject) {
		this.showReject = showReject;
	}
	public boolean isPostToSAP() {
		return postToSAP;
	}
	public void setPostToSAP(boolean postToSAP) {
		this.postToSAP = postToSAP;
	}
	public List<PrdPlanRequestItemDto> getPrdPlanRequestItems() {
		return prdPlanRequestItems;
	}
	public void setPrdPlanRequestItems(List<PrdPlanRequestItemDto> prdPlanRequestItems) {
		this.prdPlanRequestItems = prdPlanRequestItems;
	}
	public String getSelVendor() {  
		return selVendor;
	}
	public void setSelVendor(String selVendor) {
		this.selVendor = selVendor;
	}
	public String getSelOutPlant() {
		return selOutPlant;
	}
	public void setSelOutPlant(String selOutPlant) {
		this.selOutPlant = selOutPlant;
	}
	public String getSelMonthYr() {
		return selMonthYr;
	}
	public void setSelMonthYr(String selMonthYr) {
		this.selMonthYr = selMonthYr;
	}
	public String getSelPurGrp() {
		return selPurGrp;
	}
	public void setSelPurGrp(String selPurGrp) {
		this.selPurGrp = selPurGrp;
	}
	public String getDocId() {
		return docId;
	}
	public void setDocId(String docId) {
		this.docId = docId;
	}
	@Override
	public String toString() {
		return "PrdPlanFormDetInput [prdPlanRequestItems=" + prdPlanRequestItems + ", selVendor=" + selVendor
				+ ", selOutPlant=" + selOutPlant + ", selMonthYr=" + selMonthYr + ", selPurGrp=" + selPurGrp
				+ ", showApprove=" + showApprove + ", showSubmit=" + showSubmit + ", showReject=" + showReject
				+ ", postToSAP=" + postToSAP + ", status=" + status + ", createdBy=" + createdBy + ", currentStep="
				+ currentStep + ", nextPart=" + nextPart + ", docId=" + docId + ", w1PO=" + w1PO + ", w2PO=" + w2PO
				+ ", w3PO=" + w3PO + ", w4PO=" + w4PO + "]";
	}
	public String getSelWeek() {
		return selWeek;
	}
	public void setSelWeek(String selWeek) {
		this.selWeek = selWeek;
	}
	public EzcPoWeekPlan getPoSelWeekPlan() {
		return poSelWeekPlan;
	}
	public void setPoSelWeekPlan(EzcPoWeekPlan poSelWeekPlan) {
		this.poSelWeekPlan = poSelWeekPlan;
	}
	public Integer getMaxLineNo() {
		return maxLineNo;
	}
	public void setMaxLineNo(Integer maxLineNo) {
		this.maxLineNo = maxLineNo;
	}
	public EzcPoWeekPlan getPoNxtWeekPlan() {
		return poNxtWeekPlan;
	}
	public void setPoNxtWeekPlan(EzcPoWeekPlan poNxtWeekPlan) {
		this.poNxtWeekPlan = poNxtWeekPlan;
	}
	public String getPlanId() {
		return planId;
	}
	public void setPlanId(String planId) {
		this.planId = planId;
	}
	
	
	
		
	
}
