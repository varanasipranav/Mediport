package com.ezc.aragenPR.webapp.model;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
@EqualsAndHashCode
public class EzPoItemsKey implements Serializable {
    
    private static final long serialVersionUID = 1L; // ✅ Added serialVersionUID

    @Column(name = "po_no")
    private String poNo;
    
    @Column(name = "po_item_number")
    private Integer poItemNumber;
    
    // Default constructor (required for JPA)
    public EzPoItemsKey() {}

    // Constructor with fields
    public EzPoItemsKey(String poNo, Integer poItemNumber) {
        this.poNo = poNo;
        this.poItemNumber = poItemNumber;
    }
}
