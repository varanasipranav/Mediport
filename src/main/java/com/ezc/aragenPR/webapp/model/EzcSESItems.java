package com.ezc.aragenPR.webapp.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "EZC_SES_ITEMS")
@IdClass(EzcSESItemKey.class)
public class EzcSESItems {

    @Id
    @Column(name = "ESI_LINE_NUMBER")
    private Integer lineNumber;

    @Column(name = "ESI_SERVICE_NUMBER", length = 40)
    private String serviceNumber;

    @Column(name = "ESI_SHORT_TEXT", length = 255)
    private String shortText;

    @Column(name = "ESI_QUANTITY")
    private Double quantity;

    @Column(name = "ESI_UNIT", length = 10)
    private String unit;

    @Column(name = "ESI_GROSS_PRICE")
    private Double grossPrice;

    @Column(name = "ESI_PERIOD_FROM")
    private LocalDate periodFrom;

    @Column(name = "ESI_PERIOD_TO")
    private LocalDate periodTo;

    @Column(name = "ESI_COST_CENTER", length = 20)
    private String costCenter;

    @Column(name = "ESI_ORDER", length = 20)
    private String order;

    @Column(name = "ESI_GL_ACCT", length = 20)
    private String glAccount;

    @Column(name = "ESI_TAX_CODE", length = 5)
    private String taxCode;


    @Id
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ESI_SES_NUMBER", referencedColumnName = "ESH_SES_NUMBER")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private EzcSESHeader header;
}
