package com.appointment.webapp.controller.doctor;

import com.appointment.webapp.model.DoctorSchedule;
import com.appointment.webapp.model.User;
import com.appointment.webapp.model.enums.ShiftType;
import com.appointment.webapp.repository.UserRepository;
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
import java.time.LocalTime;
import java.util.List;

/**
 * Controller for doctor schedule management
 */
@Controller
@RequestMapping("/doctor/schedule")
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorScheduleController {

    private static final Logger logger = LoggerFactory.getLogger(DoctorScheduleController.class);

    @Autowired
    private DoctorScheduleService scheduleService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Show doctor's schedule management page
     */
    @GetMapping
    public String showSchedulePage(Authentication authentication, Model model) {
        User doctor = userRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // Get upcoming schedules
        List<DoctorSchedule> upcomingSchedules = scheduleService.getUpcomingSchedules(doctor);

        model.addAttribute("doctor", doctor);
        model.addAttribute("schedules", upcomingSchedules);
        model.addAttribute("shiftTypes", ShiftType.values());
        model.addAttribute("today", LocalDate.now());

        return "doctor/schedule";
    }

    /**
     * Create or update schedule
     */
    @PostMapping("/save")
    public String saveSchedule(@RequestParam LocalDate scheduleDate,
                              @RequestParam(required = false) ShiftType shiftType,
                              @RequestParam(required = false) String startTime,
                              @RequestParam(required = false) String endTime,
                              @RequestParam(defaultValue = "15") Integer slotDuration,
                              @RequestParam(required = false) Integer maxPatients,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        try {
            User doctor = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

            LocalTime start = null;
            LocalTime end = null;

            if (shiftType == ShiftType.CUSTOM) {
                if (startTime == null || endTime == null) {
                    redirectAttributes.addFlashAttribute("error", "Please provide start and end time for custom shift");
                    return "redirect:/doctor/schedule";
                }
                start = LocalTime.parse(startTime);
                end = LocalTime.parse(endTime);
            }

            DoctorSchedule schedule = scheduleService.createOrUpdateSchedule(
                doctor, scheduleDate, shiftType, start, end, slotDuration, maxPatients, authentication.getName()
            );

            redirectAttributes.addFlashAttribute("success",
                "Schedule saved successfully for " + scheduleDate);
            logger.info("Doctor {} saved schedule for {}", doctor.getUsername(), scheduleDate);

        } catch (Exception e) {
            logger.error("Error saving schedule", e);
            redirectAttributes.addFlashAttribute("error", "Error saving schedule: " + e.getMessage());
        }

        return "redirect:/doctor/schedule";
    }

    /**
     * Add priority work delay
     */
    @PostMapping("/add-priority-work")
    public String addPriorityWork(@RequestParam Long scheduleId,
                                 @RequestParam Integer delayMinutes,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        try {
            scheduleService.addPriorityWork(scheduleId, delayMinutes, authentication.getName());
            redirectAttributes.addFlashAttribute("success",
                "Added " + delayMinutes + " minutes priority work delay");
            logger.info("Added {} minutes priority work to schedule {}", delayMinutes, scheduleId);
        } catch (Exception e) {
            logger.error("Error adding priority work", e);
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/doctor/schedule";
    }

    /**
     * Deactivate a schedule
     */
    @PostMapping("/deactivate/{scheduleId}")
    public String deactivateSchedule(@PathVariable Long scheduleId,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes) {
        try {
            scheduleService.deactivateSchedule(scheduleId, authentication.getName());
            redirectAttributes.addFlashAttribute("success", "Schedule deactivated successfully");
        } catch (Exception e) {
            logger.error("Error deactivating schedule", e);
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/doctor/schedule";
    }
}
