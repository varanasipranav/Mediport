package com.ezc.aragenPR.webapp.model;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Id;

public class EzInvoiceReport {
	@Id
	@Column(name = "SAP_INV_NO", length = 10)
    private String sapInvNo;
	
	@Column(name = "VENDOR", length = 10)
    private String vendor;
	
	@Column(name = "VENDOR_INV_NO")
    private String vendorInvNo;
	
	@Column(name = "INVOICE_DATE")
    private Date invoiceDate;
	
	@Column(name = "PAYMENT_DUE_DATE")
    private Date paymentDueDate;
	
	@Column(name = "PO_NUMBER", length = 10)
    private String poNumber;
	
	@Column(name = "CURRENCY")
    private String currency;
	
	@Column(name= "AMOUNT")
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

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public Date getPaymentDueDate() {
		return paymentDueDate;
	}

	public void setPaymentDueDate(Date paymentDueDate) {
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
