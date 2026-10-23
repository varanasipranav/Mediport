package com.appointment.webapp.whatsapp.service.messaging;

import com.appointment.webapp.whatsapp.config.WhatsAppConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * WhatsApp Message Sender Service
 * Handles sending messages via WhatsApp Cloud API
 */
@Service
public class WhatsAppMessageSender {

    private static final Logger logger = LoggerFactory.getLogger(WhatsAppMessageSender.class);

    @Autowired
    private WhatsAppConfig whatsAppConfig;

    @Autowired
    @Qualifier("whatsAppRestTemplate")
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Send text message with retry logic
     */
    public String sendTextMessage(String toPhone, String text) {
        logger.info("Sending text message to: {}", toPhone);
        logger.debug("Text content: {}", text);

        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("messaging_product", "whatsapp");
            payload.put("recipient_type", "individual");
            payload.put("to", toPhone);
            payload.put("type", "text");

            Map<String, String> textContent = new HashMap<>();
            textContent.put("preview_url", "false");
            textContent.put("body", text);
            payload.put("text", textContent);

            return sendMessageWithRetry(payload, 3); // 3 retry attempts

        } catch (Exception e) {
            logger.error("Error sending text message to {}", toPhone, e);
            return null;
        }
    }

    /**
     * Send interactive button message
     */
    public String sendButtonMessage(String toPhone, String bodyText, List<Button> buttons) {
        logger.info("Sending button message to: {}", toPhone);

        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("messaging_product", "whatsapp");
            payload.put("recipient_type", "individual");
            payload.put("to", toPhone);
            payload.put("type", "interactive");

            Map<String, Object> interactive = new HashMap<>();
            interactive.put("type", "button");

            Map<String, String> body = new HashMap<>();
            body.put("text", bodyText);
            interactive.put("body", body);

            List<Map<String, Object>> buttonList = new ArrayList<>();
            for (Button btn : buttons) {
                Map<String, Object> buttonObj = new HashMap<>();
                buttonObj.put("type", "reply");

                Map<String, String> reply = new HashMap<>();
                reply.put("id", btn.getId());
                reply.put("title", btn.getTitle());
                buttonObj.put("reply", reply);

                buttonList.add(buttonObj);
            }

            Map<String, Object> action = new HashMap<>();
            action.put("buttons", buttonList);
            interactive.put("action", action);

            payload.put("interactive", interactive);

            return sendMessageWithRetry(payload, 3); // 3 retry attempts

        } catch (Exception e) {
            logger.error("Error sending button message to {}", toPhone, e);
            return null;
        }
    }

    /**
     * Send interactive list message
     */
    public String sendListMessage(String toPhone, String headerText, String bodyText,
                                  String buttonText, List<ListSection> sections) {
        logger.info("Sending list message to: {}", toPhone);

        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("messaging_product", "whatsapp");
            payload.put("recipient_type", "individual");
            payload.put("to", toPhone);
            payload.put("type", "interactive");

            Map<String, Object> interactive = new HashMap<>();
            interactive.put("type", "list");

            if (headerText != null && !headerText.isEmpty()) {
                Map<String, String> header = new HashMap<>();
                header.put("type", "text");
                header.put("text", fit(headerText, 60));
                interactive.put("header", header);
            }

            Map<String, String> body = new HashMap<>();
            body.put("text", bodyText);
            interactive.put("body", body);

            Map<String, Object> action = new HashMap<>();
            action.put("button", fit(buttonText, 20));

            List<Map<String, Object>> sectionsList = new ArrayList<>();
            for (ListSection section : sections) {
                Map<String, Object> sectionObj = new HashMap<>();
                if (section.getTitle() != null) {
                    sectionObj.put("title", section.getTitle());
                }

                List<Map<String, String>> rows = new ArrayList<>();
                for (ListRow row : section.getRows()) {
                    Map<String, String> rowObj = new HashMap<>();
                    rowObj.put("id", row.getId());
                    rowObj.put("title", row.getTitle());
                    if (row.getDescription() != null) {
                        rowObj.put("description", row.getDescription());
                    }
                    rows.add(rowObj);
                }

                sectionObj.put("rows", rows);
                sectionsList.add(sectionObj);
            }

            action.put("sections", sectionsList);
            interactive.put("action", action);

            payload.put("interactive", interactive);

            return sendMessageWithRetry(payload, 3); // 3 retry attempts

        } catch (Exception e) {
            logger.error("Error sending list message to {}", toPhone, e);
            return null;
        }
    }

    /**
     * Send message to WhatsApp API
     */
    private String sendMessage(Map<String, Object> payload) {
        try {
            // A clinic with its own WhatsApp number sends from that number with its own token
            var clinic = com.appointment.webapp.tenancy.TenantContext.get();
            boolean own = clinic != null && clinic.waPhoneNumberId() != null && clinic.waAccessToken() != null;
            String endpoint = own
                    ? whatsAppConfig.getApi().getBaseUrl() + "/" + clinic.waPhoneNumberId() + "/messages"
                    : whatsAppConfig.getApi().getMessagesEndpoint();

            logger.info("📤 Sending message to WhatsApp API");
            logger.info("   Endpoint: {}", endpoint);

            String jsonPayload = objectMapper.writeValueAsString(payload);
            logger.debug("   Payload: {}", jsonPayload);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (own) {
                headers.setBearerAuth(clinic.waAccessToken());
            }
            // Otherwise the Authorization header is added by the RestTemplate interceptor

            HttpEntity<String> request = new HttpEntity<>(jsonPayload, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(endpoint, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("✅ Message sent successfully!");
                logger.info("   Response: {}", response.getBody());
                return response.getBody();
            } else {
                logger.error("❌ Failed to send message!");
                logger.error("   Status: {}", response.getStatusCode());
                logger.error("   Response: {}", response.getBody());
                return null;
            }

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            logger.error("❌ HTTP Client Error while sending message!");
            logger.error("   Status: {}", e.getStatusCode());
            logger.error("   Response Body: {}", e.getResponseBodyAsString());
            logger.error("   Full error: ", e);
            return null;
        } catch (org.springframework.web.client.HttpServerErrorException e) {
            logger.error("❌ HTTP Server Error while sending message!");
            logger.error("   Status: {}", e.getStatusCode());
            logger.error("   Response Body: {}", e.getResponseBodyAsString());
            return null;
        } catch (org.springframework.web.client.ResourceAccessException e) {
            logger.error("❌ Network/Connection error while sending message!");
            logger.error("   Error: {}", e.getMessage());
            logger.error("   Full error: ", e);
            return null;
        } catch (Exception e) {
            logger.error("❌ Unexpected exception while sending message!");
            logger.error("   Error type: {}", e.getClass().getName());
            logger.error("   Message: {}", e.getMessage());
            logger.error("   Full error: ", e);
            return null;
        }
    }

    /**
     * Send message with retry logic and exponential backoff
     */
    private String sendMessageWithRetry(Map<String, Object> payload, int maxAttempts) {
        int attempt = 1;
        long waitTime = 1000; // Start with 1 second

        while (attempt <= maxAttempts) {
            try {
                logger.info("📤 Attempt {}/{} to send message", attempt, maxAttempts);
                String result = sendMessage(payload);

                if (result != null) {
                    return result; // Success
                }

                // If result is null but no exception, still retry
                if (attempt < maxAttempts) {
                    logger.warn("⚠️ Message send returned null, retrying in {} ms...", waitTime);
                    Thread.sleep(waitTime);
                    waitTime *= 2; // Exponential backoff
                }

            } catch (org.springframework.web.client.ResourceAccessException e) {
                // Network/timeout error - retry
                logger.warn("⚠️ Network error on attempt {}/{}: {}", attempt, maxAttempts, e.getMessage());

                if (attempt < maxAttempts) {
                    try {
                        logger.info("⏳ Retrying in {} ms...", waitTime);
                        Thread.sleep(waitTime);
                        waitTime *= 2; // Exponential backoff (1s, 2s, 4s...)
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        logger.error("Retry interrupted", ie);
                        return null;
                    }
                } else {
                    logger.error("❌ All {} attempts failed due to network error", maxAttempts);
                    return null;
                }

            } catch (Exception e) {
                // Other errors - don't retry
                logger.error("❌ Non-retryable error on attempt {}", attempt, e);
                return null;
            }

            attempt++;
        }

        logger.error("❌ Failed to send message after {} attempts", maxAttempts);
        return null;
    }

    // Helper classes
    /** Cuts text to WhatsApp's limit (counted in characters) and marks the cut with an ellipsis. */
    static String fit(String text, int max) {
        if (text == null || text.codePointCount(0, text.length()) <= max) {
            return text;
        }
        int end = text.offsetByCodePoints(0, max - 1);
        return text.substring(0, end) + "\u2026";
    }

    public static class Button {
        private String id;
        private String title;

        public Button(String id, String title) {
            this.id = id;
            this.title = fit(title, 20);
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }
    }

    public static class ListSection {
        private String title;
        private List<ListRow> rows = new ArrayList<>();

        public ListSection(String title) {
            this.title = fit(title, 24);
        }

        public void addRow(ListRow row) {
            this.rows.add(row);
        }

        public String getTitle() {
            return title;
        }

        public List<ListRow> getRows() {
            return rows;
        }
    }

    public static class ListRow {
        private String id;
        private String title;
        private String description;

        public ListRow(String id, String title, String description) {
            this.id = id;
            this.title = fit(title, 24);
            this.description = fit(description, 72);
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }
    }
}
