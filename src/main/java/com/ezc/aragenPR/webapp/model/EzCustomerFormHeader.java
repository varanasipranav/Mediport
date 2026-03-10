package com.ezc.aragenPR.webapp.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "ezc_customer_form_header")
@Data @AllArgsConstructor @ToString  
public class EzCustomerFormHeader {  
	

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ECFH_DOC_ID", nullable = false)
    private Integer docId;

    @Column(name = "ECFH_CUST_NAME", length = 300)
    private String customerName;

    @Column(name = "ECFH_CREATED_BY", length = 10)
    private String createdBy;

    @Column(name = "ECFH_CREATED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    @Column(name = "ECFH_MODIFIED_BY", length = 10)
    private String modifiedBy;

    @Column(name = "ECFH_MODIFIED_ON")
    @Temporal(TemporalType.TIMESTAMP) 
    private Date modifiedOn;

    @Column(name = "ECFH_SAP_CODE", length = 10)
    private String sapCode;

    @Column(name = "ECFH_STATUS", length = 15)
    private String status;

    @Column(name = "ECFH_NEXT_PARTICIPANT", length = 100)
    private String nextParticipant;

    @Column(name = "ECFH_MASTER_TYPE", length = 10)
    private String masterType;

    @Column(name = "ECFH_NEXT_STEP", length = 50)
    private String nextStep;

    @Column(name = "ECFH_MAT_TYPE", length = 100)
    private String materialType;

    @Column(name = "ECFH_MATERIAL", length = 40)
    private String material;

    @Column(name = "ECFH_MAT_DESC", length = 100)
    private String materialDescription;

    @Column(name = "ECFH_SUP_PLANT", length = 100)
    private String supplierPlant;
    
    @Column(name = "ECFH_CURRENT_STEP", length = 100)
    private String currentStep;
    
    @Column(name = "ECFH_BUSINESS_TYPE", length = 100)
    private String businessType;
    
    @Column(name = "ECFH_TYPE_MATERIAL", length = 50)
    private String typeOfMaterial;
    
    @Column(name = "ECFH_COMMENTS", length = 2000)
    private String comments;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "ezCustomerFormHeader") 
	@Cascade({CascadeType.ALL})
	@ToString.Exclude
	private List<EzCustomerFormDetails> ezCustomerFormDetails  = new ArrayList<EzCustomerFormDetails>();
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "ezCustomerFormHeader") 
   	@Cascade({CascadeType.ALL})
   	@ToString.Exclude
   	private List<EzPpapQuestionnaire> ezQuestionnaire  = new ArrayList<EzPpapQuestionnaire>(); 




	public EzCustomerFormHeader() { 
		// TODO Auto-generated constructor stub
	}
}
