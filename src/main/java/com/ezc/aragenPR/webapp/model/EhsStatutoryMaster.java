package com.ezc.aragenPR.webapp.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "ehs_statutory_master")
public class EhsStatutoryMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "requirement", length = 255)
    private String requirement;

    @Column(name = "plant_name", length = 100)
    private String plantName;

    @Column(name = "is_mandatory")
    private Boolean isMandatory;

    @Column(name = "is_valid")
    private Boolean isValid;

    @Column(name = "valid_till")
    @Temporal(TemporalType.DATE)
    private LocalDate validTill;

    @Column(name = "valid_from")
    @Temporal(TemporalType.DATE)
    private LocalDate validFrom;

    @Column(name = "vendor", length = 20)
    private String vendor;
    
    @Column(name = "DOCUMENT_PATH")
    private String documentPath;
    
    @Column(name = "financial_year_id")
    private int financialYear;

    // --- Getters and Setters ---
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public Boolean getIsMandatory() {
        return isMandatory;
    }

    public void setIsMandatory(Boolean isRequired) {
        this.isMandatory = isRequired;
    }

    public Boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }

    public LocalDate getValidTill() {
        return validTill;
    }

    public void setValidTill(LocalDate validTill) {
        this.validTill = validTill;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

	public String getDocumentPath() {
		return documentPath;
	}

	public void setDocumentPath(String documentPath) {
		this.documentPath = documentPath;
	}

	public int getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(int financialYear) {
		this.financialYear = financialYear;
	}
	
    
}
