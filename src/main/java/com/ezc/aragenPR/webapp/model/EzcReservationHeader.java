package com.ezc.aragenPR.webapp.model;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "EZC_RESERVATION_HEADER")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EzcReservationHeader {

    @Id
    @Column(name = "ERH_RESERVATION_NO")
    private String reservationNo;

    @Column(name = "ERH_MOVEMENT_TYPE")
    private String movementType;

    @Column(name = "ERH_WBS")
    private String wbs;

    @Column(name = "ERH_WBS_DESC" , length=100)
    private String wbsDesc;

    @Column(name = "ERH_COST_CENTER")
    private String costCenter;

    @Column(name = "ERH_INTERNAL_ORDER")
    private String internalOrder;

    @Column(name = "ERH_NETWORK")
    private String network;

    @Column(name = "ERH_ACTIVITY")
    private String activity;

    @Column(name = "ERH_GL_ACCOUNT")
    private String glAccount;

    @Column(name = "ERH_GOODS_RECEIPT" , length = 50)
    private String goodsReceipt;

    @Column(name = "ERH_REQ_DATE")
    private LocalDate reqDate;

    @Column(name = "ERH_CREATED_BY")
    private String createdBy;

    @Column(name = "ERH_CREATED_ON")
    private LocalDateTime createdOn;

    @Column(name = "ERH_STATUS")
    private String status;

    @JsonManagedReference
    @OneToMany(mappedBy = "header", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<EzcReservationItems> lineItems= new ArrayList<EzcReservationItems>();

}
