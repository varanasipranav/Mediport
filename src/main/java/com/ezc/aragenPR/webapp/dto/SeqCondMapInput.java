package com.ezc.aragenPR.webapp.dto;

import java.util.List;

import com.ezc.aragenPR.webapp.model.Users;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SeqCondMapInput {

	
	private List<SeqCondMapObj> condMapObjList;
	
	private List<Users> creatorList;
	
	private String selUser;	
	
	private List<String> checkedItems;	
	
}
