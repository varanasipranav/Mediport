package com.appointment.webapp.service;

import com.appointment.webapp.model.DoctorSchedule;
import com.appointment.webapp.model.DoctorWeeklyTemplate;
import com.appointment.webapp.model.User;
import com.appointment.webapp.model.enums.ShiftType;
import com.appointment.webapp.repository.DoctorScheduleRepository;
import com.appointment.webapp.repository.DoctorWeeklyTemplateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * Service for managing doctor weekly schedule templates
 * Handles recurring weekly schedules that auto-generate daily schedules
 */
@Service
@Transactional
public class DoctorWeeklyTemplateService {

    private static final Logger logger = LoggerFactory.getLogger(DoctorWeeklyTemplateService.class);

    @Autowired
    private DoctorWeeklyTemplateRepository weeklyTemplateRepository;

    @Autowired
    private DoctorScheduleRepository scheduleRepository;

    /**
     * Create or update weekly template for a specific day
     */
    public DoctorWeeklyTemplate createOrUpdateDayTemplate(
            User doctor,
            DayOfWeek dayOfWeek,
            boolean isActive,
            ShiftType shiftType,
            LocalTime startTime,
            LocalTime endTime,
            Integer slotDurationMinutes,
            Integer maxPatients,
            String createdBy) {

        Integer dayValue = dayOfWeek.getValue(); // 1=Monday, 7=Sunday

        Optional<DoctorWeeklyTemplate> existing =
            weeklyTemplateRepository.findByDoctorAndDayOfWeek(doctor, dayValue);

        DoctorWeeklyTemplate template;
        if (existing.isPresent()) {
            template = existing.get();
            logger.info("Updating weekly template for doctor {} on {}",
                       doctor.getUsername(), dayOfWeek);
        } else {
            template = new DoctorWeeklyTemplate();
            template.setDoctor(doctor);
            template.setDayOfWeek(dayValue);
            template.setCreatedBy(createdBy);
            logger.info("Creating new weekly template for doctor {} on {}",
                       doctor.getUsername(), dayOfWeek);
        }

        template.setIsActive(isActive);
        template.setShiftType(shiftType);
        template.setStartTime(startTime);
        template.setEndTime(endTime);
        template.setSlotDurationMinutes(slotDurationMinutes != null ? slotDurationMinutes : 15);
        template.setMaxPatients(maxPatients);
        template.setUpdatedBy(createdBy);

        return weeklyTemplateRepository.save(template);
    }

    /**
     * Setup default weekly schedule (Monday-Friday, excluding weekends)
     * This is the initialization method for new doctors
     */
    public List<DoctorWeeklyTemplate> setupDefaultWeeklySchedule(
            User doctor,
            ShiftType defaultShift,
            LocalTime morningStart,
            LocalTime morningEnd,
            LocalTime eveningStart,
            LocalTime eveningEnd,
            Integer slotDuration,
            String createdBy) {

        List<DoctorWeeklyTemplate> templates = new ArrayList<>();

        // Monday to Friday - Active
        for (int day = 1; day <= 5; day++) {
            DayOfWeek dayOfWeek = DayOfWeek.of(day);

            LocalTime start, end;
            ShiftType shift = defaultShift;

            if (defaultShift == ShiftType.MORNING) {
                start = morningStart != null ? morningStart : LocalTime.of(9, 0);
                end = morningEnd != null ? morningEnd : LocalTime.of(13, 0);
            } else if (defaultShift == ShiftType.EVENING) {
                start = eveningStart != null ? eveningStart : LocalTime.of(14, 0);
                end = eveningEnd != null ? eveningEnd : LocalTime.of(18, 0);
            } else {
                start = morningStart != null ? morningStart : LocalTime.of(9, 0);
                end = eveningEnd != null ? eveningEnd : LocalTime.of(18, 0);
            }

            DoctorWeeklyTemplate template = createOrUpdateDayTemplate(
                doctor, dayOfWeek, true, shift, start, end,
                slotDuration, null, createdBy
            );
            templates.add(template);
        }

        // Saturday & Sunday - Inactive (day off)
        for (int day = 6; day <= 7; day++) {
            DayOfWeek dayOfWeek = DayOfWeek.of(day);
            DoctorWeeklyTemplate template = createOrUpdateDayTemplate(
                doctor, dayOfWeek, false, null,
                LocalTime.of(9, 0), LocalTime.of(13, 0), // Placeholder times
                slotDuration, null, createdBy
            );
            templates.add(template);
        }

        logger.info("Setup default weekly schedule for doctor {}: Mon-Fri active, Sat-Sun off",
                   doctor.getUsername());
        return templates;
    }

