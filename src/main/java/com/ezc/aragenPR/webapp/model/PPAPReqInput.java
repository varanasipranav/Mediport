package com.ezc.aragenPR.webapp.model;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PPAPReqInput {
	
	private List<String> materialTypes;
	private String selMaterialType;
	private String selMaterial;
	private String selDesc;
	private MultipartFile file; 
	private String uuid;
	private List<String> selectedTypeOfBusiness;
	private List<String> typeOfBusinessList;
	private String errFlag;
	private String errText;
	private String comments;
	private String plant;
	List<EzValueMapping> locations;
	
	

}
