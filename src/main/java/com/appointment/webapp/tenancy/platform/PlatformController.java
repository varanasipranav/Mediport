package com.appointment.webapp.tenancy.platform;

import com.appointment.webapp.tenancy.MasterRepository;
import com.appointment.webapp.tenancy.TenantInfo;
import com.appointment.webapp.tenancy.TenantProvisioningService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Map;

/** Operator console: review demo requests, onboard clinics, manage them. */
@Controller
@ConditionalOnProperty(name = "app.tenancy.enabled", havingValue = "true")
public class PlatformController {

    private final MasterRepository master;
    private final TenantProvisioningService provisioning;

    public PlatformController(MasterRepository master, TenantProvisioningService provisioning) {
        this.master = master;
        this.provisioning = provisioning;
    }

    @GetMapping("/platform/login")
    public String login() {
        return "platform/login";
    }

    @GetMapping("/platform")
    public String console(Model model) {
        model.addAttribute("requests", master.demoRequests(null));
        model.addAttribute("tenants", master.allTenants());
        model.addAttribute("pageTitle", "Console");
        return "platform/console";
    }

    /** The approve form, prefilled from a demo request (id 0 = a clinic with no request). */
    @GetMapping("/platform/provision")
    public String form(@RequestParam(defaultValue = "0") long requestId, Model model) {
        var req = requestId > 0 ? master.findDemoRequest(requestId).orElse(null) : null;
        model.addAttribute("requestId", requestId);
        model.addAttribute("req", req);
        model.addAttribute("suggestedCode", req != null && req.requestedCode() != null ? req.requestedCode() : suggest(req));
        return "platform/provision";
    }

    private static String signInUrl(HttpServletRequest request) {
        return org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromContextPath(request).path("/login").toUriString();
    }

    private static String suggest(MasterRepository.DemoRequest req) {
        if (req == null) return "";
        String s = req.clinicName().toLowerCase().replaceAll("[^a-z0-9]", "");
        return s.length() > 20 ? s.substring(0, 20) : s;
    }

    @GetMapping("/platform/check-code")
    @ResponseBody
    public Map<String, Object> checkCode(@RequestParam String code) {
        String problem = provisioning.validateCode(code == null ? null : code.trim().toLowerCase());
        return Map.of("ok", problem == null, "message", problem == null ? "Available" : problem);
    }

    @PostMapping("/platform/provision")
    public String provision(@RequestParam(defaultValue = "0") long requestId,
                            @RequestParam String siteId, @RequestParam String displayName,
                            @RequestParam(defaultValue = "") String tagline, @RequestParam(defaultValue = "") String address,
                            @RequestParam(defaultValue = "") String phone, @RequestParam(defaultValue = "") String email,
                            @RequestParam(defaultValue = "false") boolean sampleData,
                            @RequestParam(defaultValue = "") String waPhoneNumberId, @RequestParam(defaultValue = "") String waAccessToken,
                            @RequestParam(defaultValue = "") String waAppSecret,
                            Principal principal, Model model, HttpServletResponse response, HttpServletRequest request) {
        response.setHeader("Cache-Control", "no-store");
        try {
            var result = provisioning.provision(new TenantProvisioningService.Request(siteId, displayName, tagline, address, phone, email,
                    sampleData, waPhoneNumberId, waAccessToken, waAppSecret));
            if (requestId > 0) master.decideDemoRequest(requestId, "APPROVED", result.tenant().siteId(), principal.getName());
            // Rendered straight from this response (no redirect, no session): the password is shown once.
            model.addAttribute("result", result);
            model.addAttribute("signInUrl", signInUrl(request));
            return "platform/credentials";
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("requestId", requestId);
            model.addAttribute("req", requestId > 0 ? master.findDemoRequest(requestId).orElse(null) : null);
            model.addAttribute("suggestedCode", siteId);
            model.addAttribute("form", Map.of("displayName", displayName, "tagline", tagline, "address", address, "phone", phone, "email", email));
            return "platform/provision";
        }
    }

    @PostMapping("/platform/requests/{id}/reject")
    public String reject(@PathVariable long id, @RequestParam(defaultValue = "REJECTED") String as, Principal principal) {
        master.decideDemoRequest(id, "SPAM".equals(as) ? "SPAM" : "REJECTED", null, principal.getName());
        return "redirect:/platform";
    }

    @PostMapping("/platform/tenants/{site}/suspend")
    public String suspend(@PathVariable String site, RedirectAttributes ra) {
        provisioning.suspend(site);
        ra.addFlashAttribute("notice", "Clinic " + site + " suspended - its users can no longer sign in.");
        return "redirect:/platform";
    }

    @PostMapping("/platform/tenants/{site}/activate")
    public String activate(@PathVariable String site, RedirectAttributes ra) {
        provisioning.activate(site);
        ra.addFlashAttribute("notice", "Clinic " + site + " is active again.");
        return "redirect:/platform";
    }

    @PostMapping("/platform/tenants/{site}/retry-migration")
    public String retry(@PathVariable String site, RedirectAttributes ra) {
        try {
            ra.addFlashAttribute("notice", "Clinic " + site + " is at schema version " + provisioning.retryMigration(site) + ".");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", "Migration failed for " + site + ": " + e.getMessage());
        }
        return "redirect:/platform";
    }

    @PostMapping("/platform/tenants/{site}/whatsapp")
    public String whatsapp(@PathVariable String site, @RequestParam(defaultValue = "") String waPhoneNumberId,
                           @RequestParam(defaultValue = "") String waAccessToken, @RequestParam(defaultValue = "") String waAppSecret,
                           RedirectAttributes ra) {
        try {
            provisioning.updateWhatsApp(site, waPhoneNumberId, waAccessToken, waAppSecret);
            ra.addFlashAttribute("notice", "WhatsApp number saved for " + site + ".");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", "Could not save the WhatsApp number (is it already used by another clinic?)");
        }
        return "redirect:/platform";
    }

    @PostMapping("/platform/tenants/{site}/reset-admin")
    public String resetAdmin(@PathVariable String site, Model model, HttpServletResponse response, HttpServletRequest request, RedirectAttributes ra) {
        response.setHeader("Cache-Control", "no-store");
        try {
            String pw = provisioning.resetAdminPassword(site);
            TenantInfo t = master.findTenant(site).orElseThrow();
            model.addAttribute("result", new TenantProvisioningService.Result(t, "admin@" + site, pw, null));
            model.addAttribute("signInUrl", signInUrl(request));
            return "platform/credentials";
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/platform";
        }
    }
}
