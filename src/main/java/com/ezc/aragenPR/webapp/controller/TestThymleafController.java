package com.ezc.aragenPR.webapp.controller;

import com.ezc.aragenPR.webapp.dto.ListSelector;
import com.ezc.aragenPR.webapp.model.*;
import com.ezc.aragenPR.webapp.persistance.dao.EzQmisHeaderRepository;
import com.ezc.aragenPR.webapp.persistance.dao.EzcPRHeaderRepository;
import com.ezc.aragenPR.webapp.persistance.dao.EzcReservationHeaderRepo;
import com.ezc.aragenPR.webapp.persistance.dao.SESRepo;
import com.ezc.aragenPR.webapp.service.EzcPRHeaderService;
import com.ezc.aragenPR.webapp.service.IEmailService;
import com.ezc.aragenPR.webapp.service.IRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Controller
public class TestThymleafController {

    @Autowired
    private IRequestService iRequestService;
    @Autowired
    private IEmailService iEmailService;
    //@Autowired
    // private EzcEHSActionService actionService;
    @Autowired
    private EzcPRHeaderService headerService;

    @Autowired
    private EzQmisHeaderRepository qmisHeaderRepository;

    @Autowired
    private EzcPRHeaderRepository headerRepo;


    @Autowired
    private SESRepo sesRepo;

    @Autowired
    private EzcReservationHeaderRepo resRepo;
    // @Autowired
    //IPrdPlanService prdPlanService;

    @GetMapping("/dashboard/*")
    public String showView(Principal principal,Authentication auth,Model model,SecurityContextHolderAwareRequestWrapper requestWrapper) {
        System.out.print("in Dashboard");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userRole="";
        String plantCode="";
        Users userObj = (Users)authentication.getPrincipal();
        Set<UserDefaults> defaults = userObj.getUserDefaults();
        List<String> userRoles = userObj.getRoles().stream()
                .map(Roles::getName)
                .toList();

        List<Object[]> userListObj = null, userListObjTPS =null,userListObjBD =null;
        ArrayList<String> userList = new ArrayList<String>();
        ListSelector listSelector= new ListSelector();
        String loggedUser=userObj.getUserId();
        userList.add(userObj.getUserId());
        System.out.print("in Dashboard loggedUser"+loggedUser);
        model.addAttribute("userId", userObj.getUserId());
        if(requestWrapper.isUserInRole("ROLE_INT"))
        {
            listSelector.setUser(userList);
            listSelector.setRole("ROLE_INT");
            listSelector.setStatus("S");
        }
        else if(requestWrapper.isUserInRole("ROLE_APR"))
        {
            listSelector.setWfUser(userObj.getVendorCode());
            listSelector.setRole("ROLE_APR");
            listSelector.setStatus("S");
        }
        else if(requestWrapper.isUserInRole("ROLE_MD_CELL"))
        {
            listSelector.setStatus("A");
            listSelector.setRole("ROLE_MD_CELL");
        }
        else
        {
            //log.debug("before synchMaterials");
            //iMasterService.synchMaterials();
        }
        int reqLisCnt = 0;
        List<EzPriceRequestHeader> reqList = iRequestService.getRequestList(listSelector);
        if(reqList != null)
            reqLisCnt = reqList.size();
        if (authentication != null && authentication.isAuthenticated()) {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                log.debug("Role: " + authority.getAuthority());
                userRole=authority.getAuthority();
            }
        }
        List<String> plantList= new ArrayList<>();
        if (defaults != null) {
            for (UserDefaults def : defaults) {
                if ("LOCATION".equalsIgnoreCase(def.getKey())) {
                    plantCode = def.getValue();
                    log.debug("User plantCode: " + plantCode);
                    break; // if only one location per user
                }
            }
            if (plantCode.contains("##")) {
                String[] plantListArr = plantCode.split("##");
                for (int i = 0; i < plantListArr.length; i++) {
                    String currentPlant = plantListArr[i];
                    log.debug("Plant:" + (i + 1) + ":" + currentPlant);
                    plantList.add(currentPlant);
                    // Add additional logic here if you want to handle more than 2 plants
                }
            }
            else {
                plantList.add(plantCode);
            }
        }
        int ppapPendingCnt = 0;
        boolean dashBoardFlag=true;
        if((listSelector == null || listSelector.getFromDate() == null) )
        {
            Date todayDate = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(todayDate);
            c.add(Calendar.MONTH, -3);
            listSelector = new ListSelector();
            listSelector.setFromDate(c.getTime());
            listSelector.setToDate(todayDate);
            listSelector.setUser(userList);
            listSelector.setVendor(userObj.getVendorCode());
        }

