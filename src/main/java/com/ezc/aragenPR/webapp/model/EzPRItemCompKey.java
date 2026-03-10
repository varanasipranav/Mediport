package com.ezc.aragenPR.webapp.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EzPRItemCompKey implements Serializable {

    private static final long serialVersionUID = 1L;

    // Must match the PK columns in table
    private String header;  // maps to EPRI_REQ_NUMBER
    private String itemNO;  // maps to EPRI_ITEM_NUMBER
}
