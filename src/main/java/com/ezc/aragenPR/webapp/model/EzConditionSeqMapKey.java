package com.ezc.aragenPR.webapp.model;

import java.io.Serializable;

public class EzConditionSeqMapKey implements Serializable{

	
	/**
	 * 
	 */
	
	private String userId;
	private String conditionType;
	private Integer seqId;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getConditionType() {
		return conditionType;
	}
	public void setConditionType(String conditionType) {
		this.conditionType = conditionType;
	}
	public Integer getSeqId() {
		return seqId;
	}
	public void setSeqId(Integer seqId) {
		this.seqId = seqId;
	}
	public EzConditionSeqMapKey() {
		super();
		// TODO Auto-generated constructor stub
	}
	public EzConditionSeqMapKey(String userId, String conditionType, Integer seqId) {
		super();
		this.userId = userId;
		this.conditionType = conditionType;
		this.seqId = seqId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((conditionType == null) ? 0 : conditionType.hashCode());
		result = prime * result + ((seqId == null) ? 0 : seqId.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
		EzConditionSeqMapKey other = (EzConditionSeqMapKey) obj;
		if (conditionType == null) {
			if (other.conditionType != null)
				return false;
		} else if (!conditionType.equals(other.conditionType))
			return false;
		if (seqId == null) {
			if (other.seqId != null)
				return false;
		} else if (!seqId.equals(other.seqId))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "EzConditionSeqMapKey [userId=" + userId + ", conditionType=" + conditionType + ", seqId=" + seqId + "]";
	}



}
