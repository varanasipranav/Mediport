package com.ezc.aragenPR.webapp.dto;

import lombok.Data;

@Data
public class ReservationItemDTO {




        private String reservationNo;
        private Integer itemNo;

        private String material;
        private String shortText;
        private Double qty;
        private Double withDrawQty;
        private String uom;
        private String plant;
        private String storageLoc;
        private String labStorageLoc;
        private String status;
        private String batch;
        private String movementAllowed;




}
