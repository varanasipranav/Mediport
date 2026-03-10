package com.ezc.aragenPR.webapp.dto;

import java.util.ArrayList;
import java.util.List;

import com.ezc.aragenPR.webapp.model.EzPriceSeqMaster;

public class PriceFormDetInput {

	private EzPriceSeqMaster priceSeqMasterObj = null;
	private List<PriceRequestItemDto> priceRequestItems = new ArrayList<PriceRequestItemDto>();
	private String comments;
	private String condType;
	private String valArea;
	private boolean showMatDisc;
	private boolean showSubmit;
	private String matWarnStr;
	private String wfDistChnl;
	private String distCat;
	private boolean showSubCond;
	private List<String> subCondList=null;
	
	private String selDivision;
	private String selDistChnl;
	private String selSubChnl;
	private String selMainChnl;
	private String selBrand;
	
	
	
	
	public String getSelDivision() {
		return selDivision;
	}
	public void setSelDivision(String selDivision) {
		this.selDivision = selDivision;
	}
	public String getSelDistChnl() {
		return selDistChnl;
	}
	public void setSelDistChnl(String selDistChnl) {
		this.selDistChnl = selDistChnl;
	}
	public String getSelSubChnl() {
		return selSubChnl;
	}
	public void setSelSubChnl(String selSubChnl) {
		this.selSubChnl = selSubChnl;
	}
	public String getSelMainChnl() {
		return selMainChnl;
	}
	public void setSelMainChnl(String selMainChnl) {
		this.selMainChnl = selMainChnl;
	}
	public List<String> getSubCondList() {
		return subCondList;
	}
	public void setSubCondList(List<String> subCondList) {
		this.subCondList = subCondList;
	}
	public String getWfDistChnl() {
		return wfDistChnl;
	}
	public void setWfDistChnl(String wfDistChnl) {
		this.wfDistChnl = wfDistChnl;
	}
	public String getDistCat() {
		return distCat;
	}
	public void setDistCat(String distCat) {
		this.distCat = distCat;
	}
	public EzPriceSeqMaster getPriceSeqMasterObj() {
		return priceSeqMasterObj;
	}
	public void setPriceSeqMasterObj(EzPriceSeqMaster priceSeqMasterObj) {
		this.priceSeqMasterObj = priceSeqMasterObj;
	}
	public List<PriceRequestItemDto> getPriceRequestItems() {
		return priceRequestItems;
	}
	public void setPriceRequestItems(List<PriceRequestItemDto> priceRequestItems) {
		this.priceRequestItems = priceRequestItems;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getCondType() {
		return condType;
	}
	public void setCondType(String condType) {
		this.condType = condType;
	}
	public String getValArea() {
		return valArea;
	}
	public void setValArea(String valArea) {
		this.valArea = valArea;
	}
	public boolean isShowMatDisc() {
		return showMatDisc;
	}
	public void setShowMatDisc(boolean showMatDisc) {
		this.showMatDisc = showMatDisc;
	}
	/**
	 * @return the showSubmit
	 */
	public boolean isShowSubmit() {
		return showSubmit;
	}
	/**
	 * @param showSubmit the showSubmit to set
	 */
	public void setShowSubmit(boolean showSubmit) {
		this.showSubmit = showSubmit;
	}
	/**
	 * @return the matWarnStr
	 */
	public String getMatWarnStr() {
		return matWarnStr;
	}
	/**
	 * @param matWarnStr the matWarnStr to set
	 */
	public void setMatWarnStr(String matWarnStr) {
		this.matWarnStr = matWarnStr;
	}
	public boolean isShowSubCond() {
		return showSubCond;
	}
	public void setShowSubCond(boolean showSubCond) {
		this.showSubCond = showSubCond;
	}
	public String getSelBrand() {
		return selBrand;
	}
	public void setSelBrand(String selBrand) {
		this.selBrand = selBrand;
	}
	
	
	
}
