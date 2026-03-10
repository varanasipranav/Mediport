package com.ezc.aragenPR.webapp.service;
import org.springframework.security.core.Authentication;

import com.ezc.aragenPR.webapp.model.EzPurchaseRequisitionHeader;
import com.ezc.aragenPR.webapp.model.EzcSESHeader;
import com.ezc.aragenPR.webapp.model.EzcSESItems;

import com.ezc.aragenPR.webapp.model.Users;
import com.ezc.aragenPR.webapp.persistance.dao.SESRepo;
import com.sap.conn.jco.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@Transactional
public class SESService {

    @Autowired
    RFCReadSAPService rfcReadSAPService;

    @Autowired
    SESRepo sesRepo;


    private String checkNull(Object value) {
        return (value == null) ? "" : value.toString().trim();
    }
    private String removeLeadingZeros(String value) {
        if (value == null) return "";
        return value.replaceFirst("^0+(?!$)", ""); // removes only leading zeros
    }



    public List<EzcSESHeader> getAllSes() {
        return sesRepo.findAll();
    }
    @Transactional
    public List<EzcSESHeader> syncFromSAP() {

        List<EzcSESHeader> syncedHeaders  = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate threeMonthsAgo = today.minusMonths(3);

        DateTimeFormatter sapDateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
        String startDate = threeMonthsAgo.format(sapDateFormat);
        String endDate = today.format(sapDateFormat);

            try {

                JCoDestination destination = rfcReadSAPService.getDestination();
                JCoRepository repository = destination.getRepository();
                JCoFunction function = repository.getFunction("ZEZ_BAPI_SES_LIST");

                if (function == null) {
                    throw new RuntimeException("ZEZ_BAPI_SES_LIST not found in SAP repository.");
                }


                JCoParameterList imports = function.getImportParameterList();

               imports.setValue("FROM_DATE", startDate);
               imports.setValue("TO_DATE", endDate);
                imports.setValue("UNRELEASED", "X");
                log.debug("startDate"+startDate+"endDate"+endDate);

                try {
                    JCoContext.begin(destination);
                    function.execute(destination);
                } finally {
                    JCoContext.end(destination);
                }



                JCoTable essrTable = function.getTableParameterList().getTable("ESSR_DATA");
                if (essrTable == null || essrTable.getNumRows() == 0) {
                    log.debug("No SES records returned from SAP for this range.");
                    return Collections.emptyList();
                }


                int inserted = 0, skipped = 0;

                do {
                    String sesNumber = essrTable.getString("LBLNI");
                    if (sesNumber == null || sesNumber.isBlank()) continue;


                 /*   if (sesRepo.existsBySesNumber(sesNumber)) {
                        skipped++;
                        continue;
                    }
*/

                    EzcSESHeader header = new EzcSESHeader();
                    header.setSesNumber(sesNumber);
                    header.setPoNumber(essrTable.getString("EBELN"));
                    header.setVendorName(essrTable.getString("SBNAMAN"));
                    header.setShortText(essrTable.getString("TXZ01"));
                    header.setServiceLocation(essrTable.getString("DLORT"));
                    header.setCurrency(essrTable.getString("WAERS"));
                //    header.setEntryDate(LocalDate.parse(essrTable.getString("LBLDT")));
                    String status="";
                    String relStat = essrTable.getString("FRGKL");
                    String delInd = essrTable.getString("LOEKZ");
                    String releaseStatus = essrTable.getString("FRGZU");
                    //if(!"".equalsIgnoreCase(releaseStatus)) continue;

                    if ("X".equalsIgnoreCase(delInd))
                    {
                        status = "DELETED";
                    }
                    else if ("B".equalsIgnoreCase(relStat))
                    {
                        status = "RELEASED";
                    }
                    else if ("A".equalsIgnoreCase(relStat) && releaseStatus != null && releaseStatus.contains("X"))
                    {
                        status = "PENDING";
                    }
                    else if ("A".equalsIgnoreCase(relStat) && (releaseStatus == null || releaseStatus.trim().isEmpty()))
                    {
                        status = "UNRELEASED";
                    }
                    else {
                        status = "UNRELEASED";
                    }


                    header.setStatus(status);


                    DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    String erdatStr = essrTable.getString("BLDAT");
                    if (erdatStr != null && !erdatStr.trim().isEmpty()) {
                        try {
                            header.setEntryDate(LocalDate.parse(erdatStr.trim(), dateFmt));
                        } catch (Exception e) {
                            System.err.println("⚠️ Date parse failed for BLDAT: " + erdatStr);
                        }
                    }
                    String postDT = essrTable.getString("BUDAT");
                    if (postDT != null && !postDT.trim().isEmpty()) {


                        try {

                                header.setPostingDate(LocalDate.parse(postDT.trim(), dateFmt));
                        } catch (Exception e) {
                            System.err.println(" Date parse failed for SES " + sesNumber + ": " + e.getMessage());
                        }
                    }
                    // Parse numeric fields
                    try {
                        String amountStr = essrTable.getString("NETWR").replace(",", "").trim();
                        header.setTotalAmount(Double.parseDouble(amountStr));
                    } catch (Exception e) {
                        header.setTotalAmount(0.0);
                    }

                    header.setCreatedBy(essrTable.getString("ERNAM"));
                    header.setModifiedBy(essrTable.getString("AENAM"));
                    header.setReleaseGroup(essrTable.getString("FRGGR"));
                    header.setReleaseStrategy(essrTable.getString("FRGSX"));
                    header.setReleaseIndicator(essrTable.getString("FRGKL"));
                    header.setCreatedOn(LocalDateTime.now());
                    header.setModifiedOn(LocalDateTime.now());


                    List<EzcSESItems> itemList = new ArrayList<>();

                    // For simplicity, create 1–3 dummy items per SES


                    for (int i = 1; i <= 1; i++) {
                        EzcSESItems item = new EzcSESItems();
                     //   item.setSesNumber(sesNumber);
                        item.setLineNumber(i * 10);
                        item.setQuantity(1.0 + i);
                        item.setUnit("AU");
                        item.setPeriodFrom(LocalDate.now().minusDays(i * 3));
                        item.setPeriodTo(LocalDate.now());
                        item.setHeader(header);

                        itemList.add(item);
                    }

                    header.setLineItems(itemList);

                    // Save new SES
                    sesRepo.save(header);
                    syncedHeaders.add(header);
                    inserted++;

                } while (essrTable.nextRow());

                System.out.printf("✅ SES Sync completed: Inserted %d, Skipped %d (existing).%n", inserted, skipped);

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("❌ Error syncing SES from SAP: " + e.getMessage());
            }


        return syncedHeaders ;
    }
    @Transactional
    public EzcSESHeader fetchSESFromSAP(String sesNumber) throws Exception
    {

        System.out.println("fetchSESFromSAP"+sesNumber);
        JCoDestination destination = rfcReadSAPService.getDestination();
        JCoRepository repository = destination.getRepository();
        JCoFunction function = repository.getFunction("ZEZ_BAPI_ENTRYSHEET_GETDETAIL");

        if (function == null)
            throw new RuntimeException("ZEZ_BAPI_ENTRYSHEET_GETDETAIL not found in SAP");

        function.getImportParameterList().setValue("ENTRYSHEET", sesNumber);
        try{
            JCoContext.begin(destination);
            function.execute(destination);
            JCoContext.end(destination);
        }catch(Exception e){}


        JCoStructure headerStr = function.getExportParameterList().getStructure("ENTRYSHEET_HEADER");
        if (headerStr == null)
            return null;
        System.out.println("fetchSESFromSAP1");
       // EzcSESHeader header = new EzcSESHeader();

        EzcSESHeader header = sesRepo.findBySesNumber(sesNumber);
        //EzcSESHeader header = new EzcSESHeader();
        if (header == null) {
            header = new EzcSESHeader();
            header.setSesNumber(sesNumber);
            header.setLineItems(new ArrayList<>());
        } else {
            header.getLineItems().clear(); // cleanup existing items
        }
        header.setSesNumber(sesNumber);
        header.setExtNumber(headerStr.getString("LBLNE"));
        header.setPoNumber(headerStr.getString("EBELN"));
        header.setAccAsst(headerStr.getString("KNTTP"));
        header.setPerExt(headerStr.getString("SBNAMAN"));
        header.setPackNO(headerStr.getString("PACKNO"));
        header.setReferenceDoc(headerStr.getString("XBLNR"));
        header.setPoItemNO(headerStr.getString("EBELP"));
        header.setCurrency(headerStr.getString("WAERS"));
        header.setCreatedBy(headerStr.getString("ERNAM"));
        header.setReleaseGroup(headerStr.getString("FRGGR"));
        header.setReleaseStrategy(headerStr.getString("FRGSX"));
        header.setReleaseIndicator(headerStr.getString("FRGKL"));
        header.setShortText(headerStr.getString("TXZ01"));
        header.setDocText(headerStr.getString("BKTXT"));
        String delInd = headerStr.getString("LOEKZ");
        String relInd = headerStr.getString("FRGKL");
        String releaseStatus = headerStr.getString("FRGZU");
        String status="";

        if("B".equalsIgnoreCase(relInd))status="RELEASED";
        else if("A".equalsIgnoreCase(relInd))status="UNRELEASED";

        if("A".equalsIgnoreCase(relInd) && releaseStatus.contains("X"))status="PENDING";
        if("X".equalsIgnoreCase(delInd))status="DELETED";

        header.setStatus(status);
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        System.out.print("entry date"+headerStr.getString("BLDAT"));
        try {
            if (!headerStr.getString("BLDAT").isBlank())
                header.setEntryDate(LocalDate.parse(headerStr.getString("BLDAT"), dateFmt));
            if (!headerStr.getString("BUDAT").isBlank())
                header.setPostingDate(LocalDate.parse(headerStr.getString("BUDAT"), dateFmt));
        } catch (Exception e) {
            System.err.println(" Date parse failed for SES " + sesNumber + ": " + e.getMessage());
        }
        System.out.println("fetchSESFromSAP2");
        JCoTable itemAccTab = function.getTableParameterList().getTable("ENTRYSHEET_ACCOUNT_ASSIGNMENT");
        Map<String, Map<String, String>> accMap = new HashMap<>();
        System.out.print("itemAccTab"+itemAccTab.getNumRows());
        if (itemAccTab != null && itemAccTab.getNumRows() > 0) {
            do {
                String pkgNo  = itemAccTab.getString("PCKG_NO");
                System.out.println("pkgNo"+pkgNo);
                if (pkgNo  == null || pkgNo .isBlank()) continue;
                String serNO = itemAccTab.getString("SERIAL_NO");
                String key = removeLeadingZeros(pkgNo);

                Map<String, String> accData = new HashMap<>();
                accData.put("TAX_CODE", itemAccTab.getString("TAX_CODE"));
                accData.put("COSTCENTER", itemAccTab.getString("COSTCENTER"));
                accData.put("GL_ACCOUNT", itemAccTab.getString("GL_ACCOUNT"));
                accData.put("ORDER", itemAccTab.getString("ORDER"));

                accMap.put(key, accData);
            } while (itemAccTab.nextRow());
        }

        JCoTable itemTab = function.getTableParameterList().getTable("ENTRYSHEET_SERVICES");
        System.out.println("accMap"+accMap+"itemTab"+ itemTab.getNumRows());
        List<EzcSESItems> items = new ArrayList<>();

        if (itemTab != null && itemTab.getNumRows() > 0) {
            String mainPackNo="";
            itemTab.firstRow();
            do {

                log.debug("item service"+itemTab.getString("SERVICE"));
                String servNO=(String)itemTab.getString("SERVICE");
                String packgNO=(String)itemTab.getString("PCKG_NO");
                String outlInd = checkNull(itemTab.getString("OUTL_IND"));

                System.out.println("servNO"+servNO+"OUTL_IND"+itemTab.getString("OUTL_IND"));
                if (!packgNO.isEmpty() && (servNO.isEmpty() || "X".equalsIgnoreCase(outlInd))) {
                    mainPackNo = packgNO;
                    System.out.println("Updated mainPackNo = " + mainPackNo);
                }

                // ✅ Skip if SERVICE is empty OR it's an outline row
                if (servNO.isEmpty() || "X".equalsIgnoreCase(outlInd)) {
                    System.out.println("Skipping outline or empty service row...");
                    continue;
                }
                System.out.println("packgNO"+mainPackNo);
                String key=removeLeadingZeros(mainPackNo);
                Map<String, String> acc = accMap.get(key);

                EzcSESItems i = new EzcSESItems();
                String extLineNo=(String)itemTab.getString("LINE_NO");
                System.out.print("acc"+acc+"key"+key);
                if (acc != null) {
                    i.setTaxCode(acc.getOrDefault("TAX_CODE", ""));
                    i.setCostCenter(acc.getOrDefault("COSTCENTER", ""));
                    i.setGlAccount(acc.getOrDefault("GL_ACCOUNT", ""));
                    i.setOrder(acc.getOrDefault("ORDER", ""));
                }

               // i.setSesNumber(sesNumber);
                i.setLineNumber(itemTab.getInt("EXT_LINE"));
                System.out.print("lineNO"+itemTab.getInt("EXT_LINE"));
                i.setServiceNumber(removeLeadingZeros(itemTab.getString("SERVICE")));
                i.setShortText(itemTab.getString("SHORT_TEXT"));
                i.setQuantity(itemTab.getDouble("QUANTITY"));
                i.setUnit(itemTab.getString("BASE_UOM"));
                System.out.print("BASE_UOM"+itemTab.getString("BASE_UOM"));
                i.setGrossPrice(itemTab.getDouble("GR_PRICE"));
                System.out.print("GR_PRICE"+itemTab.getDouble("GR_PRICE"));
                System.out.print("END oF ITEM");
                i.setHeader(header);
                items.add(i);
            } while (itemTab.nextRow());
        }
        System.out.println("items size"+items.size());
        System.out.println("**************");
        header.getLineItems().addAll(items);

        //header.setLineItems(items);
        System.out.print("items header"+header.getLineItems()+"header"+header);
        sesRepo.save(header);
        System.out.print("sesRepo Saved");
        return header;
    }
    public String getRelCode(String userName)  throws JCoException
    {
    System.out.println("userName"+userName);

        JCoDestination destination = rfcReadSAPService.getDestination();
        JCoRepository repository = destination.getRepository();
        JCoFunction function = repository.getFunction("ZEZ_PR_USER_DATA");

        if (function == null) {
            throw new RuntimeException("ZEZ_PR_USER_DATA not found in SAP.");
        }

        // INPUT
        function.getImportParameterList().setValue("S_UNAME", userName);
        function.getImportParameterList().setValue("PR_REL_STRATEGY", "");
        function.getImportParameterList().setValue("PR_SE_REL_STRATEGY", "X");


        JCoContext.begin(destination);
        function.execute(destination);
        JCoContext.end(destination);

        JCoTable returnTable = function.getTableParameterList().getTable("PR_USER_DATA_SE");
        String relCode = "";
        for (int i = 0; i < returnTable.getNumRows(); i++)
        {
            returnTable.setRow(i);

            relCode = returnTable.getString("FRGC1");
            System.out.println("relCode"+relCode);
        }




        return relCode;
    }

