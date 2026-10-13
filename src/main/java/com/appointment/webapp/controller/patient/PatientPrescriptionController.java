package com.appointment.webapp.controller.patient;

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

import java.util.List;

/**
 * Controller for patient's prescription viewing
 */
@Controller
@RequestMapping("/patient/prescriptions")
@PreAuthorize("hasRole('PATIENT')")
public class PatientPrescriptionController {

    private static final Logger logger = LoggerFactory.getLogger(PatientPrescriptionController.class);

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private com.appointment.webapp.service.RxDocumentService rxDocumentService;

    /**
     * Show patient's prescription history
     */
    @GetMapping
    public String listPrescriptions(Authentication authentication, Model model) {
        User patient = userRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("Patient not found"));

        List<Prescription> prescriptions = prescriptionService.getPatientPrescriptions(patient);

        model.addAttribute("patient", patient);
        model.addAttribute("prescriptions", prescriptions);
        model.addAttribute("totalCount", prescriptions.size());

        return "patient/prescriptions";
    }

    /**
     * View prescription details
     */
    @GetMapping("/view/{prescriptionId}")
    public String viewPrescription(@PathVariable Long prescriptionId,
                                  Authentication authentication,
                                  Model model) {
        User patient = userRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("Patient not found"));

        Prescription prescription = prescriptionService.getPrescriptionById(prescriptionId);

        // Verify this is the patient's prescription
        if (!prescription.getPatient().getId().equals(patient.getId())) {
            throw new RuntimeException("Unauthorized access to prescription");
        }

        rxDocumentService.addDocumentModel(model, prescription);

        return "patient/view-prescription";
    }
}
