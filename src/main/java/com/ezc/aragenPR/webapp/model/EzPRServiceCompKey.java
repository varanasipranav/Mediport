package com.ezc.aragenPR.webapp.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EzPRServiceCompKey implements Serializable {

    private static final long serialVersionUID = 1L;

    private String reqNumber;   // maps to EPRS_REQ_NUMBER
    private String itemNo;      // maps to EPRS_ITEM_NO
    private String servLine;    // maps to EPRS_SERVICE_LINE
}
