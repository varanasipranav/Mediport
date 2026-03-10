/*
package com.ezc.aragenPR.webapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

@Service
public class GRNItemsSapService {

    @Autowired
    @Qualifier("grItems")
    private WebServiceTemplate grItems;

    public Object callService(Object requestPayload) {
        return grItems.marshalSendAndReceive(requestPayload);
    }
}*/
