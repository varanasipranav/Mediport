package com.appointment.webapp.controller.doctor;

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
 * Controller for doctor's appointment management
 */
@Controller
@RequestMapping("/doctor/appointments")
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorAppointmentController {

    private static final Logger logger = LoggerFactory.getLogger(DoctorAppointmentController.class);

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Show today's appointments (the queue)
     */
    @GetMapping("/today")
    public String showTodaysAppointments(Authentication authentication, Model model) {
        User doctor = userRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("Doctor not found"));

        List<Appointment> appointments = appointmentService.getTodaysAppointments(doctor);

        model.addAttribute("doctor", doctor);
        model.addAttribute("appointments", appointments);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("totalPatients", appointments.size());

        // Count by status
        long waiting = appointments.stream()
            .filter(a -> a.getStatus().name().equals("WAITING"))
            .count();
        long inConsultation = appointments.stream()
            .filter(a -> a.getStatus().name().equals("IN_CONSULTATION"))
            .count();
        long completed = appointments.stream()
            .filter(a -> a.getStatus().name().equals("COMPLETED"))
            .count();

        model.addAttribute("waitingCount", waiting);
        model.addAttribute("inConsultationCount", inConsultation);
        model.addAttribute("completedCount", completed);

        return "doctor/appointments-today";
    }

    /**
     * Show appointments for a specific date
     */
    @GetMapping("/date/{date}")
    public String showAppointmentsByDate(@PathVariable String date,
                                        Authentication authentication,
                                        Model model) {
        User doctor = userRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("Doctor not found"));

        LocalDate appointmentDate = LocalDate.parse(date);
        List<Appointment> appointments = appointmentService.getAppointmentsByDoctorAndDate(doctor, appointmentDate);

        model.addAttribute("doctor", doctor);
        model.addAttribute("appointments", appointments);
        model.addAttribute("appointmentDate", appointmentDate);

        return "doctor/appointments-today";
    }

    /**
     * Start consultation
     */
    @PostMapping("/{id}/start")
    public String startConsultation(@PathVariable Long id,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        try {
            appointmentService.startConsultation(id, authentication.getName());
            redirectAttributes.addFlashAttribute("success", "Consultation started");
            logger.info("Started consultation for appointment {}", id);
        } catch (Exception e) {
            logger.error("Error starting consultation", e);
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/doctor/appointments/today";
    }

    /**
     * Complete consultation
     */
    @PostMapping("/{id}/complete")
    public String completeConsultation(@PathVariable Long id,
                                      Authentication authentication,
                                      RedirectAttributes redirectAttributes) {
        try {
            appointmentService.completeConsultation(id, authentication.getName());
            redirectAttributes.addFlashAttribute("success", "Consultation completed");
            logger.info("Completed consultation for appointment {}", id);
        } catch (Exception e) {
            logger.error("Error completing consultation", e);
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/doctor/appointments/today";
    }

    /**
     * Add notes to appointment
     */
    @PostMapping("/{id}/notes")
    public String addNotes(@PathVariable Long id,
                          @RequestParam String notes,
                          Authentication authentication,
                          RedirectAttributes redirectAttributes) {
        try {
            appointmentService.addNotes(id, notes, authentication.getName());
            redirectAttributes.addFlashAttribute("success", "Notes added successfully");
        } catch (Exception e) {
            logger.error("Error adding notes", e);
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/doctor/appointments/today";
    }
}
