package com.ezc.aragenPR.webapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Entity
@Table(name="EZC_MATERIAL_SALES_DATA")
@IdClass(EzMaterialSalesOrgDataKey.class)
@Data @AllArgsConstructor @ToString 
public class EzMaterialSalesOrgData{


	@Id
	@Column(name="EMSD_MAT_NO",length=40)
	private String materialNo;
	@Id
	@Column(name="EMSD_SALES_ORG",length=4)
	private String salesOrg;
	@Id
	@Column(name="EMSD_DIST_CHNL",length=2)
	private String distChnl;
	 
	@Column(name="EMSD_BRAND",length=3)
	private String brand;
	
	
			
	public EzMaterialSalesOrgData() {
		 
		
	}
		
}
