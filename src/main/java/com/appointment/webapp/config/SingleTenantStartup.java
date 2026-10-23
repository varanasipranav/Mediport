package com.appointment.webapp.config;

import com.appointment.webapp.service.DataInitializer;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** Single-clinic mode: make sure roles and privileges exist before the first-admin loader runs. */
@Component
@Order(1)
@ConditionalOnProperty(name = "app.tenancy.enabled", havingValue = "false", matchIfMissing = true)
public class SingleTenantStartup implements ApplicationRunner {

    private final DataInitializer dataInitializer;

    public SingleTenantStartup(DataInitializer dataInitializer) {
        this.dataInitializer = dataInitializer;
    }

    @Override
    public void run(ApplicationArguments args) {
        dataInitializer.init();
    }
}
