package com.ezc.aragenPR.webapp.service;

import java.util.List;

import com.ezc.aragenPR.webapp.dto.PlaceMasterDto;
import com.ezc.aragenPR.webapp.model.EzConditionSeqMap;
import com.ezc.aragenPR.webapp.model.EzPlaceMaster;
import com.ezc.aragenPR.webapp.model.EzPriceSeqMaster;

public interface IPriceReqService {
	
	List<EzPriceSeqMaster> getPriceSeqMaster();
	EzPriceSeqMaster getPriceSeqMasterById(int id);
	List<Object[]> getUserWFDefaults(String username);
	List<EzPriceSeqMaster> getPriceSeqByCondType(String condType,String userId);
	List<EzConditionSeqMap> getUserCondTypes(String userId);

}
