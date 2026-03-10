package com.ezc.aragenPR.webapp.model;

import static jakarta.persistence.GenerationType.IDENTITY;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;


@Entity
@Table(name="EZC_PRICE_REQ_HEADER")
@Data @AllArgsConstructor @ToString
public class EzPriceRequestHeader{

	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	/*
	 * @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "req_seq")
	 * 
	 * @GenericGenerator( name = "req_seq", strategy =
	 * "com.ezc.vip.webapp.model.ReqSequenceGenerator", parameters = {
	 * 
	 * @Parameter(name = ReqSequenceGenerator.INCREMENT_PARAM, value = "1") })
	 */
	@Column(name = "EPRH_DOC_ID", unique = true, nullable = false,length=20)
	private String id;
	
	@Column(name = "EPRH_SEQ_ID", unique = true, nullable = false)
	private Integer seqId;

	@Column(name="EPRH_TABLE",length=5)
	private String table;
	
	@Column(name="EPRH_COND_TYPE",length=5)
	private String condType;
	
	@Column(name="ERPH_COND_DESC",length=100)
	private String condDesc;
	
	@Column(name="ERPH_VAL_AREA",length=5)
	private String valArea;
	
	@Column(name="ERPH_VAL_DESC",length=100)
	private String valDesc;
	
	@Column(name="EPRH_CREATED_BY",length=5)
	private String createdBy;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "EPRH_CREATED_ON", length = 19)
	private Date createdOn;
	
	@Column(name="EPRH_MODIFIED_BY",length=5)
	private String modifiedBy;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "EPRH_MODIFIED_ON", length = 19)
	private Date modifiedOn;
	
	@Column(name="EPRH_WF_KEY")
	private Integer wfKey;
	
	@Column(name="EPRH_WF_LEVEL")
	private Integer wfLevel;
	
	@Column(name="EPRH_STATUS",length=5)
	private String status;
	
	@Column(name="EPRH_DIST_CATEGORY",length=20)
	private String distCat;
	
	@Column(name="EPRH_WF_DIST_CHNL",length=4)
	private String wfDistChnl;
	
	@Column(name="EPRH_APPROVED_BY",length=50)
	private String approvedBy;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ezPriceRequestHeader") 
	@Cascade({CascadeType.ALL})
	@ToString.Exclude
	private List<EzPriceRequestItems> ezPriceRequestItems = new ArrayList<EzPriceRequestItems>();
		
	public EzPriceRequestHeader() {
		 
		 
	}
		
}
