/*
package com.ezc.aragenPR.webapp.service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.ezc.aragenPR.webapp.dto.WeeklyPlanAmmendItem;
import com.ezc.aragenPR.webapp.dto.WeeklyPlanFormDetInput;
import com.ezc.aragenPR.webapp.model.EzcPoPlanHeader;
import com.ezc.aragenPR.webapp.model.EzcPoPlanItemSummry;
import com.ezc.aragenPR.webapp.model.EzcPoPlanItems;
import com.ezc.aragenPR.webapp.model.EzcPoWeekPlan;
import com.ezc.aragenPR.webapp.model.PrdPlanFormDetInput;
import com.ezc.aragenPR.webapp.model.PrdPlanRequestItemDto;
import com.ezc.aragenPR.webapp.model.Users;
import com.ezc.aragenPR.webapp.persistance.dao.PoPlanHeaderRepository;
import com.ezc.aragenPR.webapp.wsdl.po.change.gen.BAPIMEPOHEADER;
import com.ezc.aragenPR.webapp.wsdl.po.change.gen.BAPIMEPOHEADERX;
import com.ezc.aragenPR.webapp.wsdl.po.change.gen.BAPIMEPOITEM;
import com.ezc.aragenPR.webapp.wsdl.po.change.gen.BAPIMEPOITEMX;
import com.ezc.aragenPR.webapp.wsdl.po.change.gen.BAPIMEPOSCHEDULE;
import com.ezc.aragenPR.webapp.wsdl.po.change.gen.BAPIMEPOSCHEDULX;
import com.ezc.aragenPR.webapp.wsdl.po.change.gen.BAPIRET2;
import com.ezc.aragenPR.webapp.wsdl.po.change.gen.BAPI_PO_CHANGE;
import com.ezc.aragenPR.webapp.wsdl.po.change.gen.BAPI_PO_CHANGEResponse;
import com.ibm.icu.text.SimpleDateFormat;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class PrdPlanAmmendService implements IPrdPlanAmmendService{
	
		private static final Exception Exception = null;

		@Autowired
		private  PoPlanHeaderRepository headerRep;
		
		
		
		@Autowired
		private  POChangeSAPService poSAPService;
		
		@Autowired
		RFCReadSAPService rfcReadSAPService;
		

		
		@Override
		public String ammendWeeklyPlan(String planId, int weekId, List<WeeklyPlanAmmendItem> itemList,String btnType) {
			// TODO Auto-generated method stub
			boolean isError=false;
			String outResp="";
			EzcPoPlanHeader poPlanHdrIn=headerRep.getById(planId);
			List<EzcPoPlanItemSummry> poPlanItemSumList=poPlanHdrIn.getEzcPoPlanItemSummry();
			List<EzcPoWeekPlan> poWeekPlanList=poPlanHdrIn.getEzcPoWeekPlan();
			EzcPoWeekPlan ezcPoWeekPlan= poWeekPlanList.stream().
				    filter(p -> p.getWeek().equals(weekId)).
				    findFirst().get();
			
			List<EzcPoPlanItems> planItemList= ezcPoWeekPlan.getEzcPoPlanItems();
			Set<String> uploadedLinesSet = new HashSet<>();
			

			if("CPT_INIT".equals(btnType))
			{
				ezcPoWeekPlan.setStatus("SUBMITTED");
				ezcPoWeekPlan.setNextPart("ROLE_VEND");
				ezcPoWeekPlan.setCurrentStep("VENDSUB");
				
				for(WeeklyPlanAmmendItem ammendedItems:itemList)
				{
					
					String ammendedItem=ammendedItems.getLineNo();
					uploadedLinesSet.add(ammendedItem);
					if(!"I".equals(ammendedItems.getStatus()) && ammendedItem != null && !"null".equals(ammendedItem)  && !"".equals(ammendedItem.trim()))
					{
						log.debug("in update :: "+ammendedItem);
						Optional<EzcPoPlanItems> poPlanItems= planItemList.stream().
							    filter(p -> (p.getLineNo()+"").equals(ammendedItem)).
							    findFirst();
						
						if(poPlanItems.isPresent())
						{
							EzcPoPlanItems ezcPoPlanItems=poPlanItems.get();
							ezcPoPlanItems.setAmmendSts(ammendedItems.getStatus());
							log.debug("VALUES => " + ammendedItems.getLineNo() + "-" + ammendedItems.getCptWeekPropQty() + "-" + ammendedItems.getCptWeekAccQty());
							
//							if(!ammendedItems.getCptWeekPropQty().equals(ammendedItems.getCptWeekAccQty()))
//							{
//									ezcPoPlanItems.setCptWeekPropQty(new BigDecimal(ammendedItems.getCptWeekPropQty()));
//							}
//							else if("D".equals(ammendedItems.getStatus()))
//							{
//								ezcPoPlanItems.setCptWeekPropQty(BigDecimal.ZERO);
//							}
//							else
//							{
//								ezcPoPlanItems.setCptWeekPropQty(ezcPoPlanItems.getQuantity());
//							}
							
							if("U".equals(ammendedItems.getStatus()))
							{
									ezcPoPlanItems.setCptWeekPropQty(new BigDecimal(ammendedItems.getCptWeekPropQty()));
							}
							else
							{
								ezcPoPlanItems.setCptWeekPropQty(ezcPoPlanItems.getQuantity());
							}
							ezcPoPlanItems.setCptWeekPropRem(ammendedItems.getRemarks());
						}
						//deleting
						for(EzcPoPlanItems tempLineItem:planItemList)
						{
							if(!uploadedLinesSet.contains(tempLineItem.getLineNo()+""))
							{	
								tempLineItem.setAmmendSts("D");
								tempLineItem.setCptWeekPropQty(BigDecimal.ZERO);
							}
						}
						//deleting
					}
					else
					{
						log.debug("IN SERVICE CALL");
						Optional<EzcPoPlanItems> maxPOPlanLineItemObj = planItemList.stream().max(Comparator.comparing(EzcPoPlanItems::getLineNo));
						int maxLineItem=0;
						if(maxPOPlanLineItemObj.isPresent())
							maxLineItem=maxPOPlanLineItemObj.get().getLineNo()+10;
						log.debug("maxLineItem::::"+maxLineItem);
						for(EzcPoWeekPlan ezcPoWeekPlanObj : poWeekPlanList)
						{
							if(ezcPoWeekPlanObj.getWeek().equals(weekId))
							{
								EzcPoPlanItemSummry itemSummry=new EzcPoPlanItemSummry();
								itemSummry.setPoPlanHeader(poPlanHdrIn);
								itemSummry.setLineNo(maxLineItem);
								itemSummry.setMatDesc(ammendedItems.getDescription());
								itemSummry.setMaterial(ammendedItems.getMaterial());
								itemSummry.setPropQty(BigDecimal.ZERO);
								itemSummry.setVendPropQty(BigDecimal.ZERO);
								itemSummry.setAcceptedQty(BigDecimal.ZERO);
								poPlanItemSumList.add(itemSummry);
							}
							EzcPoPlanItems poLineItem=new EzcPoPlanItems();
							//poLineItem.setWeek(i);
							//poLineItem.setDocId(poPlanHdrIn.getDocId());
							poLineItem.setPoWeekPlan(ezcPoWeekPlanObj);
							poLineItem.setLineNo(maxLineItem);
							poLineItem.setDescription(ammendedItems.getDescription());
							poLineItem.setMaterial(ammendedItems.getMaterial());
							poLineItem.setUom(ammendedItems.getUom());
							poLineItem.setStorLoc(ammendedItems.getLocation());
							poLineItem.setBatch(poPlanHdrIn.getOutSrcLoc());
							poLineItem.setQuantity(BigDecimal.ZERO);
							if(ezcPoWeekPlanObj.getWeek().equals(weekId))
							{
								poLineItem.setCptWeekPropQty(new BigDecimal(ammendedItems.getCptWeekPropQty()));
								poLineItem.setCptWeekPropRem(ammendedItems.getRemarks());
							}
							else
							{
								poLineItem.setCptWeekPropQty(BigDecimal.ZERO);
							}
						    poLineItem.setAmmendSts("I");
						    ezcPoWeekPlanObj.getEzcPoPlanItems().add(poLineItem);						}
					}
				}
			}
			else if("CPT_APR".equals(btnType) && !"PARKED".equals(ezcPoWeekPlan.getStatus()))
			{
				
				ezcPoWeekPlan.setStatus("PARKED");
				for(WeeklyPlanAmmendItem ammendedItems:itemList)
				{
					
					String ammendedItem=ammendedItems.getLineNo();
					uploadedLinesSet.add(ammendedItem);
					Optional<EzcPoPlanItems> poPlanItems= planItemList.stream().
						    filter(p -> (p.getLineNo()+"").equals(ammendedItem)).
						    findFirst();
					//if(!"I".equals(ammendedItems.getStatus()) && ammendedItem != null && !"null".equals(ammendedItem)  && !"".equals(ammendedItem.trim()))
					if(poPlanItems.isPresent())
					{
						log.debug("in update :: "+ammendedItem);
						
						if(poPlanItems.isPresent())
						{
							EzcPoPlanItems ezcPoPlanItems=poPlanItems.get();
							ezcPoPlanItems.setAmmendSts(ammendedItems.getStatus());
							if("U".equals(ammendedItems.getStatus()))
							{
									ezcPoPlanItems.setCptWeekAccQty(new BigDecimal(ammendedItems.getCptWeekAccQty()));
							}
							else
							{
								ezcPoPlanItems.setCptWeekAccQty(new BigDecimal(ammendedItems.getCptWeekAccQty()));
							}
							ezcPoPlanItems.setCptWeekAccRem(ammendedItems.getRemarks());
						}
						//deleting
						for(EzcPoPlanItems tempLineItem:planItemList)
						{
							if(!uploadedLinesSet.contains(tempLineItem.getLineNo()+""))
							{	
								tempLineItem.setAmmendSts("D");
								tempLineItem.setCptWeekAccQty(BigDecimal.ZERO);
							}
						}
						//deleting
					}
					else
					{
						if(!poPlanItems.isPresent())
						{
							Optional<EzcPoPlanItems> maxPOPlanLineItemObj = planItemList.stream().max(Comparator.comparing(EzcPoPlanItems::getLineNo));
							int maxLineItem=0;
							if(maxPOPlanLineItemObj.isPresent())
								maxLineItem=maxPOPlanLineItemObj.get().getLineNo()+10;
							log.debug("maxLineItem::::"+maxLineItem);
							for(EzcPoWeekPlan ezcPoWeekPlanObj : poWeekPlanList)
							{
								if(ezcPoWeekPlanObj.getWeek().equals(weekId))
								{
									EzcPoPlanItemSummry itemSummry=new EzcPoPlanItemSummry();
									itemSummry.setPoPlanHeader(poPlanHdrIn);
									itemSummry.setLineNo(maxLineItem);
									itemSummry.setMatDesc(ammendedItems.getDescription());
									itemSummry.setMaterial(ammendedItems.getMaterial());
									itemSummry.setPropQty(BigDecimal.ZERO);
									itemSummry.setVendPropQty(BigDecimal.ZERO);
									itemSummry.setAcceptedQty(BigDecimal.ZERO);
									poPlanItemSumList.add(itemSummry);
								}
								EzcPoPlanItems poLineItem=new EzcPoPlanItems();
								//poLineItem.setWeek(i);
								//poLineItem.setDocId(poPlanHdrIn.getDocId());
								poLineItem.setPoWeekPlan(ezcPoWeekPlanObj);
								poLineItem.setLineNo(maxLineItem);
								poLineItem.setDescription(ammendedItems.getDescription());
								poLineItem.setMaterial(ammendedItems.getMaterial());
								poLineItem.setUom(ammendedItems.getUom()); 
								poLineItem.setStorLoc(ammendedItems.getLocation());
								poLineItem.setBatch(poPlanHdrIn.getOutSrcLoc());
								poLineItem.setQuantity(BigDecimal.ZERO);
								if(ezcPoWeekPlanObj.getWeek().equals(weekId)) 
								{
									poLineItem.setCptWeekAccQty(new BigDecimal(ammendedItems.getCptWeekAccQty()));
									poLineItem.setCptWeekAccRem(ammendedItems.getRemarks());
								}
								else
								{
									poLineItem.setCptWeekAccQty(BigDecimal.ZERO);
								}
							    poLineItem.setAmmendSts("I");
							    ezcPoWeekPlanObj.getEzcPoPlanItems().add(poLineItem);						}
						}
					}
				}
			}

	
			if(!isError)
			{
				if("CPT_INIT".equals(btnType))
					outResp+="Plan updated successfully and sent to vendor";
				else
					outResp+="Plan updated successfully";
			}
			else
				outResp+="Error while updating PO";
			
			return outResp;
		}

		@Override
		public String ammendAndUpdatePO(String planId, int weekId, List<WeeklyPlanAmmendItem> itemList) 
		{
			boolean isError=false;
			String outResp="";
			EzcPoPlanHeader poPlanHdrIn=headerRep.getById(planId);
			List<EzcPoPlanItemSummry> poPlanItemSumList=poPlanHdrIn.getEzcPoPlanItemSummry();
			List<EzcPoWeekPlan> poWeekPlanList=poPlanHdrIn.getEzcPoWeekPlan();
			EzcPoWeekPlan ezcPoWeekPlan= poWeekPlanList.stream().
				    filter(p -> p.getWeek().equals(weekId)).
				    findFirst().get();
			List<EzcPoPlanItems> planItemList= ezcPoWeekPlan.getEzcPoPlanItems();
			Set<String> uploadedLinesSet = new HashSet<>();
			*/