    public Map<String, Object> releaseSES(String sesNumber, String releaseCode) throws JCoException {

        Map<String, Object> response = new HashMap<>();
        JCoDestination destination = rfcReadSAPService.getDestination();
        JCoRepository repository = destination.getRepository();
        JCoFunction function = repository.getFunction("BAPI_ENTRYSHEET_RELEASE");

        if (function == null) {
            throw new RuntimeException("BAPI_ENTRYSHEET_RELEASE not found in SAP.");
        }
        String uName="";
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Users userObj = (Users)authentication.getPrincipal();
            String loggedUser=(String)userObj.getUserId();
            uName=loggedUser;
        } catch (Exception e) {

        }
        System.out.print("uName"+uName);
        function.getImportParameterList().setValue("ENTRYSHEET", sesNumber);
        releaseCode=getRelCode(uName);
        if (releaseCode != null && !releaseCode.isEmpty()) {
            function.getImportParameterList().setValue("REL_CODE", releaseCode);
        }


        JCoContext.begin(destination);
        function.execute(destination);
        JCoContext.end(destination);

        JCoTable returnTable = function.getTableParameterList().getTable("RETURN");
        // Parse return messages
        List<String> messages = new ArrayList<>();
        String status = "SUCCESS";

        for (int i = 0; i < returnTable.getNumRows(); i++) {
            returnTable.setRow(i);

            String type = returnTable.getString("TYPE");
            String message = returnTable.getString("MESSAGE");

            messages.add(type + " - " + message);

            if ("E".equals(type) || "A".equals(type)) {
                status = "FAILED";
            }
        }

        response.put("status", status);
        response.put("messages", messages);

        return response;
    }

}
