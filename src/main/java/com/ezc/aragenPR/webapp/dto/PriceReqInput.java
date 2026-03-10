
package com.ezc.aragenPR.webapp.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ezc.aragenPR.webapp.model.EzPriceSeqMaster;
import com.ezc.aragenPR.webapp.model.EzValueMapping;

public class PriceReqInput {

	private List<EzPriceSeqMaster> priceSeqList = new ArrayList<EzPriceSeqMaster>();
	private String condType;
	private String seqId;
	private String comments;
	private String valArea;

	private List<EzValueMapping> salesOrg;
	private List<EzValueMapping> division;
	private List<EzValueMapping> distChnl;
	private List<EzValueMapping> subChnl;
	private List<EzValueMapping> mainChnl;
	private List<EzValueMapping> condTypes;
	private List<EzValueMapping> brands;
	private List<EzValueMapping> distCat;
	private String selSalesOrg;
	public List<EzValueMapping> getBrands() {
		return brands;
	}
	public void setBrands(List<EzValueMapping> brands) {
		this.brands = brands;
	}
	public List<EzValueMapping> getDistCat() {
		return distCat;
	}
	public void setDistCat(List<EzValueMapping> distCat) {
		this.distCat = distCat;
	}
	private String selDivision;
	private String selDistChnl;
	private String selSubChnl;
	private String selMainChnl;
	private String selCondTypes;
	private String selSequence;
	private String selBrand;
	private String selDistCat;
    private MultipartFile file;

	
	public String getSelBrand() {
		return selBrand;
	}
	public void setSelBrand(String selBrand) {
		this.selBrand = selBrand;
	}
	public String getSelDistCat() {
		return selDistCat;
	}
	public void setSelDistCat(String selDistCat) {
		this.selDistCat = selDistCat;
	}
	private boolean showFormDet;
	
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
	
	public List<EzValueMapping> getSalesOrg() {
		return salesOrg;
	}
	public void setSalesOrg(List<EzValueMapping> salesOrg) {
		this.salesOrg = salesOrg;
	}
	public List<EzValueMapping> getDivision() {
		return division;
	}
	public void setDivision(List<EzValueMapping> division) {
		this.division = division;
	}
	public List<EzValueMapping> getDistChnl() {
		return distChnl;
	}
	public void setDistChnl(List<EzValueMapping> distChnl) {
		this.distChnl = distChnl;
	}
	public List<EzValueMapping> getSubChnl() {
		return subChnl;
	}
	public void setSubChnl(List<EzValueMapping> subChnl) {
		this.subChnl = subChnl;
	}
	public List<EzValueMapping> getMainChnl() {
		return mainChnl;
	}
	public void setMainChnl(List<EzValueMapping> mainChnl) {
		this.mainChnl = mainChnl;
	}
	public List<EzValueMapping> getCondTypes() {
		return condTypes;
	}
	public void setCondTypes(List<EzValueMapping> condTypes) {
		this.condTypes = condTypes;
	}
	public String getSelSalesOrg() {
		return selSalesOrg;
	}
	public void setSelSalesOrg(String selSalesOrg) {
		this.selSalesOrg = selSalesOrg;
	}
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
	public String getSelCondTypes() {
		return selCondTypes;
	}
	public void setSelCondTypes(String selCondTypes) {
		this.selCondTypes = selCondTypes;
	}
	public boolean isShowFormDet() {
		return showFormDet;
	}
	public void setShowFormDet(boolean showFormDet) {
		this.showFormDet = showFormDet;
	}

/**
	 * @return the selSequence
	 */

	public String getSelSequence() {
		return selSequence;
	}

/**
	 * @param selSequence the selSequence to set
	 */

	public void setSelSequence(String selSequence) {
		this.selSequence = selSequence;
	}

/**
	 * @return the file
	 */

	public MultipartFile getFile() {
		return file;
	}

/**
	 * @param file the file to set
	 */

	public void setFile(MultipartFile file) {
		this.file = file;
	}
	
	
	
}

