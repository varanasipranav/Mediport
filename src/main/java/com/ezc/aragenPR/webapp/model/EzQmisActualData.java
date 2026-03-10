package com.ezc.aragenPR.webapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Table(name = "ezc_qmis_actual_data", uniqueConstraints = @UniqueConstraint(
        columnNames = {"EQAD_TYPE1", "EQAD_TYPE2", "EQAD_TYPE3", "EQAD_MONTH", "EQAD_YEAR", "EQAD_VENDOR"}
))
@Data
public class EzQmisActualData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EQAD_ID")
    private Long eqadId;

    @Column(name = "EQAD_TYPE1")
    private String eqadType1;

    @Column(name = "EQAD_TYPE2")
    private String eqadType2;

    @Column(name = "EQAD_TYPE3")
    private String eqadType3;

    @Column(name = "EQAD_MONTH")
    private int eqadMonth;

    @Column(name = "EQAD_YEAR")
    private int eqadYear;

    @Column(name = "EQAD_ACTUAL_VALUE")
    private float eqadActualValue;

    @Column(name = "EQAD_VENDOR")
    private String eqadVendor;

}
