package com.ezc.aragenPR.webapp.dto;

import org.springframework.format.annotation.DateTimeFormat;

public class PoListInput {
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	public java.sql.Date fromDate;
	
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	public java.sql.Date toDate;
	
	public java.sql.Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(java.sql.Date fromDate) {
		this.fromDate = fromDate;
	}

	public java.sql.Date getToDate() {
		return toDate;
	}

	public void setToDate(java.sql.Date toDate) {
		this.toDate = toDate;
	}

}
