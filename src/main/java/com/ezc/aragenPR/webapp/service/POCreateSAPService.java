/*
package com.ezc.aragenPR.webapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

@Service
public class POCreateSAPService {

    @Autowired
    @Qualifier("poCreate")
    private WebServiceTemplate poCreate;

    public Object callService(Object requestPayload) {
        return poCreate.marshalSendAndReceive(requestPayload);
    }
}*/
