/*
package com.ezc.aragenPR.webapp.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.ezc.aragenPR.webapp.dto.EzQuestionMap;
import com.ezc.aragenPR.webapp.dto.PPAPDocumentForm;
import com.ezc.aragenPR.webapp.model.EzComments;
import com.ezc.aragenPR.webapp.model.EzCustomerFormDetails;
import com.ezc.aragenPR.webapp.model.EzCustomerFormHeader;
import com.ezc.aragenPR.webapp.model.EzUploadDocument;

public interface IPPAPService {
	
		public Integer createPpapRequest(EzCustomerFormHeader ezCustomerFormHeader, String string);


		public void saveFile(String uuid, Integer docId,String userId);



		public List<EzUploadDocument> getFilesById(Integer docId);


		public Optional<EzCustomerFormHeader> getDocumentByID(Integer docId);


		public EzCustomerFormHeader submitRequest(PPAPDocumentForm ppapDocumentForm, String status);


		public List<EzUploadDocument> getFilesByType(Integer docId, String string);


		public List<EzCustomerFormHeader> getDocuments(Date fromDate, Date toDate,String status, List<String> plantCodeList, String userRole, boolean dashBoardFlag);


		void saveComments(String comments, Integer docId, String userId,String tab);


		List<EzComments> getCommetnsById(String docId, String docType);


		void createPpapDetails(List<EzCustomerFormDetails> ezCustomerFormDetailsList);


		public List<EzCustomerFormDetails> getDocumentDetailsByIDAndType(Integer docId, String string);
		
		public List<String> getPrevApprMails(Integer docId, String string);


		public List<EzQuestionMap> getPpapQuestionaMap();






	

}
*/