    /**
     * Get all weekly templates for a doctor
     */
    @Transactional(readOnly = true)
    public List<DoctorWeeklyTemplate> getWeeklyTemplates(User doctor) {
        return weeklyTemplateRepository.findByDoctorOrderByDayOfWeek(doctor);
    }

    /**
     * Get template for a specific day
     */
    @Transactional(readOnly = true)
    public Optional<DoctorWeeklyTemplate> getTemplateForDay(User doctor, DayOfWeek dayOfWeek) {
        return weeklyTemplateRepository.findByDoctorAndDayOfWeek(doctor, dayOfWeek.getValue());
    }

    /**
     * Get only active working days
     */
    @Transactional(readOnly = true)
    public List<DoctorWeeklyTemplate> getActiveWorkingDays(User doctor) {
        return weeklyTemplateRepository.findActiveDaysByDoctor(doctor);
    }

    /**
     * Toggle a specific day on/off
     */
    public DoctorWeeklyTemplate toggleDayAvailability(User doctor, DayOfWeek dayOfWeek, String updatedBy) {
        Optional<DoctorWeeklyTemplate> templateOpt =
            weeklyTemplateRepository.findByDoctorAndDayOfWeek(doctor, dayOfWeek.getValue());

        if (templateOpt.isEmpty()) {
            throw new RuntimeException("Weekly template not found for " + dayOfWeek);
        }

        DoctorWeeklyTemplate template = templateOpt.get();
        template.setIsActive(!template.getIsActive());
        template.setUpdatedBy(updatedBy);

        logger.info("Toggled {} availability for doctor {} to {}",
                   dayOfWeek, doctor.getUsername(), template.getIsActive());

        return weeklyTemplateRepository.save(template);
    }

    /**
     * Generate daily schedules from weekly templates for a date range
     * This is the KEY method - it creates actual daily schedules from templates
     */
    public List<DoctorSchedule> generateSchedulesFromTemplates(
            User doctor,
            LocalDate startDate,
            LocalDate endDate,
            String createdBy) {

        List<DoctorSchedule> generatedSchedules = new ArrayList<>();

        // Get all weekly templates for this doctor
        List<DoctorWeeklyTemplate> templates = weeklyTemplateRepository.findByDoctorOrderByDayOfWeek(doctor);

        if (templates.isEmpty()) {
            logger.warn("No weekly templates found for doctor {}. Cannot generate schedules.",
                       doctor.getUsername());
            return generatedSchedules;
        }

        // Convert to map for quick lookup
        Map<Integer, DoctorWeeklyTemplate> templateMap = new HashMap<>();
        for (DoctorWeeklyTemplate template : templates) {
            templateMap.put(template.getDayOfWeek(), template);
        }

        // Iterate through each date in range
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            int dayOfWeek = currentDate.getDayOfWeek().getValue(); // 1=Monday, 7=Sunday

            // Check if we have a template for this day
            DoctorWeeklyTemplate template = templateMap.get(dayOfWeek);

            if (template != null && template.getIsActive()) {
                // Check if a schedule already exists for this date
                boolean scheduleExists = scheduleRepository.existsByDoctorAndScheduleDate(doctor, currentDate);

                if (!scheduleExists) {
                    // Create new schedule from template
                    DoctorSchedule schedule = new DoctorSchedule();
                    schedule.setDoctor(doctor);
                    schedule.setScheduleDate(currentDate);
                    schedule.setShiftType(template.getShiftType());
                    schedule.setStartTime(template.getStartTime());
                    schedule.setEndTime(template.getEndTime());
                    schedule.setSlotDurationMinutes(template.getSlotDurationMinutes());
                    schedule.setMaxPatients(template.getMaxPatients());
                    schedule.setIsActive(true);
                    schedule.setIsTemplateGenerated(true); // Mark as auto-generated
                    schedule.setIsOverride(false);
                    schedule.setCreatedBy(createdBy != null ? createdBy : "system-auto");

                    DoctorSchedule saved = scheduleRepository.save(schedule);
                    generatedSchedules.add(saved);

                    logger.debug("Generated schedule for doctor {} on {} from template",
                               doctor.getUsername(), currentDate);
                }
            } else {
                logger.debug("No active template for doctor {} on {} ({})",
                           doctor.getUsername(), currentDate, currentDate.getDayOfWeek());
            }

            currentDate = currentDate.plusDays(1);
        }

