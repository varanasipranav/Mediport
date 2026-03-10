/*
package com.ezc.aragenPR.webapp.service;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

import java.util.Base64;
import java.util.HashMap;

@Slf4j
@Service
public class RestTemplateService {

	    private final RestTemplate restTemplate = new RestTemplate();
	    public ResponseEntity<String> callApi(String base64File) {
	        String url = "https://sigixtractbackendapp.azurewebsites.net/api/sourceinvoices/real-time-process";
//	        String url = "https://sigixtractbackend-stg.azurewebsites.net/api/sourceinvoices/real-time-process";
	        
	        
	        String username = "SIGITEK";
	        String password = "ceat@1888";
	        String auth = username + ":" + password;
	        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
	        String authHeader = "Basic " + encodedAuth;
	       

	        // Create the request body
	        Map<String, Object> requestBody = new HashMap<>();
	        requestBody.put("file", base64File);
	        requestBody.put("fileType", "base64");
	        requestBody.put("companyId", "627cfcf04d84cb2638ca6385");
	        
	        System.out.println("REQUESTBODY =>>>"+ requestBody);

	        // Set the headers
	        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
	        log.debug("added headers");
//	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        headers.set("Authorization", authHeader);
	        log.debug("below headers");

	        // Wrap the body and headers in the request entity
	        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
	        log.debug("below http");

	        // Make the POST request
	        ResponseEntity<String> response = restTemplate.exchange(
	                url,
	                HttpMethod.POST,
	                requestEntity,
	                String.class
	        );
	        log.debug("waiting for resp");
	        System.out.println("response =>>>>" + response);
	        String responseOut=response.getBody();
	        // Return the response body
	        System.out.println(responseOut);
	        log.debug("responseOut:::"+responseOut);
	        return response;
	    }

}
*/
