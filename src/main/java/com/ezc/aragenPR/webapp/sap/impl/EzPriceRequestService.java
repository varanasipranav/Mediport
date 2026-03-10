//package com.ezc.outsrc.webapp.sap.impl;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Hashtable;
//import java.util.List;
//import java.util.Optional;
//
//import org.apache.commons.codec.binary.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.util.NumberUtils;
//
//import com.ezc.outsrc.webapp.dto.CustomerSalesData;
//import com.ezc.outsrc.webapp.dto.PriceFormViewDet;
//import com.ezc.outsrc.webapp.dto.SAPReturnTable;
//import com.ezc.outsrc.webapp.dto.SAPReturnTableRow;
//import com.ezc.outsrc.webapp.dto.ZSD_MATPRICE_INPUT;
//import com.ezc.outsrc.webapp.dto.ZSD_MATPRICE_OUTPUT;
//import com.ezc.outsrc.webapp.model.EzMaterialMaster;
//import com.ezc.outsrc.webapp.model.EzPriceRequestHeader;
//import com.ezc.outsrc.webapp.model.EzPriceRequestItems;
//import com.ezc.outsrc.webapp.model.EzPriceSeqMaster;
//import com.ezc.outsrc.webapp.persistance.dao.PriceRequestHeaderRepository;
//import com.ezc.outsrc.webapp.persistance.dao.PriceSeqMasterRepository;
//import com.ezc.outsrc.webapp.sap.IPriceRequestService;
//import com.ezc.outsrc.webapp.sapconnection.EzSAPHandler;
//import com.sap.mw.jco.JCO;
//
//import lombok.extern.slf4j.Slf4j;
//@Slf4j
//@Component
//public class EzPriceRequestService implements IPriceRequestService {
//
//	@Autowired
//    PriceSeqMasterRepository seqMasterRepo;
//
//	@Autowired
//    PriceRequestHeaderRepository reqHeaderRepo;
//
//	@Override
//	public SAPReturnTable postRequestItems(PriceFormViewDet priceFormViewDet) {
//		SAPReturnTable sapReturnTable = new SAPReturnTable();
//		List<SAPReturnTableRow> sapReturnTableRowList=new  ArrayList<SAPReturnTableRow>();
//		boolean sapError=false;
//		Optional<EzPriceSeqMaster> seqMasterOpt=seqMasterRepo.findById(priceFormViewDet.getPriceSeqMaster().getId());
//		EzPriceRequestHeader reqHeaderInput=priceFormViewDet.getReqHeader();
//		Optional<EzPriceRequestHeader> reqHeaderOpt=reqHeaderRepo.findById(reqHeaderInput.getId());
//		EzPriceRequestHeader reqHeader=null;
//		if(reqHeaderOpt.isPresent())
//		{
//			reqHeader=reqHeaderOpt.get();
//		}
//		List<EzPriceRequestItems> reqItems=reqHeader.getEzPriceRequestItems();
//		EzPriceSeqMaster ezPriceSeqMaster=null;
//		if(seqMasterOpt.isPresent())
//		{
//			ezPriceSeqMaster=seqMasterOpt.get();
//		}
//
//		String condType=reqHeader.getCondType();
//		String table=ezPriceSeqMaster.getTable();
//		String tableNum=table.substring(1,table.length());
//
//		log.info("table::::::"+table);
//		log.info("tableNum::::::"+tableNum);
//		log.info("condType::::::"+condType);
//
//
//		JCO.Client client=null;
//		JCO.Function myFunction = null;
//		SimpleDateFormat sdf=new SimpleDateFormat("dd.MM.yyyy");
//		 try
//		 {
//				client = EzSAPHandler.getSAPConnection("200~999");
//				 myFunction = EzSAPHandler.getFunction("ZSDEZ_VK15","200~999");
//				int colCount = 1;
//
//				JCO.Table inputTable = myFunction.getTableParameterList().getTable("INPUT");
//				inputTable.appendRow();
//				inputTable.setValue(table+"-KOTABNR","FIELD"+colCount++);//table
//				inputTable.setValue(table+"-KSCHL","FIELD"+colCount++);//Condition type
//
//				if("X".equals(ezPriceSeqMaster.getSalesOrg()))
//					inputTable.setValue(table+"-VKORG","FIELD"+colCount++);//Sales Org.
//				if("X".equals(ezPriceSeqMaster.getDistChnl()))
//					inputTable.setValue(table+"-VTWEG","FIELD"+colCount++);//Dist Chnl.
//				if("X".equals(ezPriceSeqMaster.getSalesDocType()))
//					inputTable.setValue(table+"-AUART_SD","FIELD"+colCount++);//Sales doc. type
//				if("X".equals(ezPriceSeqMaster.getMainChnl()))
//					inputTable.setValue(table+"-KVGR1","FIELD"+colCount++);//Main Chnl.
//				if("X".equals(ezPriceSeqMaster.getSubChnl()))
//					inputTable.setValue(table+"-KVGR2","FIELD"+colCount++);//Sub Chnl.
//				if("X".equals(ezPriceSeqMaster.getCustType()))
//					inputTable.setValue(table+"-KVGR3","FIELD"+colCount++);//Cust Type.
//				if("X".equals(ezPriceSeqMaster.getDivision()))
//					inputTable.setValue(table+"-SPART","FIELD"+colCount++);//Division.
//				if("X".equals(ezPriceSeqMaster.getCustomer()))
//					inputTable.setValue(table+"-KUNNR","FIELD"+colCount++);//Customer
//				if("X".equals(ezPriceSeqMaster.getSalesOffice()))
//					inputTable.setValue(table+"-VKBUR","FIELD"+colCount++);//Sales Office
//				if("X".equals(ezPriceSeqMaster.getChainCode()))
//					inputTable.setValue(table+"-ZZ_KATR1","FIELD"+colCount++);//Chain Code
//				if("X".equals(ezPriceSeqMaster.getMaterial()))
//					inputTable.setValue(table+"-MATNR","FIELD"+colCount++);//Material
//				if("X".equals(ezPriceSeqMaster.getBrand()))
//					inputTable.setValue(table+"-ZZ_MVGR1","FIELD"+colCount++);//Brand
//				if("X".equals(ezPriceSeqMaster.getDistCat()))
//					inputTable.setValue(table+"-ZZ_MVGR2","FIELD"+colCount++);//Dist Cat
//				if("X".equals(ezPriceSeqMaster.getPrdType()))
//					inputTable.setValue(table+"-ZZ_MVGR4","FIELD"+colCount++);//Product Type
//
//				inputTable.setValue(table+"-DATBI","FIELD"+colCount++);//valid to
//				inputTable.setValue(table+"-DATAB","FIELD"+colCount++);//valid from
//				inputTable.setValue(table+"-KBETR","FIELD"+colCount++);//Amount
//				inputTable.setValue(table+"-KONWA","FIELD"+colCount++);//Cond. currency
//				inputTable.setValue(table+"-KPEIN","FIELD"+colCount++);//Pricing Unit
//				inputTable.setValue(table+"-KMEIN","FIELD"+colCount++);//Unit of Measure
//				inputTable.setValue(table+"-KRECH","FIELD"+colCount++);//Calculat.Type
//
//				log.info("before adding conditions::::::");
//				for(EzPriceRequestItems reqItem:reqItems)
//				{
//					colCount = 1;
//					inputTable.appendRow();
//					inputTable.setValue(tableNum,"FIELD"+colCount++);
//					inputTable.setValue(condType,"FIELD"+colCount++);
//
//					if("X".equals(ezPriceSeqMaster.getSalesOrg()))
//						inputTable.setValue(reqItem.getSalesOrg(),"FIELD"+colCount++);//Sales Org.
//					if("X".equals(ezPriceSeqMaster.getDistChnl()))
//						inputTable.setValue(reqItem.getDistChnl(),"FIELD"+colCount++);//Dist Chnl.
//					if("X".equals(ezPriceSeqMaster.getSalesDocType()))
//						inputTable.setValue(reqItem.getSalesDocType(),"FIELD"+colCount++);//Sales doc. type
//					if("X".equals(ezPriceSeqMaster.getMainChnl()))
//						inputTable.setValue(reqItem.getMainChnl(),"FIELD"+colCount++);//Main Chnl.
//					if("X".equals(ezPriceSeqMaster.getSubChnl()))
//						inputTable.setValue(reqItem.getSubChnl(),"FIELD"+colCount++);//Sub Chnl.
//					if("X".equals(ezPriceSeqMaster.getCustType()))
//						inputTable.setValue(reqItem.getCustType(),"FIELD"+colCount++);//Cust Type.
//					if("X".equals(ezPriceSeqMaster.getDivision()))
//						inputTable.setValue(reqItem.getDivision(),"FIELD"+colCount++);//Division.
//					if("X".equals(ezPriceSeqMaster.getCustomer()))
//						inputTable.setValue(reqItem.getCustomer(),"FIELD"+colCount++);//Customer
//					if("X".equals(ezPriceSeqMaster.getSalesOffice()))
//						inputTable.setValue(reqItem.getSalesOffice(),"FIELD"+colCount++);//Sales Office
//					if("X".equals(ezPriceSeqMaster.getChainCode()))
//						inputTable.setValue(reqItem.getChainCode(),"FIELD"+colCount++);//Chain Code
//					if("X".equals(ezPriceSeqMaster.getMaterial()))
//						inputTable.setValue(reqItem.getMaterial(),"FIELD"+colCount++);//Material
//					if("X".equals(ezPriceSeqMaster.getBrand()))
//						inputTable.setValue(reqItem.getBrand(),"FIELD"+colCount++);//Brand
//					if("X".equals(ezPriceSeqMaster.getDistCat()))
//						inputTable.setValue(reqItem.getDistCat(),"FIELD"+colCount++);//Dist Cat
//					if("X".equals(ezPriceSeqMaster.getPrdType()))
//						inputTable.setValue(reqItem.getPrdType(),"FIELD"+colCount++);//Product Type
//
//					inputTable.setValue(sdf.format(reqItem.getValidTo()),"FIELD"+colCount++);
//					inputTable.setValue(sdf.format(reqItem.getValidFrom()),"FIELD"+colCount++);
//					inputTable.setValue(reqItem.getValue(),"FIELD"+colCount++);
//					if("ZMRP".equals(condType) || "ZLP".equals(condType))
//					{
//						if("VIPL".equals(reqItem.getSalesOrg()))
//								inputTable.setValue("INR","FIELD"+colCount++);
//						else
//								inputTable.setValue("USD","FIELD"+colCount++);
//						inputTable.setValue("1","FIELD"+colCount++);
//						inputTable.setValue("NOS","FIELD"+colCount++);
//						inputTable.setValue("C","FIELD"+colCount++);
//					}
//					else
//					{
//						inputTable.setValue("%","FIELD"+colCount++);
//						inputTable.setValue("0","FIELD"+colCount++);
//						inputTable.setValue("","FIELD"+colCount++);
//						inputTable.setValue("A","FIELD"+colCount++);
//					}
//				}
//
//				log.info(inputTable+"::inputTable");
//				client.execute(myFunction);
//
//
//				JCO.Table outputTable = myFunction.getTableParameterList().getTable("RETURN");
//				log.info(outputTable+"::outputTable");
//				do
//				{
//					String msgType = (String)outputTable.getValue("MSGTY");
//					if("E".equals(msgType))
//						sapError = true;
//					log.debug("msgType:::"+msgType);
//					SAPReturnTableRow retTabRow= new SAPReturnTableRow();
//					retTabRow.setMsgType(msgType);
//					retTabRow.setMsgTxt((String)outputTable.getValue("MSG"));
//					sapReturnTableRowList.add(retTabRow);
//				}while(outputTable.nextRow());
//
//		 }catch(Exception e){
//			 sapError=true;
//			 log.debug("Error while posting"+e);
//			}
//			finally
//			{
//				   if (client!=null)
//				   {
//					JCO.releaseClient(client);
//					client = null;
//					myFunction=null;
//
//				    }
//			}
//		 sapReturnTable.setSapError(sapError);
//		 sapReturnTable.setSapReturnTableRowList(sapReturnTableRowList);
//
//		return sapReturnTable;
//	}
//
//	@Override
//	public Hashtable<String,ZSD_MATPRICE_OUTPUT> getMaterialPrice(List<ZSD_MATPRICE_INPUT> inputList) {
//
//		Hashtable<String,ZSD_MATPRICE_OUTPUT> outputList= new Hashtable<String,ZSD_MATPRICE_OUTPUT>();
//		JCO.Client client=null;
//		JCO.Function myFunction = null;
//		 try
//		 {
//				client = EzSAPHandler.getSAPConnection("200~999");
//				 myFunction = EzSAPHandler.getFunction("ZSD_MATERIAL_PRICE","200~999");
//
//
//				JCO.Table inputTable = myFunction.getTableParameterList().getTable("ZINPUT_MATPRICE");
//				for(ZSD_MATPRICE_INPUT input:inputList)
//				{
//					inputTable.appendRow();
//					inputTable.setValue(input.getMaterial(),"MATNR");
//					inputTable.setValue(input.getSalesOrg(),"VKORG");
//					try {
//						if (input.getMainChannel() != null && !"null".equals(input.getMainChannel()) && !"".equals(input.getMainChannel()))
//							inputTable.setValue(input.getMainChannel(), "KVGR1");
//						if (input.getSubChannel() != null && !"null".equals(input.getSubChannel()) && !"".equals(input.getSubChannel()))
//							inputTable.setValue(input.getSubChannel(), "KVGR2");
//					} catch (Exception e) {
//						log.error("Exception while setting channels :::"+e);
//					}
//					inputTable.setValue("Y","ALL_AREAS");
//					inputTable.setValue("","BWKEY");
//					/*
//					if("ALL##ALL".equals(input.getValArea()))
//					{
//						inputTable.setValue("X","ALL_AREAS");
//						inputTable.setValue("","BWKEY");
//					}
//					else if("SUB##SUB".equals(input.getValArea()))
//					{
//						inputTable.setValue("Y","ALL_AREAS");
//						inputTable.setValue("","BWKEY");
//					}
//					else
//					{
//						inputTable.setValue(input.getValArea(),"BWKEY");
//					}*/
//					inputTable.setValue(input.getValKey(),"BWTAR");
//					log.info("MATNR:::"+inputTable.getValue("MATNR"));
//					log.info("VKORG::"+inputTable.getValue("VKORG"));
//					log.info("BWKEY:::"+inputTable.getValue("BWKEY"));
//					log.info("BWTAR:::"+inputTable.getValue("BWTAR"));
//					log.info("ALL_AREAS:::"+inputTable.getValue("ALL_AREAS"));
//				}
//
//				client.execute(myFunction);
//
//
//				JCO.Table outputTable = myFunction.getTableParameterList().getTable("ZSD_MATPRICE");
//				do
//				{
//					String salesOrg=(String)outputTable.getValue("SALES_ORG");
//					String material=(String)outputTable.getValue("MATERIAL");
//					if(salesOrg != null)
//						salesOrg=salesOrg.trim();
//					if(material != null)
//						material=material.trim();
//					log.info("ZMRP_AMT:::"+outputTable.getValue("ZMRP_AMT"));
//					log.info("ZLP_AMT:::"+outputTable.getValue("ZLP_AMT"));
//					log.info("MATERIAL:::"+material);
//					ZSD_MATPRICE_OUTPUT matOutput=new ZSD_MATPRICE_OUTPUT();
//					matOutput.setMaterial(material);
//					matOutput.setSalesOrg(salesOrg);
//					matOutput.setMrp((String)outputTable.getValue("ZMRP_AMT"));
//					matOutput.setLeastPrice((String)outputTable.getValue("ZLP_AMT"));
//					matOutput.setVprs((String)outputTable.getValue("VPRS"));
//					outputList.put(salesOrg+"##"+material,matOutput);
//				}
//				while(outputTable.nextRow());
//		 }catch(Exception e){
//			 log.error("Exception while getting prices:::"+e);
//			}
//			finally
//			{
//				   if (client!=null)
//				   {
//					JCO.releaseClient(client);
//					client = null;
//					myFunction=null;
//
//				    }
//			}
//
//		 return outputList;
//	}
//
//	@Override
//	public List<CustomerSalesData> getCustomerData(String customerCode) {
//		List<CustomerSalesData> salesData=new ArrayList<CustomerSalesData>();
//		JCO.Client client=null;
//		JCO.Function myFunction = null;
//		if(isNumeric(customerCode))
//		{
//			customerCode="00000000000"+customerCode;
//			customerCode = customerCode.substring(customerCode.length() - 10);
//		}
//		 try
//		 {
//				client = EzSAPHandler.getSAPConnection("200~999");
//				 myFunction = EzSAPHandler.getFunction("RFC_READ_TABLE","200~999");
//
//				JCO.ParameterList impParams = myFunction.getImportParameterList();
//				impParams.setValue("KNVV","QUERY_TABLE");
//				impParams.setValue("#","DELIMITER");
//
//				JCO.Table fieldTable = myFunction.getTableParameterList().getTable("FIELDS");
//				fieldTable.appendRow();
//				fieldTable.setValue("VKORG","FIELDNAME");
//				fieldTable.appendRow();
//				fieldTable.setValue("VTWEG","FIELDNAME");
//				fieldTable.appendRow();
//				fieldTable.setValue("KVGR1","FIELDNAME");
//				fieldTable.appendRow();
//				fieldTable.setValue("KVGR2","FIELDNAME");
//
//				log.debug("customerCode::"+customerCode);
//				JCO.Table optTable = myFunction.getTableParameterList().getTable("OPTIONS");
//				optTable.appendRow();
//				optTable.setValue("KUNNR EQ '"+customerCode+"'","TEXT");
//
//				client.execute(myFunction);
//
//
//
//				JCO.Table outData =(JCO.Table)myFunction.getTableParameterList().getTable("DATA");
//
//				if(outData != null)
//				{
//					do
//					{
//						String [] tempOutArr 	= ((String)outData.getValue("WA")).split("#");
//						salesData.add(new CustomerSalesData(tempOutArr[0], tempOutArr[1],tempOutArr[2],tempOutArr[3]));
//					}
//					while(outData.nextRow());
//				}
//		 }catch(Exception e){
//			 log.error("Exception in gettting customer sales data::::"+e);
//			}
//			finally
//			{
//				   if (client!=null)
//				   {
//					JCO.releaseClient(client);
//					client = null;
//					myFunction=null;
//
//				    }
//			}
//
//		return salesData;
//	}
//
//	public static boolean isNumeric(String str) {
//		  try {
//		    Double.parseDouble(str);
//		    return true;
//		  } catch(NumberFormatException e){
//		    return false;
//		  }
//		}
//
//}
