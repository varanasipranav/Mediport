package com.ezc.aragenPR.webapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.YearMonth;

@Getter
@Setter
@Entity
@Table(name = "EZC_INVOICE_BUDGET_REPORT")
public class EzcInvoiceBudgetReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plant")
    private String plant;
    
    @Column(name = "material_type")
    private String materialType;

    @Column(name = "budget_kgs")
    private BigDecimal budgetKgs;

    @Column(name = "budget_cc")
    private BigDecimal budgetCC;

    @Column(name = "budget_mix")
    private String budgetMix; // store as "50%", "8%" etc.

    @Column(name = "budget_rt")
    private BigDecimal budgetRt;

    @Column(name = "actual_kgs")
    private BigDecimal actualKgs;

    @Column(name = "actual_cc")
    private BigDecimal actualCC;

    @Column(name = "per_kg")
    private BigDecimal perKg;

    @Column(name = "volume")
    private BigDecimal volume;

    @Column(name = "vol_impact")
    private BigDecimal volImpact;

    @Column(name = "rt_impact")
    private BigDecimal rtImpact;

    @Column(name = "month")
    private Integer month;
    
    @Column(name = "year")
    private Integer year;
    
    

    // Getters and setters
    // (You can use Lombok if preferred)
}
