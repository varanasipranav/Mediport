package com.ezc.aragenPR.webapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name="EZC_MATERIAL_MASTER")
@Data @AllArgsConstructor @ToString @NoArgsConstructor
public class EzMaterialMaster { 
	
	@Id
	@Column(name = "EMM_MAT_NO",length=40, unique = true, nullable = false)
	private String material;
	
	@Column(name = "EMM_MAT_DESC",length=40)
	private String description;
	   
	}