package com.ezc.aragenPR.webapp.dto;

import java.math.BigDecimal;
import java.util.Date;

public class EzInvoiceReportDto {

    private String sapInvNo;
	
    private String vendor;
	
    private String vendorInvNo;
	
    private String invoiceDate;
	
    private String paymentDueDate;
	
    private String poNumber;
	
    private String currency;
    
	private BigDecimal amount;
	
	public String getSapInvNo() {
		return sapInvNo;
	}

	public void setSapInvNo(String sapInvNo) {
		this.sapInvNo = sapInvNo;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getVendorInvNo() {
		return vendorInvNo;
	}

	public void setVendorInvNo(String vendorInvNo) {
		this.vendorInvNo = vendorInvNo;
	}

	public String getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(String invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public String getPaymentDueDate() {
		return paymentDueDate;
	}

	public void setPaymentDueDate(String paymentDueDate) {
		this.paymentDueDate = paymentDueDate;
	}

	public String getPoNumber() {
		return poNumber;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
}
