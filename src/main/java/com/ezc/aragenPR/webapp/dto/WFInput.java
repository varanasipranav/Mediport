
package com.ezc.aragenPR.webapp.dto;

import java.util.List;

import com.ezc.aragenPR.webapp.model.EzValueMapping;

public class WFInput {
	
	private List<EzValueMapping> salesOrg;
	private List<EzValueMapping> division;
	private List<EzValueMapping> distChnl;
	private List<EzValueMapping> subChnl;
	private List<EzValueMapping> mainChnl;
	private List<EzValueMapping> condTypes;
	private List<EzValueMapping> brands;
	private String selSalesOrg;
	private String selDivision;
	private String selDistChnl;
	private String selSubChnl;
	private String selMainChnl;
	private String selCondTypes;
	private String selBrand;
	
	private Integer wfKey;
	
	private List<WFRow> wfRowList;
	
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
	public Integer getWfKey() {
		return wfKey;
	}
	public void setWfKey(Integer wfKey) {
		this.wfKey = wfKey;
	}
	public List<WFRow> getWfRowList() {
		return wfRowList;
	}
	public void setWfRowList(List<WFRow> wfRowList) {
		this.wfRowList = wfRowList;
	}

/**
	 * @return the brands
	 */

	public List<EzValueMapping> getBrands() {
		return brands;
	}

/**
	 * @param brands the brands to set
	 */

	public void setBrands(List<EzValueMapping> brands) {
		this.brands = brands;
	}

/**
	 * @return the selBrand
	 */

	public String getSelBrand() {
		return selBrand;
	}

/**
	 * @param selBrand the selBrand to set
	 */

	public void setSelBrand(String selBrand) {
		this.selBrand = selBrand;
	}
	
	
}

