package com.ezc.aragenPR.webapp.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Entity
@Table(name="EZC_PRICE_REQ_ITEMS")
@IdClass(EzPriceReqItemCompKey.class)
@Data @AllArgsConstructor @ToString
public class EzPriceRequestItems{

	
	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EPRI_DOC_ID")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private EzPriceRequestHeader ezPriceRequestHeader;

	
//	@Id
//	@Column(name = "EPRI_DOC_ID")
//	private Integer docId;
	
	@Id
	@Column(name = "EPRI_LINE_NO")
	private Integer lineNo;
	
	@Column(name="EPRI_SALES_ORG",length=4)
	private String salesOrg;
	
	@Column(name="EPRI_SALES_DOC_TYPE",length=4)
	private String salesDocType;
	
	@Column(name="EPRI_CUSTOMER",length=10)
	private String customer;
	
	@Column(name="EPRI_MATERIAL",length=40)
	private String material;
	
	@Column(name="EPRI_BRAND",length=3)
	private String brand;
	
	@Column(name="EPRI_PLANT",length=4)
	private String plant;
	
	@Column(name="EPRI_DIST_CHNL",length=3)
	private String distChnl;
	
	@Column(name="EPRI_MAIN_CHNL",length=3)
	private String mainChnl;

	@Column(name="EPRI_SUB_CHNL",length=3)
	private String subChnl;
	
	@Column(name="EPRI_CUST_TYPE",length=3)
	private String custType;
	
	@Column(name="EPRI_CHAIN_CODE",length=2)
	private String chainCode;
	
	@Column(name="EPRI_PRD_TYPE",length=3)
	private String prdType;
	
	@Column(name="EPRI_DIST_CAT",length=3)
	private String distCat;
	
	@Column(name="EPRI_DIVISION",length=2)
	private String division;
	
	@Column(name="EPRI_SALES_OFF",length=5)
	private String salesOffice;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "EPRI_VALID_FROM", length = 19)
	private Date validFrom;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "EPRI_VALID_TO", length = 19)
	private Date validTo;
	
	@Column(name = "EPRI_VALUE")
	private Double value;
	
	@Column(name = "EPRI_MRP")
	private Double mrp;
	
	@Column(name = "EPRI_LP")
	private Double listPrice;
	
	@Column(name = "EPRI_SUB_TOT")
	private Double subTot;
	
	@Column(name = "EPRI_COGS")
	private Double cogs;
	
	@Column(name = "EPRI_MAR_AFT_DIS")
	private Double marAftDis;
	
	@Column(name = "EPRI_MAR_BEF_DIS")
	private Double marBefDis;
	
	@Column(name = "EPRI_STATUS", length = 1)
	private String status;
	
	
	@Column(name = "EPRI_CONDITIONS", length = 100)
	private String conditions;
	
	@Column(name = "EPRI_COND_TOT")
	private Double condTot;
	
	@Column(name = "EPRI_TRD_DIS")
	private Double trdDis;
	
	@Column(name = "EPRI_VAROH_PER")
	private Double varOHDisPer;
	
	@Column(name = "EPRI_VAROH_VAL")
	private Double varOHDisVal;
	
	@Column(name = "EPRI_GST_PER")
	private Double gstPer;
	
	@Column(name = "EPRI_GST_VAL")
	private Double gstVal;
	
	@Column(name = "EPRI_TVC")
	private Double tvc;
	
	@Column(name = "EPRI_GC_AMT")
	private Double gcAmount;
	
	@Column(name = "EPRI_BUD_GC")
	private Double budgetGC;
		
	public EzPriceRequestItems() {
		 
		
	}
		
}
