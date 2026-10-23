package com.appointment.webapp.tenancy.platform;

import com.appointment.webapp.tenancy.MasterRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

/** The public landing page and the "request a demo" form. */
@Controller
@ConditionalOnProperty(name = "app.tenancy.enabled", havingValue = "true")
public class PublicSiteController {

    private static final Logger log = LoggerFactory.getLogger(PublicSiteController.class);
    private static final int MAX_PER_HOUR = 5;

    public static class DemoForm {
        @NotBlank @Size(max = 150) private String clinicName;
        @NotBlank @Size(max = 100) private String contactName;
        @NotBlank @Email @Size(max = 150) private String email;
        @NotBlank @Size(min = 7, max = 40) private String phone;
        @Size(max = 80) private String city;
        @Size(max = 20) private String doctorsCount;
        @Size(max = 1000) private String message;
        /** Honeypot: hidden from people, filled by bots. */
        private String website;
        /** When the form was served (epoch millis); real people take more than a few seconds. */
        private long shownAt;

        public String getClinicName() { return clinicName; }
        public void setClinicName(String v) { clinicName = v; }
        public String getContactName() { return contactName; }
        public void setContactName(String v) { contactName = v; }
        public String getEmail() { return email; }
        public void setEmail(String v) { email = v; }
        public String getPhone() { return phone; }
        public void setPhone(String v) { phone = v; }
        public String getCity() { return city; }
        public void setCity(String v) { city = v; }
        public String getDoctorsCount() { return doctorsCount; }
        public void setDoctorsCount(String v) { doctorsCount = v; }
        public String getMessage() { return message; }
        public void setMessage(String v) { message = v; }
        public String getWebsite() { return website; }
        public void setWebsite(String v) { website = v; }
        public long getShownAt() { return shownAt; }
        public void setShownAt(long v) { shownAt = v; }
    }

    private record Hits(long windowStart, int count) {
    }

    private final MasterRepository master;
    private final boolean trustForwarded;
    private final ConcurrentHashMap<String, Hits> hits = new ConcurrentHashMap<>();

    public PublicSiteController(MasterRepository master, @Value("${server.forward-headers-strategy:none}") String forwardStrategy) {
        this.master = master;
        this.trustForwarded = !"none".equalsIgnoreCase(forwardStrategy);
    }

    @GetMapping("/site")
    public String landing(Model model) {
        DemoForm form = new DemoForm();
        form.setShownAt(System.currentTimeMillis());
        model.addAttribute("form", form);
        return "site/landing";
    }

    public record Slide(int n, String chapter, String title, String blurb) {
        public String file() {
            return String.format("slide-%02d.jpg", n);
        }
    }

    private static final java.util.List<Slide> TOUR = java.util.List.of(
            new Slide(1, "Welcome", "Run your clinic. Let WhatsApp handle the front desk.", "Clinic management software with a WhatsApp patient assistant."),
            new Slide(2, "The problem", "Front desks are stretched thin", "Phone calls to book, crowded waiting rooms, \"When is my turn?\", lost paper prescriptions, stock expiring unnoticed."),
            new Slide(3, "Patients", "Patients book on WhatsApp", "Name, doctor, day and confirmation, all inside one chat. No app to install."),
            new Slide(4, "Patients", "Speaks their language", "English, Hindi, Telugu and Tamil. Patients choose their language right in the chat."),
            new Slide(5, "Patients", "Manage bookings in chat", "Queue status, my appointments, reschedule and cancel, all from one menu. Queue updates arrive in the same chat."),
            new Slide(6, "Patients", "Families on one number", "A parent books for the children from one phone, then chooses who each appointment is for."),
            new Slide(7, "Doctors", "Smart scheduling", "Weekly templates, automatic queue numbers, and no double-booking."),
            new Slide(8, "Doctors", "Live queue for doctors", "Today's patients, in order, on one screen: waiting, in consultation and completed at a glance."),
            new Slide(9, "Front desk", "A calmer front desk", "Check patients in and see the whole queue in one place."),
            new Slide(10, "Doctors", "Prescriptions and medical record", "Printable A4 with a QR code, PDF, and allergies and history visible to any doctor the patient sees."),
            new Slide(11, "Pharmacy", "Pharmacy built in", "Batches and expiry, earliest-expiry-first dispensing, and low-stock alerts."),
            new Slide(12, "Portals", "One portal, every role", "Admin, doctor, patient, front desk and pharmacist each get their own dashboard."),
            new Slide(13, "Platform", "Many clinics, one platform", "Each clinic has its own private database. Approve a demo request and the new clinic is ready in minutes."),
            new Slide(14, "Trust", "Private and secure", "Role-based access, signed WhatsApp messages, an access log on medical records, and separate data per clinic."),
            new Slide(15, "Get started", "Live in three simple steps", "Request a demo, we set up your clinic, then add your doctors and connect your WhatsApp number."));

    @GetMapping("/tour")
    public String tour(Model model) {
        model.addAttribute("slides", TOUR);
        return "site/tour";
    }

    @GetMapping("/site/thanks")
    public String thanks() {
        return "site/thanks";
    }

    @PostMapping("/site/demo")
    public String request(@Validated @ModelAttribute("form") DemoForm form, BindingResult result, HttpServletRequest request, Model model) {
        if (form.getWebsite() != null && !form.getWebsite().isBlank()) {
            return "redirect:/site/thanks"; // bot: pretend it worked, save nothing
        }
        long age = System.currentTimeMillis() - form.getShownAt();
        if (age < 3000 || age > 24L * 3600 * 1000) {
            result.reject("timing", "Please wait a moment and submit again.");
        }
        String ip = request.getRemoteAddr();
        if (tooMany(ip)) {
            result.reject("rate", "Too many requests from your network. Please try again later.");
        }
        if (result.hasErrors()) {
            form.setShownAt(System.currentTimeMillis() - 5000);
            return "site/landing";
        }
        master.insertDemoRequest(new MasterRepository.DemoRequest(0, form.getClinicName().trim(), form.getContactName().trim(),
                form.getEmail().trim(), form.getPhone().trim(), form.getCity(), form.getDoctorsCount(), null, form.getMessage(), "NEW", null,
                LocalDateTime.now()), ip);
        log.info("New demo request from '{}'", form.getClinicName());
        return "redirect:/site/thanks";
    }

    private boolean tooMany(String ip) {
        long now = System.currentTimeMillis();
        if (hits.size() > 5000) hits.values().removeIf(h -> now - h.windowStart > 3_600_000L);
        Hits h = hits.merge(ip, new Hits(now, 1), (old, one) -> now - old.windowStart > 3_600_000L ? one : new Hits(old.windowStart, old.count + 1));
        return h.count > MAX_PER_HOUR;
    }
}
