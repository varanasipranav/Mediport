package com.ezc.aragenPR.webapp.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "EZC_SES_HEADER")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EzcSESHeader {

    @Id
    @Column(name = "ESH_SES_NUMBER", length = 20, nullable = false, unique = true)
    private String sesNumber;

    @Column(name = "ESH_EXT_NUMBER", length = 20)
    private String extNumber;

    @Column(name = "ESH_PO_NUMBER", length = 20)
    private String poNumber;

    @Column(name = "ESH_PO_ITEM_NO", length = 20)
    private String poItemNO;

    @Column(name = "EHS_PER_EXT", length = 20)
    private String perExt;

    @Column(name = "EHS_ACC_ASST", length = 2)
    private String accAsst;

    @Column(name = "ESH_PACK_NO", length = 20)
    private String packNO;

    @Column(name = "ESH_VENDOR_NAME", length = 200)
    private String vendorName;

    @Column(name = "ESH_SHORT_TEXT", length = 255)
    private String shortText;

    @Column(name = "ESH_DOC_TEXT", length = 255)
    private String docText;

    @Column(name = "ESH_STATUS", length = 50)
    private String status;

    @Column(name = "ESH_REFERENCE_DOC", length = 50)
    private String referenceDoc;

    @Column(name = "ESH_SERVICE_LOCATION", length = 100)
    private String serviceLocation;

    @Column(name = "ESH_CURRENCY", length = 10)
    private String currency;

    @Column(name = "ESH_CREATED_BY", length = 50)
    private String createdBy;

    @Column(name = "ESH_CREATED_ON")
    private LocalDateTime createdOn;

    @Column(name = "ESH_MODIFIED_BY", length = 50)
    private String modifiedBy;

    @Column(name = "ESH_MODIFIED_ON")
    private LocalDateTime modifiedOn;

    @Column(name = "ESH_RELEASE_GROUP", length = 10)
    private String releaseGroup;

    @Column(name = "ESH_RELEASE_STRATEGY", length = 10)
    private String releaseStrategy;

    @Column(name = "ESH_RELEASE_INDICATOR", length = 10)
    private String releaseIndicator;

    @Column(name = "ESH_ENTRY_DATE")
    private LocalDate entryDate;

    @Column(name = "ESH_POSTING_DATE")
    private LocalDate postingDate;

    @Column(name = "ESH_TOTAL_AMOUNT")
    private Double totalAmount;



    // --- Relationship ---
    @JsonManagedReference
    @OneToMany(mappedBy = "header", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<EzcSESItems> lineItems= new ArrayList<EzcSESItems>();
}
