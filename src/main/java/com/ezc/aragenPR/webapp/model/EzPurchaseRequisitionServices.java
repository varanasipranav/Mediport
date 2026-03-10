package com.ezc.aragenPR.webapp.model;


import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "EZC_PURCHASE_REQUISITION_SERVICES")
@IdClass(EzPRServiceCompKey.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class EzPurchaseRequisitionServices {

    @Id
    @Column(name = "EPRS_REQ_NUMBER")
    private String reqNumber;

    @Id
    @Column(name = "EPRS_ITEM_NO")
    private String itemNo;

    @Id
    @Column(name = "EPRS_SERVICE_LINE")
    private String servLine;

    @Column(name = "EPRS_SERVICE_NO")
    private String servNo;

    @Column(name = "EPRS_SERVICE_DESC")
    private String servDesc;

    @Column(name = "EPRS_SERVICE_QTY")
    private BigDecimal servQty;

    @Column(name = "EPRS_SERVICE_UNIT")
    private String servUnit;

    @Column(name = "EPRS_SERVICE_PRICE")
    private BigDecimal servPrice;

    @Column(name = "EPRS_NET_VALUE")
    private BigDecimal servNetPrice;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "EPRS_REQ_NUMBER", referencedColumnName = "EPRI_REQ_NUMBER", insertable = false, updatable = false),
            @JoinColumn(name = "EPRS_ITEM_NO", referencedColumnName = "EPRI_ITEM_NUMBER", insertable = false, updatable = false)
    })
    private EzPurchaseRequisitionItems item;
}

