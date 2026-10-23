package com.appointment.webapp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** Says out loud, at start-up, what is still set to a development value. Warnings only: nothing here stops the app. */
@Component
@Order(0)
public class StartupChecks implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(StartupChecks.class);

    @Value("${app.public-url:}") private String publicUrl;
    @Value("${app.remember-me.key:}") private String rememberMeKey;
    @Value("${app.rx.signing-key:}") private String rxKey;
    @Value("${spring.datasource.password:}") private String dbPassword;
    @Value("${whatsapp.webhook.app-secret:}") private String appSecret;
    @Value("${whatsapp.webhook.verify-token:}") private String verifyToken;
    @Value("${whatsapp.api.access-token:}") private String waToken;
    @Value("${app.tenancy.enabled:false}") private boolean tenancy;
    @Value("${app.platform.admin-password:}") private String platformPassword;
    @Value("${server.servlet.session.cookie.secure:false}") private boolean secureCookie;
    @Value("${spring.jpa.show-sql:false}") private boolean showSql;

    @Override
    public void run(ApplicationArguments args) {
        if (publicUrl.isBlank() || publicUrl.contains("localhost")) {
            log.warn("CHECK: app.public-url (APP_PUBLIC_URL) is '{}'. Prescription QR codes and WhatsApp links will point there - set your real https address.", publicUrl);
        } else if (!publicUrl.startsWith("https://")) {
            log.warn("CHECK: app.public-url is not https. WhatsApp and browsers expect https in production.");
        }
        if (!secureCookie) {
            log.warn("CHECK: session cookies are not marked Secure (SESSION_COOKIE_SECURE=true once you serve over https).");
        }
        if (rememberMeKey.isBlank()) {
            log.warn("CHECK: REMEMBER_ME_KEY is not set - 'keep me signed in' resets on every restart and does not work across several instances.");
        }
        if (!tenancy && rxKey.isBlank()) {
            log.warn("CHECK: RX_SIGNING_KEY is not set - prescription QR codes stop verifying after a restart.");
        }
        if (dbPassword.isBlank()) {
            log.warn("CHECK: the database password (DB_PASSWORD) is empty.");
        }
        if (appSecret.isBlank()) {
            log.warn("CHECK: WHATSAPP_APP_SECRET is not set - every WhatsApp webhook will be rejected until it is.");
        }
        if (verifyToken.isBlank()) {
            log.warn("CHECK: WHATSAPP_VERIFY_TOKEN is not set - Meta cannot verify the webhook URL.");
        }
        if (tenancy && platformPassword.isBlank()) {
            log.info("Platform operator password is not configured: on a brand-new master database a random one is generated and logged once.");
        }
        if (showSql) {
            log.warn("CHECK: spring.jpa.show-sql is on - SQL (and patient data in it) will reach the logs.");
        }
        if (tenancy) {
            log.info("Multi-clinic mode is ON.");
        }
    }
}
