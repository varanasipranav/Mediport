package com.ezc.aragenPR.webapp.model;
import static jakarta.persistence.GenerationType.IDENTITY;

import java.util.Date;
import java.util.List;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "EZC_PO_PLAN_HEADER")
public class EzcPoPlanHeader {

	@Id
	@GeneratedValue(strategy = IDENTITY)
    @Column(name = "EPPH_DOC_ID", nullable = false)
    private Integer docId;

    @Column(name = "EPPH_MONTH_YR", length = 8, nullable = false)
    private String monthYear;

    @Column(name = "EPPH_SUPPLIER", length = 30)
    private String supplier;

    @Column(name = "EPPH_CO_CODE", length = 5)
    private String companyCode;

    @Column(name = "EPPH_PUR_ORG", length = 5)
    private String purchasingOrg;

    @Column(name = "EPPH_PUR_GRP", length = 5)
    private String purchasingGroup;

    @Column(name = "EPPH_OUT_LOC", length = 5)
    private String outSrcLoc;
    
    @Column(name = "EPPH_PYM_TERM", length = 5)
    private String paymentTerm;

    @Column(name = "EPPH_INC_TERM", length = 5)
    private String incoterm;

    @Column(name = "EPPH_NEXT_PART")
    private String nextPart;

    @Column(name = "EPPH_CUR_STEP", length = 10)
    private String currentStep;

    @Column(name = "EPPH_STATUS", length = 10)
    private String status;

    @Column(name = "EPPH_CREATED_BY", length = 10)
    private String createdBy;
 
    @Temporal(TemporalType.DATE)
    @Column(name = "EPPH_CREATED_ON")
    private Date createdOn;

    @Column(name = "EPPH_MODIFIED_BY", length = 10)
    private String modifiedBy;

    @Temporal(TemporalType.DATE)
    @Column(name = "EPPH_MODIFIED_ON")
    private Date modifiedOn;

    @Column(name = "EPPH_PLAN_ID", length = 36)
    private String planId;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "poPlanHeader") 
	@Cascade({CascadeType.ALL})
	@ToString.Exclude
	private List<EzcPoWeekPlan> ezcPoWeekPlan;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "poPlanHeader") 
	@Cascade({CascadeType.ALL})
	@ToString.Exclude
	private List<EzcPoPlanItemSummry> ezcPoPlanItemSummry;
    
}
