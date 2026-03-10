package com.ezc.aragenPR.webapp.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "EZC_EHS_SUSTAINABILITY_MATERIAL")
public class EzEhsSustainabilityMaterial {

    @Id
    @Column(name = "EESUM_KEY")
    private String key;

    @Column(name = "EESUM_DEFAULT_VALUE")
    private String defaultValue;

    @Column(name = "EESUM_DERIVED_VALUE")
    private String derivedValue;

    @Column(name = "EESUM_DESCRIPTION")
    private String description;

    @Column(name = "EESUM_EDITABLE")
    private Boolean editable;

    @Column(name = "FORM_INDEX")
    private Integer formIndex;

    @Column(name = "EESUM_HIGH_RANGE")
    private Double highRange;

    @Column(name = "EESUM_LOW_RANGE")
    private Double lowRange;

    @Column(name = "EESUM_REQUIRED")
    private Boolean required;

    @Enumerated(EnumType.STRING)
    @Column(name = "EESUM_STATUS")
    private Status status;

    @Column(name = "EESUM_TYPE")
    private String type;

    @Column(name = "EESUM_IS_HEADER")
    private Boolean isHeader;

    @Column(name = "EESUM_UOM")
    private String uom;

    public enum Status {
        ACTIVE, INACTIVE
    }

    @OneToMany(mappedBy = "sustainabilityMaterial", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EzEhsSustainabilityParameter> sustainabilityParameters;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getDerivedValue() {
		return derivedValue;
	}

	public void setDerivedValue(String derivedValue) {
		this.derivedValue = derivedValue;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getEditable() {
		return editable;
	}

	public void setEditable(Boolean editable) {
		this.editable = editable;
	}

	public Integer getFormIndex() {
		return formIndex;
	}

	public void setFormIndex(Integer formIndex) {
		this.formIndex = formIndex;
	}

	public Double getHighRange() {
		return highRange;
	}

	public void setHighRange(Double highRange) {
		this.highRange = highRange;
	}

	public Double getLowRange() {
		return lowRange;
	}

	public void setLowRange(Double lowRange) {
		this.lowRange = lowRange;
	}

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Boolean getIsHeader() {
		return isHeader;
	}

	public void setIsHeader(Boolean isHeader) {
		this.isHeader = isHeader;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public List<EzEhsSustainabilityParameter> getSustainabilityParameters() {
		return sustainabilityParameters;
	}

	public void setSustainabilityParameters(List<EzEhsSustainabilityParameter> sustainabilityParameters) {
		this.sustainabilityParameters = sustainabilityParameters;
	}
    
    
}