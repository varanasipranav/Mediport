package com.ezc.aragenPR.webapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "ezc_qmis_form_template")
@Data
public class EzcQmisFormTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EQFT_ID")
    private Integer eqftId;

    @Column(name = "EQFT_VENDOR")
    private String eqftVendor;

    @Column(name = "EQFT_MATERIAL_TYPE")
    private String eqftMaterialType;

    @Column(name = "EQFT_UOM")
    private String eqftUom;

    @Column(name = "EQFT_TYPE")
    private String eqftType;

    @Column(name = "EQFT_QMIS_TYPE")
    private String eqftQmisType;

    @Column(name = "EQFT_ATTACHMENT_MANDATORY")
    private boolean eqftAttachmentMandatory;
}
