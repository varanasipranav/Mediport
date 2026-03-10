package com.ezc.aragenPR.webapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;

@Entity
@Table(name = "EZC_VENDOR")
@Getter
@Setter
public class EzcVendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VENDOR_ID")
    private Long id;

    @Column(name = "VENDOR_NAME", length = 100, nullable = false)
    private String name;

    @Column(name = "VENDOR_CODE", length = 50, nullable = false)
    private String code;
}

