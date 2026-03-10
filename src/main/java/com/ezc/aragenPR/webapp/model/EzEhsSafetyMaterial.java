package com.ezc.aragenPR.webapp.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "EZC_EHS_SAFETY_MATERIAL")
public class EzEhsSafetyMaterial{

    @Id
    @Column(name = "EESM_KEY")
    private String key;

    @Column(name = "EESM_DEFAULT_VALUE")
    private String defaultValue;

    @Column(name = "EESM_DERIVED_VALUE")
    private String derivedValue;

    @Column(name = "EESM_DESCRIPTION")
    private String description;

    @Column(name = "EESM_EDITABLE")
    private Boolean editable;

    @Column(name = "FORM_INDEX")
    private Integer formIndex;

    @Column(name = "EESM_HIGH_RANGE")
    private Double highRange;

    @Column(name = "EESM_LOW_RANGE")
    private Double lowRange;

    @Column(name = "EESM_REQUIRED")
    private Boolean required;
    
    @Enumerated(EnumType.STRING)  
    @Column(name = "EESM_STATUS")  
    private Status status;  

    @Column(name = "EESM_TYPE")
    private String type;
    
    public enum Status {  
        ACTIVE, INACTIVE  
    }

    @OneToMany(mappedBy = "safetyMaterial", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EzEhsSafetyParameter> safetyParameters;

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

	public String getStatus() {
	    return status.name(); 
	}


	public void setStatus(Status status) {
		this.status = status;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<EzEhsSafetyParameter> getSafetyParameters() {
		return safetyParameters;
	}

	public void setSafetyParameters(List<EzEhsSafetyParameter> safetyParameters) {
		this.safetyParameters = safetyParameters;
	}

    
}
//package com.ezc.outsrc.webapp.model;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//
//@Entity
//@Table(name = "EZC_EHS_SAFETY_MATERIAL")
//@Getter
//@Setter
//public class EzEhsSafetyMaterial {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "EESM_KEY")
//    private Integer key;
//
//    @Column(name = "EESM_DESCRIPTION", nullable = false, columnDefinition = "TEXT")
//    private String description;
//
//    @Column(name = "EESM_TYPE", nullable = false, length = 50)
//    private String type;
//
//    @Column(name = "EESM_LOW_RANGE", nullable = false, columnDefinition = "INT DEFAULT 0")
//    private Integer lowRange;
//
//    @Column(name = "EESM_HIGH_RANGE", nullable = false)
//    private Integer highRange;
//
//    @Column(name = "EESM_DEFAULT_VALUE", nullable = false, length = 255)
//    private String defaultValue;
//
//    @Column(name = "EESM_REQUIRED", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
//    private Boolean required;
//
//    @Column(name = "EESM_DERIVED_VALUE", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
//    private Boolean derivedValue;
//
//    @Column(name = "EESM_STATUS", nullable = false)
//    @Enumerated(EnumType.STRING)
//    private Status status;
//
//    @Column(name = "EESM_EDITABLE", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
//    private Boolean editable;
//
//    @Column(name = "Form_Index", unique = true)
//    private Integer formIndex;
//
//    public enum Status {
//        ACTIVE, INACTIVE
//    }
//    
//   
//}
