package com.ezc.aragenPR.webapp.model;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@IdClass(EzcPoPlanItemSummryId.class)
@Table(name = "EZC_PO_PLAN_ITEM_SUMMARY")
public class EzcPoPlanItemSummry {
    
	@Id
	@ManyToOne
	@JoinColumn(name = "EPPIS_DOC_ID", referencedColumnName = "EPPH_DOC_ID")
	private EzcPoPlanHeader poPlanHeader;

    
    @Id
    @Column(name = "EPPIS_LINE_NO", nullable = false)
    private Integer lineNo;

    @Column(name = "EPPIS_MATERIAL", length = 40)
    private String material;

    @Column(name = "EPPIS_DESCRIPTION", length = 100)
    private String matDesc;

    @Column(name = "EPPIS_PROP_QTY")
    private BigDecimal propQty; 

    @Column(name = "EPPIS_VEND_PROP_QTY")
    private BigDecimal vendPropQty;

    @Column(name = "EPPIS_ACC_QTY", length = 10)
    private BigDecimal acceptedQty;

    @Column(name = "EPPIS_VEND_PROP_REM")
    private String vendPropRemarks;
    
    @Column(name = "EPPIS_CPT_ACCPT_REM")
    private String cptAccRemarks;    
    
    @Column(name = "EPPIS_NET_WEIGHT", length = 10)
    private Double netWt;

    
}
