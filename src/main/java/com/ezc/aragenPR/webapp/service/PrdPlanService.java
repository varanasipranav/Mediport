/*
package com.ezc.aragenPR.webapp.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.ezc.aragenPR.webapp.dto.ListSelector;
import com.ezc.aragenPR.webapp.dto.WeeklyPlanFormDetInput;
import com.ezc.aragenPR.webapp.dto.WeeklyPlanItem;
import com.ezc.aragenPR.webapp.model.EzcPoPlanHeader;
import com.ezc.aragenPR.webapp.model.EzcPoPlanItemSummry;
import com.ezc.aragenPR.webapp.model.EzcPoPlanItems;
import com.ezc.aragenPR.webapp.model.EzcPoWeekPlan;
import com.ezc.aragenPR.webapp.model.EzcPoWeekPlanId;
import com.ezc.aragenPR.webapp.model.PrdPlanFormDetInput;
import com.ezc.aragenPR.webapp.model.PrdPlanRequestItemDto;
import com.ezc.aragenPR.webapp.model.Users;
import com.ezc.aragenPR.webapp.persistance.dao.PoPlanHeaderRepository;
import com.ezc.aragenPR.webapp.persistance.dao.PoWeekPlanRepository;
import com.ezc.aragenPR.webapp.persistance.dao.PrdPlanCustomDto;
import com.ezc.aragenPR.webapp.wsdl.po.create.gen.BAPIMEPOHEADER;
import com.ezc.aragenPR.webapp.wsdl.po.create.gen.BAPIMEPOHEADERX;
import com.ezc.aragenPR.webapp.wsdl.po.create.gen.BAPIMEPOITEM;
import com.ezc.aragenPR.webapp.wsdl.po.create.gen.BAPIMEPOITEMX;
import com.ezc.aragenPR.webapp.wsdl.po.create.gen.BAPIMEPOSCHEDULE;
import com.ezc.aragenPR.webapp.wsdl.po.create.gen.BAPIMEPOSCHEDULX;
import com.ezc.aragenPR.webapp.wsdl.po.create.gen.BAPIRET2;
import com.ezc.aragenPR.webapp.wsdl.po.create.gen.BAPI_PO_CREATE1;
import com.ezc.aragenPR.webapp.wsdl.po.create.gen.BAPI_PO_CREATE1Response;
import com.ibm.icu.text.SimpleDateFormat;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class PrdPlanService implements IPrdPlanService{
	
		private static final Exception Exception = null;

		@Autowired
		RFCReadSAPService rfcReadSAPService;
		
		@Autowired
		private  PoPlanHeaderRepository headerRep;
		
		@Autowired
		private  PoWeekPlanRepository weekRep;
		
		@Autowired
		private  PrdPlanCustomDto customPlanDAO;
		
		@Autowired
		private  POCreateSAPService poSAPService;
		
		@Autowired
		IPrdPlanAmmendService prdPlanAmmendService;


		@Override
		public EzcPoPlanHeader createPlan(PrdPlanFormDetInput prdPlanFormDetInput) {
			
			List<PrdPlanRequestItemDto> itemList= prdPlanFormDetInput.getPrdPlanRequestItems();
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    	Users userObj = (Users)authentication.getPrincipal();

			
			EzcPoPlanHeader poPlanHdrIn=new EzcPoPlanHeader();
			poPlanHdrIn.setMonthYear(prdPlanFormDetInput.getSelMonthYr());
			poPlanHdrIn.setCompanyCode("1000");
			poPlanHdrIn.setPurchasingOrg("1000");
			poPlanHdrIn.setPurchasingGroup(prdPlanFormDetInput.getSelPurGrp());
			poPlanHdrIn.setSupplier(prdPlanFormDetInput.getSelVendor());
			poPlanHdrIn.setOutSrcLoc(prdPlanFormDetInput.getSelOutPlant());

			if(userObj.getVendorCode() == null || "null".equalsIgnoreCase(userObj.getVendorCode())) {
				poPlanHdrIn.setCreatedBy("CPTTEAM");
			} else {
				poPlanHdrIn.setCreatedBy(userObj.getVendorCode());
			}

			poPlanHdrIn.setCreatedOn(new Date());
			poPlanHdrIn.setModifiedBy(userObj.getVendorCode());
			poPlanHdrIn.setModifiedOn(new Date());
			poPlanHdrIn.setStatus("SUBMITTED");
			poPlanHdrIn.setCurrentStep("VEN_APR");
			poPlanHdrIn.setNextPart("ROLE_VEND"); 
			poPlanHdrIn.setPlanId(prdPlanFormDetInput.getPlanId());

			List<EzcPoWeekPlan> ezcPoWeekPlanList=new ArrayList<EzcPoWeekPlan>();
			
			for(int i=1;i<=4;i++)
			{
				EzcPoWeekPlan poWeekPlan=new EzcPoWeekPlan();
				poWeekPlan.setWeek(i);
				//poWeekPlan.setDocId(poPlanHdrIn.getDocId());
				poWeekPlan.setModifiedBy(userObj.getVendorCode());
				poWeekPlan.setPoPlanHeader(poPlanHdrIn);
				poWeekPlan.setModifiedOn(new Date());
				poWeekPlan.setStatus("NEW");
				List<EzcPoPlanItems> ezcPoPlanItemsList=new ArrayList<EzcPoPlanItems>();
				int lineCount=1;
				
				for(PrdPlanRequestItemDto itemDto:itemList)
				{
					log.debug("itemDto.getStatus()4 => " + itemDto.getErrFlag());
					if(itemDto.getErrFlag().equals("N")) {
						EzcPoPlanItems poLineItem=new EzcPoPlanItems();
						//poLineItem.setWeek(i);
						//poLineItem.setDocId(poPlanHdrIn.getDocId());
						poLineItem.setPoWeekPlan(poWeekPlan);
						poLineItem.setLineNo(lineCount*10);
						lineCount++;
						poLineItem.setDescription(itemDto.getMaterialDesc());
						poLineItem.setCategory(itemDto.getCategory());
						poLineItem.setMaterial(itemDto.getMaterial());
						poLineItem.setWeight(itemDto.getWeight());
						poLineItem.setUom(itemDto.getUom());
						poLineItem.setStorLoc(itemDto.getStorageLocation());
						poLineItem.setBatch(prdPlanFormDetInput.getSelOutPlant());
						poLineItem.setCreatedOn(new Date());
						log.debug("itemDto.getNetWt() => " + itemDto.getNetWt());
						poLineItem.setNetWeight(itemDto.getNetWt());
						
						if(i==1)poLineItem.setQuantity(new BigDecimal(itemDto.getWeek1Qty()));
						else if(i==2)poLineItem.setQuantity(new BigDecimal(itemDto.getWeek2Qty()));
						else if(i==3)poLineItem.setQuantity(new BigDecimal(itemDto.getWeek3Qty()));
						else if(i==4)poLineItem.setQuantity(new BigDecimal(itemDto.getWeek4Qty()));
						ezcPoPlanItemsList.add(poLineItem);	
					}
				}
				poWeekPlan.setEzcPoPlanItems(ezcPoPlanItemsList);
				ezcPoWeekPlanList.add(poWeekPlan);
			}
			List<EzcPoPlanItemSummry> poPlanItemSummryList=new ArrayList<EzcPoPlanItemSummry>();
			int lineCount=1;
			for(PrdPlanRequestItemDto itemDto:itemList)
			{
				if(itemDto.getErrFlag().equals("N")) {
				EzcPoPlanItemSummry poPlanItemSummry=new EzcPoPlanItemSummry();
				poPlanItemSummry.setPoPlanHeader(poPlanHdrIn);
				poPlanItemSummry.setLineNo(lineCount*10);
				lineCount++;
				poPlanItemSummry.setMatDesc(itemDto.getMaterialDesc());
				poPlanItemSummry.setMaterial(itemDto.getMaterial());
				poPlanItemSummry.setPropQty(new BigDecimal(itemDto.getWeek1Qty()+itemDto.getWeek2Qty()+itemDto.getWeek3Qty()+itemDto.getWeek4Qty()));
				poPlanItemSummryList.add(poPlanItemSummry);
				}
			}
			poPlanHdrIn.setEzcPoWeekPlan(ezcPoWeekPlanList);
			poPlanHdrIn.setEzcPoPlanItemSummry(poPlanItemSummryList);
			
			return headerRep.save(poPlanHdrIn);
		}

		@Override
		public List<EzcPoPlanHeader> getPlanList(ListSelector listSelector) {
			return customPlanDAO.findRequestList(listSelector);
		}



		public List<EzcPoPlanHeader> getFilteredPlanList(ListSelector listSelector) {
			List<EzcPoPlanHeader> allHeaders = customPlanDAO.findRequestList(listSelector);; // your full list
			List<EzcPoPlanHeader> filtered = allHeaders.stream()
			    .filter(h -> "ROLE_VEND".equals(h.getNextPart()))
			    .collect(Collectors.toList());
			
			return filtered;
		}

		
		
		@SuppressWarnings("deprecation")
		@Override
		public PrdPlanFormDetInput getPlanById(String docId) {
			PrdPlanFormDetInput prdPlan=new PrdPlanFormDetInput();
			EzcPoPlanHeader poPlanHdrIn=headerRep.getById(docId);
			List<EzcPoWeekPlan> ezcPoWeekPlanList=poPlanHdrIn.getEzcPoWeekPlan();
			List<EzcPoPlanItemSummry> ezcPoPlanItemSummryList=poPlanHdrIn.getEzcPoPlanItemSummry();
			Map<Integer, EzcPoPlanItemSummry> itemSumList = ezcPoPlanItemSummryList.stream().collect(Collectors.toMap(EzcPoPlanItemSummry::getLineNo,Function.identity()));
			prdPlan.setCreatedBy(poPlanHdrIn.getCreatedBy());
			prdPlan.setCurrentStep(poPlanHdrIn.getCurrentStep());
			prdPlan.setNextPart(poPlanHdrIn.getNextPart());
			prdPlan.setSelVendor(poPlanHdrIn.getSupplier());
			prdPlan.setStatus(poPlanHdrIn.getStatus());
			prdPlan.setSelMonthYr(poPlanHdrIn.getMonthYear());
			prdPlan.setSelOutPlant(poPlanHdrIn.getOutSrcLoc());
			prdPlan.setPlanId(poPlanHdrIn.getPlanId());
			prdPlan.setDocId(docId);
			Hashtable<Integer,PrdPlanRequestItemDto> htWeekItems=new Hashtable<Integer,PrdPlanRequestItemDto>(); 
			for(EzcPoWeekPlan plan:ezcPoWeekPlanList)
			{
				if(plan.getWeek() == 1) prdPlan.setW1PO(plan.getPo());
				else if(plan.getWeek() == 2) prdPlan.setW2PO(plan.getPo());
				else if(plan.getWeek() == 3) prdPlan.setW3PO(plan.getPo());
				else if(plan.getWeek() == 4) prdPlan.setW4PO(plan.getPo());
				
				for(EzcPoPlanItems item:plan.getEzcPoPlanItems())
				{
					PrdPlanRequestItemDto itemDro=null;
					log.debug("XYZ => " + htWeekItems.size() + " => " + item.getLineNo());
					if(htWeekItems.containsKey(item.getLineNo()))
						itemDro=htWeekItems.get(item.getLineNo());
					else
					{
						itemDro=new PrdPlanRequestItemDto();
						EzcPoPlanItemSummry itemSum=itemSumList.get(item.getLineNo());
						itemDro.setVendAcpRemarks(itemSum.getVendPropRemarks());
						if(itemSum.getVendPropQty() != null)
							itemDro.setTotVendAcpQty(itemSum.getVendPropQty().doubleValue());
						itemDro.setCptAcpRemarks(itemSum.getCptAccRemarks());
						if(itemSum.getAcceptedQty() != null)
							itemDro.setTotCPTAcpQty(itemSum.getAcceptedQty().doubleValue());
						if(itemSum.getPropQty() != null)
							itemDro.setTotal(itemSum.getPropQty().doubleValue());
					}
					itemDro.setCategory(item.getCategory());
					itemDro.setMaterial(item.getMaterial());
					itemDro.setMaterialDesc(item.getDescription());
					log.debug("item.getWeight() => " + item.getWeight());
					if(item.getWeight() != null) 
						itemDro.setWeight(item.getWeight());
					itemDro.setLineNo(item.getLineNo());
					itemDro.setUom(item.getUom());
					itemDro.setStorageLocation(item.getStorLoc());
					log.debug("item.getQuantity() => " + item.getQuantity());
					if(item.getQuantity() != null) {
						if(plan.getWeek() == 1)itemDro.setWeek1Qty(item.getQuantity().doubleValue());
						else if(plan.getWeek() == 2)itemDro.setWeek2Qty(item.getQuantity().doubleValue());
						else if(plan.getWeek() == 3)itemDro.setWeek3Qty(item.getQuantity().doubleValue());
						else if(plan.getWeek() == 4)itemDro.setWeek4Qty(item.getQuantity().doubleValue());
					}
					htWeekItems.put(item.getLineNo(), itemDro);
				}
			}
			prdPlan.setPrdPlanRequestItems(new ArrayList<PrdPlanRequestItemDto>(htWeekItems.values()));
			log.debug("111 => " + prdPlan.toString());
			return prdPlan;
		}

		@SuppressWarnings("deprecation")
		@Override
		public PrdPlanFormDetInput getWekklyPlanAllById(String docId,int weekId) {
			PrdPlanFormDetInput prdPlan=new PrdPlanFormDetInput();
			EzcPoPlanHeader poPlanHdrIn=headerRep.getById(docId);
			List<EzcPoWeekPlan> ezcPoWeekPlanList=poPlanHdrIn.getEzcPoWeekPlan();
			List<EzcPoPlanItemSummry> ezcPoPlanItemSummryList=poPlanHdrIn.getEzcPoPlanItemSummry();
			Map<Integer, EzcPoPlanItemSummry> itemSumList = ezcPoPlanItemSummryList.stream().collect(Collectors.toMap(EzcPoPlanItemSummry::getLineNo,Function.identity()));
			prdPlan.setCreatedBy(poPlanHdrIn.getCreatedBy());
			prdPlan.setCurrentStep(poPlanHdrIn.getCurrentStep());
			prdPlan.setNextPart(poPlanHdrIn.getNextPart());
			prdPlan.setSelVendor(poPlanHdrIn.getSupplier());
			prdPlan.setStatus(poPlanHdrIn.getStatus());
			prdPlan.setSelMonthYr(poPlanHdrIn.getMonthYear());
			prdPlan.setSelOutPlant(poPlanHdrIn.getOutSrcLoc());
			prdPlan.setPlanId(poPlanHdrIn.getPlanId());
			
			prdPlan.setDocId(docId);
			Hashtable<Integer,PrdPlanRequestItemDto> htWeekItems=new Hashtable<Integer,PrdPlanRequestItemDto>(); 
			for(EzcPoWeekPlan plan:ezcPoWeekPlanList)
			{
				if(plan.getWeek() == weekId) prdPlan.setPoSelWeekPlan(plan);
				if(plan.getWeek() == weekId+1) prdPlan.setPoNxtWeekPlan(plan);
				
				if(plan.getWeek() == 1) prdPlan.setW1PO(plan.getPo());
				else if(plan.getWeek() == 2) prdPlan.setW2PO(plan.getPo());
				else if(plan.getWeek() == 3) prdPlan.setW3PO(plan.getPo());
				else if(plan.getWeek() == 4) prdPlan.setW4PO(plan.getPo());
				
				for(EzcPoPlanItems item:plan.getEzcPoPlanItems())
				{
					PrdPlanRequestItemDto itemDro=null;
					log.debug("XYZ => " + htWeekItems.size() + " => " + item.getLineNo());
					if(htWeekItems.containsKey(item.getLineNo()))
						itemDro=htWeekItems.get(item.getLineNo());
					else
					{
						itemDro=new PrdPlanRequestItemDto();
						EzcPoPlanItemSummry itemSum=itemSumList.get(item.getLineNo());
						itemDro.setVendAcpRemarks(itemSum.getVendPropRemarks());
						if(itemSum.getVendPropQty() != null)
							itemDro.setTotVendAcpQty(itemSum.getVendPropQty().doubleValue());
						itemDro.setCptAcpRemarks(itemSum.getCptAccRemarks());
						if(itemSum.getAcceptedQty() != null)
							itemDro.setTotCPTAcpQty(itemSum.getAcceptedQty().doubleValue());
						if(itemSum.getPropQty() != null)
							itemDro.setTotal(itemSum.getPropQty().doubleValue());
					}
					itemDro.setCategory(item.getCategory());
					itemDro.setMaterial(item.getMaterial());
					itemDro.setMaterialDesc(item.getDescription());
					log.debug("item.getWeight() => " + item.getWeight());
					if(item.getWeight() != null)
					{
						itemDro.setWeight(item.getWeight());
						itemDro.setNetWt(item.getWeight());
					}
					itemDro.setLineNo(item.getLineNo());
					itemDro.setUom(item.getUom());
					itemDro.setStorageLocation(item.getStorLoc());
					
					log.debug("item.getQuantity() => " + item.getQuantity());
					if(item.getQuantity() != null) {
						if(plan.getWeek() == 1)
						{	
							if(item.getCptWeekAccQty() != null && item.getCptWeekAccQty().compareTo(BigDecimal.ZERO) > 0)
								itemDro.setWeek1Qty(item.getCptWeekAccQty().doubleValue());
							else if(item.getCptWeekPropQty() != null && item.getCptWeekPropQty().compareTo(BigDecimal.ZERO) > 0)
								itemDro.setWeek1Qty(item.getCptWeekPropQty().doubleValue());
							else
								itemDro.setWeek1Qty(item.getQuantity().doubleValue());
							
							if(weekId == 1)
							{	
								itemDro.setVendWeekPropQty(item.getVendWeekPropQty()+"");
								itemDro.setVendWeekPropRem(item.getVendWeekPropRem()+"");
							}
						}
						else if(plan.getWeek() == 2)
						{	
							if(item.getCptWeekAccQty() != null && item.getCptWeekAccQty().compareTo(BigDecimal.ZERO) > 0)
								itemDro.setWeek2Qty(item.getCptWeekAccQty().doubleValue());
							else if(item.getCptWeekPropQty() != null && item.getCptWeekPropQty().compareTo(BigDecimal.ZERO) > 0)
								itemDro.setWeek2Qty(item.getCptWeekPropQty().doubleValue());
							else
								itemDro.setWeek2Qty(item.getQuantity().doubleValue());

							if(weekId == 2)
							{
								itemDro.setVendWeekPropQty(item.getVendWeekPropQty()+"");
								itemDro.setVendWeekPropRem(item.getVendWeekPropRem()+"");
							}
						}
						else if(plan.getWeek() == 3)
						{	
							if(item.getCptWeekAccQty() != null && item.getCptWeekAccQty().compareTo(BigDecimal.ZERO) > 0)
								itemDro.setWeek3Qty(item.getCptWeekAccQty().doubleValue());
							else if(item.getCptWeekPropQty() != null && item.getCptWeekPropQty().compareTo(BigDecimal.ZERO) > 0)
								itemDro.setWeek3Qty(item.getCptWeekPropQty().doubleValue());
							else
								itemDro.setWeek3Qty(item.getQuantity().doubleValue());
							if(weekId == 3)
							{
								itemDro.setVendWeekPropQty(item.getVendWeekPropQty()+"");
								itemDro.setVendWeekPropRem(item.getVendWeekPropRem()+"");
							}
						}
						else if(plan.getWeek() == 4)
						{	
							if(item.getCptWeekAccQty() != null && item.getCptWeekAccQty().compareTo(BigDecimal.ZERO) > 0)
								itemDro.setWeek4Qty(item.getCptWeekAccQty().doubleValue());
							else if(item.getCptWeekPropQty() != null && item.getCptWeekPropQty().compareTo(BigDecimal.ZERO) > 0)
								itemDro.setWeek4Qty(item.getCptWeekPropQty().doubleValue());
							else
								itemDro.setWeek4Qty(item.getQuantity().doubleValue());
							if(weekId == 4)
							{
								itemDro.setVendWeekPropQty(item.getVendWeekPropQty()+"");
								itemDro.setVendWeekPropRem(item.getVendWeekPropRem()+"");
							}
						}
					}
					htWeekItems.put(item.getLineNo(), itemDro);
				}
			}
			prdPlan.setPrdPlanRequestItems(new ArrayList<PrdPlanRequestItemDto>(htWeekItems.values()));
			log.debug("111 => " + prdPlan.toString());
			return prdPlan;
		}

		
		@Override
		public String createPO(String  planId) {
			
			String outResp="";
			EzcPoPlanHeader poHeader= headerRep.getById(planId);
		    String tempVal=poHeader.getMonthYear();
		    
		    int tempMon = Integer.parseInt(tempVal.split("/")[0]);
		    int tempyr = Integer.parseInt(tempVal.split("/")[1]);
			String outSrcPlant=poHeader.getOutSrcLoc();
			List<EzcPoWeekPlan> weekPlanList= poHeader.getEzcPoWeekPlan();
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdfSchd=new SimpleDateFormat("dd.MM.yyyy");
			boolean isError=false;
			for(EzcPoWeekPlan weekPlan:weekPlanList)
			{
				
				String poNo=weekPlan.getPo();
				if(isError || (poNo != null && !"null".equals(poNo) && !"".equals(poNo)))
						continue;
				String tempSupplier= poHeader.getSupplier();
				tempSupplier="0000000000000"+tempSupplier;
				tempSupplier=tempSupplier.substring(tempSupplier.length()-10);
				log.debug("tempSupplier:::"+tempSupplier);
				BAPI_PO_CREATE1 mainParams = new BAPI_PO_CREATE1(); 
	
				BAPIMEPOHEADER hdr_structure 	= new BAPIMEPOHEADER();
				BAPIMEPOHEADERX hdr_structurex 	= new BAPIMEPOHEADERX();
	
				BAPI_PO_CREATE1.POITEM poitem 	= new BAPI_PO_CREATE1.POITEM();
				BAPI_PO_CREATE1.POITEMX poitemx = new BAPI_PO_CREATE1.POITEMX();
	
				BAPI_PO_CREATE1.POSCHEDULE poschedule 	= new BAPI_PO_CREATE1.POSCHEDULE();
				BAPI_PO_CREATE1.POSCHEDULEX poschedulex = new BAPI_PO_CREATE1.POSCHEDULEX();
	
				BAPI_PO_CREATE1.POCOND pocond 	= new BAPI_PO_CREATE1.POCOND(); 
				BAPI_PO_CREATE1.POCONDX pocondx = new BAPI_PO_CREATE1.POCONDX();   
	
				BAPI_PO_CREATE1.POTEXTHEADER potextheader = new BAPI_PO_CREATE1.POTEXTHEADER();   
	
				hdr_structure.setCOMP_CODE(poHeader.getCompanyCode());		   hdr_structurex.setCOMP_CODE("X");
				hdr_structure.setDOC_TYPE("ZSPO");		   hdr_structurex.setDOC_TYPE("X");
				hdr_structure.setVENDOR(tempSupplier);		   hdr_structurex.setVENDOR("X");
				hdr_structure.setPURCH_ORG(poHeader.getPurchasingOrg());		   hdr_structurex.setPURCH_ORG("X");
				hdr_structure.setPUR_GROUP(poHeader.getPurchasingGroup());		   hdr_structurex.setPUR_GROUP("X");
				hdr_structure.setCURRENCY("INR");		   hdr_structurex.setCURRENCY("X");
				//poHeader.getPurchasingGroup()
				hdr_structure.setDOC_DATE(sdf.format(new Date()));	           hdr_structurex.setDOC_DATE("X");
				
				hdr_structure.setINCOTERMS1("");	   	   hdr_structurex.setINCOTERMS1("X");
				hdr_structure.setINCOTERMS2("");	   	   hdr_structurex.setINCOTERMS2("X");
				hdr_structure.setINCOTERMS2L("");	   hdr_structurex.setINCOTERMS2L("X"); 

					
				List<EzcPoPlanItems>  ezcPoPlanItemsList = weekPlan.getEzcPoPlanItems();
				for(EzcPoPlanItems ezcPoPlanItems:ezcPoPlanItemsList)
				{
					BAPIMEPOITEM  poItemRow		=	new BAPIMEPOITEM();
					BAPIMEPOITEMX poItemRowx	=	new BAPIMEPOITEMX();
					String tempMat=ezcPoPlanItems.getMaterial();
					
					
					if(tempMat.matches("\\d*"))
					{
						tempMat="00000000000000000000000000000000"+tempMat;
						tempMat=tempMat.substring(tempMat.length()-18);
						log.debug("tempMat:::"+tempMat);
					}
					
					String lineNo="00000"+ezcPoPlanItems.getLineNo();
					lineNo=lineNo.substring(lineNo.length()-5);
					String delDate="";//sdfSchd.format(getFirstDayOfMonthYearAndWeek(tempyr, tempMon,weekPlan.getWeek()));
					if(weekPlan.getWeek() == 1)delDate="09."+tempMon+"."+tempyr;
					else if(weekPlan.getWeek() == 2)delDate="16."+tempMon+"."+tempyr;
					else if(weekPlan.getWeek() == 3)delDate="24."+tempMon+"."+tempyr;
					else if(weekPlan.getWeek() == 4)delDate=getLastDayOfMonth(tempMon,tempyr)+"."+tempMon+"."+tempyr;
					
					
					log.debug(weekPlan.getWeek()+"delDate:::"+delDate);
					
					
					poItemRow.setPO_ITEM(lineNo);poItemRowx.setPO_ITEM(lineNo);
					poItemRow.setMATERIAL(tempMat);	 
					poItemRowx.setMATERIAL("X");
					poItemRow.setVEND_MAT("");	 	 poItemRowx.setVEND_MAT("X");
					poItemRow.setPLANT(outSrcPlant);		 poItemRowx.setPLANT("X");
					poItemRow.setBATCH(ezcPoPlanItems.getBatch()); poItemRowx.setBATCH("X");
					if(ezcPoPlanItems.getQuantity().compareTo(BigDecimal.ZERO)<=0)
							poItemRow.setQUANTITY(new BigDecimal(1));
					else
						    poItemRow.setQUANTITY(ezcPoPlanItems.getQuantity().setScale(2, RoundingMode.HALF_UP)); 
					
					poItemRowx.setQUANTITY("X");
					poItemRow.setSTGE_LOC(ezcPoPlanItems.getStorLoc());poItemRowx.setSTGE_LOC("X");
					//poItemRow.setNET_PRICE(new java.math.BigDecimal(rfqPrice));	 poItemRowx.setNET_PRICE("X");
					//poItemRow.setPRICE_UNIT(new java.math.BigDecimal("1000"));	 poItemRowx.setPRICE_UNIT("X");
					poItemRow.setGR_TO_DATE(sdf.format(new Date()));	 poItemRowx.setGR_TO_DATE("X");
					poItemRow.setITEM_CAT("L");poItemRowx.setITEM_CAT("X");
					//poItemRow.setCONF_CTRL("0004");	 	 poItemRowx.setCONF_CTRL("X");
					poItemRow.setVAL_TYPE("MANUFACTUR");	 poItemRowx.setVAL_TYPE("X");
					poitem.getItem().add(poItemRow); 	 poitemx.getItem().add(poItemRowx);
					
					BAPIMEPOSCHEDULE poSchdRow = new BAPIMEPOSCHEDULE();
					BAPIMEPOSCHEDULX poSchdRowx = new BAPIMEPOSCHEDULX();

					poSchdRow.setPO_ITEM(lineNo);			poSchdRowx.setPO_ITEM(lineNo);		poSchdRowx.setPO_ITEMX("X");
					poSchdRow.setSCHED_LINE("0001");		poSchdRowx.setSCHED_LINE("0001");	poSchdRowx.setSCHED_LINEX("X");
					poSchdRow.setDELIVERY_DATE(delDate);		poSchdRowx.setDELIVERY_DATE("X");
					
					if(ezcPoPlanItems.getQuantity().compareTo(BigDecimal.ZERO)<=0)
						poSchdRow.setQUANTITY(new BigDecimal(1));
					else 
						poSchdRow.setQUANTITY(ezcPoPlanItems.getQuantity().setScale(2, RoundingMode.HALF_UP));	
					
					poSchdRowx.setQUANTITY("X");

					poschedule.getItem().add(poSchdRow); 		poschedulex.getItem().add(poSchdRowx);
					
				}
				mainParams.setPOHEADER(hdr_structure);
				mainParams.setPOHEADERX(hdr_structurex);
				//mainParams.setNO_PRICE_FROM_PO("X");
				mainParams.setPOITEM(poitem);
				mainParams.setPOITEMX(poitemx);
				mainParams.setPOSCHEDULE(poschedule);
				mainParams.setPOSCHEDULEX(poschedulex);
				mainParams.setPOCOND(pocond);
				mainParams.setPOCONDX(pocondx);
				mainParams.setPOTEXTHEADER(potextheader);
				BAPI_PO_CREATE1Response resp = null;

				try{
					resp = (BAPI_PO_CREATE1Response)poSAPService.callService(mainParams);	
				}catch(Exception ep){
					log.error("ERROR -- PO CREATION::::: "+ep);	
					isError      = true;
				}	


				List<BAPIRET2> repOutput =  null;// resp.getOUT_PO_TAB().getItem();
				int repOutputRows = 0;

				//out.println("<Br>:::::::PO NUMBER:::::::"+mainParams.getEXPPURCHASEORDER());

				if(resp.getRETURN()!=null)
				{
					repOutput = resp.getRETURN().getItem();
					repOutputRows = repOutput.size();
				}
				if(repOutputRows>0)
				{		
					for(int r=0;r<repOutputRows;r++)
					{	
						BAPIRET2 outputRows = (BAPIRET2)repOutput.get(r);
						
						String lv_Type = (String)outputRows.getTYPE();
						String lv_Msg  = (String)outputRows.getMESSAGE();
						
						log.debug(lv_Type+"::::::::::::::"+lv_Msg);	
						
						if("S".equals(lv_Type))
						{
							//dispMessage += lv_Msg+"<Br>"; 
							poNo = 	(String)outputRows.getMESSAGE_V2();
							outResp+="<br>PO No : <strong>"+poNo+"</strong>created for Week <strong>"+weekPlan.getWeek()+"</strong>";
							log.debug("poNo:::"+poNo);
						}else if("E".equals(lv_Type)){
							isError      = true;
							outResp+=lv_Msg;
						}

						
						
						*/
