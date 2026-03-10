package com.ezc.aragenPR.webapp.model;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@IdClass(EzcPoPlanItemsId.class)
@Table(name = "EZC_PO_PLAN_ITEMS")
public class EzcPoPlanItems {
  /*
    @Id
    @Column(name = "EPPI_DOC_ID",  nullable = false)
    private Integer docId;
    
    @Id
    @Column(name = "EPPI_WEEK", nullable = false)
    private Integer week;
    */
    @Id
    @Column(name = "EPPI_LINE_NO", nullable = false)
    private Integer lineNo;
    
    @Column(name = "EPPI_CATEGORY", length = 40)
    private String category;
    
    @Column(name = "EPPI_WEIGHT", length = 40)
    private Double weight;
    
    @Column(name = "EPPI_NET_WEIGHT", length = 40)
    private Double netWeight;

    @Column(name = "EPPI_MATERIAL", length = 40)
    private String material;

    @Column(name = "EPPI_DESCRIPTION", length = 100)
    private String description;

    @Column(name = "EPPI_UOM", length = 3)
    private String uom;

    @Column(name = "EPPI_STOR_LOC", length = 5)
    private String storLoc;
    
    @Column(name = "EPPI_BATCH", length = 15)
    private String batch;
    
    @Column(name = "EPPI_QTY")
    private BigDecimal quantity;

    @Column(name = "EPPI_PRICE")
    private BigDecimal price;

    @Column(name = "EPPI_VALUE")
    private BigDecimal value;

    @Column(name = "EPPI_CPT_WEEK_PROP_QTY")
    private BigDecimal cptWeekPropQty;

    @Column(name = "EPPI_CPT_WEEK_PROP_REM")
    private String cptWeekPropRem;
    
    @Column(name = "EPPI_VEND_WEEK_PROP_QTY")
    private BigDecimal vendWeekPropQty;

    @Column(name = "EPPI_VEND_WEEK_PROP_REM")
    private String vendWeekPropRem;

    @Column(name = "EPPI_CPT_WEEK_ACC_QTY")
    private BigDecimal cptWeekAccQty;

    @Column(name = "EPPI_CPT_WEEK_ACC_REM")
    private String cptWeekAccRem;

    @Column(name = "EPPI_AMMEND_STS")
    private String ammendSts;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "EPPI_CREATED_ON")
    private Date createdOn;
    
    @Id
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "EPPI_DOC_ID", referencedColumnName = "EPWP_DOC_ID"),
        @JoinColumn(name = "EPPI_WEEK", referencedColumnName = "EPWP_WEEK")
    })
    @JsonBackReference
    private EzcPoWeekPlan poWeekPlan;
}
