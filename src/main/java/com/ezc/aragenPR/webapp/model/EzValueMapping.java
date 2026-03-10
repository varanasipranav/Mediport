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
@Table(name="EZC_VALUE_MAPPING")
@IdClass(EzValueMappingKey.class)
@Data @AllArgsConstructor @ToString
public class EzValueMapping{

	@Id
	@Column(name="EVM_MAP_KEY",length=10)	
	private String mapKey;
	@Id
	@Column(name="EVM_VALUE1",length=20)
	private String value1;
	@Id
	@Column(name="EVM_VALUE2",length=100) 
	private String value2;

	
			
	public EzValueMapping() {
		 
		
	}
		
}
