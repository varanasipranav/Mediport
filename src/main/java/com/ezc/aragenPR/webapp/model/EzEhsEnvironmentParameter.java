package com.ezc.aragenPR.webapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "EZC_EHS_ENVIRONMENT_PARAMETER",
    uniqueConstraints = @UniqueConstraint(name = "uq_eeep_key_doc_id", columnNames = {"EEEP_KEY", "EEEP_DOC_ID"}),
    indexes = @Index(name = "idx_eeep_doc_id", columnList = "EEEP_DOC_ID")
)
public class EzEhsEnvironmentParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EEEP_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EEEP_DOC_ID", nullable = false)
    private EzcEhsHeader header;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EEEP_KEY", nullable = false)
    private EzEhsEnvironmentMaterial environmentMaterial;

    @Column(name = "EEEP_DESCRIPTION", length = 255)
    private String description;

    @Column(name = "EEEP_VALUE", length = 100)
    private String value;

    
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public EzcEhsHeader getHeader() {
		return header;
	}

	public void setHeader(EzcEhsHeader header) {
		this.header = header;
	}

	public EzEhsEnvironmentMaterial getEnvironmentMaterial() {
		return environmentMaterial;
	}

	public void setEnvironmentMaterial(EzEhsEnvironmentMaterial environmentMaterial) {
		this.environmentMaterial = environmentMaterial;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

    
    
    
}
