package com.ezc.aragenPR.webapp.model;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.AllArgsConstructor;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@IdClass(EzcPoWeekPlanId.class)
@Table(name = "EZC_PO_WEEK_PLAN")
public class EzcPoWeekPlan {

	/*
	 * @Id
	 * 
	 * @Column(name = "EPWP_DOC_ID", nullable = false) private Integer docId;
	 */
    @Id
    @Column(name = "EPWP_WEEK", nullable = false)
    private Integer week;

    @Column(name = "EPWP_PO", length = 10)
    private String po;

    @Column(name = "EPWP_NEXT_PART")
    private String nextPart;

    @Column(name = "EPWP_CUR_STEP", length = 10)
    private String currentStep;

    @Column(name = "EPWP_STATUS", length = 10)
    private String status;

    @Column(name = "EPWP_MODIFIED_BY", length = 10)
    private String modifiedBy;

    @Temporal(TemporalType.DATE)
    @Column(name = "EPWP_MODIFIED_ON")
    private Date modifiedOn;

    @Id
    @ManyToOne
    @JoinColumn(name = "EPWP_DOC_ID", referencedColumnName = "EPPH_DOC_ID")
    private EzcPoPlanHeader poPlanHeader;
    
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "poWeekPlan") 
	@Cascade({CascadeType.ALL})
	@ToString.Exclude
    @JsonManagedReference
	private List<EzcPoPlanItems> ezcPoPlanItems;
    
}
