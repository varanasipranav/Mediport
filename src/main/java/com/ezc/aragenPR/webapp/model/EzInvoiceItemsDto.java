package com.ezc.aragenPR.webapp.model;

import java.math.BigDecimal;


public class EzInvoiceItemsDto {
	
//	 public EzInvoiceItemsDto(Integer lineNo, String poNo, String material) {
//		super();
//		this.lineNo = lineNo;
//		this.poNo = poNo;
//		this.material = material;
//	}

	private Integer lineNo;
	    private String poNo;
	    private String plantNo;
	    private String material;
	    private String matDesc;
	    private String uom;
	    private BigDecimal totalQty;
	    private BigDecimal openQty;
	    private BigDecimal submittedQty;
		private BigDecimal quantity;
	    private BigDecimal price;
	    private BigDecimal value;
	    private BigDecimal taxes;
		private BigDecimal totalValue;
		private Integer poItemNumber;
		private String netWeight;
		private String pricePerKg;
		private BigDecimal invoiceQty;
		private BigDecimal invoiceAmount;
		private String vendor;
		
		
		
		public String getVendor() {
			return vendor;
		}

		public void setVendor(String vendor) {
			this.vendor = vendor;
		}

		public String getNetWeight() {
			return netWeight;
		}

		public void setNetWeight(String netWeight) {
			this.netWeight = netWeight;
		}

		public String getPricePerKg() {
			return pricePerKg;
		}

		public void setPricePerKg(String pricePerKg) {
			this.pricePerKg = pricePerKg;
		}



	    public Integer getPoItemNumber() {
			return poItemNumber;
		}

		public void setPoItemNumber(Integer poItemNumber) {
			this.poItemNumber = poItemNumber;
		}

		// Getters and Setters
	    public Integer getLineNo() {
	        return lineNo;
	    }

	    public void setLineNo(Integer lineNo) {
	        this.lineNo = lineNo;
	    }

	    public String getPoNo() {
	        return poNo;
	    }

	    public void setPoNo(String poNo) {
	        this.poNo = poNo;
	    }

	public String getPlantNo() {
		return plantNo;
	}

	public void setPlantNo(String plantNo) {
		this.plantNo = plantNo;
	}


	public String getMaterial() {
	        return material;
	    }

	    public void setMaterial(String material) {
	        this.material = material;
	    }

	    public String getMatDesc() {
	        return matDesc;
	    }

	    public void setMatDesc(String matDesc) {
	        this.matDesc = matDesc;
	    }

	    public String getUom() {
	        return uom;
	    }

	    public void setUom(String uom) {
	        this.uom = uom;
	    }

	    public BigDecimal getTotalQty() {
	        return totalQty;
	    }

	    public void setTotalQty(BigDecimal totalQty) {
	        this.totalQty = totalQty;
	    }
	    

	    public BigDecimal getOpenQty() {
			return openQty;
		}

		public void setOpenQty(BigDecimal openQty) {
			this.openQty = openQty;
		}

		public BigDecimal getSubmittedQty() {
	        return submittedQty;
	    }

	    public void setSubmittedQty(BigDecimal submittedQty) {
	        this.submittedQty = submittedQty;
	    }

	    public BigDecimal getQuantity() {
	        return quantity;
	    }

	    public void setQuantity(BigDecimal quantity) {
	        this.quantity = quantity;
	    }

	    public BigDecimal getPrice() {
	        return price;
	    }

	    public void setPrice(BigDecimal price) {
	        this.price = price;
	    }

	    public BigDecimal getValue() {
	        return value;
	    }

	    public void setValue(BigDecimal value) {
	        this.value = value;
	    }

	    public BigDecimal getTaxes() {
	        return taxes;
	    }

	    public void setTaxes(BigDecimal taxes) {
	        this.taxes = taxes;
	    }
	    public BigDecimal getTotalValue() {
			return totalValue;
		}

		public void setTotalValue(BigDecimal totalValue) {
			this.totalValue = totalValue;
		}
		public BigDecimal getInvoiceQty() {
			return invoiceQty;
		}

		public void setInvoiceQty(BigDecimal invoiceQty) {
			this.invoiceQty = invoiceQty;
		}

		public BigDecimal getInvoiceAmount() {
			return invoiceAmount;
		}

		public void setInvoiceAmount(BigDecimal invoiceAmount) {
			this.invoiceAmount = invoiceAmount;
		}

}
