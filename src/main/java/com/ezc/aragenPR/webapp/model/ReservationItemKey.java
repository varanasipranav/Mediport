package com.ezc.aragenPR.webapp.model;


import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationItemKey implements Serializable {

    private String header;
    private Integer itemNo;


    // getters & setters
}
