package com.ezc.aragenPR.webapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "EZC_EHS_INITIATIVE")
public class EzcEhsInitiative {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EEI_ID")
    private Integer id;

    @Column(name = "EEI_INITIATIVE")
    private String initiative;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EEI_DOC_ID", nullable = false)
    private EzcEhsHeader header;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getInitiative() {
        return initiative;
    }

    public void setInitiative(String initiative) {
        this.initiative = initiative;
    }

    public EzcEhsHeader getHeader() {
        return header;
    }

    public void setHeader(EzcEhsHeader header) {
        this.header = header;
    }
}

