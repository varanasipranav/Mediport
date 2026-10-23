package com.appointment.webapp.whatsapp.controller;

import com.appointment.webapp.whatsapp.config.WhatsAppConfig;
import com.appointment.webapp.whatsapp.dto.webhook.ParsedMessage;
import com.appointment.webapp.whatsapp.dto.webhook.WebhookEvent;
import com.appointment.webapp.whatsapp.service.webhook.WebhookDispatcher;
import com.appointment.webapp.whatsapp.service.webhook.WebhookMessageParser;
import com.appointment.webapp.whatsapp.service.webhook.WebhookSignatureValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * WhatsApp Webhook Controller
 *
 * Main entry point for WhatsApp Cloud API webhooks
 * Handles:
 * 1. Webhook verification (GET request from Facebook)
 * 2. Incoming messages (POST request from Facebook)
 * 3. Message status updates (delivered, read, etc.)
 *
 * Webhook URL: https://yourdomain.com/api/whatsapp/webhook
 */
@RestController
@RequestMapping("/api/whatsapp")
public class WhatsAppWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(WhatsAppWebhookController.class);

    @Autowired
    private WhatsAppConfig whatsAppConfig;

    @Autowired
    private WebhookSignatureValidator signatureValidator;

    @Autowired
    private WebhookMessageParser messageParser;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebhookDispatcher webhookDispatcher;

    @Autowired
    private com.appointment.webapp.whatsapp.service.webhook.WhatsAppTenantRouter tenantRouter;

    @org.springframework.beans.factory.annotation.Value("${whatsapp.webhook.test-endpoints-enabled:false}")
    private boolean testEndpointsEnabled;

    /**
     * Webhook Verification Endpoint (GET)
     *
     * Facebook sends a GET request to verify your webhook:
     * GET /api/whatsapp/webhook?hub.mode=subscribe&hub.challenge=12345&hub.verify_token=YOUR_TOKEN
     *
     * You must respond with the hub.challenge value if verify_token matches
     *
     * @param mode Should be "subscribe"
     * @param token Your verify token (set in application.properties)
     * @param challenge Random string from Facebook
     * @return The challenge value if verification succeeds
     */
    @GetMapping("/webhook")
    public ResponseEntity<String> verifyWebhook(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.verify_token") String token,
            @RequestParam("hub.challenge") String challenge) {

        logger.info("Webhook verification request received");
        logger.debug("Mode: {}, Challenge: {}", mode, challenge);

        // Verify the mode
        if (!"subscribe".equals(mode)) {
            logger.warn("Invalid mode: {}", mode);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid mode");
        }

        // Verify the token
        String expectedToken = whatsAppConfig.getWebhook().getVerifyToken();
        if (expectedToken == null || expectedToken.isBlank() || !token.equals(expectedToken)) {
            logger.error("Verify token mismatch");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Verify token mismatch");
        }

        // Verification successful - return the challenge
        logger.info("Webhook verification successful! Challenge: {}", challenge);
        return ResponseEntity.ok(challenge);
    }

    /**
     * Webhook Event Endpoint (POST)
     *
     * Facebook sends POST requests when:
     * - User sends a message
     * - Message status changes (sent, delivered, read, failed)
     * - User clicks interactive buttons/lists
     *
     * Headers:
     * - X-Hub-Signature-256: HMAC signature for security verification
     *
     * Body: JSON webhook event (see WebhookEvent.java for structure)
     *
     * @param payload Raw JSON payload as string
     * @param signature Signature from X-Hub-Signature-256 header
     * @return 200 OK (must respond quickly, within 20 seconds)
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> receiveWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature) {

        logger.info("==================================================");
        logger.info("Webhook POST request received");

        // Step 1: Verify signature - rejects anything not signed by Meta with our app secret
        if (!signatureValidator.validateSignature(payload, signature, tenantRouter.appSecretFor(payload))) {
            logger.error("SECURITY ALERT: Invalid webhook signature!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature");
        }

        // Step 2: Parse JSON payload
        WebhookEvent webhookEvent;
        try {
            webhookEvent = objectMapper.readValue(payload, WebhookEvent.class);
            logger.info("Webhook event parsed successfully");
        } catch (Exception e) {
            logger.error("Failed to parse webhook payload", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid JSON");
        }

        // Step 3: Extract messages from webhook event
        List<ParsedMessage> messages = messageParser.parseMessages(webhookEvent);

        if (messages.isEmpty()) {
            logger.info("No messages to process (might be status update or unsupported type)");
            return ResponseEntity.ok("OK");  // Still return 200
        }

        // Step 4: Process each message through conversation flow
        for (ParsedMessage message : messages) {
            logger.info("Received message {} (type {})", message.getMessageId(), message.getMessageType());

            if (tenantRouter.enabled()) {
                var clinic = tenantRouter.clinicFor(message.getPhoneNumberId());
                if (clinic.isEmpty()) {
                    logger.warn("Ignoring message {}: no clinic is registered for WhatsApp number {}", message.getMessageId(), message.getPhoneNumberId());
                    continue;
                }
                message.setSiteId(clinic.get().siteId());
            }

            // Hand off to the async dispatcher (ordered per sender, de-duplicated by message ID)
            webhookDispatcher.dispatch(message);
        }

        logger.info("==================================================");

        // Step 5: Return 200 OK immediately
        // IMPORTANT: WhatsApp requires response within 20 seconds
        // Do all heavy processing asynchronously
        return ResponseEntity.ok("OK");
    }

    /**
     * Test endpoint to manually trigger webhook processing
     * Only for development/testing
     */
    @PostMapping("/webhook/test")
    public ResponseEntity<String> testWebhook(@RequestBody String payload) {
        if (!testEndpointsEnabled) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        logger.warn("TEST ENDPOINT CALLED - Only use in development!");

        try {
            WebhookEvent webhookEvent = objectMapper.readValue(payload, WebhookEvent.class);
            List<ParsedMessage> messages = messageParser.parseMessages(webhookEvent);

            return ResponseEntity.ok("Parsed " + messages.size() + " messages: " + messages);
        } catch (Exception e) {
            logger.error("Test parsing failed", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/webhook/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("WhatsApp Webhook is UP ✓");
    }
}
