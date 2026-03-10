package com.ezc.aragenPR.webapp.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class VendorConfigFieldId implements Serializable {

    private static final long serialVersionUID = 1L; // Add this line

    @Column(name = "EVFM_CONFIG_ID")
    private Long evfmConfigId;

    @Column(name = "EFFM_FIELD_ID")
    private Long effmFieldId;

    // Constructors, equals, and hashCode
}
