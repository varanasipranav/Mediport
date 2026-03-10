
package com.ezc.aragenPR.webapp.dto;

import java.util.ArrayList;
import java.util.List;

import com.ezc.aragenPR.webapp.model.EzComments;
import com.ezc.aragenPR.webapp.model.EzPriceRequestHeader;
import com.ezc.aragenPR.webapp.model.EzPriceSeqMaster;

public class PriceFormViewDet {

	private EzPriceRequestHeader reqHeader=new EzPriceRequestHeader();
	private EzPriceSeqMaster priceSeqMaster = new EzPriceSeqMaster();
	private List<EzComments> commentsList = new ArrayList<EzComments>();
	List<PriceRequestItemDto> pricReqItemList=new ArrayList<PriceRequestItemDto>(); 
	private String createdBy="";
	private String comments="";
	boolean showSubmit;
	boolean showPost;
	boolean showMatDisc;
	boolean isOffline;
	boolean showSubCond;
	
	public boolean isShowSubCond() {
		return showSubCond;
	}
	public void setShowSubCond(boolean showSubCond) {
		this.showSubCond = showSubCond;
	}
	public boolean isShowPost() {
		return showPost;
	}
	public void setShowPost(boolean showPost) {
		this.showPost = showPost;
	}
	public boolean isShowSubmit() {
		return showSubmit;
	}
	public void setShowSubmit(boolean showSubmit) {
		this.showSubmit = showSubmit;
	}
	public EzPriceRequestHeader getReqHeader() {
		return reqHeader;
	}
	public void setReqHeader(EzPriceRequestHeader reqHeader) {
		this.reqHeader = reqHeader;
	}
	public EzPriceSeqMaster getPriceSeqMaster() {
		return priceSeqMaster;
	}
	public void setPriceSeqMaster(EzPriceSeqMaster priceSeqMaster) {
		this.priceSeqMaster = priceSeqMaster;
	}
	public List<EzComments> getCommentsList() {
		return commentsList;
	}
	public void setCommentsList(List<EzComments> commentsList) {
		this.commentsList = commentsList;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public List<PriceRequestItemDto> getPricReqItemList() {
		return pricReqItemList;
	}
	public void setPricReqItemList(List<PriceRequestItemDto> pricReqItemList) {
		this.pricReqItemList = pricReqItemList;
	}
	public boolean isShowMatDisc() {
		return showMatDisc;
	}
	public void setShowMatDisc(boolean showMatDisc) {
		this.showMatDisc = showMatDisc;
	}
	public boolean isOffline() {
		return isOffline;
	}
	public void setOffline(boolean isOffline) {
		this.isOffline = isOffline;
	}

/**
	 * @return the createdBy
	 *//*

	public String getCreatedBy() {
		return createdBy;
	}
	*/
/**
	 * @param createdBy the createdBy to set
	 */

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	
	
	
}