/*
			for(EzcPoPlanItems planItem:planItemList)
			{
				if(!"D".equals(planItem.getAmmendSts()))				
						uploadedLinesSet.add(planItem.getLineNo()+"");
			}
			*//*

			BAPI_PO_CHANGE mainParams = new BAPI_PO_CHANGE(); 
			
			BAPIMEPOHEADER hdr_structure 	= new BAPIMEPOHEADER();
			BAPIMEPOHEADERX hdr_structurex 	= new BAPIMEPOHEADERX();

			BAPI_PO_CHANGE.POITEM poitem 	= new BAPI_PO_CHANGE.POITEM();
			BAPI_PO_CHANGE.POITEMX poitemx = new BAPI_PO_CHANGE.POITEMX();

			BAPI_PO_CHANGE.POSCHEDULE poschedule 	= new BAPI_PO_CHANGE.POSCHEDULE();
			BAPI_PO_CHANGE.POSCHEDULEX poschedulex = new BAPI_PO_CHANGE.POSCHEDULEX();

			BAPI_PO_CHANGE.POCOND pocond 	= new BAPI_PO_CHANGE.POCOND(); 
			BAPI_PO_CHANGE.POCONDX pocondx = new BAPI_PO_CHANGE.POCONDX();   

			BAPI_PO_CHANGE.POTEXTHEADER potextheader = new BAPI_PO_CHANGE.POTEXTHEADER();   

			
			
			
			boolean changeINSAP=false;
		
			mainParams.setPURCHASEORDER(ezcPoWeekPlan.getPo());
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
			for(WeeklyPlanAmmendItem ammendedItems:itemList)
			{
				String ammendedItem=ammendedItems.getLineNo();
				BigDecimal itemQtyFinal=new BigDecimal(ammendedItems.getCptWeekAccQty());
				Optional<EzcPoPlanItems> poPlanItemsOpt= planItemList.stream().
					    filter(p -> (p.getLineNo()+"").equals(ammendedItem)).
					    findFirst();
				log.debug("in ammendedItems");
					if(poPlanItemsOpt.isPresent())
					{
						
						EzcPoPlanItems poPlanItems=poPlanItemsOpt.get();						log.debug("item added ");
						
						if("D".equals(poPlanItems.getAmmendSts()))
								continue;
						uploadedLinesSet.add(ammendedItems.getLineNo()+"");
						
						String lineNo="00000"+poPlanItems.getLineNo();
						lineNo=lineNo.substring(lineNo.length()-5);
						BAPIMEPOITEM  poItemRow		=	new BAPIMEPOITEM();
						BAPIMEPOITEMX poItemRowx	=	new BAPIMEPOITEMX();
						poItemRow.setPO_ITEM(lineNo);poItemRowx.setPO_ITEM(lineNo);
						
						if(poPlanItems.getCptWeekAccQty().compareTo(itemQtyFinal) != 0)
						{
							poItemRow.setQUANTITY(itemQtyFinal);poItemRowx.setQUANTITY("X");
							poitem.getItem().add(poItemRow); 	 poitemx.getItem().add(poItemRowx);
							changeINSAP=true;
							poPlanItems.setCptWeekAccQty(itemQtyFinal);
						}
						
					}
					else
					{
						BAPIMEPOITEM  poItemRow		=	new BAPIMEPOITEM();
						BAPIMEPOITEMX poItemRowx	=	new BAPIMEPOITEMX();
						Optional<EzcPoPlanItems> maxPOPlanLineItemObj = planItemList.stream().max(Comparator.comparing(EzcPoPlanItems::getLineNo));
						int maxLineItem=0;
						if(maxPOPlanLineItemObj.isPresent())
							maxLineItem=maxPOPlanLineItemObj.get().getLineNo()+10;
						String lineNo="00000"+maxLineItem;
						log.debug("maxLineItem::::"+maxLineItem);
						for(EzcPoWeekPlan ezcPoWeekPlanObj : poWeekPlanList)
						{
							if(ezcPoWeekPlanObj.getWeek().equals(weekId))
							{
								EzcPoPlanItemSummry itemSummry=new EzcPoPlanItemSummry();
								itemSummry.setPoPlanHeader(poPlanHdrIn);
								itemSummry.setLineNo(maxLineItem);
								itemSummry.setMatDesc(ammendedItems.getDescription());
								itemSummry.setMaterial(ammendedItems.getMaterial());
								itemSummry.setPropQty(BigDecimal.ZERO);
								itemSummry.setVendPropQty(BigDecimal.ZERO);
								itemSummry.setAcceptedQty(BigDecimal.ZERO);
								poPlanItemSumList.add(itemSummry);
							}
							EzcPoPlanItems poLineItem=new EzcPoPlanItems();
							//poLineItem.setWeek(i);
							//poLineItem.setDocId(poPlanHdrIn.getDocId());
							poLineItem.setPoWeekPlan(ezcPoWeekPlanObj);
							poLineItem.setLineNo(maxLineItem);
							poLineItem.setDescription(ammendedItems.getDescription());
							poLineItem.setMaterial(ammendedItems.getMaterial());
							poLineItem.setUom(ammendedItems.getUom());
							poLineItem.setStorLoc(ammendedItems.getLocation());
							poLineItem.setBatch(poPlanHdrIn.getOutSrcLoc());
							poLineItem.setQuantity(BigDecimal.ZERO);
							if(ezcPoWeekPlanObj.getWeek().equals(weekId))
							{
								poLineItem.setCptWeekPropQty(new BigDecimal(ammendedItems.getCptWeekPropQty()));
								poLineItem.setCptWeekPropRem(ammendedItems.getRemarks());
							}
							else
							{
								poLineItem.setCptWeekPropQty(BigDecimal.ZERO);
							}
						    poLineItem.setAmmendSts("I");
						    ezcPoWeekPlanObj.getEzcPoPlanItems().add(poLineItem);						
						    }
						String tempVal=poPlanHdrIn.getMonthYear();
					    int tempMon = Integer.parseInt(tempVal.split("/")[0]);
					    int tempyr = Integer.parseInt(tempVal.split("/")[1]);
						String delDate="";//sdfSchd.format(getFirstDayOfMonthYearAndWeek(tempyr, tempMon,weekId));
						if(weekId == 1)delDate="09."+tempMon+"."+tempyr;
						else if(weekId == 2)delDate="16."+tempMon+"."+tempyr;
						else if(weekId == 3)delDate="24."+tempMon+"."+tempyr;
						else if(weekId == 4)delDate=getLastDayOfMonth(tempMon,tempyr)+"."+tempMon+"."+tempyr;
						String tempMat =ammendedItems.getMaterial();
						if(tempMat.matches("\\d*"))
						{
							tempMat="00000000000000000000000000000000"+tempMat;
							tempMat=tempMat.substring(tempMat.length()-18);
							log.debug("tempMat:::"+tempMat);
						}
						poItemRow.setQUANTITY(itemQtyFinal);poItemRowx.setQUANTITY("X");
						
						poItemRow.setMATERIAL(tempMat);	 poItemRowx.setMATERIAL("X");
						poItemRow.setVEND_MAT("");	 	 poItemRowx.setVEND_MAT("X");
						poItemRow.setPLANT(poPlanHdrIn.getOutSrcLoc());		 poItemRowx.setPLANT("X");
						poItemRow.setBATCH(poPlanHdrIn.getOutSrcLoc()); poItemRowx.setBATCH("X");
						poItemRow.setQUANTITY(itemQtyFinal);	 poItemRowx.setQUANTITY("X");
						poItemRow.setSTGE_LOC(ammendedItems.getLocation());poItemRowx.setSTGE_LOC("X");
						//poItemRow.setNET_PRICE(new java.math.BigDecimal(rfqPrice));	 poItemRowx.setNET_PRICE("X");
						//poItemRow.setPRICE_UNIT(new java.math.BigDecimal("1000"));	 poItemRowx.setPRICE_UNIT("X");
						poItemRow.setGR_TO_DATE(sdf.format(new Date()));	 poItemRowx.setGR_TO_DATE("X");
						poItemRow.setITEM_CAT("L");poItemRowx.setITEM_CAT("X");
										
						poitem.getItem().add(poItemRow); 	 poitemx.getItem().add(poItemRowx);
						
						BAPIMEPOSCHEDULE poSchdRow = new BAPIMEPOSCHEDULE();
						BAPIMEPOSCHEDULX poSchdRowx = new BAPIMEPOSCHEDULX();

						poSchdRow.setPO_ITEM(lineNo);			poSchdRowx.setPO_ITEM(lineNo);		poSchdRowx.setPO_ITEMX("X");
						poSchdRow.setSCHED_LINE("0001");		poSchdRowx.setSCHED_LINE("0001");	poSchdRowx.setSCHED_LINEX("X");
						poSchdRow.setDELIVERY_DATE(delDate);		poSchdRowx.setDELIVERY_DATE("X");
						poSchdRow.setQUANTITY(itemQtyFinal);	poSchdRowx.setQUANTITY("X");

						poschedule.getItem().add(poSchdRow); 		poschedulex.getItem().add(poSchdRowx);

						
						
						changeINSAP=true;
					}
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
			if(changeINSAP) 
			{
				BAPI_PO_CHANGEResponse resp = null;
	
				try{
					resp = (BAPI_PO_CHANGEResponse)poSAPService.callService(mainParams);	
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
						
						if("E".equals(lv_Type)){
							isError      = true;
							outResp+="Error while ammending PO "+lv_Msg;
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
	  
					
			}
		}
		if(!isError)
		{
			try {
				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
				Users userObj = (Users)authentication.getPrincipal();
				String loggedUser=(String)userObj.getUserId();
				ezcPoWeekPlan.setModifiedBy(loggedUser);
				ezcPoWeekPlan.setModifiedOn(new Date());
				
			} catch (Exception e) {
				
			}
			//ezcPoWeekPlan.setStatus("APPROVED");
			//ezcPoWeekPlan.setNextPart("");
			
		}
			if(isError)
				return "N##"+outResp;
			else
				return "Y##"+outResp;
		}

		
		@Override
		public String updateVendorWeeklyPlan(WeeklyPlanFormDetInput weeklyPlanFormDetInput) {
			EzcPoPlanHeader poPlanHdrIn=headerRep.getById(weeklyPlanFormDetInput.getDocId());
			String weekId=weeklyPlanFormDetInput.getWeek();
			List<EzcPoWeekPlan> poWeekPlanList=poPlanHdrIn.getEzcPoWeekPlan();
			EzcPoWeekPlan ezcPoWeekPlan= poWeekPlanList.stream().
				    filter(p -> (p.getWeek()+"").equals(weekId)).
				    findFirst().get();
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			Users userObj = (Users)authentication.getPrincipal();
			String loggedUser=(String)userObj.getUserId();
			ezcPoWeekPlan.setModifiedBy(loggedUser);
			ezcPoWeekPlan.setModifiedOn(new Date());
			ezcPoWeekPlan.setStatus("SUBMITTED");
			ezcPoWeekPlan.setNextPart("ROLE_CPT");
			ezcPoWeekPlan.setCurrentStep("CPRACC");
			List<EzcPoPlanItems> planItemListIn= weeklyPlanFormDetInput.getEzcPoPlanItems();
			List<EzcPoPlanItems> planItemList= ezcPoWeekPlan.getEzcPoPlanItems();
			for(EzcPoPlanItems ammendedItems:planItemListIn)
			{
				
				int ammendedItem=ammendedItems.getLineNo();
				Optional<EzcPoPlanItems> poPlanItems= planItemList.stream().
					    filter(p -> (p.getLineNo() == ammendedItem)).
					    findFirst();
					if(poPlanItems.isPresent())
					{
						EzcPoPlanItems ezcPoPlanItems=poPlanItems.get();
						
						ezcPoPlanItems.setVendWeekPropQty(ammendedItems.getVendWeekPropQty());
						ezcPoPlanItems.setVendWeekPropRem(ammendedItems.getVendWeekPropRem());
					}
			}
			
			return null;
		}
		  public static int getLastDayOfMonth(int month, int year) {
		        YearMonth yearMonth = YearMonth.of(year, month);
		        return yearMonth.lengthOfMonth();
		    }

		@Override
			public String updateInSAP(String planId, int weekId, List<WeeklyPlanAmmendItem> itemList) {
				boolean isError=false;
				String outResp="";
				BAPI_PO_CHANGE mainParams = new BAPI_PO_CHANGE(); 
				
				BAPIMEPOHEADER hdr_structure 	= new BAPIMEPOHEADER();
				BAPIMEPOHEADERX hdr_structurex 	= new BAPIMEPOHEADERX();

				BAPI_PO_CHANGE.POITEM poitem 	= new BAPI_PO_CHANGE.POITEM();
				BAPI_PO_CHANGE.POITEMX poitemx = new BAPI_PO_CHANGE.POITEMX();

				BAPI_PO_CHANGE.POSCHEDULE poschedule 	= new BAPI_PO_CHANGE.POSCHEDULE();
				BAPI_PO_CHANGE.POSCHEDULEX poschedulex = new BAPI_PO_CHANGE.POSCHEDULEX();

				BAPI_PO_CHANGE.POCOND pocond 	= new BAPI_PO_CHANGE.POCOND(); 
				BAPI_PO_CHANGE.POCONDX pocondx = new BAPI_PO_CHANGE.POCONDX();   

				BAPI_PO_CHANGE.POTEXTHEADER potextheader = new BAPI_PO_CHANGE.POTEXTHEADER();   

				
				
				
				boolean changeINSAP=false;
				EzcPoPlanHeader poPlanHdrIn=headerRep.getById(planId);
				List<EzcPoWeekPlan> poWeekPlanList=poPlanHdrIn.getEzcPoWeekPlan();
				EzcPoWeekPlan ezcPoWeekPlan= poWeekPlanList.stream().
					    filter(p -> p.getWeek().equals(weekId)).
					    findFirst().get();
				List<EzcPoPlanItems> planItemList= ezcPoWeekPlan.getEzcPoPlanItems();
				mainParams.setPURCHASEORDER(ezcPoWeekPlan.getPo());
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
				for(EzcPoPlanItems ezcPoPlanItems:planItemList)
				{
					log.debug("in ammendedItems");
					String ammendedItem=ezcPoPlanItems.getLineNo()+"";
					//if("U".equals(ammendedItems.getStatus()) || "D".equals(ammendedItems.getStatus()))
					{
						
						//if(poPlanItems.isPresent())
						{
							log.debug("item added ");
							
							String lineNo="00000"+ezcPoPlanItems.getLineNo();
							lineNo=lineNo.substring(lineNo.length()-5);
							BAPIMEPOITEM  poItemRow		=	new BAPIMEPOITEM();
							BAPIMEPOITEMX poItemRowx	=	new BAPIMEPOITEMX();
							poItemRow.setPO_ITEM(lineNo);poItemRowx.setPO_ITEM(lineNo);
							if("U".equals(ezcPoPlanItems.getAmmendSts()))
							{
								poItemRow.setQUANTITY(ezcPoPlanItems.getCptWeekAccQty());poItemRowx.setQUANTITY("X");
								poitem.getItem().add(poItemRow); 	 poitemx.getItem().add(poItemRowx);
								changeINSAP=true;
							}
							else if("D".equals(ezcPoPlanItems.getAmmendSts()))
							{
							
								poItemRow.setQUANTITY(new BigDecimal("0.1"));poItemRowx.setQUANTITY("X");
								poItemRow.setDELETE_IND("X");poItemRowx.setDELETE_IND("X");
								poitem.getItem().add(poItemRow); 	 poitemx.getItem().add(poItemRowx);
								changeINSAP=true;
							}
							else if("I".equals(ezcPoPlanItems.getAmmendSts()))
							{
								String tempVal=poPlanHdrIn.getMonthYear();
							    int tempMon = Integer.parseInt(tempVal.split("/")[0]);
							    int tempyr = Integer.parseInt(tempVal.split("/")[1]);
								String delDate="";//sdfSchd.format(getFirstDayOfMonthYearAndWeek(tempyr, tempMon,weekId));
								if(weekId == 1)delDate="09."+tempMon+"."+tempyr;
								else if(weekId == 2)delDate="16."+tempMon+"."+tempyr;
								else if(weekId == 3)delDate="24."+tempMon+"."+tempyr;
								else if(weekId == 4)delDate=getLastDayOfMonth(tempMon,tempyr)+"."+tempMon+"."+tempyr;
								String tempMat =ezcPoPlanItems.getMaterial();
								if(tempMat.matches("\\d*"))
								{
									tempMat="00000000000000000000000000000000"+tempMat;
									tempMat=tempMat.substring(tempMat.length()-18);
									log.debug("tempMat:::"+tempMat);
								}
								poItemRow.setQUANTITY(ezcPoPlanItems.getCptWeekAccQty());poItemRowx.setQUANTITY("X");
								
								poItemRow.setMATERIAL(tempMat);	 poItemRowx.setMATERIAL("X");
								poItemRow.setVEND_MAT("");	 	 poItemRowx.setVEND_MAT("X");
								poItemRow.setPLANT(poPlanHdrIn.getOutSrcLoc());		 poItemRowx.setPLANT("X");
								poItemRow.setBATCH(ezcPoPlanItems.getBatch()); poItemRowx.setBATCH("X");
								poItemRow.setQUANTITY(ezcPoPlanItems.getCptWeekAccQty());	 poItemRowx.setQUANTITY("X");
								poItemRow.setSTGE_LOC(ezcPoPlanItems.getStorLoc());poItemRowx.setSTGE_LOC("X");
								//poItemRow.setNET_PRICE(new java.math.BigDecimal(rfqPrice));	 poItemRowx.setNET_PRICE("X");
								//poItemRow.setPRICE_UNIT(new java.math.BigDecimal("1000"));	 poItemRowx.setPRICE_UNIT("X");
								poItemRow.setGR_TO_DATE(sdf.format(new Date()));	 poItemRowx.setGR_TO_DATE("X");
								poItemRow.setITEM_CAT("L");poItemRowx.setITEM_CAT("X");
												
								poitem.getItem().add(poItemRow); 	 poitemx.getItem().add(poItemRowx);
								
								BAPIMEPOSCHEDULE poSchdRow = new BAPIMEPOSCHEDULE();
								BAPIMEPOSCHEDULX poSchdRowx = new BAPIMEPOSCHEDULX();

								poSchdRow.setPO_ITEM(lineNo);			poSchdRowx.setPO_ITEM(lineNo);		poSchdRowx.setPO_ITEMX("X");
								poSchdRow.setSCHED_LINE("0001");		poSchdRowx.setSCHED_LINE("0001");	poSchdRowx.setSCHED_LINEX("X");
								poSchdRow.setDELIVERY_DATE(delDate);		poSchdRowx.setDELIVERY_DATE("X");
								poSchdRow.setQUANTITY(ezcPoPlanItems.getCptWeekAccQty());	poSchdRowx.setQUANTITY("X");

								poschedule.getItem().add(poSchdRow); 		poschedulex.getItem().add(poSchdRowx);

								
								
								changeINSAP=true;
							}
						}
					} 
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
				if(changeINSAP) 
				{
					BAPI_PO_CHANGEResponse resp = null;
		
					try{
						resp = (BAPI_PO_CHANGEResponse)poSAPService.callService(mainParams);	
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
							
							if("E".equals(lv_Type)){
								isError      = true;
								outResp+="Error while creating PO "+lv_Msg;
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
		  
						
				}
			}
			if(!isError)
			{
				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
				Users userObj = (Users)authentication.getPrincipal();
				String loggedUser=(String)userObj.getUserId();
				ezcPoWeekPlan.setModifiedBy(loggedUser);
				ezcPoWeekPlan.setModifiedOn(new Date());
				ezcPoWeekPlan.setStatus("APPROVED");
				ezcPoWeekPlan.setNextPart("");
				ezcPoWeekPlan.setCurrentStep("");
			}
				if(isError)
					return "N##"+outResp;
				else
					return "Y##"+outResp;
			}
			@Override
			public String updatePOInSAP(EzcPoWeekPlan poWeekPlan,String plant,String monthYr)
			{
				String poNo=poWeekPlan.getPo();
				int weekId=poWeekPlan.getWeek();
				boolean isError=false;
				String outResp="";
				List<EzcPoPlanItems> poItems=poWeekPlan.getEzcPoPlanItems();
				String [] outputFields =new String[] {"EBELP","MENGE"};
				String [] queryValues =new String[1];
				queryValues[0]= "EBELN EQ '"+poNo+"'";
				Hashtable<String,String> htPOQty=new Hashtable<String,String>();
				
				List<String[]> matList= rfcReadSAPService.readRFCTable("EKPO", outputFields, queryValues);
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
				for(String[] tempArrObj : matList)
				{
					String tempQty=tempArrObj[1].trim();
					String tempItem=tempArrObj[0].trim();
					//tempItem=tempItem.replaceFirst("^0+(?!$)", "");
					htPOQty.put(tempItem, tempQty);
				}
				log.debug("htPOQty:::"+htPOQty);
				BAPI_PO_CHANGE mainParams = new BAPI_PO_CHANGE(); 
				
				BAPIMEPOHEADER hdr_structure 	= new BAPIMEPOHEADER();
				BAPIMEPOHEADERX hdr_structurex 	= new BAPIMEPOHEADERX();

				BAPI_PO_CHANGE.POITEM poitem 	= new BAPI_PO_CHANGE.POITEM();
				BAPI_PO_CHANGE.POITEMX poitemx = new BAPI_PO_CHANGE.POITEMX();

				BAPI_PO_CHANGE.POSCHEDULE poschedule 	= new BAPI_PO_CHANGE.POSCHEDULE();
				BAPI_PO_CHANGE.POSCHEDULEX poschedulex = new BAPI_PO_CHANGE.POSCHEDULEX();

				BAPI_PO_CHANGE.POCOND pocond 	= new BAPI_PO_CHANGE.POCOND(); 
				BAPI_PO_CHANGE.POCONDX pocondx = new BAPI_PO_CHANGE.POCONDX();   

				BAPI_PO_CHANGE.POTEXTHEADER potextheader = new BAPI_PO_CHANGE.POTEXTHEADER();   

				
				
				
				boolean changeINSAP=false;
			
				mainParams.setPURCHASEORDER(poNo);
				BAPIMEPOITEM  poItemRow		=	new BAPIMEPOITEM();
				BAPIMEPOITEMX poItemRowx	=	new BAPIMEPOITEMX();
				
				for(EzcPoPlanItems poItem:poItems)
				{
					
					String lineNo="00000"+poItem.getLineNo();
					lineNo=lineNo.substring(lineNo.length()-5);
					BigDecimal itemQtyFinal=poItem.getCptWeekAccQty();
					String poItemQty=htPOQty.get(lineNo);
					poItemRow		=	new BAPIMEPOITEM();
					poItemRowx	=	new BAPIMEPOITEMX();
					poItemRow.setPO_ITEM(lineNo);poItemRowx.setPO_ITEM(lineNo);
					if(!htPOQty.containsKey(lineNo))
					{
						String tempMat=poItem.getMaterial();
						if(tempMat.matches("\\d*"))
						{
							tempMat="00000000000000000000000000000000"+tempMat;
							tempMat=tempMat.substring(tempMat.length()-18);
							log.debug("tempMat:::"+tempMat);
						}
						log.debug("inserting item in PO");
						String tempVal=monthYr;
						int tempMon = Integer.parseInt(tempVal.split("/")[0]);
					    int tempyr = Integer.parseInt(tempVal.split("/")[1]);
					    log.debug("tempVal:::"+tempVal);
						String delDate="";//sdfSchd.format(getFirstDayOfMonthYearAndWeek(tempyr, tempMon,weekId));
						if(weekId == 1)delDate="09."+tempMon+"."+tempyr;
						else if(weekId == 2)delDate="16."+tempMon+"."+tempyr;
						else if(weekId == 3)delDate="24."+tempMon+"."+tempyr;
						else if(weekId == 4)delDate=getLastDayOfMonth(tempMon,tempyr)+"."+tempMon+"."+tempyr;
						
						poItemRow		=	new BAPIMEPOITEM();
						poItemRowx	=	new BAPIMEPOITEMX();
						poItemRow.setQUANTITY(itemQtyFinal);poItemRowx.setQUANTITY("X");

						poItemRow.setPO_ITEM(lineNo);	poItemRowx.setPO_ITEM(lineNo);
						poItemRow.setMATERIAL(tempMat);	 poItemRowx.setMATERIAL("X");
						poItemRow.setVEND_MAT("");	 	 poItemRowx.setVEND_MAT("X");
						poItemRow.setPLANT(plant);		 poItemRowx.setPLANT("X");
						poItemRow.setBATCH(plant); poItemRowx.setBATCH("X");
						poItemRow.setQUANTITY(itemQtyFinal);	 poItemRowx.setQUANTITY("X");
						poItemRow.setSTGE_LOC(poItem.getStorLoc());poItemRowx.setSTGE_LOC("X");
						//poItemRow.setNET_PRICE(new java.math.BigDecimal(rfqPrice));	 poItemRowx.setNET_PRICE("X");
						//poItemRow.setPRICE_UNIT(new java.math.BigDecimal("1000"));	 poItemRowx.setPRICE_UNIT("X");
						poItemRow.setGR_TO_DATE(sdf.format(new Date()));	 poItemRowx.setGR_TO_DATE("X");
						poItemRow.setITEM_CAT("L");poItemRowx.setITEM_CAT("X");
										
						poitem.getItem().add(poItemRow); 	 poitemx.getItem().add(poItemRowx);

						log.debug("delDate:::"+delDate);	
						BAPIMEPOSCHEDULE poSchdRow = new BAPIMEPOSCHEDULE();
						BAPIMEPOSCHEDULX poSchdRowx = new BAPIMEPOSCHEDULX();

						poSchdRow.setPO_ITEM(lineNo);			poSchdRowx.setPO_ITEM(lineNo);		poSchdRowx.setPO_ITEMX("X");
						poSchdRow.setSCHED_LINE("0001");		poSchdRowx.setSCHED_LINE("0001");	poSchdRowx.setSCHED_LINEX("X");
						poSchdRow.setDELIVERY_DATE(delDate);		poSchdRowx.setDELIVERY_DATE("X");
						poSchdRow.setQUANTITY(itemQtyFinal);	poSchdRowx.setQUANTITY("X");

						poschedule.getItem().add(poSchdRow); 		poschedulex.getItem().add(poSchdRowx);
						changeINSAP=true;
					}
					else if("D".equals(poItem.getAmmendSts()))
					{
					
						poItemRow.setQUANTITY(new BigDecimal("0.1"));poItemRowx.setQUANTITY("X");
						poItemRow.setDELETE_IND("X");poItemRowx.setDELETE_IND("X");
						poitem.getItem().add(poItemRow); 	 poitemx.getItem().add(poItemRowx);
						changeINSAP=true;
					}
					else if(new BigDecimal(poItemQty).compareTo(itemQtyFinal) != 0)
					{
						poItemRow.setQUANTITY(itemQtyFinal);poItemRowx.setQUANTITY("X");
						poitem.getItem().add(poItemRow); 	 poitemx.getItem().add(poItemRowx);
						changeINSAP=true;
					}
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
				if(changeINSAP) 
				{
					BAPI_PO_CHANGEResponse resp = null;
		
					try{
						resp = (BAPI_PO_CHANGEResponse)poSAPService.callService(mainParams);	
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
							
							if("E".equals(lv_Type)){
								isError      = true;
								outResp+="Error while updating PO "+lv_Msg;
							}							
						} 
		  
						
				}
			}
				
				if(isError)
					return "N##"+outResp;
				else 
					return "Y##Plan Updated Successfully";	
			}
			
			@Override
			public String updateVendorWeeklyPlanAll(PrdPlanFormDetInput prdPlanFormDetInput) 
			{
				EzcPoPlanHeader poPlanHdrIn=headerRep.getById(prdPlanFormDetInput.getDocId());
				String weekId=prdPlanFormDetInput.getSelWeek();
				List<EzcPoWeekPlan> poWeekPlanList=poPlanHdrIn.getEzcPoWeekPlan();
				EzcPoWeekPlan ezcPoWeekPlan= poWeekPlanList.stream().
					    filter(p -> (p.getWeek()+"").equals(weekId)).
					    findFirst().get();
				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
				Users userObj = (Users)authentication.getPrincipal();
				String loggedUser=(String)userObj.getUserId();
				ezcPoWeekPlan.setModifiedBy(loggedUser);
				ezcPoWeekPlan.setModifiedOn(new Date());
				ezcPoWeekPlan.setStatus("SUBMITTED");
				ezcPoWeekPlan.setNextPart("ROLE_CPT");
				ezcPoWeekPlan.setCurrentStep("CPRACC");
				List<EzcPoPlanItems> planItemList= ezcPoWeekPlan.getEzcPoPlanItems();
				for(PrdPlanRequestItemDto ammendedItems:prdPlanFormDetInput.getPrdPlanRequestItems())
				{
					
					int ammendedItem=ammendedItems.getLineNo();
					Optional<EzcPoPlanItems> poPlanItems= planItemList.stream().
						    filter(p -> (p.getLineNo() == ammendedItem)).
						    findFirst();
						if(poPlanItems.isPresent())
						{
							EzcPoPlanItems ezcPoPlanItems=poPlanItems.get();
							
							ezcPoPlanItems.setVendWeekPropQty(new BigDecimal(ammendedItems.getVendWeekPropQty()));
							ezcPoPlanItems.setVendWeekPropRem(ammendedItems.getVendWeekPropRem());
						}
				}
				
				return null;
			}
			
			
}
*/
