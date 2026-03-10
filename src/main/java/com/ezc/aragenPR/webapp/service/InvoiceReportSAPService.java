/*
package com.ezc.aragenPR.webapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class InvoiceReportSAPService {

    @Autowired
    @Qualifier("invoiceReport")
    private WebServiceTemplate invoiceReport;

    public Object callService(Object requestPayload) {
        return invoiceReport.marshalSendAndReceive(requestPayload);
    }
}*/
