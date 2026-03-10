package com.ezc.aragenPR.webapp.model;

import java.util.Date;
import java.time.LocalDate;

public class EzPurchaseOrderDto {
	private String poNo;
    private String vendor;
    private Date docDate;
    private LocalDate createdOn;
    private String purOrg;
    private String coCode;
    private String purGrp;
    private String plant;
    private String status;

    // Getters and Setters
    public String getPoNo() {
        return poNo;
    }

    public void setPoNo(String poNo) {
        this.poNo = poNo;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public Date getDocDate() {
        return docDate;
    }

    public void setDocDate(Date docDate) {
        this.docDate = docDate;
    }

    public LocalDate getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDate createdOn) {
        this.createdOn = createdOn;
    }

    public String getPurOrg() {
        return purOrg;
    }

    public void setPurOrg(String purOrg) {
        this.purOrg = purOrg;
    }

    public String getCoCode() {
        return coCode;
    }

    public void setCoCode(String coCode) {
        this.coCode = coCode;
    }

    public String getPurGrp() {
        return purGrp;
    }

    public void setPurGrp(String purGrp) {
        this.purGrp = purGrp;
    }

    public String getPlant() {
        return plant;
    }

    public void setPlant(String plant) {
        this.plant = plant;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