/*out.println("<Br>:::::::TYPE:::::::"+outputRows.getTYPE());
						out.println("<Br>:::::::ID:::::::"+outputRows.getID());
						out.println("<Br>:::::::MESSAGE:::::::"+outputRows.getMESSAGE());
						out.println("<Br>:::::::MESSAGE_V1:::::::"+outputRows.getMESSAGE_V1());
						out.println("<Br>:::::::MESSAGE_V2:::::::"+outputRows.getMESSAGE_V2());
						out.println("<Br>:::::::MESSAGE_V3:::::::"+outputRows.getMESSAGE_V3());
						out.println("<Br>:::::::MESSAGE_V4:::::::"+outputRows.getMESSAGE_V4());*//*

						
					}
					if(isError)
					{
						outResp ="<br>Error While creating PO for Week "+weekPlan.getWeek() +" "+outResp;
					}
					else
					{
						weekPlan.setPo(poNo);
					}

					
			}
			
						
			
		}
			if(!isError)
			{
				poHeader.setStatus("APPROVED");
				outResp+="Plan approved for all weeks";
			}
			else
				outResp+="Data posted partially or Error in posting PO";
			
			return outResp;
		
		}

		@Override
		public String submitRequest(PrdPlanFormDetInput prdPlanFormDetInput) {
			//PrdPlanFormDetInput prdPlan=new PrdPlanFormDetInput();
			
		
			List<PrdPlanRequestItemDto> itemList= prdPlanFormDetInput.getPrdPlanRequestItems();
			Map<Integer, PrdPlanRequestItemDto> itemInputList = itemList.stream().collect(Collectors.toMap(PrdPlanRequestItemDto::getLineNo,Function.identity()));
			
			log.debug("itemInputList => " + itemInputList);
			
			EzcPoPlanHeader poPlanHdrIn=headerRep.getById(prdPlanFormDetInput.getDocId());
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    	Users userObj = (Users)authentication.getPrincipal();
	    	
	    	List<EzcPoPlanItemSummry> ezcPoPlanItemSummryList=poPlanHdrIn.getEzcPoPlanItemSummry();
	    	
	    	for(EzcPoPlanItemSummry ezcPoPlanItemSummry:ezcPoPlanItemSummryList)
	    	{
	    		PrdPlanRequestItemDto prdPlanRequestItemDto=itemInputList.get(ezcPoPlanItemSummry.getLineNo());
	    		log.debug("prdPlanRequestItemDto => " + prdPlanRequestItemDto);
	    		ezcPoPlanItemSummry.setVendPropQty(new BigDecimal(prdPlanRequestItemDto.getTotVendAcpQty()));
	    		ezcPoPlanItemSummry.setVendPropRemarks(prdPlanRequestItemDto.getVendAcpRemarks());
	    	}
	    	
	    	poPlanHdrIn.setCurrentStep("CPTAPR");
			poPlanHdrIn.setNextPart("ROLE_CPT");
			poPlanHdrIn.setStatus("SUBMITTED");
			poPlanHdrIn.setModifiedBy(userObj.getVendorCode());
			poPlanHdrIn.setModifiedOn(new Date());
						
			return "";

		}

		@Override
		public String approveRequest(PrdPlanFormDetInput prdPlanFormDetInput) {
			Hashtable<String, BigDecimal> htInput=new Hashtable<String, BigDecimal>();
			EzcPoPlanHeader poPlanHdrIn=headerRep.getById(prdPlanFormDetInput.getDocId());
			
			List<PrdPlanRequestItemDto> itemList= prdPlanFormDetInput.getPrdPlanRequestItems();
			Map<Integer, PrdPlanRequestItemDto> itemInputList = itemList.stream().collect(Collectors.toMap(PrdPlanRequestItemDto::getLineNo,Function.identity()));

			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    	Users userObj = (Users)authentication.getPrincipal();
	    	for(PrdPlanRequestItemDto itemDto:itemList)
			{
	    		htInput.put(itemDto.getLineNo()+"#1",new BigDecimal(itemDto.getWeek1Qty()));
	    		htInput.put(itemDto.getLineNo()+"#2",new BigDecimal(itemDto.getWeek2Qty()));
	    		htInput.put(itemDto.getLineNo()+"#3",new BigDecimal(itemDto.getWeek3Qty()));
	    		htInput.put(itemDto.getLineNo()+"#4",new BigDecimal(itemDto.getWeek4Qty()));
			}
	    	List<EzcPoWeekPlan> ezcPoWeekPlanList=poPlanHdrIn.getEzcPoWeekPlan();
			
			for(EzcPoWeekPlan poWeekPlan:ezcPoWeekPlanList)
			{
				int i=poWeekPlan.getWeek();
				poWeekPlan.setModifiedBy(userObj.getVendorCode());
				poWeekPlan.setModifiedOn(new Date());
					
				List<EzcPoPlanItems> ezcPoPlanItemsList=poWeekPlan.getEzcPoPlanItems();
				for(EzcPoPlanItems poLineItem:ezcPoPlanItemsList)
				{
					BigDecimal currentQty=poLineItem.getQuantity();
					BigDecimal updatedQty=htInput.get(poLineItem.getLineNo()+"#"+i);
					if(currentQty.compareTo(updatedQty) != 0)
					{
						poLineItem.setQuantity(updatedQty);
					}
				}
				List<EzcPoPlanItemSummry> ezcPoPlanItemSummryList=poPlanHdrIn.getEzcPoPlanItemSummry();
		    	
				for(EzcPoPlanItemSummry ezcPoPlanItemSummry:ezcPoPlanItemSummryList)
		    	{
		    		PrdPlanRequestItemDto prdPlanRequestItemDto=itemInputList.get(ezcPoPlanItemSummry.getLineNo());
		    		ezcPoPlanItemSummry.setAcceptedQty(new BigDecimal(prdPlanRequestItemDto.getWeek1Qty()+prdPlanRequestItemDto.getWeek2Qty()+prdPlanRequestItemDto.getWeek3Qty()+prdPlanRequestItemDto.getWeek4Qty()));
		    		ezcPoPlanItemSummry.setCptAccRemarks(prdPlanRequestItemDto.getCptAcpRemarks());
		    	}
			}
	
			return createPO(prdPlanFormDetInput.getDocId());
			
		}	
		
		 public static Date getFirstDayOfMonthYearAndWeek(int year, int month, int week) {
		        // Validate inputs
		        if (month < 1 || month > 12) {
		            throw new IllegalArgumentException("Month must be between 1 and 12.");
		        }
		        if (week < 1 || week > 6) {
		            throw new IllegalArgumentException("Week must be between 1 and 6.");
		        }

		        // Create a Calendar instance
		        Calendar calendar = Calendar.getInstance();

		        // Set the year and month
		        calendar.set(Calendar.YEAR, year);
		        calendar.set(Calendar.MONTH, month - 1); // Month is 0-based in Calendar

		        // Set the first day of the month
		        calendar.set(Calendar.DAY_OF_MONTH, 1);

		        // Set the week and the first day of the week (e.g., Monday)
		        calendar.set(Calendar.WEEK_OF_MONTH, week);
		        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // Assuming the week starts on Monday

		        // Return the Date object
		        return calendar.getTime();
		    }
		 
		 @Override
			public List<WeeklyPlanItem> getWeeklyPlanList(ListSelector listSelector) {
				
			 	List<WeeklyPlanItem> outList=new ArrayList<WeeklyPlanItem>(); 
			 	
			 	Hashtable<String,WeeklyPlanItem> htWeeklyPlan=new Hashtable<String, WeeklyPlanItem>();
			 	log.debug("listSelector.getVendor( => " + listSelector.getVendor());

			 	List<Object[]> objList;
			 	if("CPTTEAM".equals(listSelector.getVendor())) {
			 		objList= headerRep.getWeeklyPlanListDate(listSelector.getFromDate(), listSelector.getToDate());
			 	}
			 	else {
			 		objList= headerRep.getWeeklyPlanListBySupplierAndDate(listSelector.getVendor(), listSelector.getFromDate(), listSelector.getToDate());
			 	}
			 	for(Object[] objArr:objList)
			 	{
			 		log.debug("099099 =>" + objArr.toString());
			 		String docId=objArr[0]+"";
			 		WeeklyPlanItem tempItem=null;
			 		if(htWeeklyPlan.containsKey(docId))
	 				{
			 			tempItem=htWeeklyPlan.get(docId);
	 				}
			 		else
			 		{
			 			log.debug("objArr => " + objArr[5]  );
			 			tempItem=new WeeklyPlanItem();
			 			tempItem.setPlanId(docId);
			 			tempItem.setMonthYr(objArr[1]+"");
			 			tempItem.setVendor(objArr[2]+"");
			 			tempItem.setCreatedOn((Date) objArr[5]);
			 			tempItem.setPlant(objArr[6]+"");
			 		}
			 		int week=(int)objArr[3];	
		 			
		 			if(week == 1)tempItem.setWeek1Sts(objArr[4]+"");
		 			else if(week == 2)tempItem.setWeek2Sts(objArr[4]+"");
		 			else if(week == 3)tempItem.setWeek3Sts(objArr[4]+"");
		 			else if(week == 4)tempItem.setWeek4Sts(objArr[4]+"");
		 			htWeeklyPlan.put(docId, tempItem);
			 	}
			 	log.debug("htWeeklyPlan:::"+htWeeklyPlan.toString());
			 	Collection<WeeklyPlanItem> weeklyPlanItemCol= htWeeklyPlan.values();
			 	 for (WeeklyPlanItem weeklyList : weeklyPlanItemCol) {
			 		outList.add(weeklyList);
			        }
				return outList;
			}

		@Override
		public WeeklyPlanFormDetInput getWeekPlanById(String planId, int weekId) {
			
			EzcPoPlanHeader poPlanHeader=new EzcPoPlanHeader();
			poPlanHeader.setDocId(Integer.parseInt(planId));
			List<EzcPoPlanItemSummry> ezcPoPlanItemSummry=poPlanHeader.getEzcPoPlanItemSummry();		WeeklyPlanFormDetInput weeklyPlanFormDetInput=new WeeklyPlanFormDetInput();
			*/
