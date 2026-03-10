package com.ezc.aragenPR.webapp.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name="EZC_PURCHASE_REQUISITION_ITEMS")
@IdClass(EzPRItemCompKey.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class EzPurchaseRequisitionItems {

    @Id
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EPRI_REQ_NUMBER")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private EzPurchaseRequisitionHeader header;

    @Id
    @Column(name = "EPRI_ITEM_NUMBER", length = 15)
    private String itemNO;

    @Column(name = "EPRI_SAP_PR_NO", length = 15)
    private String sapPrNo;

    @Column(name = "EPRI_SAP_PR_ITEM_NO", length = 5)
    private String sapPrItemNo;

    @Column(name = "EPRI_PO_STATUS", length = 10)
    private String poStatus;

    @Column(name = "EPRI_GRN_STATUS", length = 10)
    private String grnStatus;

    @Column(name = "EPRI_DOC_TYPE", length = 4)
    private String itemDocType;

    @Column(name = "EPRI_CLOSED", length = 1)
    private String closed;

    @Column(name = "EPRI_MAT_NUMBER", length = 18)
    private String matNumber;

    @Column(name = "EPRI_SHORT_TEXT", length = 200)
    private String shortText;

    @Column(name = "EPRI_SHORT_TEXT1", length = 200)
    private String shortText1;

    @Column(name = "EPRI_ITEM_CAT", length = 10)
    private String itemCat;

    @Column(name = "EPRI_PLANT", length = 4)
    private String plant;

    @Column(name = "EPRI_STORAGE_LOC", length = 4)
    private String storageLoc;

    @Column(name = "EPRI_PURCHASE_GROUP", length = 3)
    private String purchaseGroup;

    @Column(name = "EPRI_MATERIAL_GROUP", length = 9)
    private String materialGroup;

    @Column(name = "EPRI_QUANTITY", precision = 15, scale = 3)
    private BigDecimal quantity;

    @Column(name = "EPRI_VAL_PRICE", precision = 15, scale = 3)
    private BigDecimal valPrice;

    @Column(name = "EPRI_UOM", length = 20)
    private String uom;

    @Column(name = "EPRI_UOM1", length = 20)
    private String uom1;

    @Column(name = "EPRI_ACC_ASSIGN_CAT", length = 4)
    private String accAssignCat;

    @Column(name = "EPRI_MRP_CONTROLLER", length = 3)
    private String mrpController;

    @Column(name = "EPRI_GL_ACC_NO", length = 10)
    private String glAccNo;

    @Column(name = "EPRI_PACK_SIZE", length = 10)
    private String packSize;

    @Column(name = "EPRI_COA", length = 3)
    private String coa;

    @Column(name = "EPRI_MSDS", length = 3)
    private String msds;

    @Column(name = "EPRI_PASS_THROUGH", length = 3)
    private String passThrough;

    @Column(name = "EPRI_BUSINESS_AREA", length = 4)
    private String businessArea;

    @Column(name = "EPRI_COST_CENTER", length = 10)
    private String costCenter;

    @Column(name = "EPRI_WBS_ELEMENT", length = 24)
    private String wbsElement;

    @Column(name = "EPRI_INT_ORDER", length = 20)
    private String internalOrder;

    @Column(name = "EPRI_NETWORK", length = 20)
    private String network;

    @Column(name = "EPRI_GR_SHTPARTY", length = 12)
    private String grShtParty;

    @Column(name = "EPRI_CAS", length = 30)
    private String cas;

    @Column(name = "EPRI_VENDOR", length = 80)
    private String vendor;

    @Column(name = "EPRI_DELIVERY_DATE")
    private Date deliveryDate;

    @Column(name = "EPRI_STATUS", length = 10)
    private String status;

    @Column(name = "EPRI_REL_STATUS", length = 10)
    private String relStatus;

    @Column(name = "EPRI_REL_IND", length = 10)
    private String relInd;

    @Column(name = "EPRI_DEL_IND", length = 3)
    private String delInd;

    @Column(name = "EPRI_WH_SPVSR", length = 15)
    private String whSpvsr;

    @Column(name = "EPRI_VENDORS", length = 200)
    private String vendors;

    @Column(name = "EPRI_EXT1", length = 10)
    private String ext1;

    @Column(name = "EPRI_EXT2", length = 25)
    private String ext2;

    @Lob
    @Column(name = "EPRI_EXT3")
    private String ext3;

    @Column(name = "EPRI_PGR_STATUS", length = 5)
    private String pgrStatus;

    @Column(name = "EPRI_PGR_PENDING_QTY", precision = 18, scale = 3)
    private BigDecimal pgrPendingQty;

    @Column(name = "EPRI_ASSET_NO", length = 100)
    private String assetNo;

    @Column(name = "EPRI_ASSET_TEXT", length = 100)
    private String assetText;

    // Other LOB service-related columns
    @Lob
    @Column(name = "EPRI_SERVICE_LINE_NO")
    private String serviceLineNo;

    @Lob
    @Column(name = "EPRI_SERVICE_NO")
    private String serviceNo;

    @Lob
    @Column(name = "EPRI_SERVICE_SHORT_TEXT")
    private String serviceShortText;

    @Lob
    @Column(name = "EPRI_SERVICE_QTY")
    private String serviceQty;

    @Lob
    @Column(name = "EPRI_SERVICE_UNIT")
    private String serviceUnit;

    @Lob
    @Column(name = "EPRI_SERVICE_GROSS_PRICE")
    private String serviceGrossPrice;

    @Lob
    @Column(name = "EPRI_SERVICE_TOLERANCE")
    private String serviceTolerance;

    @Column(name = "EPRI_PRICE_UNIT", length = 100)
    private String priceUnit;

    @Column(name = "EPRI_TOTAL_VALUE", length = 100)
    private String total;

    @Column(name = "EPRI_TRACK_NO", length = 100)
    private String trackNo;

    @Lob
    @Column(name = "EPRI_ITEM_TEXT")
    private String itemText;

    @Column(name = "EPRI_DESIRED_VEND", length = 100)
    private String desiredVend;

    @Column(name = "EPRI_APPROVED_MANUFACTURER", length = 100)
    private String approvedManufacturer;

    @Column(name = "EPRI_EXT4", length = 100)
    private String ext4;

    @Column(name = "EPRI_EXT5")
    private Date ext5;

    @Column(name = "EPRI_EXT6", length = 100)
    private String ext6;

    @Column(name = "EPRI_MAX_RATE", length = 100)
    private String maxRate;

    @Column(name = "EPRI_NARCOTIC", length = 10)
    private String narcotic;

    @Column(name = "EPRI_VAL_TYPE", length = 25)
    private String valType;

    @Column(name = "EPRI_REASON", length = 2)
    private String reason;

    @Column(name = "EPRI_AGGREMENT", length = 10)
    private String agreement;

    @Column(name = "EPRI_AGGR_ITEM", length = 5)
    private String aggrItem;

    @Column(name = "EPRI_FIXED_VENDOR", length = 10)
    private String fixVendor;

    @JsonManagedReference
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EzPurchaseRequisitionServices> services = new ArrayList<>();

}
