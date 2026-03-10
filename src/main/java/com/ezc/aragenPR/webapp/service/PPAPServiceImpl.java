/*
package com.ezc.aragenPR.webapp.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.ezc.aragenPR.webapp.dto.EzQuestionMap;
import com.ezc.aragenPR.webapp.dto.FileCommentsDto;
import com.ezc.aragenPR.webapp.dto.PPAPDocumentForm;
import com.ezc.aragenPR.webapp.model.EzComments;
import com.ezc.aragenPR.webapp.model.EzCustomerFormDetails;
import com.ezc.aragenPR.webapp.model.EzCustomerFormHeader;
import com.ezc.aragenPR.webapp.model.EzPpapQuestionnaire;
import com.ezc.aragenPR.webapp.model.EzUploadDocument;
import com.ezc.aragenPR.webapp.model.EzcPpapQuestionMap;
import com.ezc.aragenPR.webapp.model.Users;
import com.ezc.aragenPR.webapp.persistance.dao.CommentsRepo;
import com.ezc.aragenPR.webapp.persistance.dao.EzCustomerFormDetailsRepo;
import com.ezc.aragenPR.webapp.persistance.dao.EzCustomerFormHeaderRepo;
import com.ezc.aragenPR.webapp.persistance.dao.EzUploadDocumentRepo;
import com.ezc.aragenPR.webapp.persistance.dao.EzcPpapQuestionaMapRepo;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class PPAPServiceImpl implements IPPAPService {
	
	@Autowired
    private EzCustomerFormHeaderRepo ezCustomerFormHeaderRepo;
	
	@Autowired
    private EzCustomerFormDetailsRepo ezCustomerFormDetailsRepo;
	
	@Autowired
    private EzUploadDocumentRepo ezUploadDocumentRepo;
	
	@Autowired
	IStorageService storageService;
	
	@Autowired
	IEmailService emailService;
	
	@Autowired
	private CommentsRepo commentsRepo;
	
	@Autowired
	EzcPpapQuestionaMapRepo ezQuestionMapRepo;

	

	@Override
	public Integer createPpapRequest(EzCustomerFormHeader ezCustomerFormHeader,String userID) {
		// TODO Auto-generated method stub 
		
		EzCustomerFormHeader custFormHeader=ezCustomerFormHeaderRepo.save(ezCustomerFormHeader);
		List<String> emailList=new ArrayList<String>();
		emailList.add(userID);
		emailService.sendRequestMailToNextPart(custFormHeader,emailList);
		return custFormHeader.getDocId();
		
	}
	
	@Override
	public void createPpapDetails(List<EzCustomerFormDetails> ezCustomerFormDetailsList) {
		// TODO Auto-generated method stub
		
		ezCustomerFormDetailsRepo.saveAll(ezCustomerFormDetailsList);
		
		
	}

	@Override
	public void saveFile(String uuid,Integer docId,String userId) {
		// TODO Auto-generated method stub
		storageService.saveFile(uuid,docId,userId);
		
	}
	
	@Override
	public List<EzComments> getCommetnsById(String docId,String docType) {
		// TODO Auto-generated method stub
		return commentsRepo.findByDocIdAndDocType(docId,docType);
		
	}
	
	
	@Override
	public void saveComments(String comments,Integer docId,String userId,String tab) {
		Date today= new Date();
		
		log.debug("saveComments in comments::"+comments); 
		EzComments commentObj= new EzComments();
		commentObj.setComments(comments);
		commentObj.setCreatedBy(userId);
		commentObj.setDate(today);
		commentObj.setDocType("PPAP");
		commentObj.setTab(tab);
		if(docId!=null)
			commentObj.setDocId(docId.toString());
		
		
		
		commentsRepo.save(commentObj);
		
	}

	@Override
	public List<EzCustomerFormHeader> getDocuments(Date fromDate, Date toDate,String status,List<String> plantCodeList,String userRole,boolean dashBoardFlag) {
		List<EzCustomerFormHeader> docList=null;
		log.debug("getDocuments Service::fromDate:"+fromDate);
		log.debug("getDocuments Service::toDate:"+toDate);
		log.debug("getDocuments Service::status:"+status);
		log.debug("getDocuments Service::plantCodeList:"+plantCodeList.toString());
		log.debug("getDocuments Service::userRole:"+userRole);
		log.debug("getDocuments Service::dashBoardFlag:"+dashBoardFlag);
		if (dashBoardFlag) {
			docList= ezCustomerFormHeaderRepo.findDocumentWithRole(fromDate, toDate, status, plantCodeList, userRole);
		} else {
		     docList= ezCustomerFormHeaderRepo.findDocumentWithoutRole(fromDate, toDate, status, plantCodeList);
		}
		
    	return docList;
		
	}



	@Override
	public List<EzUploadDocument> getFilesById(Integer docId) {
		// TODO Auto-generated method stub
		List<EzUploadDocument> ezUploadDocumentList= ezUploadDocumentRepo.getFilesById(docId);
		return ezUploadDocumentList;
	}

	@Override
	public Optional<EzCustomerFormHeader> getDocumentByID(Integer docId) {
		// TODO Auto-generated method stub
		Optional<EzCustomerFormHeader> docList = ezCustomerFormHeaderRepo.findById(docId);
		return docList;
	}
	
	@Override
	public List<EzCustomerFormDetails> getDocumentDetailsByIDAndType(Integer docId,String type) {
		// TODO Auto-generated method stub
		List<EzCustomerFormDetails> docList = ezCustomerFormDetailsRepo.findAllByIdAndType(docId,type);
		return docList;
	} 
	
	@Override
	public List<String> getPrevApprMails(Integer docId,String type) {
		// TODO Auto-generated method stub
		List<String> emailList = ezCustomerFormDetailsRepo.findPrevApprMail(docId,type);
		return emailList;
	}


	@Override
	public EzCustomerFormHeader submitRequest(PPAPDocumentForm ppapDocumentForm, String status) {
		
		// TODO Auto-generated method stub
		Integer docId= ppapDocumentForm.getDocNo();
		List<EzPpapQuestionnaire> questionaireList= new ArrayList<EzPpapQuestionnaire>();
		Optional<EzCustomerFormHeader> ezCustomerForm=ezCustomerFormHeaderRepo.findById(docId);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	Users userObj = (Users)authentication.getPrincipal();
    	EzCustomerFormHeader ezCustomerFormheader=null;
		if(ezCustomerForm.isPresent()) {
			 ezCustomerFormheader = ezCustomerForm.get();
			 if(ezCustomerFormheader.getNextStep().equalsIgnoreCase("PPAP_INITIATION")) {
				 ezCustomerFormheader.setNextParticipant("ROLE_OS_TECH_CEAT");
				 ezCustomerFormheader.setCurrentStep("PPAP_INITIATION");
				 ezCustomerFormheader.setNextStep("PPAP_DRAWING"); 
				 ezCustomerFormheader.setStatus("SUBMITTED");
				 ezCustomerFormheader.setComments(ppapDocumentForm.getComments());
				 if(null !=ppapDocumentForm.getEzUploadDoc()) {
					 updateComments(ppapDocumentForm.getEzUploadDoc(),status); 
				 }
				
			 }
			 else if(ezCustomerFormheader.getNextStep().equalsIgnoreCase("PPAP_DRAWING")) {//2nd step
				 if("SUBMITTED".equalsIgnoreCase(status)) {
					 ezCustomerFormheader.setNextParticipant("ROLE_OS_QA_CEAT");
					 ezCustomerFormheader.setCurrentStep("PPAP_DRAWING");
					 ezCustomerFormheader.setNextStep("PPAP_HOMOLOGATION");
					 ezCustomerFormheader.setStatus("SUBMITTED"); 
					 ezCustomerFormheader.setComments(ppapDocumentForm.getComments());
				 }
				 else if("SENDBACK".equalsIgnoreCase(status)) {
					 ezCustomerFormheader.setNextParticipant("ROLE_OS_OP_CEAT");
					 ezCustomerFormheader.setCurrentStep("PPAP_DRAWING");
					 ezCustomerFormheader.setNextStep("PPAP_INITIATION");
					 ezCustomerFormheader.setStatus("SENDBACK");
					 ezCustomerFormheader.setComments(ppapDocumentForm.getComments());
					 if(null !=ppapDocumentForm.getEzUploadDoc()) {
						 updateComments(ppapDocumentForm.getEzUploadDoc(),status); 
					 }
				 }
			 
				
				//ezCustomerFormheader.setBusinessType(ppapDocumentForm.getTypeOfBusiness());
				
			 }
			 else if(ezCustomerFormheader.getNextStep().equalsIgnoreCase("PPAP_HOMOLOGATION")) {//3rd step
				 ezCustomerFormheader.setNextParticipant("ROLE_OS_TECH_CEAT"); 
				 ezCustomerFormheader.setCurrentStep("PPAP_HOMOLOGATION");
				 ezCustomerFormheader.setNextStep("PPAP_HOMOLOG_DOC_REVIEW");
				 ezCustomerFormheader.setTypeOfMaterial(ppapDocumentForm.getTypeOfMaterial());
				 ezCustomerFormheader.setStatus("SUBMITTED");
				 ezCustomerFormheader.setComments(ppapDocumentForm.getComments());
				 if(null !=ppapDocumentForm.getEzUploadDoc()) {
					 updateComments(ppapDocumentForm.getEzUploadDoc(),status); 
				 }
				 
				for(EzQuestionMap ezQuestionMap:ppapDocumentForm.getQuestionMapList()) {
				 addQuestionnaire(questionaireList, ezCustomerFormheader, ezQuestionMap.getName(), ezQuestionMap.getAnswer());
				}
				ezCustomerFormheader.setEzQuestionnaire(questionaireList); 
				  
				 
			 } 
			 else if(ezCustomerFormheader.getNextStep().equalsIgnoreCase("PPAP_HOMOLOG_DOC_REVIEW")) {//4th step
				 if("SUBMITTED".equalsIgnoreCase(status)){
					 ezCustomerFormheader.setNextParticipant("ROLE_OS_QA_VEND");
					 ezCustomerFormheader.setCurrentStep("PPAP_HOMOLOG_DOC_REVIEW");
					 ezCustomerFormheader.setNextStep("PPAP_UPLOAD_QUALITY");
					 ezCustomerFormheader.setStatus("SUBMITTED");
					 ezCustomerFormheader.setComments(ppapDocumentForm.getComments());
					 }
					 else if("SENDBACK".equalsIgnoreCase(status)) {
					 ezCustomerFormheader.setNextParticipant("ROLE_OS_QA_CEAT");
					 ezCustomerFormheader.setCurrentStep("PPAP_HOMOLOG_DOC_REVIEW");
					 ezCustomerFormheader.setNextStep("PPAP_HOMOLOGATION");
					 ezCustomerFormheader.setStatus("SENDBACK");
					 ezCustomerFormheader.setComments(ppapDocumentForm.getComments());
					 if(null !=ppapDocumentForm.getEzUploadDoc()) {
						 updateComments(ppapDocumentForm.getEzUploadDoc(),status); 
					 }
					 }
				 
			 }
			 else if(ezCustomerFormheader.getNextStep().equalsIgnoreCase("PPAP_UPLOAD_QUALITY")) {//5th step
				 ezCustomerFormheader.setNextParticipant("ROLE_OS_TECH_CEAT"); 
				 ezCustomerFormheader.setCurrentStep("PPAP_UPLOAD_QUALITY");
				 ezCustomerFormheader.setNextStep("PPAP_UPLOAD_QUALITY_REVIEW_TT");
				 ezCustomerFormheader.setStatus("SUBMITTED"); 
				 ezCustomerFormheader.setComments(ppapDocumentForm.getComments());
				 if(null !=ppapDocumentForm.getEzUploadDoc()) {
					 updateComments(ppapDocumentForm.getEzUploadDoc(),status); 
				 }
				 
			 }
			 else if(ezCustomerFormheader.getNextStep().equalsIgnoreCase("PPAP_UPLOAD_QUALITY_REVIEW_TT")) {//6th step technical team approval
				 if("SUBMITTED".equalsIgnoreCase(status)) {
					 ezCustomerFormheader.setNextParticipant("ROLE_OS_QA_CEAT"); 
					 ezCustomerFormheader.setCurrentStep("PPAP_UPLOAD_QUALITY_REVIEW_TT");
					 ezCustomerFormheader.setNextStep("PPAP_UPLOAD_QUALITY_REVIEW_QT");
					 ezCustomerFormheader.setStatus("SUBMITTED"); 
					 ezCustomerFormheader.setComments(ppapDocumentForm.getComments());
					 if(null !=ppapDocumentForm.getEzUploadDoc()) {
						 updateComments(ppapDocumentForm.getEzUploadDoc(),status); 
					 }
				 }
				 else if("REJECTED".equalsIgnoreCase(status)) {// if reject it will go to 5th step
					 ezCustomerFormheader.setNextParticipant("ROLE_OS_QA_VEND"); 
					 ezCustomerFormheader.setCurrentStep("PPAP_UPLOAD_QUALITY_REVIEW_TT");
					 ezCustomerFormheader.setNextStep("PPAP_UPLOAD_QUALITY");
					 ezCustomerFormheader.setStatus("REJECTED");
					 ezCustomerFormheader.setComments(ppapDocumentForm.getComments());
					 if(null !=ppapDocumentForm.getEzUploadDoc()) {
						 updateComments(ppapDocumentForm.getEzUploadDoc(),status); 
					 }
				 }
				 
			 }
			 else if(ezCustomerFormheader.getNextStep().equalsIgnoreCase("PPAP_UPLOAD_QUALITY_REVIEW_QT")) {//7th Step Quality Team approval
				 if("SUBMITTED".equalsIgnoreCase(status)) {
					 ezCustomerFormheader.setNextParticipant("ROLE_OS_CQA_CEAT"); 
					 ezCustomerFormheader.setCurrentStep("PPAP_UPLOAD_QUALITY_REVIEW_QT");
					 ezCustomerFormheader.setNextStep("PPAP_UPLOAD_QUALITY_REVIEW_CQT");
					 ezCustomerFormheader.setStatus("SUBMITTED"); 
					 ezCustomerFormheader.setComments(ppapDocumentForm.getComments());
					 if(null !=ppapDocumentForm.getEzUploadDoc()) {
						 updateComments(ppapDocumentForm.getEzUploadDoc(),status); 
					 }
				 }
				 else if("REJECTED".equalsIgnoreCase(status)) {// if reject it will go to 5th Step
					 ezCustomerFormheader.setNextParticipant("ROLE_OS_QA_VEND"); 
					 ezCustomerFormheader.setCurrentStep("PPAP_UPLOAD_QUALITY_REVIEW_QT");
					 ezCustomerFormheader.setNextStep("PPAP_UPLOAD_QUALITY");
					 ezCustomerFormheader.setStatus("REJECTED"); 
					 ezCustomerFormheader.setComments(ppapDocumentForm.getComments()); 
					 if(null !=ppapDocumentForm.getEzUploadDoc()) {
						 updateComments(ppapDocumentForm.getEzUploadDoc(),status); 
					 }
				 }
				 
			 }
			 else if(ezCustomerFormheader.getNextStep().equalsIgnoreCase("PPAP_UPLOAD_QUALITY_REVIEW_CQT")) {//8th Step Central Quality team approval
				 if("SUBMITTED".equalsIgnoreCase(status)) {
					 ezCustomerFormheader.setNextParticipant("ROLE_OS_HOS_CEAT"); 
					 ezCustomerFormheader.setCurrentStep("PPAP_UPLOAD_QUALITY_REVIEW_CQT");
					 ezCustomerFormheader.setNextStep("PPAP_UPLOAD_QUALITY_REVIEW_OH"); 
					 ezCustomerFormheader.setStatus("SUBMITTED"); 
					 ezCustomerFormheader.setComments(ppapDocumentForm.getComments()!=null?ppapDocumentForm.getComments().trim():ppapDocumentForm.getComments());
					 if(null !=ppapDocumentForm.getEzUploadDoc()) {
						 updateComments(ppapDocumentForm.getEzUploadDoc(),status); 
					 }
				 } 
				 else if("REJECTED".equalsIgnoreCase(status)) {//if reject it will go to 7th step
					 ezCustomerFormheader.setNextParticipant("ROLE_OS_QA_CEAT"); 
					 ezCustomerFormheader.setCurrentStep("PPAP_UPLOAD_QUALITY_REVIEW_CQT");
					 ezCustomerFormheader.setNextStep("PPAP_UPLOAD_QUALITY_REVIEW_QT");
					 ezCustomerFormheader.setStatus("REJECTED"); 
					 ezCustomerFormheader.setComments(ppapDocumentForm.getComments());
					 if(null !=ppapDocumentForm.getEzUploadDoc()) {
						 updateComments(ppapDocumentForm.getEzUploadDoc(),status); 
					 }
				 }
				 
			 }
			 else if(ezCustomerFormheader.getNextStep().equalsIgnoreCase("PPAP_UPLOAD_QUALITY_REVIEW_OH")) {//9th Step Outsourcing head Approval
				 if("APPROVED".equalsIgnoreCase(status)) {
				 ezCustomerFormheader.setNextParticipant(""); 
				 ezCustomerFormheader.setCurrentStep("PPAP_UPLOAD_QUALITY_REVIEW_OH");
				 ezCustomerFormheader.setNextStep("PPAP_COMPLETED");  
				 ezCustomerFormheader.setComments(ppapDocumentForm.getComments());
				 ezCustomerFormheader.setStatus("APPROVED"); 
				 }
				 else if("REJECTED".equalsIgnoreCase(status)) {//if reject it will go to 8th step
					 ezCustomerFormheader.setNextParticipant("ROLE_OS_CQA_CEAT"); 
					 ezCustomerFormheader.setCurrentStep("PPAP_UPLOAD_QUALITY_REVIEW_OH");
					 ezCustomerFormheader.setNextStep("PPAP_UPLOAD_QUALITY_REVIEW_CQT");
					 ezCustomerFormheader.setStatus("REJECTED");
					 ezCustomerFormheader.setComments(ppapDocumentForm.getComments());
					 if(null !=ppapDocumentForm.getEzUploadDoc()) {
						 updateComments(ppapDocumentForm.getEzUploadDoc(),status); 
					 }
				 }
				 
				 
			 }
				ezCustomerFormheader.setModifiedBy(userObj.getUserId()); 
				ezCustomerFormheader.setModifiedOn(new Date()); 
				
			
			ezCustomerFormHeaderRepo.save(ezCustomerFormheader); 
			if (ppapDocumentForm.getComments() != null && !ppapDocumentForm.getComments().trim().isEmpty()) {
				log.debug("saveComments:before:commetns"+ppapDocumentForm.getComments()); 
			    saveComments(ppapDocumentForm.getComments(), docId, userObj.getUserId(), ezCustomerFormheader.getCurrentStep());
			}
			log.debug("length of comments::"+ezCustomerFormheader.getComments().length());
			List<String> emailList=getPrevApprMails(ezCustomerFormheader.getDocId(),"previousApprover");
			emailService.sendRequestMailToNextPart(ezCustomerFormheader,emailList);
			 
		} 
		
		
		
		
		
		
		return ezCustomerFormheader;
	}
	
  private void addQuestionnaire(List<EzPpapQuestionnaire> questionaireList,EzCustomerFormHeader ezCustomerFormheader,String field, String value) {
				EzPpapQuestionnaire questionaire = new EzPpapQuestionnaire();
				questionaire.setTabName("PPAP_HOMOLOGATION");
				questionaire.setField(field);
				questionaire.setValue(value); 
				questionaire.setEzCustomerFormHeader(ezCustomerFormheader);
				questionaireList.add(questionaire);
				}

	@Override
	public List<EzUploadDocument> getFilesByType(Integer docId, String docType) {
		List<EzUploadDocument> fileList=ezUploadDocumentRepo.findFileByDocType(docId,docType);
		return fileList;
		
	}
	
	  @Transactional
	    public void updateComments(List<FileCommentsDto> comments,String staus) {
	        for (FileCommentsDto dto : comments) {
	        	log.debug("Comments =>" + comments);
	        	Optional<EzUploadDocument> documentOpt = ezUploadDocumentRepo.findByDocNoAndItemNoAndDocType( 
	                    dto.getDocNo(), dto.getItemNo(), dto.getDocType());
	 
	            if (documentOpt.isPresent()) {
	            	EzUploadDocument document = documentOpt.get();
	            	if("SUBMITTED".equalsIgnoreCase(staus) && null!=dto.getComment() && !"".equalsIgnoreCase(dto.getComment())) {
	            		 document.setComment(dto.getComment()); 
	            	}
	            	else {
	            		 document.setComment(dto.getComment()); 
	            	}
	               
	                ezUploadDocumentRepo.save(document);
	            }
	        }
	    }

	@Override
	public List<EzQuestionMap> getPpapQuestionaMap() {
		List<EzQuestionMap> questionList=new ArrayList<>();
		
		List<EzcPpapQuestionMap> questionaMapList=ezQuestionMapRepo.findAll();
		for(EzcPpapQuestionMap question:questionaMapList) {
			EzQuestionMap ezQuestionMap = new EzQuestionMap();
			ezQuestionMap.setId(question.getId());
			ezQuestionMap.setQuestion(question.getQuestion());
			ezQuestionMap.setName(question.getFieldName());
			ezQuestionMap.setAnswer(question.getDefaultAnswer());
			ezQuestionMap.setDocExist(question.isDocExist());
			questionList.add(ezQuestionMap);
			
		}
		
		return questionList; 
	}



	



}
*/
