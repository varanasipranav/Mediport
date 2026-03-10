package com.ezc.aragenPR.webapp.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
class SessionListener implements HttpSessionListener {

    @Value("${session.timeout}")
    private Integer sessionTimeout; 

    @Override
    public void sessionCreated(HttpSessionEvent event) {
    	log.debug("Session created:::"+sessionTimeout);
        event.getSession().setMaxInactiveInterval(sessionTimeout);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {}

}
