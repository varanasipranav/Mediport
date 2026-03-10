/*
package com.ezc.aragenPR.webapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

@Service
public class POChangeSAPService {

    @Autowired
    @Qualifier("poChange")
    private WebServiceTemplate poChange;

    public Object callService(Object requestPayload) {
        return poChange.marshalSendAndReceive(requestPayload);
    }
}*/
