package com.ezc.aragenPR.webapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "EZC_EHS_FORM_FIELD_MASTER")
public class EzcEhsFormFieldMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EFFM_FIELD_ID")
    private Long fieldId;

    @Column(name = "EFFM_SERIAL_NO")
    private Integer serialNo;

    @Column(name = "EFFM_PARTICULARS")
    private String particulars;

    @Column(name = "EFFM_CONSENT_CONDITIONS")
    private String consentConditions;

    @Column(name = "EFFM_UOM")
    private String uom;

    @Column(name = "EFFM_ATTACHMENT")
    private String attachment;

    @Column(name = "EFFM_NORM_CHAR")
    private String normChar;

    @Column(name = "EFFM_NORMS")
    private String norms;

    @Column(name = "EFFM_ACTUAL")
    private String actual;

    @Column(name = "EFFM_STATUS")
    private String status;

    @Column(name = "EFFM_REMARKS")
    private String remarks;
    
    @Column(name = "EFFM_IS_HEADER")
    private Boolean isHeader;
    
    @Column(name = "EFFM_IS_MANDATORY")
    private Boolean isMandatory;
    
    @Column(name = "EFFM_INPUT_TYPE")
    private String inputType;

    // Getters and Setters
}
