package com.ezc.aragenPR.webapp.model;

import java.io.Serializable;
import java.util.Objects;

public class EzCustomerFormDetailsKey implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private  EzCustomerFormHeader ezCustomerFormHeader;
    private String field;
    private String tabName;

    // Default constructor
    public EzCustomerFormDetailsKey() {}

	public EzCustomerFormHeader getEzCustomerFormHeader() {
		return ezCustomerFormHeader;
	}

	public void setEzCustomerFormHeader(EzCustomerFormHeader ezCustomerFormHeader) {
		this.ezCustomerFormHeader = ezCustomerFormHeader;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	
	public String getTabName() {
		return tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

 
}
