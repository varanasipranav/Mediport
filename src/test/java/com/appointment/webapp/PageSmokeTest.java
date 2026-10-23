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
@SpringBootTest(properties = "app.tenancy.enabled=false")
@AutoConfigureMockMvc
class PageSmokeTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private com.appointment.webapp.repository.PrescriptionRepository prescriptionRepository;

    @Autowired
    private com.appointment.webapp.service.RxDocumentService rxDocumentService;

    @Test
    @org.springframework.transaction.annotation.Transactional
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

        // Prescription document pages, using whichever prescription exists in the dev database
        var rx = prescriptionRepository.findAll().stream().findFirst().orElse(null);
        if (rx != null) {
            pagesByUser.put(rx.getDoctor().getUsername() + ":DOCTOR", new String[] {"/doctor/prescriptions/view/" + rx.getId(),
                "/doctor/patients/" + rx.getPatient().getId() + "/record"});
            pagesByUser.put(rx.getPatient().getUsername() + ":PATIENT", new String[] {"/patient/prescriptions/view/" + rx.getId(), "/prescriptions/" + rx.getId() + "/pdf"});
            pagesByUser.put("phr01:PHARMA", new String[] {"/dashboard/index", "/pharma/dashboard", "/pharma/prescriptions/view/" + rx.getId(), "/pharma/prescriptions/search?searchTerm=a"});
            pagesByUser.put("anon:PUBLIC", new String[] {"/verify/rx/" + rx.getId() + "/" + rxDocumentService.sign(rx.getId()), "/verify/rx/" + rx.getId() + "/bad"});
        }

        // Inventory pages (pharmacy) - the medicine / dispense pages need data, so add them when it exists
        pagesByUser.put("phr01:PHARMA:inv", new String[] {"/pharma/inventory"});
        pagesByUser.put("admin:ADMIN:inv", new String[] {"/pharma/inventory"});
        if (rx != null) {
            pagesByUser.put("phr01:PHARMA:inv", new String[] {"/pharma/inventory", "/pharma/prescriptions/dispense/" + rx.getId(), "/pharma/dashboard"});
        }

        Path out = Path.of("target", "pages");
        Files.createDirectories(out);
        List<String> failures = new ArrayList<>();

        for (var entry : pagesByUser.entrySet()) {
            String[] parts = entry.getKey().split(":");
            for (String url : entry.getValue()) {
                try {
                    var req = get(url);
                    if (!"PUBLIC".equals(parts[1])) req = req.with(user(parts[0]).roles(parts[1]));
                    MvcResult result = mvc.perform(req).andReturn();
                    int status = result.getResponse().getStatus();
                    String body = result.getResponse().getContentAsString();
                    if (url.endsWith("/pdf")) {
                        byte[] pdf = result.getResponse().getContentAsByteArray();
                        Files.write(out.resolve("rx.pdf"), pdf);
                        if (pdf.length < 1000 || pdf[0] != '%' || pdf[1] != 'P') failures.add(url + " did not return a PDF");
                        continue;
                    }
                    Files.writeString(out.resolve(parts[1] + url.replaceAll("[/?=]", "_") + ".html"), body);
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
