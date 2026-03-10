package com.ezc.aragenPR.webapp.persistance.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.ezc.aragenPR.webapp.model.EzUploadDocument;
import com.ezc.aragenPR.webapp.model.EzUploadDocumentKey;

public interface EzUploadDocumentRepo extends JpaRepository<EzUploadDocument, EzUploadDocumentKey> {
	
	@Query(nativeQuery=true,value="select * from ezc_upload_document WHERE EUD_DOC_NO=:docNo ")
	List<EzUploadDocument> getFilesById(Integer docNo);

	@Query(nativeQuery=true,value="select * from ezc_upload_document WHERE EUD_DOC_NO=:docNo and EUD_DOC_TYPE=:docType ")
	List<EzUploadDocument> findFileByDocType(Integer docNo, String docType);

	@Query(nativeQuery=true,value="select * from ezc_upload_document WHERE EUD_DOC_NO=:docNo and EUD_DOC_TYPE=:docType and EUD_ITEM_NO=:docItemType ")
	List<EzUploadDocument> findFileByDocTypeDocItemType(Integer docNo, String docType, String docItemType);
	
	@Modifying
	@Query(nativeQuery=true,value="delete  from ezc_upload_document WHERE EUD_DOC_NO=:docNo and EUD_DOC_TYPE=:docType and EUD_FILE_NAME=:filename and EUD_ITEM_NO=:docItemType ")
	void deleteFileEntryByIdType(String filename, Integer docNo, String docType, String docItemType); 
	
	Optional<EzUploadDocument> findByDocNoAndItemNoAndDocType(Integer docNo, String itemNo, String docType);
	

	 

	

}
