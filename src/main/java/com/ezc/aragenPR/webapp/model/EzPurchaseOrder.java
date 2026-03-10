package com.ezc.aragenPR.webapp.model;

import jakarta.persistence.*;

import java.util.Date;
import java.time.LocalDate;

@Entity
@Table(name = "EZC_PURCHASE_ORDERS")
public class EzPurchaseOrder {

    @Id
    @Column(name = "EPO_PO_NO", length = 10, nullable = false)
    private String poNo;

    @Column(name = "EPO_VENDOR", length = 10, nullable = false)
    private String userId;

    @Temporal(TemporalType.DATE)
    @Column(name = "EPO_DOC_DATE")
    private Date docDate;

    @Column(name = "EPO_CREATED_ON")
    private LocalDate createdOn;

    @Column(name = "EPO_PUR_ORG", length = 4)
    private String purOrg;

    @Column(name = "EPO_CO_CODE", length = 4)
    private String coCode;

    @Column(name = "EPO_PUR_GRP", length = 4)
    private String purGrp;

    @Column(name = "EPO_PLANT", length = 4)
    private String plant;

    @Column(name = "EPO_STATUS", length = 4)
    private String status;

    // Getters and Setters
    public String getPoNo() { return poNo; }
    public void setPoNo(String poNo) { this.poNo = poNo; }
    
    public String getVendor() { return userId; }
    public void setVendor(String userId) { this.userId = userId; }
    
    public Date getDocDate() { return docDate; }
    public void setDocDate(Date docDate) { this.docDate = docDate; }
    
    public LocalDate getCreatedOn() { return createdOn; }
    public void setCreatedOn(LocalDate createdOn) { this.createdOn = createdOn; }
    
    public String getPurOrg() { return purOrg; }
    public void setPurOrg(String purOrg) { this.purOrg = purOrg; }
    
    public String getCoCode() { return coCode; }
    public void setCoCode(String coCode) { this.coCode = coCode; }
    
    public String getPurGrp() { return purGrp; }
    public void setPurGrp(String purGrp) { this.purGrp = purGrp; }
    
    public String getPlant() { return plant; }
    public void setPlant(String plant) { this.plant = plant; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

// Created model, getter, setter. Added types and name for table and table column.