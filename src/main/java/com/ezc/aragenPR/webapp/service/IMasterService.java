
package com.ezc.aragenPR.webapp.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ezc.aragenPR.webapp.dto.PlaceMasterDto;
import com.ezc.aragenPR.webapp.dto.WFRow;
import com.ezc.aragenPR.webapp.model.EzConditionSeqMap;
import com.ezc.aragenPR.webapp.model.EzConditionValueMap;
import com.ezc.aragenPR.webapp.model.EzMaterialLimits;
import com.ezc.aragenPR.webapp.model.EzMaterialSalesOrgData;
import com.ezc.aragenPR.webapp.model.EzMaterialSalesOrgDataKey;
import com.ezc.aragenPR.webapp.model.EzPlaceMaster;
import com.ezc.aragenPR.webapp.model.EzValueMapping;
//import com.ezc.aragenPR.webapp.model.EzWorkFlowKey;

public interface IMasterService {

	List<EzPlaceMaster> findAllCities();
	PlaceMasterDto addNewCity(PlaceMasterDto pDto);
	String deleteCity(int id);
	PlaceMasterDto getCityDetails(String city);
	List<EzValueMapping> findAllByMapKey(String mapKey);
	List<EzConditionSeqMap> findCondSeqMapByCondition(String condType);
	List<EzConditionSeqMap> findCondSeqMapByUserId(String userId);
	void addCondSeqMapping(List<EzConditionSeqMap> seqMap,String userId);
	Map<String, List<EzValueMapping>> getValueMapList(List<String> listInput);
	//EzWorkFlowKey addWorkflow(EzWorkFlowKey wfkey);
	//void updateWorkflow(List<WFRow> wfRowList,Integer wfKey);
	//EzWorkFlowKey getWFKey(String salesOrg,String distChnl,String mainChnl,String subChnl,String condType,String division,String creator,String brand);
	//EzWorkFlowKey getWorkFlowByKey(Integer wfKey);
	List<Object[]> getAllMaterialList();
	List<Object[]> getAllMaterialLimits();
	void updateMaterialLimit(List<EzMaterialLimits> matLimitList);
	List<EzMaterialSalesOrgData> getMaterialListById(List<EzMaterialSalesOrgDataKey> matSalesOrgList);
	//List<Object[]> getWorkflowDetails();
	//HashMap<String,String> getWFUsers();
	List<EzValueMapping> getChainMapList();
	void updateChainCodeMap(List<Object[]> lisObjArr);
	List<EzValueMapping> getChainMapByCode(String chainCode);
	List<EzConditionValueMap> getAllConditionValueMap();
	void updateMaterialDiscounts(List<EzConditionValueMap> matList);
	List<Object[]> getAllMaterialDiscounts();

}