/*
			HashMap<Integer, BigDecimal> monTotQty=new HashMap<Integer, BigDecimal>();
			for(EzcPoPlanItemSummry itemSummry:ezcPoPlanItemSummry)
			{
				monTotQty.put(itemSummry.getLineNo(),itemSummry.getAcceptedQty());
			}*//*

			
			EzcPoWeekPlanId weekPlanId= new EzcPoWeekPlanId(poPlanHeader, weekId);
			EzcPoWeekPlan poWeekPlan=weekRep.getReferenceById(weekPlanId);
			EzcPoPlanHeader poPlanHeaderOut=poWeekPlan.getPoPlanHeader();
			weeklyPlanFormDetInput.setCurrentStep(poWeekPlan.getCurrentStep());
			weeklyPlanFormDetInput.setDocId(planId); 
			weeklyPlanFormDetInput.setMonthYear(poPlanHeaderOut.getMonthYear());
			weeklyPlanFormDetInput.setNextPart(poWeekPlan.getNextPart()); 
			weeklyPlanFormDetInput.setOutSrcLoc(poPlanHeaderOut.getOutSrcLoc());
			weeklyPlanFormDetInput.setPo(poWeekPlan.getPo());
			weeklyPlanFormDetInput.setPurchasingGroup(poPlanHeaderOut.getPurchasingGroup());
			weeklyPlanFormDetInput.setStatus(poWeekPlan.getStatus());
			weeklyPlanFormDetInput.setSupplier(poPlanHeaderOut.getSupplier()); 
			weeklyPlanFormDetInput.setWeek(poWeekPlan.getWeek()+""); 
			//weeklyPlanFormDetInput.setMonTotQty(monTotQty);
			
			log.debug("weeklyPlanFormDetInput => " + weeklyPlanFormDetInput.getStatus());
			weeklyPlanFormDetInput.setEzcPoPlanItems(poWeekPlan.getEzcPoPlanItems());
			
			return weeklyPlanFormDetInput;
		}

		  public static int getLastDayOfMonth(int month, int year) {
		        YearMonth yearMonth = YearMonth.of(year, month);
		        return yearMonth.lengthOfMonth();
		    }

		@Override
		public HashMap<String, String> getMaterialNetWt(ArrayList<String> itemsList, String plant, Date date) {
			String [] outputFields =new String[] {"MATNR","FWEIGHT"};
			
			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyyMMdd");
	        String formattedDate = outputFormat.format(date);
			
			String [] queryValues =new String[itemsList.size()+2];
			//queryValues[0] = "BEGDA >= '"+formattedDate+"' AND ENDDA <= '"+formattedDate+"'";
			//queryValues[0] = "BEGDA >= '"+formattedDate+"' AND ENDDA <= '"+formattedDate+"'";
			queryValues[0] = "WERKS EQ '"+plant+"' AND ";
			int tempMatCnt=1;
	        //log.debug("formattedDate => " + formattedDate + "PlantNo => " + plant);
			for(String tempMat : itemsList)
			{
				if(tempMat.matches("\\d*"))
				{
					tempMat="00000000000000000000000000000000" + tempMat;
					tempMat=tempMat.substring(tempMat.length()-18);
				}
				if(tempMatCnt == 1)
					queryValues[tempMatCnt]= "MATNR IN ('"+tempMat+"'";
				else
					queryValues[tempMatCnt]= ",'"+tempMat+"'";
			
				tempMatCnt++;
			}
			queryValues[tempMatCnt-1] += ")";
			queryValues[tempMatCnt] = "AND BEGDA <= '"+formattedDate+"' AND ENDDA >= '"+formattedDate+"'";
			
			for(String temp:queryValues)
			{
				log.debug("::::"+temp);	
			}
			
			HashMap<String, String> htMatWt = new HashMap<String, String>();
			
			List<String[]> matList= rfcReadSAPService.readRFCTable("ZPP007", outputFields, queryValues);
			for(String[] tempArrObj : matList)
			{
				String tempVal = tempArrObj[1].trim();
				String tempMat = tempArrObj[0].trim();
				tempMat = tempMat.replaceFirst("^0+(?!$)", "");
				htMatWt.put(tempMat, tempVal);
			}
			log.debug(htMatWt+"");
			return htMatWt;
		}

		@Override
		public EzcPoWeekPlan getWeekPlanHdrById(String planId, int weekId) {
			
			EzcPoPlanHeader poPlanHeader=new EzcPoPlanHeader();
			poPlanHeader.setDocId(Integer.parseInt(planId));
			EzcPoWeekPlanId weekPlanId= new EzcPoWeekPlanId(poPlanHeader, weekId);
			return weekRep.getReferenceById(weekPlanId);
		}
		
		@Override
		public String updateWeeklyPlanAll(PrdPlanFormDetInput prdPlanFormDetInput,String btnType) {
			Hashtable<String, BigDecimal> htInput=new Hashtable<String, BigDecimal>();
			EzcPoPlanHeader poPlanHdrIn=headerRep.getById(prdPlanFormDetInput.getDocId());
			int selWeek=Integer.parseInt(prdPlanFormDetInput.getSelWeek());
			boolean isError=false;
			List<PrdPlanRequestItemDto> itemList= prdPlanFormDetInput.getPrdPlanRequestItems();
			Map<Integer, PrdPlanRequestItemDto> itemInputList = itemList.stream().collect(Collectors.toMap(PrdPlanRequestItemDto::getLineNo,Function.identity()));
			List<EzcPoPlanItemSummry> poPlanItemSumList=poPlanHdrIn.getEzcPoPlanItemSummry();
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    	Users userObj = (Users)authentication.getPrincipal();
	    	for(PrdPlanRequestItemDto itemDto:itemList)
			{
	    		htInput.put(itemDto.getLineNo()+"#1",new BigDecimal(itemDto.getWeek1Qty()));
	    		htInput.put(itemDto.getLineNo()+"#2",new BigDecimal(itemDto.getWeek2Qty()));
	    		htInput.put(itemDto.getLineNo()+"#3",new BigDecimal(itemDto.getWeek3Qty()));
	    		htInput.put(itemDto.getLineNo()+"#4",new BigDecimal(itemDto.getWeek4Qty()));
			}
	    	List<EzcPoWeekPlan> ezcPoWeekPlanList=poPlanHdrIn.getEzcPoWeekPlan();
			
			for(EzcPoWeekPlan poWeekPlan:ezcPoWeekPlanList)
			{
				int i=poWeekPlan.getWeek();
				
				if(selWeek == 2)
				{
					if(i == 1)
						continue;
				}
				else if(selWeek == 3)
				{
					if(i == 1 || i == 2)
						continue;
				}
				else if(selWeek == 4)
				{
					if(i == 1 || i == 2 || i == 3)
						continue;
				}
				poWeekPlan.setModifiedBy(userObj.getVendorCode());
				poWeekPlan.setModifiedOn(new Date());	
				if(selWeek == i)
				{
					if("CPT_INIT".equals(btnType))
					{
						poWeekPlan.setStatus("SUBMITTED");
						poWeekPlan.setNextPart("ROLE_VEND");
						poWeekPlan.setCurrentStep("VENDSUB");
					}
					*/
