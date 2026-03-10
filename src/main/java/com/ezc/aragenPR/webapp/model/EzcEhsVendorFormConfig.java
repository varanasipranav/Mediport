package com.ezc.aragenPR.webapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter

@Entity
@Table(name = "EZC_EHS_VENDOR_FORM_CONFIG")
public class EzcEhsVendorFormConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EVFM_CONFIG_ID")
    private Long evfmConfigId;

    @Column(name = "EVFM_VENDOR_ID")
    private String evfmVendorId;
    
    @Column(name = "CONFIG_MONTH_YEAR")
    private String configMonthYear; // Format: MM/YYYY, like 04/2025

    @ManyToMany
    @JoinTable(
        name = "EZC_EHS_VENDOR_CONFIG_FIELDS",
        joinColumns = @JoinColumn(name = "EVFM_CONFIG_ID"),
        inverseJoinColumns = @JoinColumn(name = "EFFM_FIELD_ID")
    )
    private List<EzcEhsFormFieldMaster> fields;

    // Getters and Setters
}
