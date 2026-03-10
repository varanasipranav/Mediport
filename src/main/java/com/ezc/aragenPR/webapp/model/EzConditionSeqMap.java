package com.ezc.aragenPR.webapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;


@Entity
@Table(name="EZC_CONDITION_SEQ_MAP")
@IdClass(EzConditionSeqMapKey.class)
@Data @AllArgsConstructor @ToString
public class EzConditionSeqMap{

	@Id
	@Column(name="ECSM_USER_ID",length=20)
	private String userId;
	
	@Id
	@Column(name="ECSM_CONDITION_TYPE",length=4)
	private String conditionType;
	
	
	@Id 
	@Column(name = "ECSM_SEQ_ID")
	private Integer seqId;
	
	
			
	public EzConditionSeqMap() {
		 
		
	}
		
}
