package com.ezc.aragenPR.webapp.dto;

import java.util.List;

public class SAPReturnTable {

	private boolean sapError;
	private List<SAPReturnTableRow> sapReturnTableRowList;
	
	
	public boolean isSapError() {
		return sapError;
	}


	public void setSapError(boolean sapError) {
		this.sapError = sapError;
	}


	public List<SAPReturnTableRow> getSapReturnTableRowList() {
		return sapReturnTableRowList;
	}


	public void setSapReturnTableRowList(List<SAPReturnTableRow> sapReturnTableRowList) {
		this.sapReturnTableRowList = sapReturnTableRowList;
	}


	public SAPReturnTable() {

	}
}
