package com.ezc.aragenPR.webapp.dto;

import org.springframework.web.multipart.MultipartFile;

public class WeeklyPlanAmmendInput {
	private MultipartFile file;
	private String docId;
	private String week;
	private String btnType;
	
	public String getBtnType() {
		return btnType;
	}
	public void setBtnType(String btnType) {
		this.btnType = btnType;
	}
	public MultipartFile getFile() {
		return file;
	}
	public void setFile(MultipartFile file) {
		this.file = file;
	}
	public String getDocId() {
		return docId;
	}
	public void setDocId(String docId) {
		this.docId = docId;
	}
	public String getWeek() {
		return week;
	}
	public void setWeek(String week) {
		this.week = week;
	}
	
}
