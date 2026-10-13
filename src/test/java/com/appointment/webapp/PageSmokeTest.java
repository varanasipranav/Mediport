package com.appointment.webapp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Renders every page as the matching role (read-only, uses existing users in the dev database)
 * and fails if any page returns an error status. Rendered HTML is written to target/pages for inspection.
 */
@SpringBootTest
@AutoConfigureMockMvc
class PageSmokeTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void everyPageRenders() throws Exception {
        Map<String, String[]> pagesByUser = new LinkedHashMap<>();
        pagesByUser.put("admin:ADMIN", new String[] {
            "/dashboard/index", "/admin/users/list", "/admin/users/create-doctor", "/admin/users/create-patient",
            "/admin/users/create-attendant", "/admin/users/create-pharma"});
        pagesByUser.put("DOC12:DOCTOR", new String[] {
            "/dashboard/index", "/doctor/appointments/today", "/doctor/schedule", "/doctor/weekly-schedule",
            "/doctor/prescriptions/my-prescriptions"});
        pagesByUser.put("att01:ATTENDANT", new String[] {
            "/dashboard/index", "/attendant/check-in", "/attendant/queue"});
        pagesByUser.put("pranav:PATIENT", new String[] {
            "/dashboard/index", "/patient/dashboard", "/patient/book-appointment", "/patient/appointments",
            "/patient/prescriptions"});
        pagesByUser.put("phr01:PHARMA", new String[] {
            "/dashboard/index", "/pharma/dashboard"});

        Path out = Path.of("target", "pages");
        Files.createDirectories(out);
        List<String> failures = new ArrayList<>();

        for (var entry : pagesByUser.entrySet()) {
            String[] parts = entry.getKey().split(":");
            for (String url : entry.getValue()) {
                try {
                    MvcResult result = mvc.perform(get(url).with(user(parts[0]).roles(parts[1]))).andReturn();
                    int status = result.getResponse().getStatus();
                    String body = result.getResponse().getContentAsString();
                    Files.writeString(out.resolve(parts[1] + url.replace('/', '_') + ".html"), body);
                    // 3xx = legitimate redirect (e.g. /dashboard/index -> role page)
                    if (status >= 400) {
                        failures.add(parts[1] + " " + url + " -> " + status);
                    }
                } catch (Exception e) {
                    Throwable root = e;
                    while (root.getCause() != null) root = root.getCause();
                    failures.add(parts[1] + " " + url + " -> EXCEPTION " + root.getMessage());
                }
            }
        }

        if (!failures.isEmpty()) {
            throw new AssertionError("Pages failed:\n" + String.join("\n", failures));
        }
    }
}
