package com.ezc.aragenPR.webapp.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class EzInvoiceItemsCompKey implements Serializable {

    private static final long serialVersionUID = 1L; // ✅ Add serialVersionUID

    @Column(name = "PO_NO")
    private String poNo; // ✅ Keep as String to avoid NumberFormatException

    @Column(name = "LINE_NO")
    private Integer lineNo; // ✅ Keep as Integer

    // ✅ Default constructor (required for JPA)
    public EzInvoiceItemsCompKey() {}

    // ✅ Parameterized constructor
    public EzInvoiceItemsCompKey(String poNo, Integer lineNo) { // 🔥 Keep poNo as String
        this.poNo = poNo;
        this.lineNo = lineNo;
    }

    // ✅ Getters and Setters
    public String getPoNo() {
        return poNo;
    }

    public void setPoNo(String poNo) {
        this.poNo = poNo;
    }

    public Integer getLineNo() {
        return lineNo;
    }

    public void setLineNo(Integer lineNo) {
        this.lineNo = lineNo;
    }

    // ✅ Override equals() for correct key comparison
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        EzInvoiceItemsCompKey that = (EzInvoiceItemsCompKey) obj;
        return Objects.equals(poNo, that.poNo) &&
               Objects.equals(lineNo, that.lineNo);
    }

    // ✅ Override hashCode() to ensure uniqueness
    @Override
    public int hashCode() {
        return Objects.hash(poNo, lineNo);
    }
}
