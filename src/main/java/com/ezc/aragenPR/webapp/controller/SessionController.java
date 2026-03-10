package com.ezc.aragenPR.webapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpSession;


@RestController
@RequestMapping("/api/session")
public class SessionController {

    @PostMapping("/extend")
    public ResponseEntity<String> extendSession(HttpSession session) {
        session.setMaxInactiveInterval(60 * 30);
        int timeoutInSeconds = session.getMaxInactiveInterval();
        System.out.println("SessionController: extendSession called, timeout set to " + timeoutInSeconds + " seconds");
        System.out.println("SessionController: extendSession called");
        return ResponseEntity.ok("Session extended");
    }
}