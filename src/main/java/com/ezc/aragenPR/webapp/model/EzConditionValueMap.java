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
@Table(name="EZC_CONDITION_VALUE_MAP")
@IdClass(EzConditionValueMapKey.class)
@Data @AllArgsConstructor @ToString
public class EzConditionValueMap{

	@Id
	@Column(name="ECVM_CONDITION_TYPE",length=4)
	private String conditionType;
	
	@Id
	@Column(name="ECVM_BRAND",length=4)
	private String brand;
	
	@Id
	@Column(name="ECVM_DIST_CATEGORY",length=20)
	private String distCat;
	
	@Column(name="ECVM_VALUE")
	private Double value;
	
			
	public EzConditionValueMap() {
		 
		
	}
		
}
