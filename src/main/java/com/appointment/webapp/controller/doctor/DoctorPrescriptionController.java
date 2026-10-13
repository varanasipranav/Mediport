package com.appointment.webapp.controller.doctor;

import com.appointment.webapp.model.Appointment;
import com.appointment.webapp.model.Prescription;
import com.appointment.webapp.model.User;
import com.appointment.webapp.repository.UserRepository;
import com.appointment.webapp.service.AppointmentService;
import com.appointment.webapp.service.PrescriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controller for doctor's prescription management
 */
@Controller
@RequestMapping("/doctor/prescriptions")
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorPrescriptionController {

    private static final Logger logger = LoggerFactory.getLogger(DoctorPrescriptionController.class);

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private com.appointment.webapp.service.RxDocumentService rxDocumentService;

    /**
     * Show prescription form for an appointment
     */
    @GetMapping("/create/{appointmentId}")
    public String showCreatePrescriptionForm(@PathVariable Long appointmentId,
                                            Authentication authentication,
                                            Model model) {
        User doctor = userRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Appointment appointment = appointmentService.getAppointmentById(appointmentId);

        // Verify this is the doctor's appointment
        if (!appointment.getDoctor().getId().equals(doctor.getId())) {
            throw new RuntimeException("Unauthorized access to appointment");
        }

        // Check if prescription already exists
        Optional<Prescription> existingPrescription = prescriptionService.getPrescriptionByAppointment(appointmentId);

        model.addAttribute("appointment", appointment);
        model.addAttribute("prescription", existingPrescription.orElse(new Prescription()));
        model.addAttribute("isEdit", existingPrescription.isPresent());

        return "doctor/create-prescription";
    }

    /**
     * Create or update prescription
     */
    @PostMapping("/save")
    public String savePrescription(@RequestParam Long appointmentId,
                                  @RequestParam(required = false) Long prescriptionId,
                                  @RequestParam String observations,
                                  @RequestParam String medicines,
                                  @RequestParam(required = false) String tests,
                                  @RequestParam(required = false) String additionalNotes,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate nextVisitDate,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes) {
        try {
            if (prescriptionId != null) {
                // Update existing prescription
                prescriptionService.updatePrescription(prescriptionId, observations, medicines,
                    tests, additionalNotes, nextVisitDate);
                redirectAttributes.addFlashAttribute("success", "Prescription updated successfully");
            } else {
                // Create new prescription
                prescriptionService.createPrescription(appointmentId, observations, medicines,
                    tests, additionalNotes, nextVisitDate);
                redirectAttributes.addFlashAttribute("success", "Prescription created successfully");
            }

            logger.info("Prescription saved by {}", authentication.getName());

        } catch (Exception e) {
            logger.error("Error saving prescription", e);
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            return "redirect:/doctor/prescriptions/create/" + appointmentId;
        }

        return "redirect:/doctor/appointments/today";
    }

    /**
     * View prescription details
     */
    @GetMapping("/view/{prescriptionId}")
    public String viewPrescription(@PathVariable Long prescriptionId,
                                  Authentication authentication,
                                  Model model) {
        User doctor = userRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Prescription prescription = prescriptionService.getPrescriptionById(prescriptionId);

        // Verify this is the doctor's prescription
        if (!prescription.getDoctor().getId().equals(doctor.getId())) {
            throw new RuntimeException("Unauthorized access to prescription");
        }

        rxDocumentService.addDocumentModel(model, prescription);

        return "doctor/view-prescription";
    }

    /**
     * List all prescriptions written by this doctor
     */
    @GetMapping("/my-prescriptions")
    public String listMyPrescriptions(Authentication authentication, Model model) {
        User doctor = userRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("Doctor not found"));

        List<Prescription> prescriptions = prescriptionService.getDoctorPrescriptions(doctor);

        model.addAttribute("doctor", doctor);
        model.addAttribute("prescriptions", prescriptions);
        model.addAttribute("totalCount", prescriptions.size());

        return "doctor/my-prescriptions";
    }
}
