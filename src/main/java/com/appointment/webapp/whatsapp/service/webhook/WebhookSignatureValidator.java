package com.appointment.webapp.whatsapp.service.webhook;

import com.appointment.webapp.whatsapp.config.WhatsAppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Service for verifying WhatsApp webhook signatures
 * Security: Ensures webhook requests are actually from Facebook/Meta
 */
@Service
public class WebhookSignatureValidator {

    private static final Logger logger = LoggerFactory.getLogger(WebhookSignatureValidator.class);

    @Autowired
    private WhatsAppConfig whatsAppConfig;

    /**
     * Verify that the webhook request is from Facebook/Meta
     * Uses HMAC-SHA256 signature verification
     *
     * @param payload Raw request body (JSON string)
     * @param signature Signature from X-Hub-Signature-256 header
     * @return true if signature is valid, false otherwise
     */
    public boolean validateSignature(String payload, String signature) {
        String appSecret = whatsAppConfig.getWebhook().getAppSecret();
        if (appSecret == null || appSecret.isBlank() || appSecret.startsWith("YOUR_APP_SECRET")) {
            logger.error("whatsapp.webhook.app-secret is not configured - rejecting webhook (fail closed)");
            return false;
        }
        if (signature == null || signature.isEmpty()) {
            logger.warn("Missing X-Hub-Signature-256 header");
            return false;
        }

        if (!signature.startsWith("sha256=")) {
            logger.warn("Invalid signature format");
            return false;
        }

        try {
            // Remove "sha256=" prefix
            String receivedSignature = signature.substring(7);

            // Compute expected signature
            String expectedSignature = computeHmacSha256(payload, whatsAppConfig.getWebhook().getAppSecret());

            // Constant-time comparison to prevent timing attacks
            boolean isValid = MessageDigest.isEqual(
                    receivedSignature.getBytes(StandardCharsets.UTF_8),
                    expectedSignature.getBytes(StandardCharsets.UTF_8)
            );

            if (!isValid) {
                logger.error("Signature verification failed! Possible security breach attempt.");
            }

            return isValid;

        } catch (Exception e) {
            logger.error("Error validating webhook signature", e);
            return false;
        }
    }

    /**
     * Compute HMAC-SHA256 signature
     */
    private String computeHmacSha256(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKey);
        byte[] hmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hmac);
    }

    /**
     * Convert byte array to hex string
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
