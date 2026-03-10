package com.ezc.aragenPR.webapp.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.ezc.aragenPR.webapp.dto.ListSelector;
import com.ezc.aragenPR.webapp.dto.PriceFormDetInput;
import com.ezc.aragenPR.webapp.dto.PriceFormViewDet;
import com.ezc.aragenPR.webapp.dto.PriceRequestItemDto;
import com.ezc.aragenPR.webapp.model.EzComments;
import com.ezc.aragenPR.webapp.model.EzMaterialLimitKey;
import com.ezc.aragenPR.webapp.model.EzMaterialLimits;
import com.ezc.aragenPR.webapp.model.EzMaterialMaster;
import com.ezc.aragenPR.webapp.model.EzMaterialSalesOrgData;
import com.ezc.aragenPR.webapp.model.EzMaterialSalesOrgDataKey;
import com.ezc.aragenPR.webapp.model.EzPriceRequestHeader;
import com.ezc.aragenPR.webapp.model.EzPriceRequestItems;
import com.ezc.aragenPR.webapp.model.EzPriceSeqMaster;
//import com.ezc.aragenPR.webapp.model.EzWorkFlowKey;
//import com.ezc.aragenPR.webapp.model.EzWorkFlowMapping;
import com.ezc.aragenPR.webapp.model.Users;
import com.ezc.aragenPR.webapp.persistance.dao.CommentsRepo;
import com.ezc.aragenPR.webapp.persistance.dao.MaterialLimitRepo;
import com.ezc.aragenPR.webapp.persistance.dao.MaterialMasterRepo;
import com.ezc.aragenPR.webapp.persistance.dao.MaterialSalesOrgDataRepo;
import com.ezc.aragenPR.webapp.persistance.dao.PriceRequestHeaderRepository;
import com.ezc.aragenPR.webapp.persistance.dao.RequestCustomDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class RequestService implements IRequestService{
	
		private static final Exception Exception = null;

		@Autowired
		private  PriceRequestHeaderRepository headerRep;
		
		@Autowired
		private  RequestCustomDto reqCustomDto;

		@Autowired
		private  CommentsRepo commentsRepo;
		
		@Autowired
		private IMasterService masterService;

		@Autowired
		private IEmailService emailService;
		
		@Autowired
		private MaterialLimitRepo matLimitRepo;
		
		@Autowired
		private MaterialSalesOrgDataRepo materialSalesOrgDataRepo;
		
		@Override
		public List<EzPriceRequestHeader> createRequest(PriceFormDetInput priceFormDetInput) {
			
			log.info("In createRequest service");
			List<EzPriceRequestHeader> outList=new ArrayList<EzPriceRequestHeader>();
			EzPriceSeqMaster seqMaster= priceFormDetInput.getPriceSeqMasterObj();
			
			List<PriceRequestItemDto> priceRequestItemsDtoList= priceFormDetInput.getPriceRequestItems();
			Hashtable<String,List<PriceRequestItemDto>> htWFSplitTable=new Hashtable<String,List<PriceRequestItemDto>>(); 	
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			String [] condTypeArr = priceFormDetInput.getCondType().split("##");
			log.info("In createRequest before user details");
	    	Users userObj = (Users)authentication.getPrincipal();
	    	log.info("In createRequest after user details");
	    	log.debug(":::priceRequestItemsDtoList:size::"+priceRequestItemsDtoList.size());
	    	for(PriceRequestItemDto priceRequestItemDto:priceRequestItemsDtoList)
	    	{
	    		if("Y".equals(priceRequestItemDto.getErrFlag()))
	    			continue;
	    		String tempWFKey=priceRequestItemDto.getItemWFKey();
	    		if(htWFSplitTable.containsKey(tempWFKey))
	    		{
	    			List<PriceRequestItemDto> tempList=htWFSplitTable.get(tempWFKey);
	    			tempList.add(priceRequestItemDto);
	    			htWFSplitTable.put(tempWFKey, tempList);
	    		}
	    		else
	    		{
	    			List<PriceRequestItemDto> tempList=new ArrayList<PriceRequestItemDto>();
	    			tempList.add(priceRequestItemDto);
	    			htWFSplitTable.put(tempWFKey, tempList);
	    		}
	    	}
	    	Set<String> wfKeys = htWFSplitTable.keySet();
	        Iterator<String> wfKeysItr = wfKeys.iterator();
	    	
	        
	        while (wfKeysItr.hasNext()) 
	        {
	        	List<EzPriceRequestItems> priceReqItemList= new ArrayList<EzPriceRequestItems>();
	        	String tempWfKey=wfKeysItr.next();
		    	//String salesOrg="",distChnl="",mainChnl="",subChnl="",condType="",division="",matBrand="";
	        	String salesOrg="";
		    	List<PriceRequestItemDto> tempList=htWFSplitTable.get(tempWfKey);
		    	log.debug(tempWfKey+":::tempList:::"+tempList.size());
		    	for(PriceRequestItemDto priceRequestItemDto:tempList)
		    	{
		    		EzPriceRequestItems reqItem=new EzPriceRequestItems();
		    		reqItem.setBrand(priceRequestItemDto.getBrand());
		    		reqItem.setChainCode(priceRequestItemDto.getChainCode());
		    		reqItem.setCustomer(priceRequestItemDto.getCustomer());
		    		reqItem.setCustType(priceRequestItemDto.getCustType());
		    		reqItem.setDistCat(priceRequestItemDto.getDistCat());
		    		reqItem.setDistChnl(priceRequestItemDto.getDistChnl());
		    		reqItem.setDivision(priceRequestItemDto.getDivision());
		    		reqItem.setLineNo(priceRequestItemDto.getLineNo());
		    		reqItem.setMainChnl(priceRequestItemDto.getMainChnl());
		    		reqItem.setMaterial(priceRequestItemDto.getMaterial());
		    		reqItem.setPlant(priceRequestItemDto.getPlant());
		    		reqItem.setPrdType(priceRequestItemDto.getPrdType());
		    		reqItem.setSalesDocType(priceRequestItemDto.getSalesDocType());
		    		reqItem.setSalesOffice(priceRequestItemDto.getSalesOffice());
		    		reqItem.setSalesOrg(priceRequestItemDto.getSalesOrg());
		    		reqItem.setSubChnl(priceRequestItemDto.getSubChnl());
		    		reqItem.setValidFrom(priceRequestItemDto.getValidFrom());
		    		reqItem.setValidTo(priceRequestItemDto.getValidTo());
		    		reqItem.setValue(priceRequestItemDto.getValue());
		    		reqItem.setMrp(priceRequestItemDto.getMrp());
		    		reqItem.setListPrice(priceRequestItemDto.getLprice());
		    		reqItem.setSubTot(priceRequestItemDto.getSubtot());
		    		reqItem.setCogs(priceRequestItemDto.getVprs());
		    		reqItem.setMarAftDis(priceRequestItemDto.getMarAftDis());
		    		reqItem.setMarBefDis(priceRequestItemDto.getMarBefDis());
		    		reqItem.setConditions(priceRequestItemDto.getConditions());
		    		reqItem.setCondTot(priceRequestItemDto.getCondTot());
		    		reqItem.setTrdDis(priceRequestItemDto.getTrdDis());
		    		reqItem.setBudgetGC(priceRequestItemDto.getBudgetGC());
		    		reqItem.setGcAmount(priceRequestItemDto.getGcAmount());
		    		reqItem.setGstPer(priceRequestItemDto.getGstPer());
		    		reqItem.setGstVal(priceRequestItemDto.getGstVal());
		    		reqItem.setTvc(priceRequestItemDto.getTvc());
		    		reqItem.setVarOHDisPer(priceRequestItemDto.getVarOHDisPer());
		    		reqItem.setVarOHDisVal(priceRequestItemDto.getVarOHDisVal());
		    		
		    		
		    		if("".equals(salesOrg))
		    		{
		    			salesOrg=priceRequestItemDto.getSalesOrg();
//		    			distChnl=priceRequestItemDto.getDistChnl();
//		    			mainChnl=priceRequestItemDto.getMainChnl();
//		    			subChnl=priceRequestItemDto.getSubChnl();
//		    			condType=condTypeArr[0];
//		    			division=priceRequestItemDto.getDivision();
		    		}
		    		priceReqItemList.add(reqItem);
		    	}
		    	//EzWorkFlowKey wfKey=masterService.getWFKey(salesOrg, distChnl, mainChnl, subChnl, condType, division,userObj.getUserId(),matBrand);
		    	//log.debug("selected key::"+wfKey.getWfKey());
				EzPriceRequestHeader priceReqHeader=new EzPriceRequestHeader();
				priceReqHeader.setCreatedBy(userObj.getUserId());
				priceReqHeader.setCreatedOn(new Date());
				priceReqHeader.setModifiedBy(userObj.getUserId());
				priceReqHeader.setModifiedOn(new Date());
				priceReqHeader.setWfKey(Integer.parseInt(tempWfKey));
				priceReqHeader.setSeqId(seqMaster.getId());
				if("9999".equals(tempWfKey))
				{	
					
					log.info("Approval");
			    	priceReqHeader.setApprovedBy(userObj.getUserId()+"-"+userObj.getFirstName());
					priceReqHeader.setStatus("A");
					priceReqHeader.setWfLevel(2);
				}
				else
				{
					priceReqHeader.setStatus("S");
					priceReqHeader.setWfLevel(2);
				}
				priceReqHeader.setTable(seqMaster.getTable());
				priceReqHeader.setCondType(condTypeArr[0]);
				priceReqHeader.setCondDesc(condTypeArr[1]);
				priceReqHeader.setWfDistChnl(priceFormDetInput.getWfDistChnl());
				priceReqHeader.setDistCat(priceFormDetInput.getDistCat());
				if(priceFormDetInput.getValArea() != null && priceFormDetInput.getValArea().indexOf("##") > 0)
				{
					String [] valAreaArr = priceFormDetInput.getValArea().split("##");
					priceReqHeader.setValArea(valAreaArr[0]);
					priceReqHeader.setValDesc(valAreaArr[1]);
				}
//				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy");
//				Date lastDate = null;
//				try {
//					lastDate = sdf.parse("31/12/9999");
//				} catch (ParseException e) {
//				}
				List<EzPriceRequestItems> ezPriceRequestItems = new ArrayList<EzPriceRequestItems>();
				if(priceReqItemList != null)
				{
					int lineCnt = 0;
					for(EzPriceRequestItems ezPriceRequestItem:priceReqItemList)
					{
						log.info("material:::"+ezPriceRequestItem.getMaterial());
						if(ezPriceRequestItem.getValue() == 0)
								continue;
						lineCnt+=10;
						//ezPriceRequestItem.setValidFrom(new Date());
						//ezPriceRequestItem.setValidTo(lastDate);
					/*
					 * if("ZLP".equals(condTypeArr[0])) { double tempVal=0.0; tempVal =
					 * ezPriceRequestItem.getValue()-(ezPriceRequestItem.getValue()*0.18);
					 * ezPriceRequestItem.setValue(tempVal); }
					 */
						ezPriceRequestItem.setLineNo(lineCnt);
						ezPriceRequestItem.setEzPriceRequestHeader(priceReqHeader);
						ezPriceRequestItems.add(ezPriceRequestItem);
					}
				}
				priceReqHeader.setEzPriceRequestItems(ezPriceRequestItems);
				EzPriceRequestHeader priceReqOutObj= headerRep.save(priceReqHeader);
				EzComments commentObj=new EzComments();
				commentObj.setComments(priceFormDetInput.getComments());
				commentObj.setDate(new Date());
				commentObj.setDocId(priceReqOutObj.getId());
				commentObj.setDocType("PRICEAPR");
				commentObj.setCreatedBy(userObj.getUserId());
				commentsRepo.save(commentObj);
				outList.add(priceReqOutObj);
	        }
			//emailService.sendRequestMailToNextPart(priceReqOutObj);
			
			return outList;
		}

		@Override
		public List<EzPriceRequestHeader> getRequestList(ListSelector listSelector) {
			
			return reqCustomDto.findRequestList(listSelector);
		}

		@Override
		public EzPriceRequestHeader getRequestById(String docId) {
			EzPriceRequestHeader ezPriceRequestHeader=null;
			Optional<EzPriceRequestHeader> reqOptObj=headerRep.findById(docId);
			if(reqOptObj.isPresent())
				ezPriceRequestHeader=reqOptObj.get();
			return ezPriceRequestHeader;
		}

		@Override
		public List<EzComments> getRequestComments(String docId) {
			
			return commentsRepo.findByDocIdAndDocType(docId, "PRICEAPR");
		}

		/*
		 * @Override public EzPriceRequestHeader submitRequest(PriceFormViewDet
		 * priceFormViewDet,String status) { EzPriceRequestHeader
		 * priceReqHeaderInput=priceFormViewDet.getReqHeader();
		 * 
		 * 
		 * String docId=priceReqHeaderInput.getId(); Authentication authentication =
		 * SecurityContextHolder.getContext().getAuthentication(); Users userObj =
		 * (Users)authentication.getPrincipal();
		 * 
		 * Optional<EzPriceRequestHeader> priceRequestHeaderOpt =
		 * headerRep.findById(docId); if(priceRequestHeaderOpt.isPresent()) {
		 * EzPriceRequestHeader priceRequestHeader=priceRequestHeaderOpt.get();
		 * EzWorkFlowKey workFlowKey =
		 * masterService.getWorkFlowByKey(priceRequestHeader.getWfKey());
		 * List<EzWorkFlowMapping> workFlowMapList = workFlowKey.getEzWorkFlowMapping();
		 * String wfDistChnl = workFlowKey.getDistChnl(); String wfMainChnl =
		 * workFlowKey.getMainChnl(); String wfSubChnl = workFlowKey.getSubChnl(); int
		 * maxlevel=0; int curLevel = priceRequestHeader.getWfLevel();
		 * if(workFlowMapList != null) { for(EzWorkFlowMapping
		 * workFlowMapping:workFlowMapList) { if(workFlowMapping.getLevel() > maxlevel)
		 * maxlevel = workFlowMapping.getLevel(); } } if("S".equals(status)) { boolean
		 * allowApprove=false;
		 * 
		 * 
		 * List<EzPriceRequestItems>
		 * priceReqItemList=priceRequestHeader.getEzPriceRequestItems(); //***
		 * if(matLimits != null) { for(EzPriceRequestItems itemDto:priceReqItemList) {
		 * EzMaterialLimits compMatLimitObj=null; try { List<EzMaterialLimits>
		 * matLimitList = (List<EzMaterialLimits>) matLimits.stream().filter(m ->
		 * m.getMaterialNo().equals(itemDto.getMaterial())).collect(Collectors.toList())
		 * ; if(matLimitList != null && matLimitList.size() > 0) {
		 * if(matLimitList.size() == 1) { compMatLimitObj = matLimitList.get(0); } else
		 * { List<EzMaterialLimits> matLimitListByDist = (List<EzMaterialLimits>)
		 * matLimitList.stream().filter(m ->
		 * m.getDistChnl().equals(wfDistChnl)).collect(Collectors.toList());
		 * if(matLimitListByDist == null || matLimitListByDist.size() == 0) {
		 * compMatLimitObj = matLimitList.get(0); } else { if(matLimitListByDist.size()
		 * == 1) { compMatLimitObj = matLimitListByDist.get(0); } else {
		 * List<EzMaterialLimits> matLimitListByMainChnl = (List<EzMaterialLimits>)
		 * matLimitListByDist.stream().filter(m ->
		 * m.getMainChnl().equals(wfMainChnl)).collect(Collectors.toList());
		 * if(matLimitListByMainChnl == null || matLimitListByMainChnl.size() == 0) {
		 * compMatLimitObj = matLimitListByDist.get(0); } else {
		 * if(matLimitListByMainChnl.size() == 1) { compMatLimitObj =
		 * matLimitListByMainChnl.get(0); } else { List<EzMaterialLimits>
		 * matLimitListBySubChnl = (List<EzMaterialLimits>)
		 * matLimitListByMainChnl.stream().filter(m ->
		 * m.getSubChnl().equals(wfSubChnl)).collect(Collectors.toList());
		 * if(matLimitListBySubChnl == null || matLimitListBySubChnl.size() == 0) {
		 * compMatLimitObj = matLimitListByMainChnl.get(0); } else { compMatLimitObj =
		 * matLimitListBySubChnl.get(0); } } } } }
		 * 
		 * } }
		 * 
		 * }catch(Exception e) { log.error("Exception while getting limits::"+e);
		 * allowApprove = false; } if(compMatLimitObj != null) {
		 * log.debug(itemDto.getMaterial()+"::::Margin"+itemDto.getMarAftDis()+
		 * "::::Lower"+compMatLimitObj.getLower()+"::::Upper"+compMatLimitObj.getUpper()
		 * ); try { if(itemDto.getMarAftDis() >= compMatLimitObj.getLower() &&
		 * itemDto.getMarAftDis() <= compMatLimitObj.getUpper()) { allowApprove = true;
		 * }else { allowApprove = false; } }catch(Exception e) {
		 * log.error("Exception while comparing limits::"+e); allowApprove = false; } }
		 * if(!allowApprove) break; } }
		 * 
		 * //***** if(curLevel == maxlevel || allowApprove)
		 * priceRequestHeader.setStatus("A"); else {
		 * priceRequestHeader.setWfLevel(curLevel+1); priceRequestHeader.setStatus("S");
		 * } emailService.sendRequestMailToNextPart(priceRequestHeader); } else
		 * if("P".equals(status)) { priceRequestHeader.setStatus("P"); } else
		 * if("R".equals(status)) { priceRequestHeader.setStatus("R");
		 * emailService.sendRequestMailToNextPart(priceRequestHeader); }
		 * 
		 * EzComments commentObj=new EzComments();
		 * commentObj.setComments(priceFormViewDet.getComments());
		 * commentObj.setDate(new Date()); commentObj.setDocId(docId);
		 * commentObj.setDocType("PRICEAPR");
		 * commentObj.setCreatedBy(userObj.getUserId()); commentsRepo.save(commentObj);
		 * return priceRequestHeader; }
		 * 
		 * return null; }
		 */
		/*@Override
		public EzPriceRequestHeader submitRequest(PriceFormViewDet priceFormViewDet,String status) {
			EzPriceRequestHeader priceReqHeaderInput=priceFormViewDet.getReqHeader();
			
			
			String docId=priceReqHeaderInput.getId();
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    	Users userObj = (Users)authentication.getPrincipal();
			
			Optional<EzPriceRequestHeader> priceRequestHeaderOpt = headerRep.findById(docId);
			if(priceRequestHeaderOpt.isPresent())
			{
				EzPriceRequestHeader priceRequestHeader=priceRequestHeaderOpt.get();
				EzWorkFlowKey workFlowKey = masterService.getWorkFlowByKey(priceRequestHeader.getWfKey());
				List<EzWorkFlowMapping> workFlowMapList = workFlowKey.getEzWorkFlowMapping();
				String wfDistChnl = workFlowKey.getDistChnl();
				String wfMainChnl = workFlowKey.getMainChnl();
				String wfSubChnl = workFlowKey.getSubChnl();
				int maxlevel=0;
				int curLevel = priceRequestHeader.getWfLevel();
				*//*if(workFlowMapList != null)
				{
					for(EzWorkFlowMapping workFlowMapping:workFlowMapList)
					{
						if(workFlowMapping.getLevel() > maxlevel)
							maxlevel = workFlowMapping.getLevel();
					}
				}*//*
				if("S".equals(status))
				{
					boolean allowApprove=false;
					String tempBrand="";
					
					EzPriceRequestItems tempItem=priceRequestHeader.getEzPriceRequestItems().get(0);
					String tempMaterial=tempItem.getMaterial();
					String tempSalesOrg=tempItem.getSalesOrg();
					if(tempMaterial != null && !"null".equals(tempMaterial) && !"".equals(tempMaterial))
					{
						Optional<EzMaterialSalesOrgData> tempMatSalesOrgKey= materialSalesOrgDataRepo.findById(new EzMaterialSalesOrgDataKey(tempMaterial, tempSalesOrg, "ST"));
						if(tempMatSalesOrgKey.isPresent())
							tempBrand=tempMatSalesOrgKey.get().getBrand();
						
					}
					List<EzPriceRequestItems> priceReqItemList=priceRequestHeader.getEzPriceRequestItems();
					EzMaterialLimits compMatLimitObj=null;
					Optional<EzMaterialLimits> matLimitKeyOpt= matLimitRepo.findById(new EzMaterialLimitKey(tempBrand, wfDistChnl, curLevel));
					
						if(matLimitKeyOpt.isPresent())
						{
							compMatLimitObj=matLimitKeyOpt.get();
					
						for(EzPriceRequestItems itemDto:priceReqItemList)
						{
							if(compMatLimitObj != null)
							{
								log.debug(itemDto.getMaterial()+"::::Margin"+itemDto.getMarAftDis()+"::::Lower"+compMatLimitObj.getLower()+"::::Upper"+compMatLimitObj.getUpper());
								try
								{
									if(itemDto.getMarAftDis() >= compMatLimitObj.getLower() && itemDto.getMarAftDis() <= compMatLimitObj.getUpper())
									{
										allowApprove = true;
									}else
									{
										allowApprove = false;
									}
								}catch(Exception e) {
									log.error("Exception while comparing limits::"+e);
									allowApprove = false;
								}
							}
							if(!allowApprove)
								break;
						}
					}					
					
				//*****
					if(curLevel == maxlevel || allowApprove)
					{
						log.info("Approval");
				    	priceRequestHeader.setApprovedBy(userObj.getUserId()+"-"+userObj.getFirstName());
						priceRequestHeader.setStatus("A");
					}
					else
					{
						priceRequestHeader.setWfLevel(curLevel+1);
						priceRequestHeader.setStatus("S");
					}
					//emailService.sendRequestMailToNextPart(priceRequestHeader);
				}
				else if("P".equals(status))
				{
					priceRequestHeader.setStatus("P");
				}
				else if("R".equals(status))
				{
					priceRequestHeader.setStatus("R");
					//emailService.sendRequestMailToNextPart(priceRequestHeader);
				}
				
				EzComments commentObj=new EzComments();
				commentObj.setComments(priceFormViewDet.getComments());
				commentObj.setDate(new Date());
				commentObj.setDocId(docId);
				commentObj.setDocType("PRICEAPR");
				commentObj.setCreatedBy(userObj.getUserId());
				commentsRepo.save(commentObj);
				return priceRequestHeader;
			}
			
			return null;
		}
*/
		/*
		 * @Override public List<EzMaterialLimits>
		 * findReqMatLimitsByMaterial(List<String> matList) { return
		 * matLimitRepo.findReqMatLimitsByMaterial(matList); }
		 */

		/*
		 * @Override public void autoApproveCheck(String docId) {
		 * Optional<EzPriceRequestHeader> priceRequestHeaderOpt =
		 * headerRep.findById(docId); if(priceRequestHeaderOpt.isPresent()) {
		 * HashMap<Integer,Boolean> hmApprSts = new HashMap<Integer,Boolean>();
		 * EzPriceRequestHeader priceRequestHeader=priceRequestHeaderOpt.get();
		 * EzWorkFlowKey workFlowKey =
		 * masterService.getWorkFlowByKey(priceRequestHeader.getWfKey());
		 * List<EzWorkFlowMapping> workFlowMapList = workFlowKey.getEzWorkFlowMapping();
		 * String wfDistChnl = workFlowKey.getDistChnl(); String wfMainChnl =
		 * workFlowKey.getMainChnl(); String wfSubChnl = workFlowKey.getSubChnl(); int
		 * maxlevel=0; int curLevel = priceRequestHeader.getWfLevel();
		 * if(workFlowMapList != null) { for(EzWorkFlowMapping
		 * workFlowMapping:workFlowMapList) { if(workFlowMapping.getLevel() > maxlevel)
		 * maxlevel = workFlowMapping.getLevel(); } }
		 * 
		 * //boolean allowApprove=false; List<EzMaterialLimits>
		 * matLimits=matLimitRepo.findReqMatLimits(docId,curLevel);
		 * List<EzPriceRequestItems>
		 * priceReqItemList=priceRequestHeader.getEzPriceRequestItems(); //***
		 * log.debug("matLimits:::"+matLimits); if(matLimits != null &&
		 * !matLimits.isEmpty()) { for(EzPriceRequestItems itemDto:priceReqItemList) {
		 * EzMaterialLimits compMatLimitObj=null; try { List<EzMaterialLimits>
		 * matLimitList = (List<EzMaterialLimits>) matLimits.stream().filter(m ->
		 * m.getMaterialNo().equals(itemDto.getMaterial())).collect(Collectors.toList())
		 * ; if(matLimitList == null || matLimitList.isEmpty()) { continue; }
		 * if(matLimitList != null && matLimitList.size() > 0) { if(matLimitList.size()
		 * == 1) { compMatLimitObj = matLimitList.get(0); } else {
		 * List<EzMaterialLimits> matLimitListByDist = (List<EzMaterialLimits>)
		 * matLimitList.stream().filter(m ->
		 * m.getDistChnl().equals(wfDistChnl)).collect(Collectors.toList());
		 * if(matLimitListByDist == null || matLimitListByDist.size() == 0) {
		 * compMatLimitObj = matLimitList.get(0); } else { if(matLimitListByDist.size()
		 * == 1) { compMatLimitObj = matLimitListByDist.get(0); } else {
		 * List<EzMaterialLimits> matLimitListByMainChnl = (List<EzMaterialLimits>)
		 * matLimitListByDist.stream().filter(m ->
		 * m.getMainChnl().equals(wfMainChnl)).collect(Collectors.toList());
		 * if(matLimitListByMainChnl == null || matLimitListByMainChnl.size() == 0) {
		 * compMatLimitObj = matLimitListByDist.get(0); } else {
		 * if(matLimitListByMainChnl.size() == 1) { compMatLimitObj =
		 * matLimitListByMainChnl.get(0); } else { List<EzMaterialLimits>
		 * matLimitListBySubChnl = (List<EzMaterialLimits>)
		 * matLimitListByMainChnl.stream().filter(m ->
		 * m.getSubChnl().equals(wfSubChnl)).collect(Collectors.toList());
		 * if(matLimitListBySubChnl == null || matLimitListBySubChnl.size() == 0) {
		 * compMatLimitObj = matLimitListByMainChnl.get(0); } else { compMatLimitObj =
		 * matLimitListBySubChnl.get(0); } } } } }
		 * 
		 * } }
		 * 
		 * }catch(Exception e) { log.error("Exception while getting limits::"+e);
		 * //allowApprove = false; } if(compMatLimitObj != null) {
		 * log.debug("autoapproval:::::"+itemDto.getMaterial()+"::::Margin"+itemDto.
		 * getMarAftDis()+"::::Lower"+compMatLimitObj.getLower()+"::::Upper"+
		 * compMatLimitObj.getUpper()); try { if(itemDto.getMarAftDis() >
		 * compMatLimitObj.getUpper()) { //allowApprove = true;
		 * hmApprSts.put(itemDto.getLineNo(),true); }else {
		 * hmApprSts.put(itemDto.getLineNo(),false); //allowApprove = false; }
		 * }catch(Exception e) { log.error("Exception while comparing limits::"+e);
		 * //allowApprove = false; } } //if(!allowApprove) //break; } } else {
		 * //allowApprove = true; }
		 * 
		 * //***** boolean allowApprove=true; log.debug("hmApprSts::::"+hmApprSts);
		 * for(EzPriceRequestItems itemDto:priceReqItemList) {
		 * if(hmApprSts.containsKey(itemDto.getLineNo()) &&
		 * !hmApprSts.get(itemDto.getLineNo())) { allowApprove=false;
		 * itemDto.setStatus("S"); } else { itemDto.setStatus("A"); } }
		 * 
		 * if(allowApprove) priceRequestHeader.setStatus("A");
		 * 
		 * }
		 * 
		 * }
		 */
		/*@Override
		public void autoApproveCheck(String docId) {
			Optional<EzPriceRequestHeader> priceRequestHeaderOpt = headerRep.findById(docId);
			if(priceRequestHeaderOpt.isPresent())
			{
				HashMap<Integer,Boolean> hmApprSts = new HashMap<Integer,Boolean>(); 
				EzPriceRequestHeader priceRequestHeader=priceRequestHeaderOpt.get();
				EzWorkFlowKey workFlowKey = masterService.getWorkFlowByKey(priceRequestHeader.getWfKey());
				List<EzWorkFlowMapping> workFlowMapList = workFlowKey.getEzWorkFlowMapping();
				String wfDistChnl = workFlowKey.getDistChnl();
				int maxlevel=0;
				int curLevel = priceRequestHeader.getWfLevel();
				if(workFlowMapList != null)
				{
					for(EzWorkFlowMapping workFlowMapping:workFlowMapList)
					{
						if(workFlowMapping.getLevel() > maxlevel)
							maxlevel = workFlowMapping.getLevel();
					}
				}
				
				String tempBrand="";
				
				EzPriceRequestItems tempItem=priceRequestHeader.getEzPriceRequestItems().get(0);
				String tempMaterial=tempItem.getMaterial();
				String tempSalesOrg=tempItem.getSalesOrg();
				if(tempMaterial != null && !"null".equals(tempMaterial) && !"".equals(tempMaterial))
				{
					Optional<EzMaterialSalesOrgData> tempMatSalesOrgKey= materialSalesOrgDataRepo.findById(new EzMaterialSalesOrgDataKey(tempMaterial, tempSalesOrg, "ST"));
					if(tempMatSalesOrgKey.isPresent())
						tempBrand=tempMatSalesOrgKey.get().getBrand();
					
				}
				List<EzPriceRequestItems> priceReqItemList=priceRequestHeader.getEzPriceRequestItems();
				EzMaterialLimits compMatLimitObj=null;
				Optional<EzMaterialLimits> matLimitKeyOpt= matLimitRepo.findById(new EzMaterialLimitKey(tempBrand, wfDistChnl, curLevel));
				
					if(matLimitKeyOpt.isPresent())
					{
						compMatLimitObj=matLimitKeyOpt.get();
						for(EzPriceRequestItems itemDto:priceReqItemList)
						{	
							if(compMatLimitObj != null)
							{
								log.debug("autoapproval:::::"+itemDto.getMaterial()+"::::Margin"+itemDto.getMarAftDis()+"::::Lower"+compMatLimitObj.getLower()+"::::Upper"+compMatLimitObj.getUpper());
								try
								{
									if(itemDto.getMarAftDis() > compMatLimitObj.getUpper())
									{
										//allowApprove = true;
										hmApprSts.put(itemDto.getLineNo(),true);
									}else
									{
										hmApprSts.put(itemDto.getLineNo(),false);
										//allowApprove = false;
									}
								}catch(Exception e) {
									log.error("Exception while comparing limits::"+e);
									//allowApprove = false;
								}
							}
							//if(!allowApprove)
								//break;
						}
					}
					else {
						//allowApprove = true;
					}
					
				//*****
					boolean allowApprove=true;
					log.debug("hmApprSts::::"+hmApprSts);
					for(EzPriceRequestItems itemDto:priceReqItemList)
					{
						if(hmApprSts.containsKey(itemDto.getLineNo()) && !hmApprSts.get(itemDto.getLineNo()))
						{
							allowApprove=false;
							itemDto.setStatus("S");
						}
						else
						{
							itemDto.setStatus("A");
						}
					}
					
					if(allowApprove)
					{
						Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
						log.info("Approval");
				    	Users userObj = (Users)authentication.getPrincipal();
				    	priceRequestHeader.setApprovedBy(userObj.getUserId()+"-"+userObj.getFirstName());
				    	priceRequestHeader.setStatus("A");
						
					}
				
			}
						
		}
*/
		@Override
		public List<Object[]> getRequestReportByUser(ArrayList<String> userId, Date fromDate, Date toDate) {
			return headerRep.getUserReport(userId, fromDate, toDate);
		}

		@Override
		public List<Object[]> getRequestReport(Date fromDate, Date toDate) {
			return headerRep.getReport( fromDate, toDate);
		}

		@Override
		public List<Object[]> getRequestReportByWFUser(ArrayList<String> userList, Date fromDate, Date toDate) {
			return headerRep.getReportByWFKey(userList, fromDate, toDate);
		}
	    
		 
		
		

}
