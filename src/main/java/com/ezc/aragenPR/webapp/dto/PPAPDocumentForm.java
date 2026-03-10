package com.ezc.aragenPR.webapp.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ezc.aragenPR.webapp.model.EzComments;
import com.ezc.aragenPR.webapp.model.EzUploadDocument;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PPAPDocumentForm {

	
		private Integer docNo;
		private String materialType;
		private String material;
		private String desc;
		private String plant;
		private List<String> selectedTypeOfBusiness;
		private boolean showSubmit;
		private boolean showReject;
		private boolean showApprove;
		private boolean showSendBack;
		private String action;
		private List<String> typeOfBusinessList;
		private List<String> tyreDocValuesList;
		private List<String> tubeDocValuesList;
		private List<String> flapDocValuesList;
		private String typeOfBusiness;
		private String typeOfMaterial;
		private HashMap<String, String> questionMap;
		private List<EzUploadDocument> initialFileList;
		private List<EzUploadDocument> drawingFileList;
		private List<EzUploadDocument> tyreHomoLogFileList;
		private List<EzUploadDocument> tubeHomoLogFileList;
		private List<EzUploadDocument> flapHomoLogFileList;
		List<PPAPFileDetails> tyreHomoLogFileDetails;
     	List<PPAPFileDetails> tubeHomoLogFileDetails;
     	List<PPAPFileDetails> flapHomoLogFileDetails;
     	private List<FileCommentsDto> ezUploadDoc;
		private String fileUrl;
		private String partSubWarrant;
		private String testResults;
		private String qualityAgreement;
		private String others;
		private List<EzQuestionMap> questionMapList= new ArrayList<EzQuestionMap>();
		private String currentStep;
		private String currentStepNo;
		private String currentUserRole;
		private boolean initiationTabAccess;
		private boolean drawingTabAccess;
		private boolean homologationTabAccess;
		private boolean uploadQualityDocTabAccess;
		private boolean uploadQualityTTReviewAccess;
		private boolean uploadQualityQTReviewAccess;
		private boolean uploadQualityOHReviewAccess;
		private boolean uploadQualityCTReviewAccess;
		private boolean initialFileListCount;
		private boolean homologationReviewTabAccess;
		private String comments;
		private String previousComments;
		private List<EzComments> commentsList;
		
		
}
