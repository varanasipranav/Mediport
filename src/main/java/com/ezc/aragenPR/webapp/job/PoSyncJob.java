/*
package com.ezc.aragenPR.webapp.job;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ezc.aragenPR.webapp.model.EzPurchaseOrder;
import com.ezc.aragenPR.webapp.service.IPurchaseOrder;
import com.ezc.aragenPR.webapp.service.JobHistoryService;
import com.ezc.aragenPR.webapp.service.RFCReadSAPService;
import com.ezc.aragenPR.webapp.wsdl.readtable.gen.RFCDBFLD;
import com.ezc.aragenPR.webapp.wsdl.readtable.gen.RFCDBOPT;
import com.ezc.aragenPR.webapp.wsdl.readtable.gen.RFCREADTABLE;
import com.ezc.aragenPR.webapp.wsdl.readtable.gen.RFCREADTABLEResponse;
import com.ezc.aragenPR.webapp.wsdl.readtable.gen.TAB512;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class PoSyncJob implements Job {

	@Autowired
	RFCReadSAPService rfcReadSAPService;

    @Autowired
    private JobHistoryService jobHistoryService;

	@Autowired
	IPurchaseOrder poService;

	 @Override
	    public void execute(JobExecutionContext context) {

	        DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");

	        LocalDate end = LocalDate.now();

	        LocalDate start = end.minusDays(15);

	        String fromDate = start.format(inputFormat);
	        String endDate = end.format(inputFormat);

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
				  poList.add(tempPOItem);
			  }
			  poService.synchPO(poList);
			  jobHistoryService.saveJobExecution(context.getJobDetail().getKey().getName(), "SUCCESS", null);

		} catch (ParseException e) {
			log.debug("PO Sync Err => " + e.toString());
            jobHistoryService.saveJobExecution(context.getJobDetail().getKey().getName(), "FAILURE", e.getMessage());
		}
	 }

}
*/
