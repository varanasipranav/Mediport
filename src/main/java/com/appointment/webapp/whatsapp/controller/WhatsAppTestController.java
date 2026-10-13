package com.appointment.webapp.whatsapp.controller;

import com.appointment.webapp.whatsapp.service.messaging.WhatsAppMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test controller for WhatsApp message sending
 * ONLY FOR DEVELOPMENT/TESTING - REMOVE IN PRODUCTION
 */
@RestController
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "whatsapp.webhook.test-endpoints-enabled", havingValue = "true")
@RequestMapping("/api/whatsapp/test")
public class WhatsAppTestController {

    @Autowired
    private WhatsAppMessageSender messageSender;

    /**
     * Test sending a simple text message
     *
     * GET /api/whatsapp/test/send-text?phone=+919876543210&message=Hello
     */
    @GetMapping("/send-text")
    public ResponseEntity<?> sendTextMessage(
            @RequestParam String phone,
            @RequestParam(defaultValue = "Test message from WhatsApp Bot") String message) {

        String result = messageSender.sendTextMessage(phone, message);

        Map<String, Object> response = new HashMap<>();
        response.put("success", result != null);
        response.put("phone", phone);
        response.put("message", message);
        response.put("result", result);

        return ResponseEntity.ok(response);
    }

    /**
     * Test sending button message
     *
     * GET /api/whatsapp/test/send-buttons?phone=+919876543210
     */
    @GetMapping("/send-buttons")
    public ResponseEntity<?> sendButtonMessage(@RequestParam String phone) {

        var buttons = List.of(
            new WhatsAppMessageSender.Button("test_1", "Option 1"),
            new WhatsAppMessageSender.Button("test_2", "Option 2"),
            new WhatsAppMessageSender.Button("test_3", "Option 3")
        );

        String result = messageSender.sendButtonMessage(
            phone,
            "Test buttons - please select one:",
            buttons
        );

        Map<String, Object> response = new HashMap<>();
        response.put("success", result != null);
        response.put("phone", phone);
        response.put("result", result);

        return ResponseEntity.ok(response);
    }

    /**
     * Test sending list message
     *
     * GET /api/whatsapp/test/send-list?phone=+919876543210
     */
    @GetMapping("/send-list")
    public ResponseEntity<?> sendListMessage(@RequestParam String phone) {

        var section = new WhatsAppMessageSender.ListSection("Test Options");
        section.addRow(new WhatsAppMessageSender.ListRow("opt_1", "Option 1", "First option"));
        section.addRow(new WhatsAppMessageSender.ListRow("opt_2", "Option 2", "Second option"));
        section.addRow(new WhatsAppMessageSender.ListRow("opt_3", "Option 3", "Third option"));

        String result = messageSender.sendListMessage(
            phone,
            "Test List",
            "Please select an option from the list below:",
            "Select Option",
            List.of(section)
        );

        Map<String, Object> response = new HashMap<>();
        response.put("success", result != null);
        response.put("phone", phone);
        response.put("result", result);

        return ResponseEntity.ok(response);
    }

    /**
     * Test configuration
     *
     * GET /api/whatsapp/test/config
     */
    @GetMapping("/config")
    public ResponseEntity<?> testConfig(
            @org.springframework.beans.factory.annotation.Value("${whatsapp.api.base-url}") String baseUrl,
            @org.springframework.beans.factory.annotation.Value("${whatsapp.api.phone-number-id}") String phoneNumberId,
            @org.springframework.beans.factory.annotation.Value("${whatsapp.api.access-token}") String accessToken) {

        Map<String, Object> config = new HashMap<>();
        config.put("baseUrl", baseUrl);
        config.put("phoneNumberId", phoneNumberId);
        config.put("accessToken", accessToken != null && !accessToken.isEmpty() ?
                   "Present (length: " + accessToken.length() + ")" : "MISSING");
        config.put("messagesEndpoint", baseUrl + "/" + phoneNumberId + "/messages");

        return ResponseEntity.ok(config);
    }

    /**
     * Health check
     *
     * GET /api/whatsapp/test/health
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "WhatsApp Test Controller is running");
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }
}
