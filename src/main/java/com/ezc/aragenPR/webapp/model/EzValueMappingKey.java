package com.ezc.aragenPR.webapp.model;

import java.io.Serializable;

public class EzValueMappingKey implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public EzValueMappingKey() {
		
	}
	private String mapKey;
	private String value1;
	private String value2;
	public String getMapKey() {
		return mapKey;
	}
	public void setMapKey(String mapKey) {
		this.mapKey = mapKey;
	}
	public String getValue1() {
		return value1;
	}
	public void setValue1(String value1) {
		this.value1 = value1;
	}
	public String getValue2() {
		return value2;
	}
	public void setValue2(String value2) {
		this.value2 = value2;
	}
	public EzValueMappingKey(String mapKey, String value1, String value2) {
		super();
		this.mapKey = mapKey;
		this.value1 = value1;
		this.value2 = value2;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mapKey == null) ? 0 : mapKey.hashCode());
		result = prime * result + ((value1 == null) ? 0 : value1.hashCode());
		result = prime * result + ((value2 == null) ? 0 : value2.hashCode());
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
		EzValueMappingKey other = (EzValueMappingKey) obj;
		if (mapKey == null) {
			if (other.mapKey != null)
				return false;
		} else if (!mapKey.equals(other.mapKey))
			return false;
		if (value1 == null) {
			if (other.value1 != null)
				return false;
		} else if (!value1.equals(other.value1))
			return false;
		if (value2 == null) {
			if (other.value2 != null)
				return false;
		} else if (!value2.equals(other.value2))
			return false;
		return true;
	}
	
	
	}
