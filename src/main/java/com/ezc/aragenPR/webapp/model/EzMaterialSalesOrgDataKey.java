package com.ezc.aragenPR.webapp.model;

import java.io.Serializable;

public class EzMaterialSalesOrgDataKey implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String materialNo;
	private String salesOrg;
	private String distChnl;
	public String getMaterialNo() {
		return materialNo;
	}
	public void setMaterialNo(String materialNo) {
		this.materialNo = materialNo;
	}
	public String getSalesOrg() {
		return salesOrg;
	}
	public void setSalesOrg(String salesOrg) {
		this.salesOrg = salesOrg;
	}
	public String getDistChnl() {
		return distChnl;
	}
	public void setDistChnl(String distChnl) {
		this.distChnl = distChnl;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((distChnl == null) ? 0 : distChnl.hashCode());
		result = prime * result + ((materialNo == null) ? 0 : materialNo.hashCode());
		result = prime * result + ((salesOrg == null) ? 0 : salesOrg.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EzMaterialSalesOrgDataKey other = (EzMaterialSalesOrgDataKey) obj;
		if (distChnl == null) {
			if (other.distChnl != null)
				return false;
		} else if (!distChnl.equals(other.distChnl))
			return false;
		if (materialNo == null) {
			if (other.materialNo != null)
				return false;
		} else if (!materialNo.equals(other.materialNo))
			return false;
		if (salesOrg == null) {
			if (other.salesOrg != null)
				return false;
		} else if (!salesOrg.equals(other.salesOrg))
			return false;
		return true;
	}
	public EzMaterialSalesOrgDataKey(String materialNo, String salesOrg, String distChnl) {
		super();
		this.materialNo = materialNo;
		this.salesOrg = salesOrg;
		this.distChnl = distChnl;
	}
	public EzMaterialSalesOrgDataKey() {
		super();
		// TODO Auto-generated constructor stub
	}
		
	}
