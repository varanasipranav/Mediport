package com.ezc.aragenPR.webapp.service;

import com.ezc.aragenPR.webapp.dto.ReservationDTO;
import com.ezc.aragenPR.webapp.dto.ReservationItemDTO;
import com.ezc.aragenPR.webapp.model.EzcReservationHeader;
import com.ezc.aragenPR.webapp.model.EzcReservationItems;
import com.ezc.aragenPR.webapp.model.ReservationItemKey;
import com.ezc.aragenPR.webapp.model.Users;
import com.ezc.aragenPR.webapp.persistance.dao.EzcReservationHeaderRepo;
import com.sap.conn.jco.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@Transactional
public class ReservationService {

    @Autowired
    RFCReadSAPService rfcReadSAPService;

    @Autowired
    private EzcReservationHeaderRepo headerRepo;


    private String checkNull(Object value) {
        return (value == null) ? "" : value.toString().trim();
    }
    @Transactional
    public Map<String,Object> createReservation(ReservationDTO reservationDTO) throws Exception {
        Map<String,Object> result = new HashMap<>();
        System.out.print("into createReservation servcie" + reservationDTO);

        JCoDestination destination = rfcReadSAPService.getDestination();
        JCoRepository repository = destination.getRepository();
        JCoFunction function = repository.getFunction("ZEZ_BAPI_RESERVATION_CREATE1");
        JCoStructure header = function.getImportParameterList().getStructure("RESERVATIONHEADER");


        header.setValue("MOVE_TYPE", reservationDTO.getMovementType());
        header.setValue("WBS_ELEM", reservationDTO.getWbs());
        header.setValue("COSTCENTER", reservationDTO.getCostCenter());
        header.setValue("ORDERID", reservationDTO.getInternalOrder());
        //header.setValue("GL_ACCOUNT", reservationDTO.getGlAccount());
        header.setValue("NETWORK", reservationDTO.getNetwork());

        JCoTable items = function.getTableParameterList().getTable("RESERVATIONITEMS");
        JCoTable extension = function.getTableParameterList().getTable("EXTENSIONIN");
        JCoTable addData = function.getTableParameterList().getTable("ADDITIONAL_DATA");

        for (ReservationItemDTO item : reservationDTO.getItems()) {
            System.out.println("mat12345");
            String mat = String.format("%18s", item.getMaterial()).replace(' ','0');

            mat=String.format("%018d", Long.parseLong(item.getMaterial()));
            //System.out.println("mat2"+mat+"getMat"+item.getMaterial());
            items.appendRow();
            items.setValue("MATERIAL", mat);

            items.setValue("BATCH", item.getBatch());
            items.setValue("MOVEMENT", item.getMovementAllowed());
            items.setValue("PLANT", item.getPlant());
            items.setValue("STGE_LOC", item.getStorageLoc());
            items.setValue("ENTRY_QNT", item.getQty());
            items.setValue("ENTRY_UOM", item.getUom());
            items.setValue("GR_RCPT", reservationDTO.getGoodsReceipt());

            addData.appendRow();
            Integer itemNO=item.getItemNo();
            String sapItemNo = String.format("%04d", itemNO);
            addData.setValue("RSPOS",sapItemNo);
            addData.setValue("ZLGORT", item.getLabStorageLoc());

            extension.appendRow();
            String value = item.getLabStorageLoc();
            String paddedValue = String.format("%34s", value);
            extension.setValue("STRUCTURE","BAPI_TE_RSADD");
            extension.setValue("VALUEPART1",paddedValue);
        }

        try {
            JCoContext.begin(destination);
            function.execute(destination);
        } finally {
            JCoContext.end(destination);
        }

        // =========================
        // Read SAP return message
        // =========================

        JCoTable returnTable = function.getTableParameterList().getTable("RETURN");

        String reservationNumber = "";
        boolean isSuccess = false;
        List<String> errors = new ArrayList<>();
        List<String> successMessages = new ArrayList<>();

        for (int i = 0; i < returnTable.getNumRows(); i++) {
            returnTable.setRow(i);
            String type = returnTable.getString("TYPE");
            String id = returnTable.getString("ID");
            String message = returnTable.getString("MESSAGE");

            if ("S".equals(type) && "M7".equals(id)) {
                isSuccess = true;
                successMessages.add(message);
                reservationNumber = checkNull(returnTable.getString("MESSAGE_V1"));
            }

            if ("E".equals(type)) {
                errors.add(message);
            }

            if ("W".equals(type)) {
                successMessages.add("Warning: " + message);
            }
        }

        if(isSuccess && !reservationNumber.isEmpty()) {
            saveToDatabase(reservationDTO, reservationNumber);
        }

        result.put("success", isSuccess);
        result.put("sapReservationNo", reservationNumber);
        result.put("errors", errors);
        result.put("messages", successMessages);


        return result;
    }