        logger.info("Generated {} schedules for doctor {} from {} to {}",
                   generatedSchedules.size(), doctor.getUsername(), startDate, endDate);

        return generatedSchedules;
    }

    /**
     * Auto-generate next 30 days of schedules for a doctor
     * Can be called periodically (e.g., weekly) to keep schedules up to date
     */
    public List<DoctorSchedule> autoGenerateUpcomingSchedules(User doctor) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(30);

        return generateSchedulesFromTemplates(doctor, today, endDate, "system-auto");
    }

    /**
     * Check if doctor has weekly templates configured
     */
    @Transactional(readOnly = true)
    public boolean hasWeeklyTemplates(User doctor) {
        return weeklyTemplateRepository.existsByDoctor(doctor);
    }

    /**
     * Count active working days
     */
    @Transactional(readOnly = true)
    public Long countActiveWorkingDays(User doctor) {
        return weeklyTemplateRepository.countActiveDays(doctor);
    }

    /**
     * Delete all weekly templates for a doctor (reset schedule)
     */
    public void deleteAllTemplates(User doctor) {
        weeklyTemplateRepository.deleteByDoctor(doctor);
        logger.info("Deleted all weekly templates for doctor {}", doctor.getUsername());
    }

    /**
     * Update shift times for a specific day
     */
    public DoctorWeeklyTemplate updateShiftTimes(
            User doctor,
            DayOfWeek dayOfWeek,
            LocalTime startTime,
            LocalTime endTime,
            String updatedBy) {

        DoctorWeeklyTemplate template = weeklyTemplateRepository
            .findByDoctorAndDayOfWeek(doctor, dayOfWeek.getValue())
            .orElseThrow(() -> new RuntimeException("Template not found for " + dayOfWeek));

        template.setStartTime(startTime);
        template.setEndTime(endTime);
        template.setUpdatedBy(updatedBy);

        logger.info("Updated shift times for doctor {} on {} to {} - {}",
                   doctor.getUsername(), dayOfWeek, startTime, endTime);

        return weeklyTemplateRepository.save(template);
    }

    /**
     * Get weekly schedule summary (for display)
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getWeeklyScheduleSummary(User doctor) {
        List<DoctorWeeklyTemplate> templates = weeklyTemplateRepository.findByDoctorOrderByDayOfWeek(doctor);

        Map<String, Object> summary = new HashMap<>();
        summary.put("doctorName", doctor.getFirstName() + " " + doctor.getLastName());
        summary.put("totalDays", templates.size());
        summary.put("activeDays", templates.stream().filter(DoctorWeeklyTemplate::getIsActive).count());
        summary.put("templates", templates);

        return summary;
    }
}
