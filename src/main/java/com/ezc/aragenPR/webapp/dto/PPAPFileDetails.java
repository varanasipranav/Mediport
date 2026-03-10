package com.ezc.aragenPR.webapp.dto;



import com.ezc.aragenPR.webapp.model.EzUploadDocument;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PPAPFileDetails {
	
	private String documentType;
	private  EzUploadDocument ezUploadDoc;
	private boolean docExist;

}
