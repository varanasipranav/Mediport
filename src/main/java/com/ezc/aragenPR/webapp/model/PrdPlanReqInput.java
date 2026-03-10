package com.ezc.aragenPR.webapp.model;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class PrdPlanReqInput {

	private List<EzValueMapping> vendorList;
	private List<EzValueMapping> outPlantList;
	private MultipartFile file;
	private String selVendor;
	private String selOutPlant;
	private String selMonthYr;
	private String selPurGrp;
	public List<EzValueMapping> getVendorList() {
		return vendorList;
	}
	public void setVendorList(List<EzValueMapping> vendorList) {
		this.vendorList = vendorList;
	}
	public List<EzValueMapping> getOutPlantList() {
		return outPlantList;
	}
	public void setOutPlantList(List<EzValueMapping> outPlantList) {
		this.outPlantList = outPlantList;
	}
	public MultipartFile getFile() {
		return file;
	}
	public void setFile(MultipartFile file) {
		this.file = file;
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

	

}

