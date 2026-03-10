/*
package com.ezc.aragenPR.webapp.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ezc.aragenPR.webapp.dto.PoSynchAdminInput;
import com.ezc.aragenPR.webapp.model.EzPurchaseOrder;
import com.ezc.aragenPR.webapp.service.IPurchaseOrder;
import com.ezc.aragenPR.webapp.service.RFCReadSAPService;
import com.ezc.aragenPR.webapp.wsdl.readtable.gen.RFCDBFLD;
import com.ezc.aragenPR.webapp.wsdl.readtable.gen.RFCDBOPT;
import com.ezc.aragenPR.webapp.wsdl.readtable.gen.RFCREADTABLE;
import com.ezc.aragenPR.webapp.wsdl.readtable.gen.RFCREADTABLEResponse;
import com.ezc.aragenPR.webapp.wsdl.readtable.gen.TAB512;

import lombok.extern.slf4j.Slf4j;


@Controller
@Slf4j
public class SyncPoController {
	
	@Autowired
	RFCReadSAPService rfcReadSAPService;
	
	@Autowired
	IPurchaseOrder poService;
	
	@PostMapping(value = "/syncpo/save")
	public String synchPOFromSAP(@ModelAttribute PoSynchAdminInput poSynchAdminInput,Model model, final RedirectAttributes ra) {
		System.out.println("Controller hit!!!");
		
		 String fromDate=poSynchAdminInput.getStartDate();
		 String endDate=poSynchAdminInput.getEndDate();
		
		 SimpleDateFormat inputFormat=new SimpleDateFormat("dd-MM-yyyy");
		 SimpleDateFormat sapFormat=new SimpleDateFormat("yyyyMMdd");
		 
		 
		  try {
			RFCREADTABLE request1= new RFCREADTABLE();
			  request1.setDELIMITER("#"); 
			  request1.setQUERYTABLE("EKKO");
			  
			  RFCREADTABLE.FIELDS feilds = new RFCREADTABLE.FIELDS();
			  java.util.List<RFCDBFLD> fieldList=feilds.getItem(); 
			  RFCDBFLD feildItem = null;
			  
			  feildItem = new RFCDBFLD(); feildItem.setFIELDNAME("EBELN");fieldList.add(feildItem);
			  feildItem = new RFCDBFLD(); feildItem.setFIELDNAME("BUKRS");fieldList.add(feildItem);
			  feildItem = new RFCDBFLD(); feildItem.setFIELDNAME("AEDAT");fieldList.add(feildItem);
			  feildItem = new RFCDBFLD(); feildItem.setFIELDNAME("LIFNR");fieldList.add(feildItem);
			  feildItem = new RFCDBFLD(); feildItem.setFIELDNAME("EKORG");fieldList.add(feildItem);
			  feildItem = new RFCDBFLD(); feildItem.setFIELDNAME("EKGRP");fieldList.add(feildItem);
			  
			  
			  request1.setFIELDS(feilds); //setting feilds
			  
			  RFCREADTABLE.OPTIONS options = new RFCREADTABLE.OPTIONS();
			  java.util.List<RFCDBOPT> optionList=options.getItem(); 
			  RFCDBOPT optionItem = new RFCDBOPT(); 
			  optionItem.setTEXT("BUKRS EQ '1000' AND BSART EQ 'ZSPO'");optionList.add(optionItem);
			  optionItem = new RFCDBOPT();
     		  optionItem.setTEXT("AND ( AEDAT BETWEEN '"+sapFormat.format(inputFormat.parse(fromDate))+"' AND '"+sapFormat.format(inputFormat.parse(endDate))+"' )");optionList.add(optionItem);
			  
			  request1.setOPTIONS(options);//setting options
			  
			  request1.setDATA(new RFCREADTABLE.DATA());//setting data
			  
			  RFCREADTABLEResponse response1 =(RFCREADTABLEResponse)rfcReadSAPService.callService(request1);
			  log.debug("rfc::::"+response1.getDATA().getItem().size());
			  
			 List<TAB512> itemList  = response1.getDATA().getItem();
			  
			  List<EzPurchaseOrder> poList=new ArrayList<EzPurchaseOrder>(); 
			  
			  
			  for(TAB512 sapItem:itemList)
			  {
				  String [] tempArr = sapItem.getWA().split("#");
				  EzPurchaseOrder tempPOItem=new EzPurchaseOrder();
				  tempPOItem.setPoNo(tempArr[0]);
				  tempPOItem.setVendor(tempArr[3]);
				  tempPOItem.setCoCode(tempArr[1]);
				  tempPOItem.setDocDate(sapFormat.parse(tempArr[2]));
				  tempPOItem.setPurOrg(tempArr[4]);
				  tempPOItem.setPurGrp(tempArr[5]);
				  log.debug("temp[]"+tempArr[5]);
				  log.debug("temp[]"+tempArr[4]);
				  log.debug("temp[]"+tempArr[3]);
				  log.debug("temp[]"+tempArr[2]);
				  log.debug("temp[]"+tempArr[1]);
				  log.debug("temp[]"+tempArr[0]);
				  poList.add(tempPOItem);
			  }
			  
			  poService.synchPO(poList);
			 
			  
			  ra.addFlashAttribute("success","PO Data synchronized successfully");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			ra.addFlashAttribute("error","Error in synch."+e);
		}
		  
			 
			return "redirect:/syncpo/add";
	 }
	
	@RequestMapping("/syncpo/add")
    public String add(Model model) {
		PoSynchAdminInput input=new PoSynchAdminInput();
		model.addAttribute("poSynchAdminInput", input);
        return "purchaseorder/syncpo";

    }
	

	}
    
		  
		  
		*/
