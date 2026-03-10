package com.ezc.aragenPR.webapp.service;

import com.ezc.aragenPR.webapp.model.EzPurchaseRequisitionHeader;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface EzcPRHeaderService {

    List<EzPurchaseRequisitionHeader> getAllHeaders();

    List<EzPurchaseRequisitionHeader> findByReqNumber(String reqNumber);

    EzPurchaseRequisitionHeader save(EzPurchaseRequisitionHeader header);

    List<EzPurchaseRequisitionHeader> getFilteredHeaders(LocalDateTime fromDate, LocalDateTime toDate, String status, String userId);

    List<EzPurchaseRequisitionHeader> getFilteredHeaders(LocalDateTime fromDate, LocalDateTime toDate, String status);

    int updateStatusAndApprover(String reqNumber,String status,String nxtApprover);

    List<EzPurchaseRequisitionHeader> getCreatedByList(LocalDateTime fromDate, LocalDateTime toDate, String createdBy);


    List<EzPurchaseRequisitionHeader> findByReqNumberWithItems(String reqNumber);
}
