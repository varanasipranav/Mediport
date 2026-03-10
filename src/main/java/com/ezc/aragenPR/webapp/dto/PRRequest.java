package com.ezc.aragenPR.webapp.dto;
import lombok.Data;
import java.util.List;

@Data
public class PRRequest {
    private String logonSite;
    private String docType;
    private String division;
    private String accAsmt;
    private String itemCateg;
    private String plant;
    private String purGrp;
    private String department;
    private String sapReqName;
    private String portalRefNO;
    private String aonNo;
    private String sapPrRel;
    private String reqNumber;
    private List<PRItem> items;

}
