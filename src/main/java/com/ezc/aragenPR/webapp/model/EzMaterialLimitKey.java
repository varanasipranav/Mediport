package com.ezc.aragenPR.webapp.model;

import java.io.Serializable;
import java.util.Objects;

public class EzMaterialLimitKey implements Serializable{

	
	public EzMaterialLimitKey() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String brand;
	private String distChnl;
	private Integer level ;
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getDistChnl() {
		return distChnl;
	}
	public void setDistChnl(String distChnl) {
		this.distChnl = distChnl;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	@Override
	public int hashCode() {
		return Objects.hash(brand, distChnl, level);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EzMaterialLimitKey other = (EzMaterialLimitKey) obj;
		return Objects.equals(brand, other.brand) && Objects.equals(distChnl, other.distChnl)
				&& Objects.equals(level, other.level);
	}
	@Override
	public String toString() {
		return "EzMaterialLimitKey [brand=" + brand + ", distChnl=" + distChnl + ", level=" + level + "]";
	}
	public EzMaterialLimitKey(String brand, String distChnl, Integer level) {
		super();
		this.brand = brand;
		this.distChnl = distChnl;
		this.level = level;
	}
		
	
	

}
