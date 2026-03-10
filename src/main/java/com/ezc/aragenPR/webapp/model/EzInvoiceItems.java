package com.ezc.aragenPR.webapp.model;

import java.math.BigDecimal;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "EZC_INVOICE_ITEMS")
public class EzInvoiceItems {

    @EmbeddedId  // ✅ Correct way to define composite key
    private EzInvoiceItemsCompKey id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DOC_ID", nullable = false) // ✅ Ensure this is mapped correctly
    private EzInvoiceHeader ezInvoiceHeader;

    
    @Column(name = "LINE_NO", insertable = false, updatable = false)
    private Integer lineNo; 
    
    @Column(name = "EPO_PLANT", length = 4)
    private String plant;

    @Column(name = "MATERIAL", length = 50, nullable = false)
    private String material;

    @Column(name = "MAT_DESC", length = 255)
    private String matDesc;

    @Column(name = "UOM", length = 10)
    private String uom;

    @Column(name = "TOTAL_QTY", precision = 15, scale = 2)
    private BigDecimal totalQty;

    @Column(name = "SUBMITTED_QTY", nullable = true)
    private BigDecimal submittedQty; // ✅ Ensure this is an Integer, not int, for better null handling

    @Column(name = "QUANTITY", precision = 15, scale = 2, nullable = true)
    private BigDecimal quantity;

    @Column(name = "PRICE", precision = 15, scale = 2)
    private BigDecimal price;

    @Column(name = "VALUE", precision = 15, scale = 2)
    private BigDecimal value;

    @Column(name = "TAXES", precision = 15, scale = 2)
    private BigDecimal taxes;

    @Column(name = "TOTAL_VALUE", precision = 15, scale = 2)
    private BigDecimal totalValue;

    @Column(name = "PO_ITEM_NUMBER")
    private Integer poItemNumber;

    @Column(name = "INVOICE_QTY", precision = 15, scale = 2)
    private BigDecimal invoiceQty;

    @Column(name = "INVOICE_AMOUNT", precision = 15, scale = 2)
    private BigDecimal invoiceAmount;

    @Column(name = "NET_WEIGHT", length = 50)
    private String netWeight;

    @Column(name = "PRICE_PER_KG", length = 50)
    private String pricePerKg;
}
