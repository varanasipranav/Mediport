package com.ezc.aragenPR.webapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "EZC_EHS_VENDOR_CONFIG_FIELDS")
public class EzcEhsVendorConfigField {

    @EmbeddedId
    private VendorConfigFieldId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("evfmConfigId")
    @JoinColumn(name = "EVFM_CONFIG_ID")
    private EzcEhsVendorFormConfig config;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("effmFieldId")
    @JoinColumn(name = "EFFM_FIELD_ID")
    private EzcEhsFormFieldMaster field;
    
    @Column(name="EFFM_MONTH_YEAR")
    private String monthYear;

    // Getters and setters
}
