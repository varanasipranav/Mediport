package com.ezc.aragenPR.webapp.service;

import com.ezc.aragenPR.webapp.model.EzcMasterData;
import com.ezc.aragenPR.webapp.persistance.dao.EzcMasterDataRepo;
import com.sap.conn.jco.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class MasterDataService {

    @Autowired
    private EzcMasterDataRepo masterDataRepository;

    @Autowired
    private RFCReadSAPService rfcReadSAPService;

    public String sanitizeValue(String input) {
        if (input == null) return null;
        // Remove or replace non-UTF-8 or unprintable characters
        return input
                .replaceAll("[^\\x00-\\x7F]", "?")   // replaces all non-ASCII chars with ?
                .trim();
    }
    public List<EzcMasterData> syncFromSAP(String key) {
        List<EzcMasterData> masterDataList = new ArrayList<>();

        try {
            String today = new SimpleDateFormat("yyyyMMdd").format(new Date());

            log.debug("IN::MasterDataService:: TRY");

            // ✅ Establish SAP connection
            JCoDestination destination = rfcReadSAPService.getDestination();
            log.debug("SAP Connection established successfully!");
            //System.out.println("Connected User: " + destination.getUser());

            String[] outputFields =null;
            String[] queryValues=null;
            String tabName="";
            if("DIVISION".equals(key)) {
                 outputFields = new String[]{"SPART", "VTEXT"};
                 queryValues = new String[]{"SPRAS EQ 'E'"};
                tabName="TSPAT";
            }
            else if("PURGRP".equals(key)) {
                outputFields = new String[]{"EKGRP", "EKNAM"};
                queryValues = new String[]{"MANDT EQ '600'"};
                tabName="T024";
            }
            else if("PLANT".equals(key)) {
                outputFields = new String[]{"WERKS", "NAME1"};
                queryValues = new String[]{"MANDT EQ '600'"};
                tabName="T001W";
            }
            else if("ACCASSG".equals(key)) {
                outputFields = new String[]{"KNTTP", "KNTTX"};
                queryValues = new String[]{"SPRAS EQ 'E'"};
                tabName="T163I";
            }
            else if("ITEMCATEG".equals(key)) {
                outputFields = new String[]{"EPSTP", "PTEXT"};
                queryValues = new String[]{"SPRAS EQ 'E'"};
                tabName="T163Y";
            }
            else if("STRGLOC".equals(key)) {
                outputFields = new String[]{"LGORT", "LGOBE","WERKS"};
                queryValues = new String[]{"MANDT EQ '600'"};
                tabName="T001L";
            }
            else if("PURORG".equals(key)) {
                outputFields = new String[]{"EKORG", "EKOTX","BUKRS"};
                queryValues = new String[]{"MANDT EQ '600'"};
                tabName="T024E";
            }
            else if("DEPART".equals(key)) {
                outputFields = new String[]{"KOSTL","LTEXT","SPART","WERKS"};
                queryValues = new String[]{"MANDT EQ '600'"};
                tabName="ZMM_DEPT";
            }
            else if("SERVICECODE".equals(key)) {
                outputFields = new String[]{"ASNUM","ASKTX","BKLAS"};
                queryValues = new String[]{"ASNUM LIKE '0000000003%'"};
                tabName="VASMD";
            }
            else if("BATCH".equals(key)) {
                outputFields = new String[]{"MATNR","WERKS","LGORT", "CHARG"};
                queryValues = new String[]{"MANDT EQ '600'"};
                tabName = "MCHB";
            }
            else if("COSTCENTER".equals(key)) {
                outputFields = new String[]{"KOSTL","KTEXT"};
                queryValues = new String[]{"DATBI >= '" + today + "'"};
                tabName="CSKT";
            }
            else if("SERVICECD".equals(key)) {

                JCoDestination destinationSer = rfcReadSAPService.getDestination();
                JCoRepository repository = destinationSer.getRepository();
                JCoFunction poFunction = repository.getFunction("BAPI_SERVICE_GET_LIST");

                System.out.println("SAP Connection established successfully! SERVICECD");
                System.out.println("Connected User: " + destinationSer.getUser());
                JCoParameterList importParams = poFunction.getImportParameterList();

                System.out.println("Executing BAPI_SERVICE_GET_LIST");
                try {
                    JCoContext.begin(destinationSer);
                    poFunction.execute(destinationSer);
                    JCoContext.end(destinationSer);
                } catch (Exception e) {

                }
                JCoTable serviceList = poFunction.getTableParameterList().getTable("SERVICELIST");
                System.out.println(" Retrieved " + serviceList.getNumRows() + " services:");
                for (int i = 0; i < serviceList.getNumRows(); i++) {
                    serviceList.setRow(i);
                    String serviceNo = serviceList.getString("SERVICE");
                    String shortText = serviceList.getString("SHORT_TEXT");
                    String baseUom = serviceList.getString("BASE_UOM");
                    String matlGroup = serviceList.getString("MATL_GROUP");
                    EzcMasterData data = new EzcMasterData();
                    data.setEmdKey(key);
                    data.setEmdValue1(serviceNo+"#"+matlGroup);
                    data.setEmdValue2(shortText);
                    data.setEmdValue3(matlGroup);
                    data.setEmdValue4(baseUom);
                    data.setEmdCreatedBy("SAP_SYNC");
                    data.setEmdCreatedOn(LocalDateTime.now());

                    masterDataList.add(data);

                }
            }
            else if("NETWORK".equals(key) || "IO".equals(key)) {

                JCoDestination destinationSer = rfcReadSAPService.getDestination();
                JCoRepository repository = destinationSer.getRepository();
                JCoFunction poFunction = repository.getFunction("ZBAPI_NETWORK_GETLIST");


                log.debug("SAP Connection established successfully! NETWORK"+key);

                JCoParameterList importParams = poFunction.getImportParameterList();
                if("NETWORK".equals(key))
                    importParams.setValue("NETWORK_ORDER","X");
                else
                    importParams.setValue("INTERNAL_ORDER","X");

                System.out.println("Executing ZBAPI_NETWORK_GETLIST");
                try {
                    JCoContext.begin(destinationSer);
                    poFunction.execute(destinationSer);
                    JCoContext.end(destinationSer);
                } catch (Exception e) {

                }
                JCoTable networkList = poFunction.getTableParameterList().getTable("NETWORK_LIST");
                System.out.println(" Retrieved " + networkList.getNumRows() + " network:"+key);
                for (int i = 0; i < networkList.getNumRows(); i++) {
                    networkList.setRow(i);
                    String netNO = networkList.getString("AUFNR");
                    String netText = networkList.getString("KTEXT");
                    String plant="";
                    if("NETWORK".equals(key))
                         plant = networkList.getString("WERKS");
                    EzcMasterData data = new EzcMasterData();
                    data.setEmdKey(key);
                    if("NETWORK".equals(key))
                        data.setEmdValue1(netNO+"#"+plant);
                    else
                        data.setEmdValue1(netNO);
                    data.setEmdValue2(sanitizeValue(netText));
                    if("NETWORK".equals(key))
                        data.setEmdValue3(plant);
                    data.setEmdCreatedBy("SAP_SYNC");
                    data.setEmdCreatedOn(LocalDateTime.now());

                    masterDataList.add(data);

                }
            }
            else if("MATGRP".equals(key)) {
                outputFields = new String[]{"MATKL","WGBEZ"};
                queryValues = new String[]{"SPRAS EQ 'E'"};
                tabName="T023T";
            }
            else if("GLACCOUNT".equals(key)) {

                JCoDestination destinationSer = rfcReadSAPService.getDestination();
                JCoRepository repository = destinationSer.getRepository();
                JCoFunction poFunction = repository.getFunction("Z_EZ_GET_GL_ACCOUNT");

                System.out.println("SAP Connection established successfully! GLACCOUNT");
                System.out.println("Connected User: " + destinationSer.getUser());
                JCoParameterList importParams = poFunction.getImportParameterList();

                System.out.println("Executing GLACCOUNT");
                try {
                    JCoContext.begin(destinationSer);
                    poFunction.execute(destinationSer);
                    JCoContext.end(destinationSer);
                } catch (Exception e) {

                }
                JCoTable matListTable = poFunction.getTableParameterList().getTable("ET_SKA1");
                System.out.println(" Retrieved " + matListTable.getNumRows() + " GLACCOUNT:");
                for (int i = 0; i < matListTable.getNumRows(); i++) {
                    matListTable.setRow(i);
                    String glAcc = matListTable.getString("SAKAN");
                    String glAccDesc = matListTable.getString("TXT50");
                    EzcMasterData data = new EzcMasterData();
                    data.setEmdKey(key);
                    data.setEmdValue1(glAcc);
                    data.setEmdValue2(glAccDesc);
                    data.setEmdCreatedBy("SAP_SYNC");
                    data.setEmdCreatedOn(LocalDateTime.now());

                    masterDataList.add(data);

                }
            }
            // This should return List<String[]> from RFCReadSAPService
            if(!"SERVICECD".equals(key) && !"NETWORK".equals(key) && !"IO".equals(key) && !"GLACCOUNT".equals(key))
            {
                List<String[]> dataList = rfcReadSAPService.readRFCTable(tabName, outputFields, queryValues);
                //  dataList.add(new String[]{"T01#12345"});
                //dataList.add(new String []{"T02#PRODUCT"});
                System.out.println("dataList" + dataList);
                for (String[] row : dataList)
                {
                    EzcMasterData data = new EzcMasterData();
                    data.setEmdKey(key);
                    if ("BATCH".equalsIgnoreCase(key))
                    {
                        String matnr=row[0];
                        String noZeros = matnr.replaceFirst("^0+(?!$)", "");
                        String finalValue = noZeros.replaceAll("\\s+", "");
                        data.setEmdValue1(finalValue+"#"+row[1] + "#" + row[2] + "#" + row[3]);

                    }
                    else if ("STRGLOC".equalsIgnoreCase(key))
                        data.setEmdValue1(row[0] + "#" + row[2]);
                    else
                        data.setEmdValue1(row[0]);

                    if("BATCH".equalsIgnoreCase(key))
                    {
                        data.setEmdValue2(row[3]);
                    }
                    else
                        data.setEmdValue2(row[1]);

                    if ("DEPART".equalsIgnoreCase(key)) {
                        data.setEmdValue3(row[2] + "#" + row[3]);
                    }
                    if ("STRGLOC".equalsIgnoreCase(key) || "PURORG".equalsIgnoreCase(key)) {
                        data.setEmdValue3(row[2]);
                    }
                    if("BATCH".equalsIgnoreCase(key))
                    {
                        String matnr=row[0];
                        String noZeros = matnr.replaceFirst("^0+(?!$)", "");
                        String finalValue = noZeros.replaceAll("\\s+", "");
                        data.setEmdValue3(finalValue+"#"+row[1] + "#" + row[2]);
                    }
                    data.setEmdCreatedBy("SAP_SYNC");
                    data.setEmdCreatedOn(LocalDateTime.now());

                    masterDataList.add(data);
                }
            }

            // ✅ Save to DB
            masterDataRepository.deleteByEmdKey(key);
            masterDataRepository.saveAll(masterDataList);
            System.out.println("✅ Data saved to DB: " + masterDataList.size() + " records");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error syncing master data: " + e.getMessage());
        }

        return masterDataList;
    }

    public List<EzcMasterData> getAll() {
        return masterDataRepository.findAll();
    }
}
