package com.ezc.aragenPR.webapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class MaterialDto {

    private String materialCode;
    private String description;
    private String uom;
    private String casNo;
    private BigDecimal quantity;
    private String unitPrice;
    private String materialGroup;
    private String glAcc;
    private String glAccDesc;
    private String projCode;
    private String projCodeDesc;
    private String wbsCode;
    private String wbsCodeDesc;
    private String servCode;
    private String servCodeDesc;
    private String networkCode;
    private String networkDesc;
    private String aggKey;
    private String aggValue;

    public MaterialDto() {

    }

    // Getters and Setters

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public String getCasNo() {
        return casNo;
    }

    public void setCasNo(String casNo) {
        this.casNo = casNo;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }


    public String getGlAcc() {
        return glAcc;
    }

    public void setGlAcc(String glAcc) {
        this.glAcc = glAcc;
    }

    public String getGlAccDesc() {
        return glAccDesc;
    }

    public void setGlAccDesc(String glAccDesc) {
        this.glAccDesc = glAccDesc;
    }

    public String getProjCode() {
        return projCode;
    }

    public void setProjCode(String projCode) {
        this.projCode = projCode;
    }

    public String getProjCodeDesc() {
        return projCodeDesc;
    }

    public void setProjCodeDesc(String projCodeDesc) {
        this.projCodeDesc = projCodeDesc;
    }

    public String getMaterialGroup() {
        return materialGroup;
    }

    public void setMaterialGroup(String materialGroup) {
        this.materialGroup = materialGroup;
    }


}