    private void saveToDatabase(ReservationDTO reservationDTO, String reservationNumber) {

        String loggedUser = "";
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Users userObj = (Users) authentication.getPrincipal();
            loggedUser = (String) userObj.getUserId();

        }catch(Exception e){}
        EzcReservationHeader header = new EzcReservationHeader();
        header.setReservationNo(reservationNumber);
        header.setMovementType(reservationDTO.getMovementType());
        header.setWbs(reservationDTO.getWbs());
        header.setWbsDesc(reservationDTO.getWbsDesc());
        header.setCostCenter(reservationDTO.getCostCenter());
        header.setInternalOrder(reservationDTO.getInternalOrder());
        header.setNetwork(reservationDTO.getNetwork());
        header.setActivity(reservationDTO.getActivity());
        header.setGlAccount(reservationDTO.getGlAccount());
        header.setReqDate(reservationDTO.getReqDate());
        header.setCreatedBy(loggedUser);
        header.setCreatedOn(LocalDateTime.now());
        header.setStatus("CREATED");
        header.setGoodsReceipt(reservationDTO.getGoodsReceipt());

        List<EzcReservationItems> lineItems = new ArrayList<>();

        for (ReservationItemDTO itemDTO : reservationDTO.getItems()) {


            EzcReservationItems item = new EzcReservationItems();

            item.setItemNo(itemDTO.getItemNo());
            item.setMaterial(itemDTO.getMaterial());
            item.setShortText(itemDTO.getShortText());
            item.setBatch(itemDTO.getBatch());
            item.setMovementAllowed(itemDTO.getMovementAllowed());
            item.setQty(itemDTO.getQty());
            item.setUom(itemDTO.getUom());
            item.setPlant(itemDTO.getPlant());
            item.setStorageLoc(itemDTO.getStorageLoc());
            item.setLabStorageLoc(itemDTO.getLabStorageLoc());
            item.setStatus("CREATED");


            item.setHeader(header);

            lineItems.add(item);
        }

        header.setLineItems(lineItems);

        headerRepo.save(header);
    }
