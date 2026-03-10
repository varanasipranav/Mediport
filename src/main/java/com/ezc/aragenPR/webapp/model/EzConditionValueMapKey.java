package com.ezc.aragenPR.webapp.model;

import java.io.Serializable;
import java.util.Objects;

public class EzConditionValueMapKey implements Serializable{

	
	/**
	 * 
	 */
	
	
	private String conditionType;
	private String brand;
	private String distCat;
	
	public EzConditionValueMapKey() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getConditionType() {
		return conditionType;
	}
	public void setConditionType(String conditionType) {
		this.conditionType = conditionType;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getDistCat() {
		return distCat;
	}
	public void setDistCat(String distCat) {
		this.distCat = distCat;
	}
	@Override
	public int hashCode() {
		return Objects.hash(brand, conditionType, distCat);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EzConditionValueMapKey other = (EzConditionValueMapKey) obj;
		return Objects.equals(brand, other.brand) && Objects.equals(conditionType, other.conditionType)
				&& Objects.equals(distCat, other.distCat);
	}
	public EzConditionValueMapKey(String conditionType, String brand, String distCat) {
		super();
		this.conditionType = conditionType;
		this.brand = brand;
		this.distCat = distCat;
	}
	@Override
	public String toString() {
		return "EzConditionValueMapKey [conditionType=" + conditionType + ", brand=" + brand + ", distCat=" + distCat
				+ "]";
	}

	

}
