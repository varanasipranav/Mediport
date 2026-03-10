package com.ezc.aragenPR.webapp.service;

import com.ezc.aragenPR.webapp.model.EzPurchaseRequisitionHeader;
import com.ezc.aragenPR.webapp.persistance.dao.EzcPRHeaderRepository;
import com.sap.conn.jco.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class PRReleaseInfoService {


    @Autowired
    private EzcPRHeaderRepository prRepo;
    @Autowired
    private EzcPRHeaderService headerService;

    @Autowired
    RFCReadSAPService rfcReadSAPService;
    private String checkNull(Object value) {
        return (value == null) ? "" : value.toString().trim();
    }
    public List<Map<String, Object>> getPendingPRsFromDBAndSAP() throws JCoException {

        LocalDateTime toDate = LocalDateTime.now();
        LocalDateTime fromDate = toDate.minusDays(15);

        // Fetch from DB — pending PRs in last 15 days
        List<EzPurchaseRequisitionHeader> pendingPRs = prRepo.findByVendorFilters(fromDate, toDate, "UNRELEASED");

        List<Map<String, Object>> result = new ArrayList<>();
        if (pendingPRs.isEmpty()) {
            return List.of(Map.of("message", "No pending PRs found in last 15 days"));
        }
System.out.println("pendingPRs"+pendingPRs.size());
        try
        {
            JCoDestination destination = rfcReadSAPService.getDestination();

            JCoRepository repository = destination.getRepository();
            JCoFunction poFunction = repository.getFunction("ZEZ_PR_REL_DTLS");
            JCoParameterList imports = poFunction.getImportParameterList();
            JCoTable prListTable = imports.getTable("PR_LIST");


            System.out.println("SAP Connection established successfully!");
            System.out.println("Connected User: " + destination.getUser());

            for (EzPurchaseRequisitionHeader pr : pendingPRs) {
                prListTable.appendRow();
                prListTable.setValue("", pr.getReqNumber().trim());
            }


            System.out.println("Executing ZEZ_PR_REL_DTLS");
            try {
                JCoContext.begin(destination);
                poFunction.execute(destination);

                JCoContext.end(destination);
            }catch(Exception e){}


            JCoTable blockStatDt = poFunction.getTableParameterList().getTable("GENERAL_RELEASE_INFO");
            JCoTable workFlowDt = poFunction.getTableParameterList().getTable("RELEASE_FINAL");
            JCoTable statusDt = poFunction.getTableParameterList().getTable("RELEASE_ALREADY_POSTED");
            Hashtable statusHT=new Hashtable();
            Hashtable<String,String> finalStatusHT=new Hashtable();
            // Pending release info
            for (int i = 0; i < blockStatDt.getNumRows(); i++) {
                blockStatDt.setRow(i);
                String prNo = blockStatDt.getString("PREQ_NO");
                String stat = blockStatDt.getString("REL_IND_TX").toUpperCase();
                if(stat.contains("REL"))
                    finalStatusHT.put(prNo,"REL#0");
            }
            for (int i = 0; i < statusDt.getNumRows(); i++) {
                statusDt.setRow(i);
                String prNo = statusDt.getString("PREQ_NO");
                String lastApproved = "";
                for (int j = 1; j <= 8; j++) {
                    String level = checkNull(statusDt.getString("REL_CODE" + j));
                    if (!level.isEmpty()) lastApproved = level;
                }
                statusHT.put(prNo, lastApproved);


            }
            System.out.println("statusHT"+statusHT+"workFlowDt"+workFlowDt.getNumRows());
            for (int i = 0; i < workFlowDt.getNumRows(); i++)
            {
                String currentLevel="";
                workFlowDt.setRow(i);
                String prNo = workFlowDt.getString("PREQ_NO");
                String[] levels=new String[8];
                String[] approvers=new String[8];
                for(int k=0;k<8;k++)
                {
                    levels[k] = checkNull(workFlowDt.getString("REL_CODE"+(k+1)));
                    approvers[k]=checkNull(workFlowDt.getString("REL_CD_TX"+(k+1)));

                }
                System.out.println("levels"+levels.length+"approvers"+approvers.length);
                currentLevel = (String)statusHT.getOrDefault(prNo, "");
                System.out.println("currentLevel"+currentLevel);
                if (!currentLevel.isEmpty())
                {
                    for (int idx = 0; idx < levels.length ; idx++) {
                        if (currentLevel.equalsIgnoreCase(levels[idx]) && !finalStatusHT.containsKey(prNo)) {
                            finalStatusHT.put(prNo, "PEND#" + approvers[idx]);
                            break;
                        }
                    }
                } else
                {
                    if (!finalStatusHT.containsKey(prNo))
                        finalStatusHT.put(prNo, "UNR#0");
                }

            }
System.out.println("finalStatusHT"+finalStatusHT);
            for (String prNo : finalStatusHT.keySet()) {
                String statusVal = finalStatusHT.get(prNo);
                String[] parts = statusVal.split("#");
                String status = parts[0];
                String nextApprover = (parts.length > 1) ? parts[1] : null;
                if(status.equalsIgnoreCase("UNR"))status="UNRELEASED";
                if(status.equalsIgnoreCase("PEND"))status="PENDING";
                if(status.equalsIgnoreCase("REL"))status="RELEASED";
                headerService.updateStatusAndApprover(prNo, status, nextApprover);
            }
            } catch (JCoException e) {
                System.out.println("Error connecting to SAP: " + e.getMessage());
                e.printStackTrace();
            }

        return result;
    }

}
