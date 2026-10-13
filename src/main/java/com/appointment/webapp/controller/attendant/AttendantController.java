package com.appointment.webapp.controller.attendant;

import com.appointment.webapp.controller.api.ApiController;
import com.appointment.webapp.model.Appointment;
import com.appointment.webapp.model.User;
import com.appointment.webapp.repository.UserRepository;
import com.appointment.webapp.service.AppointmentService;
import com.appointment.webapp.service.DoctorScheduleService;
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
import java.util.Map;

/**
 * Controller for Attendant/Reception functions
 * - Book appointments for patients
 * - Manage queue (delay, not attended, finish)
 */
@Controller
@RequestMapping("/attendant")
@PreAuthorize("hasRole('ATTENDANT')")
public class AttendantController {

    private static final Logger logger = LoggerFactory.getLogger(AttendantController.class);

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private DoctorScheduleService scheduleService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApiController apiController; // Delegate JSON booking to ApiController

    /**
     * Show patient check-in / booking page
     */
    @GetMapping("/check-in")
    public String showCheckInPage(Model model) {
        // Get all doctors
        List<User> doctors = userRepository.findByRoleName("DOCTOR");

        // Get all patients
        List<User> patients = userRepository.findByRoleName("PATIENT");

        model.addAttribute("doctors", doctors);
        model.addAttribute("patients", patients);
        model.addAttribute("today", LocalDate.now());

        return "attendant/check-in";
    }

    /**
     * Book appointment for a patient
     */
    @PostMapping("/book-appointment")
    public String bookAppointment(@RequestParam Long patientId,
                                  @RequestParam Long doctorId,
                                  @RequestParam LocalDate appointmentDate,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes) {
        try {
            User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

            User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

            Appointment appointment = appointmentService.bookAppointment(
                patient, doctor, appointmentDate, authentication.getName()
            );

            redirectAttributes.addFlashAttribute("success",
                "Appointment booked successfully! Queue #" + appointment.getQueueNumber() +
                " - Scheduled at " + appointment.getScheduledTime());

            logger.info("Attendant {} booked appointment for patient {} with doctor {} on {}",
                       authentication.getName(), patient.getUsername(), doctor.getUsername(), appointmentDate);

        } catch (Exception e) {
            logger.error("Error booking appointment", e);
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/attendant/check-in";
    }

    /**
     * Book appointment via JSON (for AJAX calls with inline patient creation)
     */
    @PostMapping("/book-appointment-json")
    @ResponseBody
    public Map<String, Object> bookAppointmentJson(@RequestBody Map<String, Object> requestData,
                                                              Authentication authentication) {
        // Log incoming request for debugging
        logger.info("Received JSON appointment request: {}", requestData);

        // Delegate to ApiController which contains the JSON booking implementation
        try {
            Map<String, Object> resp = apiController.bookAppointmentJson(requestData, authentication);
            logger.info("Delegated booking response: {}", resp);
            return resp;
        } catch (Exception e) {
            logger.error("Error in JSON appointment booking delegation", e);
            java.util.Map<String, Object> resp = new java.util.HashMap<>();
            resp.put("success", false);
            resp.put("message", "Error: " + e.getMessage());
            return resp;
        }
    }

    /**
     * Show live queue for a specific doctor and date
     */
    @GetMapping("/queue")
    public String showQueue(@RequestParam(required = false) Long doctorId,
                           @RequestParam(required = false) String date,
                           Model model) {
        LocalDate selectedDate = date != null ? LocalDate.parse(date) : LocalDate.now();

        List<User> doctors = userRepository.findByRoleName("DOCTOR");
        model.addAttribute("doctors", doctors);
        model.addAttribute("selectedDate", selectedDate);

        if (doctorId != null) {
            User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

            List<Appointment> appointments = appointmentService.getAppointmentsByDoctorAndDate(
                doctor, selectedDate
            );

            model.addAttribute("selectedDoctor", doctor);
            model.addAttribute("appointments", appointments);

            // Statistics
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
        }

        return "attendant/queue";
    }

    /**
     * Mark appointment as finished (complete)
     */
    @PostMapping("/appointment/{id}/finish")
    public String finishAppointment(@PathVariable Long id,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes) {
        try {
            appointmentService.completeConsultation(id, authentication.getName());
            redirectAttributes.addFlashAttribute("success", "Appointment marked as finished");
        } catch (Exception e) {
            logger.error("Error finishing appointment", e);
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/attendant/queue?doctorId=" + getAppointmentDoctorId(id);
    }

    /**
     * Mark appointment as NOT ATTENDED and move to end of queue (highlighted red)
     */
    @PostMapping("/appointment/{id}/not-attended")
    public String markNotAttended(@PathVariable Long id,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes) {
        try {
            Appointment updated = appointmentService.markNotAttended(id, authentication.getName());
            redirectAttributes.addFlashAttribute("warning",
                "Patient marked as NOT ATTENDED and moved to end of queue (position #" +
                updated.getQueueNumber() + ")");
        } catch (Exception e) {
            logger.error("Error marking not attended", e);
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/attendant/queue?doctorId=" + getAppointmentDoctorId(id);
    }

    /**
     * Delay appointment by 2 positions
     */
    @PostMapping("/appointment/{id}/delay")
    public String delayAppointment(@PathVariable Long id,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes) {
        try {
            Appointment updated = appointmentService.delayAppointment(id, authentication.getName());
            redirectAttributes.addFlashAttribute("info",
                "Appointment delayed to position #" + updated.getQueueNumber());
        } catch (Exception e) {
            logger.error("Error delaying appointment", e);
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/attendant/queue?doctorId=" + getAppointmentDoctorId(id);
    }

    /**
     * Helper to get doctor ID from appointment (for redirect)
     */
    private Long getAppointmentDoctorId(Long appointmentId) {
        try {
            Appointment appointment = appointmentService.getAppointmentById(appointmentId);
            return appointment.getDoctor().getId();
        } catch (Exception e) {
            logger.error("Error getting doctor ID from appointment {}", appointmentId, e);
            return null;
        }
    }
}