/*
					else if("CPT_APR".equals(btnType))
					{
						poWeekPlan.setStatus("PARKED");
					}
					*//*

				}
				List<EzcPoPlanItems> ezcPoPlanItemsList=poWeekPlan.getEzcPoPlanItems();
				for(EzcPoPlanItems poLineItem:ezcPoPlanItemsList)
				{
					BigDecimal currentQty=poLineItem.getQuantity();
					BigDecimal updatedQty=htInput.get(poLineItem.getLineNo()+"#"+i);
					*/
/*
					if("CPT_INIT".equals(btnType) )
					{
						poLineItem.setCptWeekPropQty(updatedQty);
					}
					else 
					*//*

					if("CPT_APR".equals(btnType) || "CPT_INIT".equals(btnType) || "CPT_AMD".equals(btnType))
					{
						poLineItem.setCptWeekAccQty(updatedQty);
					}
					if(selWeek == i && updatedQty.compareTo(BigDecimal.ZERO) == 0)
					{
						poLineItem.setAmmendSts("D");
					}
					*/
/*
					if(currentQty.compareTo(updatedQty) != 0)
					{
						poLineItem.setQuantity(updatedQty);
					}
					*//*

				}
			}
			//inserting start
			for(PrdPlanRequestItemDto itemDto:itemList)
			{
				String insertTxt=itemDto.getInsertTxt();
				if(insertTxt == null || "".equals(insertTxt) || "null".equals(insertTxt))
					continue;
				log.debug("insertTxt:::"+insertTxt);
				String [] insertTxtArr=insertTxt.split("##");
				
				
				for(EzcPoWeekPlan ezcPoWeekPlanObj : ezcPoWeekPlanList)
				{
					BigDecimal insertedQty=htInput.get(itemDto.getLineNo()+"#"+ezcPoWeekPlanObj.getWeek());
					if(ezcPoWeekPlanObj.getWeek().equals(selWeek))
					{
						EzcPoPlanItemSummry itemSummry=new EzcPoPlanItemSummry();
						itemSummry.setPoPlanHeader(poPlanHdrIn);
						itemSummry.setLineNo(itemDto.getLineNo());
						itemSummry.setMatDesc(insertTxtArr[3]);
						itemSummry.setMaterial(insertTxtArr[2]);
						itemSummry.setPropQty(BigDecimal.ZERO);
						itemSummry.setVendPropQty(BigDecimal.ZERO);
						itemSummry.setAcceptedQty(BigDecimal.ZERO);
						poPlanItemSumList.add(itemSummry);
					}
					EzcPoPlanItems poLineItem=new EzcPoPlanItems();
					//poLineItem.setWeek(i);
					//poLineItem.setDocId(poPlanHdrIn.getDocId());
					poLineItem.setPoWeekPlan(ezcPoWeekPlanObj);
					poLineItem.setLineNo(itemDto.getLineNo());
					poLineItem.setDescription(insertTxtArr[3]);
					poLineItem.setMaterial(insertTxtArr[2]);
					poLineItem.setUom(insertTxtArr[5]);
					poLineItem.setStorLoc(insertTxtArr[6]);
					poLineItem.setBatch(poPlanHdrIn.getOutSrcLoc());
					poLineItem.setQuantity(BigDecimal.ZERO);
					poLineItem.setCategory(insertTxtArr[0]);
					poLineItem.setNetWeight(Double.parseDouble(insertTxtArr[4]));
					*/
