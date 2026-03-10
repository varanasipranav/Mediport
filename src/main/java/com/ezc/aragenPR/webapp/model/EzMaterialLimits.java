
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
@Table(name="EZC_MATERIAL_LIMITS")
@IdClass(EzMaterialLimitKey.class)
@Data @AllArgsConstructor @ToString 
public class EzMaterialLimits{



/*
	 * @Id
	 * 
	 * @Column(name="EML_MATERIAL",length=40) private String materialNo;
	 */

	
	
	@Id
	@Column(name="EML_BRAND",length=3)
	private String brand;
	
	@Id
	@Column(name="EML_DIST_CHNL",length=3)
	private String distChnl;
	
	@Id
	@Column(name="EML_LEVEL",length=2)
	private Integer level;
	
	
	@Column(name="EML_LOWER")
	private Float lower;
	
	@Column(name="EML_UPPER")
	private Float upper;
	

/*
	 * @Column(name="EML_MAIN_CHNL",length=3) private String mainChnl;
	 * 
	 * @Column(name="EML_SUB_CHNL",length=3) private String subChnl;
	 */

	public EzMaterialLimits() {
		 
		
	}
		
}

