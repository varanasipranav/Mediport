package com.appointment.webapp.config;

import com.appointment.webapp.service.DemoDataService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * One-time switch: start with {@code app.seed-demo-doctors=true} to add the sample doctors, then remove the setting.
 * The shared password is printed once in the console.
 */
@Component
@Order(100)
@ConditionalOnProperty(name = "app.seed-demo-doctors", havingValue = "true")
public class DemoDataRunner implements CommandLineRunner {

    private final DemoDataService demo;

    public DemoDataRunner(DemoDataService demo) {
        this.demo = demo;
    }

    @Override
    public void run(String... args) {
        var result = demo.seedDoctors("system");
        if (result.created().isEmpty()) {
            System.out.println("Sample doctors already exist - nothing added.");
        } else {
            System.out.println("Added sample doctors " + result.created() + " - password for all of them: " + result.password()
                + "  (shown once; remove app.seed-demo-doctors now)");
        }
    }
}