@Transactional
    public Map<String, Object> updateReservation(ReservationDTO reservationDTO,String reservationNo) throws Exception{


        Map<String,Object> result = new HashMap<>();
    System.out.print("into updateReservation servcie" + reservationDTO);

    JCoDestination destination = rfcReadSAPService.getDestination();
    JCoRepository repository = destination.getRepository();
    JCoFunction function = repository.getFunction("ZEZ_BAPI_RESERVATION_CHANGE");
    JCoParameterList imports = function.getImportParameterList();
    imports.setValue("RESERVATION", reservationNo);


    JCoTable itemsChanged = function.getTableParameterList().getTable("RESERVATIONITEMS_CHANGED");
    JCoTable itemsChangedX = function.getTableParameterList().getTable("RESERVATIONITEMS_CHANGEDX");
    JCoTable addData = function.getTableParameterList().getTable("ADDITIONAL_DATA");
    JCoTable extension = function.getTableParameterList().getTable("EXTENSIONIN");
    JCoTable reservationitemsNew = function.getTableParameterList().getTable("RESERVATIONITEMS_NEW");
    boolean isNew=false;
    for (ReservationItemDTO item : reservationDTO.getItems()) {
        if("N".equals(item.getStatus()))
        {
                isNew=true;

        }
    }
    for (ReservationItemDTO item : reservationDTO.getItems()) {
        System.out.println("mat12345"+item.getStatus()+"isNew"+isNew);

        if("N".equals(item.getStatus()))
        {
            String mat = String.format("%18s", item.getMaterial()).replace(' ','0');

            mat=String.format("%018d", Long.parseLong(item.getMaterial()));
            //System.out.println("mat2"+mat+"getMat"+item.getMaterial());
            reservationitemsNew.appendRow();
            reservationitemsNew.setValue("MATERIAL", mat);

            reservationitemsNew.setValue("BATCH", item.getBatch());
            reservationitemsNew.setValue("MOVEMENT", item.getMovementAllowed());
            reservationitemsNew.setValue("PLANT", item.getPlant());
            reservationitemsNew.setValue("REQ_DATE", new Date());
            reservationitemsNew.setValue("STGE_LOC", item.getStorageLoc());
            reservationitemsNew.setValue("ENTRY_QNT", item.getQty());
            reservationitemsNew.setValue("ENTRY_UOM", item.getUom());

        }
        else if(!isNew){
            itemsChanged.appendRow();
            itemsChanged.setValue("RES_ITEM", item.getItemNo());
            itemsChanged.setValue("BATCH", item.getBatch());
            if ("D".equals(item.getStatus()))
                itemsChanged.setValue("DELETE_IND", item.getStatus());
            itemsChanged.setValue("STGE_LOC", item.getStorageLoc());
            itemsChanged.setValue("ENTRY_QNT", item.getQty());

            itemsChangedX.appendRow();
            itemsChangedX.setValue("RES_ITEM", item.getItemNo());
            if(item.getBatch() !=null && !item.getBatch().isEmpty())
                itemsChangedX.setValue("BATCH", "X");
            if ("D".equals(item.getStatus()))
                itemsChangedX.setValue("DELETE_IND", "X");
            itemsChangedX.setValue("STGE_LOC", "X");
            itemsChangedX.setValue("ENTRY_QNT", "X");
        }
        addData.appendRow();
        Integer itemNO=item.getItemNo();
        String sapItemNo = String.format("%04d", itemNO);
        addData.setValue("RSPOS",sapItemNo);
        addData.setValue("ZLGORT", item.getLabStorageLoc());

        extension.appendRow();
        String value = item.getLabStorageLoc();
        String paddedValue = String.format("%34s", value);
        extension.setValue("STRUCTURE","BAPI_TE_RSADD");
        extension.setValue("VALUEPART1",paddedValue);




    }

    try {
        JCoContext.begin(destination);
        function.execute(destination);
    } finally {
        JCoContext.end(destination);
    }

    // =========================
    // Read SAP return message
    // =========================

    JCoTable returnTable = function.getTableParameterList().getTable("RETURN");

    String reservationNumber = "";
    boolean isSuccess = false;
    List<String> errors = new ArrayList<>();
    List<String> successMessages = new ArrayList<>();

    for (int i = 0; i < returnTable.getNumRows(); i++) {
        returnTable.setRow(i);
        String type = returnTable.getString("TYPE");
        String id = returnTable.getString("ID");
        String message = returnTable.getString("MESSAGE");

        if ("S".equals(type) && "M7".equals(id)) {
            isSuccess = true;
            successMessages.add(message);
            reservationNumber = checkNull(returnTable.getString("MESSAGE_V1"));
        }

        if ("E".equals(type)) {
            errors.add(message);
        }

        if ("W".equals(type)) {
            successMessages.add("Warning: " + message);
        }
    }

    if(isSuccess && !reservationNumber.isEmpty()) {
  //      saveToDatabase(reservationDTO, reservationNumber);
    }

    result.put("success", isSuccess);
    result.put("sapReservationNo", reservationNumber);
    result.put("errors", errors);
    result.put("messages", successMessages);

    return result;
    }
}
