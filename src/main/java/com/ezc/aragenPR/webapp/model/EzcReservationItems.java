package com.ezc.aragenPR.webapp.model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@Table(name = "EZC_RESERVATION_ITEMS")
@IdClass(ReservationItemKey.class)
public class EzcReservationItems {


    @Id
    @Column(name = "ERI_ITEM_NO")
    private Integer itemNo;

    @Column(name = "ERI_MATERIAL")
    private String material;

    @Column(name = "ERI_MATERIAL_DESC", length = 100)
    private String shortText;

    @Column(name = "ERI_QTY")
    private Double qty;

    @Column(name = "ERI_WITH_DRAWN_QTY")
    private Double withDrawQty;

    @Column(name = "ERI_UOM")
    private String uom;

    @Column(name = "ERI_PLANT")
    private String plant;

    @Column(name = "ERI_STORAGE_LOC")
    private String storageLoc;


    @Column(name = "ERI_LAB_STORAGE_LOC", length=20)
    private String labStorageLoc;


    @Column(name = "ERI_STATUS")
    private String status;

    @Column(name = "ERI_BATCH", length = 15)
    private String batch;

    @Column(name = "ERI_MOVEMENT_ALLOWED", length = 1)
    private String movementAllowed;

    @Id
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ERI_RESERVATION_NO", referencedColumnName = "ERH_RESERVATION_NO")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private EzcReservationHeader header;
}
