package com.ezc.aragenPR.webapp.service;

import com.ezc.aragenPR.webapp.model.EzUploadDocument;
import com.ezc.aragenPR.webapp.persistance.dao.EzUploadDocumentRepo;

import java.util.List;

public interface QmisService {
    List<EzUploadDocument> findFileByDocTypeDocItemType(Integer docNo, String docType, String docItemType);

}
