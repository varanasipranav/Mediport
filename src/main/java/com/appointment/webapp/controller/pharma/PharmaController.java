package com.appointment.webapp.controller.pharma;

import com.appointment.webapp.model.Prescription;
import com.appointment.webapp.model.User;
import com.appointment.webapp.repository.UserRepository;
import com.appointment.webapp.service.PrescriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for pharmacist's prescription management
 */
@Controller
@RequestMapping("/pharma")
@PreAuthorize("hasRole('PHARMA')")
public class PharmaController {

    private static final Logger logger = LoggerFactory.getLogger(PharmaController.class);

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Show pharma dashboard with today's prescriptions
     */
    @GetMapping("/dashboard")
    public String showDashboard(Authentication authentication, Model model) {
        // Get today's prescriptions (last 30 days)
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        List<Prescription> recentPrescriptions = prescriptionService.getPrescriptionsByDateRange(
            thirtyDaysAgo, LocalDate.now()
        );

        model.addAttribute("prescriptions", recentPrescriptions);
        model.addAttribute("totalCount", recentPrescriptions.size());
        model.addAttribute("today", LocalDate.now());

        return "pharma/dashboard";
    }

    /**
     * View prescription details (medicines only)
     */
    @GetMapping("/prescriptions/view/{prescriptionId}")
    public String viewPrescription(@PathVariable Long prescriptionId, Model model) {
        Prescription prescription = prescriptionService.getPrescriptionById(prescriptionId);

        model.addAttribute("prescription", prescription);

        return "pharma/view-prescription";
    }

    /**
     * Mark prescription as printed
     */
    @PostMapping("/prescriptions/{prescriptionId}/mark-printed")
    public String markAsPrinted(@PathVariable Long prescriptionId,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        try {
            User pharmaUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Pharma user not found"));

            prescriptionService.markAsPrinted(prescriptionId, pharmaUser);
            redirectAttributes.addFlashAttribute("success", "Prescription marked as printed");

            logger.info("Prescription {} marked as printed by {}", prescriptionId, authentication.getName());

        } catch (Exception e) {
            logger.error("Error marking prescription as printed", e);
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/pharma/dashboard";
    }

    /**
     * Search prescriptions
     */
    @GetMapping("/prescriptions/search")
    public String searchPrescriptions(@RequestParam String searchTerm, Model model) {
        List<Prescription> prescriptions = prescriptionService.searchPrescriptions(searchTerm);

        model.addAttribute("prescriptions", prescriptions);
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("totalCount", prescriptions.size());

        return "pharma/search-results";
    }
}
