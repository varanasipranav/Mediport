package com.appointment.webapp.controller.patient;

import com.appointment.webapp.model.Appointment;
import com.appointment.webapp.model.User;
import com.appointment.webapp.repository.UserRepository;
import com.appointment.webapp.service.AppointmentService;
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
 * Controller for Patient functions
 * - Book own appointments
 * - View appointment status
 * - See queue position and estimated wait time
 */
@Controller
@RequestMapping("/patient")
@PreAuthorize("hasRole('PATIENT')")
public class PatientController {

    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Show patient dashboard with today's appointment and upcoming appointments
     */
    @GetMapping("/dashboard")
    public String showDashboard(Authentication authentication, Model model) {
        User patient = userRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("Patient not found"));

        // Get upcoming appointments
        List<Appointment> upcomingAppointments = appointmentService.getUpcomingAppointmentsByPatient(patient);

        // Get today's appointment
        Appointment todayAppointment = upcomingAppointments.stream()
            .filter(a -> a.getAppointmentDate().equals(LocalDate.now()))
            .findFirst()
            .orElse(null);

        model.addAttribute("patient", patient);
        model.addAttribute("todayAppointment", todayAppointment);
        model.addAttribute("upcomingAppointments", upcomingAppointments);
        model.addAttribute("today", LocalDate.now());

        // Calculate estimated wait if there's a today's appointment
        if (todayAppointment != null) {
            int estimatedWait = appointmentService.calculateEstimatedWaitMinutes(todayAppointment.getId());
            model.addAttribute("estimatedWaitMinutes", estimatedWait);
        }

        return "patient/dashboard";
    }

    /**
     * Show book appointment page
     */
    @GetMapping("/book-appointment")
    public String showBookAppointmentPage(Model model) {
        // Get all doctors
        List<User> doctors = userRepository.findByRoleName("DOCTOR");
        model.addAttribute("doctors", doctors);
        model.addAttribute("today", LocalDate.now());

        return "patient/book-appointment";
    }

    /**
     * Book appointment
     */
    @PostMapping("/book-appointment")
    public String bookAppointment(@RequestParam Long doctorId,
                                  @RequestParam LocalDate appointmentDate,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes) {
        try {
            User patient = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

            User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

            Appointment appointment = appointmentService.bookAppointment(
                patient, doctor, appointmentDate, authentication.getName()
            );

            redirectAttributes.addFlashAttribute("success",
                "Appointment booked successfully! Queue #" + appointment.getQueueNumber() +
                " - Scheduled at " + appointment.getScheduledTime());

            logger.info("Patient {} booked appointment with doctor {} on {}",
                       patient.getUsername(), doctor.getUsername(), appointmentDate);

            return "redirect:/patient/dashboard";

        } catch (Exception e) {
            logger.error("Error booking appointment", e);
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            return "redirect:/patient/book-appointment";
        }
    }

    /**
     * View all appointments (history)
     */
    @GetMapping("/appointments")
    public String viewAppointments(Authentication authentication, Model model) {
        User patient = userRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("Patient not found"));

        List<Appointment> appointments = appointmentService.getPatientAppointmentHistory(patient);

        model.addAttribute("patient", patient);
        model.addAttribute("appointments", appointments);

        return "patient/appointments";
    }

    /**
     * Cancel appointment
     */
    @PostMapping("/appointment/{id}/cancel")
    public String cancelAppointment(@PathVariable Long id,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        try {
            Appointment owned = appointmentService.getAppointmentById(id);
            if (!owned.getPatient().getUsername().equals(authentication.getName())) {
                throw new org.springframework.security.access.AccessDeniedException("Not your appointment");
            }
            appointmentService.cancelAppointment(id, authentication.getName());
            redirectAttributes.addFlashAttribute("success", "Appointment cancelled successfully");
        } catch (org.springframework.security.access.AccessDeniedException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error cancelling appointment", e);
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/patient/appointments";
    }

    // NOTE: Prescription viewing is handled by PatientPrescriptionController
    // Path: /patient/prescriptions/view/{prescriptionId}
}
