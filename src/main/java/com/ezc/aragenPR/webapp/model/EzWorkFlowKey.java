/*
package com.ezc.aragenPR.webapp.model;

import static jakarta.persistence.GenerationType.IDENTITY;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;


@Entity
@Table(name="EZC_WORKFLOW_KEY")
@Data @AllArgsConstructor @ToString
public class EzWorkFlowKey{

	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "EWK_KEY", unique = true, nullable = false)
	private Integer wfKey;
	
	
	@Column(name="EWK_SALES_ORG",length=5)
	private String salesOrg;
	
	@Column(name="EWK_DIST_CHNL",length=3)
	private String distChnl;
	
	@Column(name="EWK_MAIN_CHNL",length=3)
	private String mainChnl;
	
	@Column(name="EWK_SUB_CHNL",length=3)
	private String subChnl;
	
	@Column(name="EWK_DIVISION",length=3)
	private String division;
	
	@Column(name="EWK_COND_TYPE",length=4)
	private String condType;
	
	@Column(name="EWK_BRAND",length=3)
	private String brand;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ezWorkFlowKey") 
	@Cascade({CascadeType.ALL})
	@ToString.Exclude
	private List<EzWorkFlowMapping> ezWorkFlowMapping = new ArrayList<EzWorkFlowMapping>();
		
	public EzWorkFlowKey() {
		 
		
	}
		
}
*/