        int unReleasedPRCnt = 0;
        int relPrCnt=0;
        int pendPrCnt=0;
        ListSelector qmisListSelector = new ListSelector();
        Date todayDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(todayDate);
        c.add(Calendar.MONTH, -3);
        qmisListSelector.setFromDate(c.getTime());
        qmisListSelector.setToDate(todayDate);
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        Date fromDate = new Date();
        Date toDate = new Date();
        cal.set(year, Calendar.APRIL, 1, 0, 0, 0);
        fromDate = cal.getTime();
        cal.set(year + 1, Calendar.MARCH, 31, 23, 59, 59);
        toDate = cal.getTime();

        LocalDateTime from = null;
        LocalDateTime to = null;

        if (fromDate != null) {
            from = fromDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
        if (toDate != null) {
            to = toDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
        List<EzPurchaseRequisitionHeader> headers = headerService.getFilteredHeaders(from, to, "UNRELEASED", loggedUser);
        List<EzPurchaseRequisitionHeader> headersRel = headerService.getFilteredHeaders(from, to, "RELEASED", loggedUser);
        List<EzPurchaseRequisitionHeader> headersPend = headerService.getFilteredHeaders(from, to, "PENDING", loggedUser);

       /* else if(userRoles.contains("ROLE_OS_QA_CEAT")){
            documents = qmisHeaderRepository.findByEqhPlantInAndEqhStatus( plantList,EqhDocStatus.PENDING);
        } else if (userRoles.contains("ROLE_OS_CQA_CEAT")) {
            documents = qmisHeaderRepository.findByEqhPlantInAndEqhStatus( plantList,EqhDocStatus.PENDING);
        }*/
        unReleasedPRCnt = headers.size();
        relPrCnt=headersRel.size();
        pendPrCnt=headersPend.size();
        model.addAttribute("unreleasedPR", unReleasedPRCnt);
        model.addAttribute("releasedPR", relPrCnt);
        model.addAttribute("pendingPR", pendPrCnt);

        // SES Count logic - similar to PR counts
        LocalDate sesFromDate = from.toLocalDate();
        long unReleasedSESCnt = sesRepo.countByEntryDateAfterAndStatusAndCreatedBy(sesFromDate, "UNRELEASED", loggedUser);
        long releasedSESCnt = sesRepo.countByEntryDateAfterAndStatusAndCreatedBy(sesFromDate, "RELEASED", loggedUser);
        long pendingSESCnt = sesRepo.countByEntryDateAfterAndStatusAndCreatedBy(sesFromDate, "PENDING", loggedUser);

        model.addAttribute("unreleasedSES", unReleasedSESCnt);
        model.addAttribute("releasedSES", releasedSESCnt);
        model.addAttribute("pendingSES", pendingSESCnt);

        // pass this fromDate and toDate of type LocalDate
        //model.addAttribute("qmisFromDate", fromDate);
        //model.addAttribute("qmisToDate", toDate);
        // model.addAttribute("listSelector", listSelector);
        return "dashboard/index";
    }
    @GetMapping("/test/email")
    public String testEmail(Principal principal,Authentication auth,Model model,SecurityContextHolderAwareRequestWrapper requestWrapper) {
        iEmailService.sendSimpleMessage("ggangula@answerthink.com", "test sub", "test cont");
        return "dashboard/index";
    }

    @GetMapping("/user-manuals")
    public String getUserManuals(Model model) {
        return "documents";
    }
}