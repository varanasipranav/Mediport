package com.ezc.aragenPR.webapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class EzcSESItemKey implements Serializable {

    private static final long serialVersionUID = 1L;

    private String header;  // maps to ESI_SES_NUMBER

    private Integer lineNumber;

}
