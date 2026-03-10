package com.ezc.aragenPR.webapp.model;

import static jakarta.persistence.GenerationType.IDENTITY;

import java.math.BigDecimal;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import jakarta.persistence.*;
import lombok.ToString;

@Entity
@Table(name = "EZC_INVOICE_HEADER")
public class EzInvoiceHeader {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "DOC_NO", length = 20)
    private Long docNo;

	@Column(name = "EPO_VENDOR", length = 10, nullable = false)
    private String userId;
	
	@Column(name = "PO_NO", length = 100)
    private String poNo;
	
	@Column(name = "INVOICE_NO", length = 20, nullable = false)
	private String invoiceNo;

	@Column(name = "INVOICE_DATE", nullable = false)
	private Date invoiceDate;

	@Column(name = "INVOICE_VALUE", nullable = false)
	private BigDecimal invoiceValue;
	
    @Column(name = "INVOICE_START_DATE")
    private Date startDate;
    
    @Column(name = "INVOICE_END_DATE")
    private Date endDate;

	@Column(name = "COMMENTS")
    private String comments;
	
    @Column(name = "CREATED_ON")
    private Date createdOn;
    
    @Column(name = "MIRO_NUMBER")
    private String miroNumber;
    
    @Column(name = "STATUS")
    private String status;
    
    @Column(name = "PLANT")
    private String plantNo;
    
    @Column(name = "UPLOADED_INVOICE")
    private String uploadedInvoice;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ezInvoiceHeader")
    @Cascade({CascadeType.ALL})
	@ToString.Exclude
	private List<EzInvoiceItems> ezInvoiceItems = new ArrayList<EzInvoiceItems>();

    // Getters and Setters
	public long getDocNo() {
		return docNo;
	}

	public void setDocNo(long docNo) {
		this.docNo = docNo;
	}
	
    public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPoNo() {
		return poNo;
	}

	public void setPoNo(String poNo) {
		this.poNo = poNo;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public BigDecimal getInvoiceValue() {
		return invoiceValue;
	}

	public void setInvoiceValue(BigDecimal invoiceValue) {
		this.invoiceValue = invoiceValue;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public List<EzInvoiceItems> getEzInvoiceItems() {
		return ezInvoiceItems;
	}

	public void setEzInvoiceItems(List<EzInvoiceItems> ezInvoiceItems) {
		this.ezInvoiceItems = ezInvoiceItems;
	}
	
    public String getMiroNumber() {
		return miroNumber;
	}

	public void setMiroNumber(String miroNumber) {
		this.miroNumber = miroNumber;
	}
	
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	

	public String getPlantNo() {
		return plantNo;
	}

	public void setPlantNo(String plantNo) {
		this.plantNo = plantNo;
	}
	
	

	public String getUploadedInvoice() {
		return uploadedInvoice;
	}

	public void setUploadedInvoice(String uploadedInvoice) {
		this.uploadedInvoice = uploadedInvoice;
	}

	public EzInvoiceHeader() {
		
	}
 
}
