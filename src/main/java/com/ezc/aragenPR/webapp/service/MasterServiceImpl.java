package com.ezc.aragenPR.webapp.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ezc.aragenPR.webapp.dto.PlaceMasterDto;
import com.ezc.aragenPR.webapp.dto.SeqCondMapInput;
import com.ezc.aragenPR.webapp.dto.WFRow;
import com.ezc.aragenPR.webapp.model.EzConditionSeqMap;
import com.ezc.aragenPR.webapp.model.EzConditionValueMap;
import com.ezc.aragenPR.webapp.model.EzConditionValueMapKey;
import com.ezc.aragenPR.webapp.model.EzMaterialLimitKey;
import com.ezc.aragenPR.webapp.model.EzMaterialLimits;
import com.ezc.aragenPR.webapp.model.EzMaterialSalesOrgData;
import com.ezc.aragenPR.webapp.model.EzMaterialSalesOrgDataKey;
import com.ezc.aragenPR.webapp.model.EzPlaceMaster;
import com.ezc.aragenPR.webapp.model.EzValueMapping;
import com.ezc.aragenPR.webapp.model.EzValueMappingKey;
//import com.ezc.aragenPR.webapp.model.EzWorkFlowKey;
//import com.ezc.aragenPR.webapp.model.EzWorkFlowMapping;
//import com.ezc.aragenPR.webapp.model.EzWorkFlowMappingKey;
import com.ezc.aragenPR.webapp.persistance.dao.ConditionSeqMapRepo;
import com.ezc.aragenPR.webapp.persistance.dao.ConditionValueMapRepo;
import com.ezc.aragenPR.webapp.persistance.dao.MaterialLimitRepo;
import com.ezc.aragenPR.webapp.persistance.dao.MaterialSalesOrgDataRepo;
import com.ezc.aragenPR.webapp.persistance.dao.PlaceMasterRepo;
import com.ezc.aragenPR.webapp.persistance.dao.ValueMappingRepo;
//import com.ezc.aragenPR.webapp.persistance.dao.WorkflowMapRepository;
//import com.ezc.aragenPR.webapp.persistance.dao.WorkflowRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class MasterServiceImpl implements IMasterService {

	
	@Autowired
	PlaceMasterRepo placeMasterRep;

	
	@Autowired
	ValueMappingRepo valueMapRep;
	
	@Autowired
	ConditionSeqMapRepo condSeqRepo;
	
	//@Autowired
	//WorkflowRepository wfRepo;
	
	//@Autowired
	//WorkflowMapRepository wfMapRepo;
	
	@Autowired
	MaterialSalesOrgDataRepo matSalesOrgRepo;
	
	@Autowired
	MaterialLimitRepo matLimitRepo;
	
	@Autowired
	ConditionValueMapRepo conditionValueMapRepo;
	
	@Override
	public PlaceMasterDto addNewCity(final PlaceMasterDto pDto) {

		if(pDto!=null) {
			
			EzPlaceMaster pm = new EzPlaceMaster(); 
			pm.setCity(pDto.getCity());
			pm.setState(pDto.getState());
			pm.setCountry(pDto.getCountry());
			
			log.debug("city"+pDto.getCity());
			placeMasterRep.save(pm);	
			
		}else
		{
			
			throw new RuntimeException();
		}
		
		
		return null;
	}
	@Override
	public String deleteCity(int code) {


		Optional<EzPlaceMaster> OPlaceMaster= placeMasterRep.findById(code);
			
			if (OPlaceMaster.isPresent()) {

				placeMasterRep.deleteById(code);
			}
		return "";
	}
	@Override
	public PlaceMasterDto getCityDetails(String city) {
		PlaceMasterDto cityDto = placeMasterRep.cityDetails(city);
		return cityDto;

 	}
	 
	
	@Override
	public List<EzPlaceMaster> findAllCities() {
		return placeMasterRep.findAll();
	}
	@Override
	public List<EzValueMapping> findAllByMapKey(String mapKey) {	
		return valueMapRep.findByMapKey(mapKey);
	}
	@Override
	public List<EzConditionSeqMap> findCondSeqMapByCondition(String condType) {
		return condSeqRepo.findByConditionType(condType);
	}
	@Override
	public void addCondSeqMapping(List<EzConditionSeqMap> seqMap,String userId) {
		if(seqMap != null && seqMap.size() > 0)
		{
			condSeqRepo.deleteByUserId(userId);
			condSeqRepo.saveAll(seqMap);
		}
	}
	@Override
	public Map<String, List<EzValueMapping>> getValueMapList(List<String> listInput) {
		
		List<EzValueMapping> valueMapList= valueMapRep.findByMapKeyIn(listInput);
		Map<String, List<EzValueMapping>> outPutMap = valueMapList.stream().collect(Collectors.groupingBy(EzValueMapping::getMapKey));
		return outPutMap;
	}
	/*@Override
	public EzWorkFlowKey addWorkflow(EzWorkFlowKey wfkey) {
		return wfRepo.save(wfkey);
	}
	@Override
	public void updateWorkflow(List<WFRow> wfRowList, Integer wfKey) {
		wfMapRepo.deleteWFMapping(wfKey);
		Optional<EzWorkFlowKey> workflowKeyOpt = wfRepo.findById(wfKey);
		if(workflowKeyOpt.isPresent())
		{
			EzWorkFlowKey workflowKey=workflowKeyOpt.get();
			
//			List<EzWorkFlowMapping> wfMapList = workflowKey.getEzWorkFlowMapping();
//			
//			workflowKey.setEzWorkFlowMapping(null);
//			wfRepo.flush();
//			if(wfMapList != null)
//			{
//				for(EzWorkFlowMapping wfMapping:wfMapList)
//				{
//					try {
//						Optional<EzWorkFlowMapping> workflowMapOpt = wfMapRepo.findById(
//								new EzWorkFlowMappingKey(wfMapping.getEzWorkFlowKey(), wfMapping.getUserId()));
//						if (workflowMapOpt.isPresent())
//						{
//							log.debug("::::workflowMapOpt"+workflowMapOpt.get().getUserId());
//							//wfMapRepo.delete(workflowMapOpt.get());
//						}
//					} catch (Exception e) {
//						log.debug("Exception:::"+e);
//					}
//				}
//			}
			log.debug("after delete");
			List<EzWorkFlowMapping> wfMapListNew = new ArrayList<EzWorkFlowMapping>();
			for(WFRow wfRow:wfRowList)
    		{
    			List<String> userList=wfRow.getSelUserList();
    			for(String user:userList)
        		{
    				log.debug("before insert  new");
    				if(user == null || "null".equals(user) || "".equals(user))
    					continue;
					EzWorkFlowMapping wfMapObj=new EzWorkFlowMapping();
					wfMapObj.setEzWorkFlowKey(workflowKey);
					wfMapObj.setLevel(wfRow.getLevel());
					wfMapObj.setUserId(user);
					log.debug("user::"+user);
					log.debug("level::"+wfRow.getLevel());
					wfMapListNew.add(wfMapObj);
        		}		
    			
    		}
			workflowKey.setEzWorkFlowMapping(wfMapListNew);
		}
	}
	@Override	
	public EzWorkFlowKey getWFKey(String salesOrg, String distChnl, String mainChnl, String subChnl,String condType, String division,String creator,String matBrand) {
		
		log.debug("in getWFKey salesOrg:::"+salesOrg);
		log.debug("in getWFKey distChnl:::"+distChnl);
		log.debug("in getWFKey mainChnl:::"+mainChnl);
		log.debug("in getWFKey subChnl:::"+subChnl);
		log.debug("in getWFKey matBrand:::"+matBrand);
		log.debug("in getWFKey condType:::"+condType);
		log.debug("in getWFKey division:::"+division);
		log.debug("in getWFKey creator:::"+creator);
		
		List<EzWorkFlowKey> wfKeyList=wfRepo.searchWFKeyByCreator(creator);
		List<EzWorkFlowKey> foundList=new ArrayList<EzWorkFlowKey>(); 
		boolean recFound=false;
		if(wfKeyList == null)
			return null;
		log.debug("wfKeyList count before salesorg:::"+wfKeyList.size());
		if(salesOrg != null && !"null".equals(salesOrg) && !"".equals(salesOrg))
		{
			for(EzWorkFlowKey wfKey:wfKeyList)
			{
				if(!salesOrg.equals(wfKey.getSalesOrg()))
					foundList.add(wfKey);
			}
			wfKeyList.removeAll(foundList);
			if(wfKeyList.size() == 0)
			{
				log.debug("no workflow exists for sales org : "+salesOrg);
				return null;
				
			}
		}
		log.debug("wfKeyList count after salesorg:::"+wfKeyList.size());
		foundList=new ArrayList<EzWorkFlowKey>();
		if(matBrand != null && !"null".equals(matBrand) && !"".equals(matBrand))
		{
			for(EzWorkFlowKey wfKey:wfKeyList)
			{
				if(!matBrand.equals(wfKey.getBrand()))
					foundList.add(wfKey);
			}
			wfKeyList.removeAll(foundList);
			if(wfKeyList.size() == 0)
			{
				log.debug("no workflow exists for brand : "+matBrand);
				return null;
				
			}
		}
		log.debug("wfKeyList count before matBrand:::"+wfKeyList.size());
		foundList=new ArrayList<EzWorkFlowKey>();
		recFound=false;
		if(condType != null && !"null".equals(condType) && !"".equals(condType))
		{
			for(EzWorkFlowKey wfKey:wfKeyList)
			{
				if(!condType.equals(wfKey.getCondType()))
					foundList.add(wfKey);
				else
					recFound=true;
			}
			if(recFound)
				wfKeyList.removeAll(foundList);
//			if(wfKeyList.size() == 0)
//			{
//				log.debug("no workflow exists for condType : "+condType);
//				return null;
//				
//			}
		}
		log.debug("wfKeyList count before condType:::"+wfKeyList.size());
		
		foundList=new ArrayList<EzWorkFlowKey>();
		recFound=false;
		if(distChnl != null && !"null".equals(distChnl) && !"".equals(distChnl))
		{
			for(EzWorkFlowKey wfKey:wfKeyList)
			{
				if(!distChnl.equals(wfKey.getDistChnl()))
					foundList.add(wfKey);
				else
					recFound=true;
			}
			if(recFound)
				wfKeyList.removeAll(foundList);
		}
		log.debug("wfKeyList count before distChnl:::"+wfKeyList.size());
		foundList=new ArrayList<EzWorkFlowKey>();
		recFound=false;
		if(mainChnl != null && !"null".equals(mainChnl) && !"".equals(mainChnl))
		{
			for(EzWorkFlowKey wfKey:wfKeyList)
			{
				if(!mainChnl.equals(wfKey.getMainChnl()))
					foundList.add(wfKey);
				else
					recFound=true;
			}
			if(recFound)
				wfKeyList.removeAll(foundList);
		}
		log.debug("wfKeyList count before mainChnl:::"+wfKeyList.size());
		foundList=new ArrayList<EzWorkFlowKey>();
		recFound=false;
		if(subChnl != null && !"null".equals(subChnl) && !"".equals(subChnl))
		{
			for(EzWorkFlowKey wfKey:wfKeyList)
			{
				if(!subChnl.equals(wfKey.getSubChnl()))
					foundList.add(wfKey);
				else
					recFound=true;
			}
			if(recFound)
				wfKeyList.removeAll(foundList);
		}
		log.debug("wfKeyList count before subChnl:::"+wfKeyList.size());
		foundList=new ArrayList<EzWorkFlowKey>();
		recFound=false;
		if(division != null && !"null".equals(division) && !"".equals(division))
		{
			for(EzWorkFlowKey wfKey:wfKeyList)
			{
				if(!division.equals(wfKey.getDivision()))
					foundList.add(wfKey);
				else
					recFound=true;
			}
			if(recFound)
				wfKeyList.removeAll(foundList);
		}
		log.debug("wfKeyList count before division:::"+wfKeyList.size());
		if(wfKeyList != null && wfKeyList.size()>0)
			return wfKeyList.get(0);
		//return wfRepo.searchWFKey(salesOrg, distChnl, mainChnl, subChnl, condType, division).get(0);
		/*
		List<EzWorkFlowKey> wfKeyList=wfRepo.searchWFKeyByCreator(creator);
		log.debug("getWFKey check point 1");
		List<EzWorkFlowKey> wfSalesOrgList=wfKeyList.stream()
	              .filter(c -> salesOrg.equals(c.getSalesOrg()))
	              .collect(Collectors.toList());
		if(wfSalesOrgList == null || wfSalesOrgList.size() == 0)
		{
			return null;
		}
		if(wfSalesOrgList != null && wfSalesOrgList.size() == 1)
		{
			return wfSalesOrgList.get(0);
		}
		log.debug("getWFKey check point 2");
		
		List<EzWorkFlowKey> wfDistChnlList=wfSalesOrgList.stream()
	              .filter(c -> distChnl.equals(c.getDistChnl()))
	              .collect(Collectors.toList());
		if(wfDistChnlList == null || wfDistChnlList.size() == 0)
		{
			return wfSalesOrgList.get(0);
		}
		if(wfDistChnlList != null && wfDistChnlList.size() == 1)
		{
			return wfDistChnlList.get(0);
		}
		log.debug("getWFKey check point 3");
		List<EzWorkFlowKey> wfMainChnlList=wfDistChnlList.stream()
	              .filter(c -> mainChnl.equals(c.getMainChnl()))
	              .collect(Collectors.toList());
		if(wfMainChnlList == null || wfMainChnlList.size() == 0)
		{
			return wfDistChnlList.get(0);
		}
		if(wfMainChnlList != null && wfMainChnlList.size() == 1)
		{
			return wfMainChnlList.get(0);
		}
		log.debug("getWFKey check point 4");
		List<EzWorkFlowKey> wfSubChnlList=wfMainChnlList.stream()
	              .filter(c -> subChnl.equals(c.getSubChnl()))
	              .collect(Collectors.toList());
		if(wfSubChnlList == null)
		{
			return wfMainChnlList.get(0);
		}
		if(wfSubChnlList != null && wfSubChnlList.size() == 1)
		{
			return wfSubChnlList.get(0);
		}
		log.debug("getWFKey check point 5");
		
		List<EzWorkFlowKey> wfCondTypeList=wfSalesOrgList.stream()
	              .filter(c -> condType.equals(c.getCondType()))
	              .collect(Collectors.toList());
		if(wfCondTypeList == null || wfCondTypeList.size() == 0)
		{
			return wfSalesOrgList.get(0);
		}
		if(wfCondTypeList != null && wfCondTypeList.size() == 1)
		{
			return wfCondTypeList.get(0);
		}
		log.debug("getWFKey check point 6");
		
		List<EzWorkFlowKey> wfDivisionList=wfCondTypeList.stream()
	              .filter(c -> division.equals(c.getDivision()))
	              .collect(Collectors.toList());
		if(wfDivisionList == null || wfCondTypeList.size() == 0)
		{
			return wfCondTypeList.get(0);
		}
		if(wfDivisionList != null && wfDivisionList.size() > 0)
		{
			return wfDivisionList.get(0);
		}
		log.debug("getWFKey check point 7");

		return null;
	}
	@Override
	public EzWorkFlowKey getWorkFlowByKey(Integer wfKey) {
		Optional<EzWorkFlowKey> wfKeyOpt = wfRepo.findById(wfKey);
		if(wfKeyOpt.isPresent())
			return wfKeyOpt.get();
		else
			return null;
	}*/
	@Override
	public List<Object[]> getAllMaterialList() {
		return matSalesOrgRepo.getAllMaterialList();
	}
	
	@Override
	public List<EzMaterialSalesOrgData> getMaterialListById(List<EzMaterialSalesOrgDataKey> matSalesOrgList) {
		return matSalesOrgRepo.findAllById(matSalesOrgList);
	}
	
	@Override
	public List<Object[]> getAllMaterialLimits() {
		return matLimitRepo.getAllMaterialLimits();
	}
	@Override
	public void updateMaterialLimit(List<EzMaterialLimits> matLimitList) {
		for(EzMaterialLimits matLimit:matLimitList)
		{
			Optional<EzMaterialLimits> matLimitObj=matLimitRepo.findById(new EzMaterialLimitKey(matLimit.getBrand(),matLimit.getDistChnl(),matLimit.getLevel()));
			if(matLimitObj.isPresent())
			{
				EzMaterialLimits matLimitOut=matLimitObj.get();
				//matLimitOut.setBrand(matLimit.getBrand());
				matLimitOut.setUpper(matLimit.getUpper());
				matLimitOut.setLower(matLimit.getLower());
			}
			else
			{
				matLimitRepo.save(matLimit);
			}
		}
	}
	@Override
	public List<EzConditionSeqMap> findCondSeqMapByUserId(String userId) {
		return condSeqRepo.findByUserId(userId);
	}
	/*@Override
	public List<Object[]> getWorkflowDetails() {
		return wfRepo.getWorkFlowDetails();
	}*/
	/*@Override
	public HashMap<String, String> getWFUsers() {
		HashMap<String,String> hmUsers= new HashMap<String,String>();
		List<Object[]> objArr=wfMapRepo.getWFUserList();
		for(Object[] obj:objArr)
		{
			String key=obj[0]+"##"+obj[1];
			String value=obj[3]+" "+obj[4];
			if(hmUsers.containsKey(key))
			{
				value=hmUsers.get(key)+","+value;
				hmUsers.put(key, value);
			}
			else
			{
				hmUsers.put(key, value);
			}
		}
		return hmUsers;
	}*/
	@Override
	public List<EzValueMapping> getChainMapList() {
		return valueMapRep.findByMapKey("CHAINMAP");
	}
	@Override
	public void updateChainCodeMap(List<Object[]> lisObjArr) {
		
		for(Object[] objArr:lisObjArr)
		{
			String chainCode = (String)objArr[0];
			String distChnl = (String)objArr[1];
			valueMapRep.deleteChainMap(chainCode);
			EzValueMapping valueMap=new EzValueMapping();
			valueMap.setValue1(chainCode);
			valueMap.setValue2(distChnl);
			valueMap.setMapKey("CHAINMAP");
			valueMapRep.save(valueMap);
		}
	}

	@Override
	public List<EzValueMapping> getChainMapByCode(String chainCode) {
		return valueMapRep.getChainMap(chainCode);
	}
	@Override
	public List<EzConditionValueMap> getAllConditionValueMap() {
		return conditionValueMapRepo.findAll();
	}
	@Override
	public void updateMaterialDiscounts(List<EzConditionValueMap> matList) {
		for(EzConditionValueMap matLimit:matList)
		{
			Optional<EzConditionValueMap> matLimitObj=conditionValueMapRepo.findById(new EzConditionValueMapKey(matLimit.getConditionType(), matLimit.getBrand(), matLimit.getDistCat()));
			if(matLimitObj.isPresent())
			{
				EzConditionValueMap matLimitOut=matLimitObj.get();
				//matLimitOut.setBrand(matLimit.getBrand());
				matLimitOut.setValue(matLimit.getValue());
			}
			else
			{
				conditionValueMapRepo.save(matLimit);
			}
		}
		
	}
	@Override
	public List<Object[]> getAllMaterialDiscounts() {
		return conditionValueMapRepo.getAllMaterialDiscounts();
	}
	

}
