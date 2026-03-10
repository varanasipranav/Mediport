package com.ezc.aragenPR.webapp.model;

import java.math.BigDecimal;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ezc_po_items")
public class EzPoItems {

    @EmbeddedId  // ✅ Use composite primary key
    private EzPoItemsKey id;

    @Column(name = "line_no")
    private Integer lineNo;
    
    @Column(name = "plant_no")
    private String plantNo;
    
    @Column(name = "material")
    private String material;
    
    @Column(name = "mat_desc")
    private String matDesc;
    
    @Column(name = "uom")
    private String uom;
    
    @Column(name = "total_qty")
    private BigDecimal totalQty;
    
    @Column(name = "open_qty")
    private BigDecimal openQty;
    
    @Column(name = "submitted_qty")
    private BigDecimal submittedQty;
    
    @Column(name = "quantity")
    private BigDecimal quantity;
    
    @Column(name = "price")
    private BigDecimal price;
    
    @Column(name = "value")
    private BigDecimal value;
    
    @Column(name = "taxes")
    private BigDecimal taxes;
    
    @Column(name = "total_value")
    private BigDecimal totalValue;
    
    @Column(name = "net_weight")
    private String netWeight;
    
    @Column(name = "price_per_kg")
    private String pricePerKg;
    
    @Column(name = "invoice_qty")
    private BigDecimal invoiceQty;
    
    @Column(name = "invoice_amount")
    private BigDecimal invoiceAmount;
    
    @Column(name = "vendor")
    private String vendor;

    // ✅ Utility method to update open_qty
    public void updateOpenQty(BigDecimal submittedQty) {
        if (this.openQty == null) {
            this.openQty = this.totalQty;
        }
        this.openQty = this.openQty.subtract(submittedQty);
        if (this.openQty.compareTo(BigDecimal.ZERO) < 0) {
            this.openQty = BigDecimal.ZERO;  // Ensure it doesn't go negative
        }
    }
}
