package com.ezc.aragenPR.webapp.service;

import com.ezc.aragenPR.webapp.model.EzUploadDocument;
import com.ezc.aragenPR.webapp.persistance.dao.EzUploadDocumentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QmisServiceImpl implements  QmisService{
    @Autowired
    private EzUploadDocumentRepo ezUploadDocumentRepo;

    public QmisServiceImpl(EzUploadDocumentRepo ezUploadDocumentRepo) {
        this.ezUploadDocumentRepo = ezUploadDocumentRepo;
    }
    @Override
    public List<EzUploadDocument> findFileByDocTypeDocItemType(Integer docNo, String docType, String docItemType) {
        return ezUploadDocumentRepo.findFileByDocTypeDocItemType(docNo,docType,docItemType);
    }
}
