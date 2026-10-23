package com.appointment.webapp.tenancy.platform;

import com.appointment.webapp.tenancy.MasterRepository;
import com.appointment.webapp.tenancy.TenantSeeder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/** Creates the first operator account on an empty master database (password from env, or random and logged once). */
@Component
@Order(2)
@ConditionalOnProperty(name = "app.tenancy.enabled", havingValue = "true")
public class PlatformBootstrap implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(PlatformBootstrap.class);

    private final MasterRepository master;
    private final String username;
    private final String configuredPassword;

    public PlatformBootstrap(MasterRepository master,
                             @Value("${app.platform.admin-user:operator}") String username,
                             @Value("${app.platform.admin-password:}") String configuredPassword) {
        this.master = master;
        this.username = username;
        this.configuredPassword = configuredPassword;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (master.countPlatformUsers() > 0) return;
        boolean generated = configuredPassword == null || configuredPassword.isBlank();
        String pw = generated ? TenantSeeder.randomPassword() : configuredPassword;
        master.insertPlatformUser(username, new BCryptPasswordEncoder(11).encode(pw));
        if (generated) {
            log.warn("Platform operator created: username={} generated password={} (shown once - sign in at /platform and change PLATFORM_ADMIN_PASSWORD)", username, pw);
        } else {
            log.info("Platform operator '{}' created (password from PLATFORM_ADMIN_PASSWORD)", username);
        }
    }
}
