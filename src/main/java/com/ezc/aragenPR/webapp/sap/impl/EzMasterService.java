//package com.ezc.outsrc.webapp.sap.impl;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.regex.Pattern;
//
//import jakarta.transaction.Transactional;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.ezc.outsrc.webapp.model.EzMaterialMaster;
//import com.ezc.outsrc.webapp.model.EzMaterialSalesOrgData;
//import com.ezc.outsrc.webapp.persistance.dao.MaterialMasterRepo;
//import com.ezc.outsrc.webapp.persistance.dao.MaterialSalesOrgDataRepo;
//import com.ezc.outsrc.webapp.sap.IMasterService;
//import com.ezc.outsrc.webapp.sapconnection.EzSAPHandler;
//import com.sap.mw.jco.JCO;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@Service
//@Transactional
//public class EzMasterService implements IMasterService{
//
//	@Autowired
//    private MaterialMasterRepo matRepo;
//
//	@Autowired
//    private MaterialSalesOrgDataRepo matSalesRepo;
//
//	@Override
//	public void synchMaterials(){
//
//		Pattern regex = Pattern.compile("^[a-zA-Z0-9]*$");
//		List<EzMaterialMaster> matList=new ArrayList<EzMaterialMaster>();
//		ArrayList<String> tempMatDescList=new ArrayList<String>();
//		ArrayList<String> tempSOrgList=new ArrayList<String>();
//		JCO.Client client=null;
//		JCO.Function myFunction = null;
//
//		 try
//		 {
//				client = EzSAPHandler.getSAPConnection("200~999");
//				 myFunction = EzSAPHandler.getFunction("RFC_READ_TABLE","200~999");
//
//				JCO.ParameterList impParams = myFunction.getImportParameterList();
//				impParams.setValue("MAKT","QUERY_TABLE");
//				impParams.setValue("#","DELIMITER");
//
//				JCO.Table fieldTable = myFunction.getTableParameterList().getTable("FIELDS");
//				fieldTable.appendRow();
//				fieldTable.setValue("MATNR","FIELDNAME");
//				fieldTable.appendRow();
//				fieldTable.setValue("MAKTX","FIELDNAME");
//
//				JCO.Table optTable = myFunction.getTableParameterList().getTable("OPTIONS");
//				optTable.appendRow();
//				optTable.setValue("SPRAS EQ 'EN'","TEXT");
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
//						log.info("tempOutArr1::"+tempOutArr.length);
//
//
//						tempOutArr[0]=tempOutArr[0].trim();
//						if (regex.matcher(tempOutArr[0]).find())
//						{
//						if(!tempMatDescList.contains(tempOutArr[0]))
//						{
//							tempMatDescList.add(tempOutArr[0]);
//							matList.add(new EzMaterialMaster(tempOutArr[0], tempOutArr[1]));
//						}
//						}
//						else
//						{
//							log.info("NOTMATCHED::"+tempOutArr.length);
//						}
//					}
//					while(outData.nextRow());
//				}
//		 }catch(Exception e){
//			 log.error("Exception in gettting material master::::"+e);
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
//		 	if(matList.size() > 0)
//		 	{
//		 		matRepo.truncateTable();
//		 		matRepo.saveAll(matList);
//		 	}
//
//			List<EzMaterialSalesOrgData> matSalesOrgList=new ArrayList<EzMaterialSalesOrgData>();
//			client=null;
//			myFunction = null;
//
//			 try
//			 {
//					client = EzSAPHandler.getSAPConnection("200~999");
//					myFunction = EzSAPHandler.getFunction("RFC_READ_TABLE","200~999");
//
//					JCO.ParameterList impParams = myFunction.getImportParameterList();
//					impParams.setValue("MVKE","QUERY_TABLE");
//					impParams.setValue("#","DELIMITER");
//
//					JCO.Table fieldTable = myFunction.getTableParameterList().getTable("FIELDS");
//					fieldTable.appendRow();
//					fieldTable.setValue("MATNR","FIELDNAME");
//					fieldTable.appendRow();
//					fieldTable.setValue("VKORG","FIELDNAME");
//					fieldTable.appendRow();
//					fieldTable.setValue("VTWEG","FIELDNAME");
//					fieldTable.appendRow();
//					fieldTable.setValue("MVGR1","FIELDNAME");
//
//					JCO.Table optTable = myFunction.getTableParameterList().getTable("OPTIONS");
//					optTable.appendRow();
//					optTable.setValue("VKORG EQ 'VIPL' AND VTWEG EQ 'ST'","TEXT");
//
////					JCO.Table optTable = myFunction.getTableParameterList().getTable("OPTIONS");
////					optTable.appendRow();
////					optTable.setValue("SPRAS EQ 'EN'","TEXT");
//
//					client.execute(myFunction);
//
//
//
//					JCO.Table outData =(JCO.Table)myFunction.getTableParameterList().getTable("DATA");
//					//log.debug("outData:::"+outData);
//					if(outData != null)
//					{
//						do
//						{
//							String [] tempOutArr 	= ((String)outData.getValue("WA")).split("#");
//							String tempBrand =  "";
//							if(tempOutArr.length > 3)
//								tempBrand =  tempOutArr[3];
//							//if (regex.matcher(tempOutArr[0]).find())
//							tempOutArr[0]=tempOutArr[0].trim();
//							if (regex.matcher(tempOutArr[0]).find())
//							{
//								if(!tempSOrgList.contains(tempOutArr[0]))
//								{
//									tempSOrgList.add(tempOutArr[0]);
//									matSalesOrgList.add(new EzMaterialSalesOrgData(tempOutArr[0].trim(),tempOutArr[1], tempOutArr[2],tempBrand));
//								}
//							}
//						}
//						while(outData.nextRow());
//					}
//			 }catch(Exception e){
//				 log.error("Exception in gettting material master::::"+e);
//				}
//				finally
//				{
//					   if (client!=null)
//					   {
//						JCO.releaseClient(client);
//						client = null;
//						myFunction=null;
//
//					    }
//				}
//			 	if(matSalesOrgList.size() > 0)
//			 	{
//			 		matSalesRepo.truncateTable();
//			 		matSalesRepo.saveAll(matSalesOrgList);
//			 	}
//
//	}
//
//
//
//}
//
//
