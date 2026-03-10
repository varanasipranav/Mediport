package com.ezc.aragenPR.webapp.model;

import java.util.ArrayList;
import java.util.List;

import com.ezc.aragenPR.webapp.model.EzPriceSeqMaster;

public class PriceReqInput {

	private List<EzPriceSeqMaster> priceSeqList = new ArrayList<EzPriceSeqMaster>();
	private String condType;
	private String seqId;
	private String comments;
	private String valArea;
	
	public List<EzPriceSeqMaster> getPriceSeqList() {
		return priceSeqList;
	}
	public void setPriceSeqList(List<EzPriceSeqMaster> priceSeqList) {
		this.priceSeqList = priceSeqList;
	}
	public String getCondType() {
		return condType;
	}
	public void setCondType(String condType) {
		this.condType = condType;
	}
	public String getSeqId() {
		return seqId;
	}
	public void setSeqId(String seqId) {
		this.seqId = seqId;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getValArea() {
		return valArea;
	}
	public void setValArea(String valArea) {
		this.valArea = valArea;
	}
	
	
	
}
