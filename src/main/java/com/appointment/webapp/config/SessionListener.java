package com.appointment.webapp.config;

import org.springframework.context.annotation.Configuration;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

@Configuration
public class SessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        event.getSession().setMaxInactiveInterval(30 * 60); // 30 minutes
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        // session ids are credentials: never log them
    }
}
