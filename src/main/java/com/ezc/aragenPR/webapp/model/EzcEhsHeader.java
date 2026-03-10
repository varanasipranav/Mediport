package com.ezc.aragenPR.webapp.model;

//import jakarta.persistence.*;
//import lombok.Data;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.util.Date;
//
//@Entity
//@Table(name = "EZC_EHS_HEADER")
//public class EzcEhsHeader {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "EEH_DOC_ID") // Correct database column mapping
//    private Integer docId; // Correct field name in Java
//
//    @Column(name = "EEH_VENDOR")
//    private String vendor;
//
//    @Column(name = "EEH_MONTH_YEAR")
//    private String monthYear;
//
//    @Column(name = "EEH_STATUS")
//    private String status;
//
//    @Column(name = "EEH_INITIATED_DATE")
//    private Date initiatedDate;
//
//    @Column(name = "EEH_VALIDITY")
//    private Date validity;
//
//    // ✅ Getters & Setters
//    public Integer getDocId() {
//        return docId;
//    }
//
//    public void setDocId(Integer docId) {
//        this.docId = docId;
//    }
//
//    public String getVendor() {
//        return vendor;
//    }
//
//    public void setVendor(String vendor) {
//        this.vendor = vendor;
//    }
//
//    public String getMonthYear() {
//        return monthYear;
//    }
//
//    public void setMonthYear(String monthYear) {
//        this.monthYear = monthYear;
//    }
//
//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }
//
//    public Date getInitiatedDate() {
//        return initiatedDate;
//    }
//
//    public void setInitiatedDate(Date initiatedDate) {
//        this.initiatedDate = initiatedDate;
//    }
//
//    public Date getValidity() {
//        return validity;
//    }
//}

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "EZC_EHS_HEADER")
public class EzcEhsHeader {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "EEH_DOC_ID")
	private Integer docId;

	@Column(name = "EEH_INITIATED_DATE")
	private Date initiatedDate;

	@Column(name = "EEH_VENDOR")
	private String vendor;

	@Column(name = "EEH_STATUS")
	private String status;

	@Column(name = "EEH_VALIDITY", insertable = false, updatable = false)
	private Date validity;

	@Column(name = "EEH_MONTH_YEAR", insertable = false, updatable = false)
	private String monthYear;

	@Column(name = "EEH_PLANT")
	private String plant;

	@OneToMany(mappedBy = "header", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<EzEhsSafetyParameter> safetyParameters;
	
	public String getPlant() {
		return plant;
	}

	public void setPlant(String plant) {
		this.plant = plant;
	}

	public Integer getDocId() {
		return docId;
	}

	public void setDocId(Integer docId) {
		this.docId = docId;
	}

	public Date getInitiatedDate() {
		return initiatedDate;
	}

	public void setInitiatedDate(Date initiatedDate) {
		this.initiatedDate = initiatedDate;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getValidity() {
		return validity;
	}

	public void setValidity(Date validity) {
		this.validity = validity;
	}

	public String getMonthYear() {
		return monthYear;
	}

	public void setMonthYear(String monthYear) {
		this.monthYear = monthYear;
	}

	public List<EzEhsSafetyParameter> getSafetyParameters() {
		return safetyParameters;
	}

	public void setSafetyParameters(List<EzEhsSafetyParameter> safetyParameters) {
		this.safetyParameters = safetyParameters;
	}

}