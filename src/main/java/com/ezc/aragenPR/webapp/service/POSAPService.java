/*
package com.ezc.aragenPR.webapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

@Service
public class POSAPService {

    @Autowired
    @Qualifier("poDetails")
    private WebServiceTemplate poDetails;

    public Object callService(Object requestPayload) {
        return poDetails.marshalSendAndReceive(requestPayload);
    }
}*/
