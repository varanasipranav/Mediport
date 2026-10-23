package com.appointment.webapp.whatsapp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * WhatsApp Business API Configuration
 * Loads configuration from application.properties
 */
@Configuration
@ConfigurationProperties(prefix = "whatsapp")
public class WhatsAppConfig {

    private Api api = new Api();
    private Webhook webhook = new Webhook();
    private Conversation conversation = new Conversation();

    /**
     * ObjectMapper for JSON serialization/deserialization
     * Configured with Java 8 date/time support
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    /**
     * RestTemplate configured for WhatsApp API calls
     * With custom timeout settings
     */
    @Bean(name = "whatsAppRestTemplate")
    public RestTemplate whatsAppRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        // Set timeouts - increased for better network reliability
        factory.setConnectTimeout(Duration.ofSeconds(30)); // 30 seconds connection timeout
        factory.setReadTimeout(Duration.ofSeconds(30)); // 30 seconds read timeout

        RestTemplate restTemplate = new RestTemplate(factory);

        // Add interceptor for logging and adding auth header
        restTemplate.getInterceptors().add((request, body, execution) -> {
            // Add authorization header to all WhatsApp API requests
            if (request.getHeaders().getFirst("Authorization") == null) {
                request.getHeaders().set("Authorization", "Bearer " + api.accessToken);
            }
            request.getHeaders().set("Content-Type", "application/json");
            return execution.execute(request, body);
        });

        return restTemplate;
    }

    // Getters and Setters
    public Api getApi() {
        return api;
    }

    public void setApi(Api api) {
        this.api = api;
    }

    public Webhook getWebhook() {
        return webhook;
    }

    public void setWebhook(Webhook webhook) {
        this.webhook = webhook;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    // Inner classes for nested properties
    public static class Api {
        private String baseUrl;
        private String phoneNumberId;
        private String accessToken;
        private int maxRequestsPerMinute = 60;

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getPhoneNumberId() {
            return phoneNumberId;
        }

        public void setPhoneNumberId(String phoneNumberId) {
            this.phoneNumberId = phoneNumberId;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public int getMaxRequestsPerMinute() {
            return maxRequestsPerMinute;
        }

        public void setMaxRequestsPerMinute(int maxRequestsPerMinute) {
            this.maxRequestsPerMinute = maxRequestsPerMinute;
        }

        /**
         * Get full WhatsApp API messages endpoint URL
         */
        public String getMessagesEndpoint() {
            return baseUrl + "/" + phoneNumberId + "/messages";
        }
    }

    public static class Webhook {
        private String verifyToken;
        private String appSecret;

        public String getVerifyToken() {
            return verifyToken;
        }

        public void setVerifyToken(String verifyToken) {
            this.verifyToken = verifyToken;
        }

        public String getAppSecret() {
            return appSecret;
        }

        public void setAppSecret(String appSecret) {
            this.appSecret = appSecret;
        }
    }

    public static class Conversation {
        private int timeoutMinutes = 30;

        public int getTimeoutMinutes() {
            return timeoutMinutes;
        }

        public void setTimeoutMinutes(int timeoutMinutes) {
            this.timeoutMinutes = timeoutMinutes;
        }
    }
}
