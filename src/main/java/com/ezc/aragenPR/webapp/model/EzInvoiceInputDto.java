package com.ezc.aragenPR.webapp.model;

import java.math.BigDecimal;
//import java.sql.Date;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

public class EzInvoiceInputDto {

	private String vendor;
    private String invoiceNumber;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date invoiceDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
    private String comments;
	private BigDecimal invoiceValue;
	private String poNo;
	List<EzInvoiceItemsDto> invoiceItems;
	private Long docNo;
	private String miroNumber;
	private List<String> invoiceNoList;
	private String plantNo;
	private List<EzInvoiceItemsDto> warningMaterials;
	private String uuid;


	public Long getDocNo() {
		return docNo;
	}

	public void setDocNo(Long docNo) {
		this.docNo = docNo;
	}

	// Getters and Setters
	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
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
    
    public BigDecimal getInvoiceValue() {
		return invoiceValue;
	}

	public void setInvoiceValue(BigDecimal invoiceValue) {
		this.invoiceValue = invoiceValue;
	}
	public String getPoNo() {
		return poNo;
	}

	public void setPoNo(String poNo) {
		this.poNo = poNo;
	}

	public String getMiroNumber() {
		return miroNumber;
	}

	public void setMiroNumber(String miroNumber) {
		this.miroNumber = miroNumber;
	}


	public List<EzInvoiceItemsDto> getInvoiceItems() {
		return invoiceItems;
	}

	public void setInvoiceItems(List<EzInvoiceItemsDto> invoiceItems) {
		this.invoiceItems = invoiceItems;
	}
	
	public List<String> getInvoiceNoList() {
		return invoiceNoList;
	}

	public void setInvoiceNoList(List<String> invoiceNoList) {
		this.invoiceNoList = invoiceNoList;
	}

	public String getPlantNo() {
		return plantNo;
	}

	public void setPlantNo(String plantNo) {
		this.plantNo = plantNo;
	}

	public List<EzInvoiceItemsDto> getWarningMaterials() {
		return warningMaterials;
	}

	public void setWarningMaterials(List<EzInvoiceItemsDto> warningMaterials) {
		this.warningMaterials = warningMaterials;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	
	
	
}
