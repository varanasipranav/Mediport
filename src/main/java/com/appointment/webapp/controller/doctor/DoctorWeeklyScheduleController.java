package com.appointment.webapp.controller.doctor;

import com.appointment.webapp.model.DoctorWeeklyTemplate;
import com.appointment.webapp.model.User;
import com.appointment.webapp.model.enums.ShiftType;
import com.appointment.webapp.repository.UserRepository;
import com.appointment.webapp.service.DoctorWeeklyTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * Controller for managing doctor weekly schedule templates
 * Allows doctors to set recurring weekly schedules
 */
@Controller
@RequestMapping("/doctor/weekly-schedule")
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorWeeklyScheduleController {

    private static final Logger logger = LoggerFactory.getLogger(DoctorWeeklyScheduleController.class);

    @Autowired
    private DoctorWeeklyTemplateService weeklyTemplateService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Show weekly schedule template management page
     */
    @GetMapping
    public String showWeeklySchedule(Authentication authentication, Model model) {
        User doctor = userRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // Get existing weekly templates
        List<DoctorWeeklyTemplate> templates = weeklyTemplateService.getWeeklyTemplates(doctor);

        // Get summary
        Map<String, Object> summary = weeklyTemplateService.getWeeklyScheduleSummary(doctor);

        model.addAttribute("doctor", doctor);
        model.addAttribute("templates", templates);
        model.addAttribute("summary", summary);
        model.addAttribute("shiftTypes", ShiftType.values());
        model.addAttribute("daysOfWeek", DayOfWeek.values());
        model.addAttribute("hasTemplates", !templates.isEmpty());

        return "doctor/weekly-schedule";
    }

    /**
     * Initialize default weekly schedule (Monday-Friday)
     */
    @PostMapping("/initialize")
    public String initializeDefaultSchedule(
            @RequestParam(required = false) ShiftType defaultShift,
            @RequestParam(required = false) String morningStart,
            @RequestParam(required = false) String morningEnd,
            @RequestParam(required = false) String eveningStart,
            @RequestParam(required = false) String eveningEnd,
            @RequestParam(defaultValue = "15") Integer slotDuration,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            User doctor = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

            // Parse times
            LocalTime mStart = morningStart != null && !morningStart.isEmpty()
                ? LocalTime.parse(morningStart) : LocalTime.of(9, 0);
            LocalTime mEnd = morningEnd != null && !morningEnd.isEmpty()
                ? LocalTime.parse(morningEnd) : LocalTime.of(13, 0);
            LocalTime eStart = eveningStart != null && !eveningStart.isEmpty()
                ? LocalTime.parse(eveningStart) : LocalTime.of(14, 0);
            LocalTime eEnd = eveningEnd != null && !eveningEnd.isEmpty()
                ? LocalTime.parse(eveningEnd) : LocalTime.of(18, 0);

            ShiftType shift = defaultShift != null ? defaultShift : ShiftType.MORNING;

            List<DoctorWeeklyTemplate> templates = weeklyTemplateService.setupDefaultWeeklySchedule(
                doctor, shift, mStart, mEnd, eStart, eEnd, slotDuration, authentication.getName()
            );

            redirectAttributes.addFlashAttribute("success",
                "Default weekly schedule initialized! " + templates.size() + " days configured.");

            logger.info("Doctor {} initialized default weekly schedule", doctor.getUsername());

        } catch (Exception e) {
            logger.error("Error initializing weekly schedule", e);
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/doctor/weekly-schedule";
    }

    /**
     * Update a specific day's template
     */
    @PostMapping("/update-day")
    public String updateDayTemplate(
            @RequestParam String dayOfWeek,
            @RequestParam boolean isActive,
            @RequestParam(required = false) ShiftType shiftType,
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam(defaultValue = "15") Integer slotDuration,
            @RequestParam(required = false) Integer maxPatients,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            User doctor = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

            DayOfWeek day = DayOfWeek.valueOf(dayOfWeek.toUpperCase());
            LocalTime start = LocalTime.parse(startTime);
            LocalTime end = LocalTime.parse(endTime);

            DoctorWeeklyTemplate template = weeklyTemplateService.createOrUpdateDayTemplate(
                doctor, day, isActive, shiftType, start, end, slotDuration, maxPatients,
                authentication.getName()
            );

            redirectAttributes.addFlashAttribute("success",
                "Updated " + day + " schedule: " +
                (isActive ? start + " - " + end : "Day Off"));

            logger.info("Doctor {} updated {} schedule", doctor.getUsername(), day);

        } catch (Exception e) {
            logger.error("Error updating day template", e);
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/doctor/weekly-schedule";
    }

    /**
     * Toggle a specific day on/off
     */
    @PostMapping("/toggle-day/{dayOfWeek}")
    public String toggleDay(
            @PathVariable String dayOfWeek,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            User doctor = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

            DayOfWeek day = DayOfWeek.valueOf(dayOfWeek.toUpperCase());
            DoctorWeeklyTemplate template = weeklyTemplateService.toggleDayAvailability(
                doctor, day, authentication.getName()
            );

            redirectAttributes.addFlashAttribute("success",
                day + " is now " + (template.getIsActive() ? "active" : "inactive"));

            logger.info("Doctor {} toggled {} to {}",
                       doctor.getUsername(), day, template.getIsActive());

        } catch (Exception e) {
            logger.error("Error toggling day", e);
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/doctor/weekly-schedule";
    }

    /**
     * Generate daily schedules from templates for next 30 days
     */
    @PostMapping("/generate-schedules")
    public String generateSchedules(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            User doctor = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

            LocalDate start = startDate != null && !startDate.isEmpty()
                ? LocalDate.parse(startDate) : LocalDate.now();
            LocalDate end = endDate != null && !endDate.isEmpty()
                ? LocalDate.parse(endDate) : LocalDate.now().plusDays(30);

            var schedules = weeklyTemplateService.generateSchedulesFromTemplates(
                doctor, start, end, authentication.getName()
            );

            redirectAttributes.addFlashAttribute("success",
                "Generated " + schedules.size() + " daily schedules from " + start + " to " + end);

            logger.info("Doctor {} generated {} schedules", doctor.getUsername(), schedules.size());

        } catch (Exception e) {
            logger.error("Error generating schedules", e);
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/doctor/weekly-schedule";
    }

    /**
     * Reset all weekly templates
     */
    @PostMapping("/reset")
    public String resetTemplates(
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            User doctor = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

            weeklyTemplateService.deleteAllTemplates(doctor);

            redirectAttributes.addFlashAttribute("success",
                "Weekly schedule templates have been reset. You can now set up a new schedule.");

            logger.info("Doctor {} reset all weekly templates", doctor.getUsername());

        } catch (Exception e) {
            logger.error("Error resetting templates", e);
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/doctor/weekly-schedule";
    }
}
