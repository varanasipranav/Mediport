package com.ezc.aragenPR.webapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ezc.aragenPR.webapp.service.RFCReadSAPService;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class UserDefaultsController {

    @Autowired
    RFCReadSAPService rfcReadSAPService;

    @Autowired
    private Environment env;

    @RequestMapping("/userDefault/getDefault")
    public String getDefaults(Model model) {

        System.out.println("IN::getDefaults:12345:");

        // Test connection
        try {
            System.out.println("IN::getDefaults:: TRY");
            JCoDestination destination = rfcReadSAPService.getDestination();
            System.out.println("SAP Connection established successfully!");
            System.out.println("Connected User: " + destination.getUser());
        } catch (JCoException e) {
            System.out.println("Error connecting to SAP: " + e.getMessage());
            e.printStackTrace();
        }

        // Example: Fetch MATNR and MAKTX from table MAKT
        String[] outputFields = new String[] { "MATNR", "MAKTX" };
        String[] queryValues = new String[] { "MATNR EQ '000000000010058342'" };

        List<String[]> matList = rfcReadSAPService.readRFCTable("MAKT", outputFields, queryValues);

        for (String[] row : matList) {
            String matnr = row[0].trim().replaceFirst("^0+(?!$)", "");
            String maktx = row[1].trim();
            System.out.println("Material: " + matnr + " | Description: " + maktx);
        }

        model.addAttribute("matList", matList); // pass to JSP if needed

        return "prCreation/createPR"; // your JSP page
    }
}