/*
					if("CPT_INIT".equals(btnType))
					{
						poLineItem.setCptWeekPropQty(insertedQty);
						poLineItem.setCptWeekPropRem("");
					}
					else*//*

					if("CPT_APR".equals(btnType) || "CPT_INIT".equals(btnType) || "CPT_AMD".equals(btnType))
					{
							poLineItem.setCptWeekAccQty(insertedQty);
							poLineItem.setCptWeekAccRem("");
						
					}
					
				    poLineItem.setAmmendSts("I");
				    ezcPoWeekPlanObj.getEzcPoPlanItems().add(poLineItem);						}
			}
			//inserting end
			if("CPT_APR".equals(btnType) ||  "CPT_AMD".equals(btnType))//changing in sap
			{
				for(EzcPoWeekPlan poWeekPlan:ezcPoWeekPlanList)
				{
					
					int i=poWeekPlan.getWeek();
					
					if(selWeek == 2)
					{
						if(i == 1)
							continue;
					}
					else if(selWeek == 3)
					{
						if(i == 1 || i == 2)
							continue;
					}
					else if(selWeek == 4)
					{
						if(i == 1 || i == 2 || i == 3)
							continue;
					}
					String retString=prdPlanAmmendService.updatePOInSAP(poWeekPlan, poPlanHdrIn.getOutSrcLoc(), poPlanHdrIn.getMonthYear());
					
					if("N".equals(retString.split("##")[0]))
					{
						isError=true;
						return retString;
					}
					
				}
				if(!isError)
				{
					
					String loggedUser=(String)userObj.getVendorCode();
					for(EzcPoWeekPlan poWeekPlan:ezcPoWeekPlanList)
					{
						if(poWeekPlan.getWeek() == selWeek)
						{
							poWeekPlan.setModifiedBy(loggedUser);
							poWeekPlan.setModifiedOn(new Date());
							poWeekPlan.setStatus("APPROVED");
							poWeekPlan.setNextPart("");
							poWeekPlan.setCurrentStep("");
						}
					}
				}
				else
				{
					return "Error while Updating the plan";
				}
			}
			return "Plan Updated Successfully";
			
		}	
		
		@Override
		public List<Object[]> getOtherPlantPlans(String planId) {
			
			return headerRep.getOtherMonthlyPlanId(planId);
		}

	@Override
	public List<EzcPoPlanHeader> getSubmittedPlansByRoleAndDateRange(String role, Date fromDate, Date toDate) {
		return headerRep.findSubmittedForRoleVend(role, fromDate, toDate);
	}

	@Override
	public List<EzcPoPlanHeader> getSubmittedPlansByRoleAndDateRangeAndUserId(
			String role, String userId, Date fromDate, Date toDate
	) {
		return headerRep.findSubmittedPlansByRoleAndUserIdAndDateRange(role, userId, fromDate, toDate);
	}


}
*/
