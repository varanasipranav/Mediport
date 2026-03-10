package com.ezc.aragenPR.webapp.model;

import static jakarta.persistence.GenerationType.IDENTITY;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "EZC_PURCHASE_REQUISITION_HEADER")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EzPurchaseRequisitionHeader {

    @Id
    @Column(name = "EPRH_REQ_NUMBER", unique = true, length = 10, nullable = false)
    private String reqNumber;

    @Column(name = "EPRH_DOC_TYPE", length = 4)
    private String docType;

    @Column(name = "EPRH_ACC_ASMT", length = 45)
    private String accAsmt;

    @Column(name = "EPRH_DIVISION", length = 2)
    private String division;

    @Column(name = "EPRH_ITEM_CAT", length = 1)
    private String itemCat;

    @Column(name = "EPRH_PLANT", length = 10)
    private String plant;

    @Column(name = "EPRH_PUR_GRP", length = 5)
    private String purGrp;

    @Column(name = "EPRH_DEPARTMENT", length = 15)
    private String dept;

    @Column(name = "EPRH_HEADER_TEXT", length = 1000)
    private String headerText;

    @Column(name = "EPRH_CREATED_BY", length = 15)
    private String createdBy;

    @Column(name = "EPRH_CREATED_ON")
    private LocalDateTime createdOn;

    @Column(name = "EPRH_MODIFIED_BY", length = 15)
    private String modifiedBy;

    @Column(name = "EPRH_MODIFIED_ON")
    private LocalDateTime modifiedOn;

    @Column(name = "EPRH_RELEASED_ON")
    private LocalDateTime releasedOn;

    @Column(name = "EPRH_STATUS", length = 10)
    private String status;

    @Column(name = "EPRH_NEXT_PARTICIPANT", length = 100)
    private String nextApprover;


    @Column(name = "EPRH_EXT1", length = 10)
    private String ext1;

    @Column(name = "EPRH_EXT2", length = 25)
    private String ext2;

    @Column(name = "EPRH_EXT3", length = 10)
    private String ext3;

    // One-to-Many with Items
    @JsonManagedReference
    @OneToMany(mappedBy = "header", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<EzPurchaseRequisitionItems> items = new ArrayList<EzPurchaseRequisitionItems>();



// helper method to add item
    /*public void addItem(EzPurchaseRequisitionItems item) {
        items.add(item);
        item.setHeader(this);
    }

    // helper method to remove item
    public void removeItem(EzPurchaseRequisitionItems item) {
        items.remove(item);
        item.setHeader(null);
    }*/
}
