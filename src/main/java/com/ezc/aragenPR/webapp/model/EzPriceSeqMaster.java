package com.ezc.aragenPR.webapp.model;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;


@Entity
@Table(name="EZC_PRICE_SEQ_MASTER")
@Data
public class EzPriceSeqMaster{

	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "EPSM_ID", unique = true, nullable = false)
	private Integer id;
	

	@Column(name="EPSM_DESCRIPTION",length=100)
	private String description;
	
	@Column(name="EPSM_TABLE",length=5)
	private String table;
	
	@Column(name="EPSM_SALES_ORG",length=1)
	private String salesOrg;
	
	@Column(name="EPSM_SALES_DOC_TYPE",length=1)
	private String salesDocType;
	
	@Column(name="EPSM_CUSTOMER",length=1)
	private String customer;
	
	@Column(name="EPSM_MATERIAL",length=1)
	private String material;
	
	@Column(name="EPSM_BRAND",length=1)
	private String brand;
	
	@Column(name="EPSM_PLANT",length=1)
	private String plant;
	
	@Column(name="EPSM_DIST_CHNL",length=1)
	private String distChnl;
	
	@Column(name="EPSM_MAIN_CHNL",length=1)
	private String mainChnl;

	@Column(name="EPSM_SUB_CHNL",length=1)
	private String subChnl;
	
	@Column(name="EPSM_CUST_TYPE",length=1)
	private String custType;
	
	@Column(name="EPSM_CHAIN_CODE",length=1)
	private String chainCode;
	
	@Column(name="EPSM_PRD_TYPE",length=1)
	private String prdType;
	
	@Column(name="EPSM_DIST_CAT",length=1)
	private String distCat;
	
	@Column(name="EPSM_DIVISION",length=1)
	private String division;
	
	@Column(name="EPSM_SALES_OFF",length=1)
	private String salesOffice;
		
	public EzPriceSeqMaster() {
		 
		
	}
		
}
