package com.appointment.webapp.service;

import com.appointment.webapp.model.DoctorSchedule;
import com.appointment.webapp.model.User;
import com.appointment.webapp.model.enums.ShiftType;
import com.appointment.webapp.repository.DoctorScheduleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing doctor schedules and availability
 */
@Service
@Transactional
public class DoctorScheduleService {

    private static final Logger logger = LoggerFactory.getLogger(DoctorScheduleService.class);

    @Autowired
    private DoctorScheduleRepository scheduleRepository;

    /**
     * Create or update a doctor's schedule for a specific date
     */
    public DoctorSchedule createOrUpdateSchedule(User doctor, LocalDate date, ShiftType shiftType,
                                                  LocalTime startTime, LocalTime endTime,
                                                  Integer slotDuration, Integer maxPatients,
                                                  String createdBy) {
        // Check if schedule already exists
        Optional<DoctorSchedule> existing = scheduleRepository.findByDoctorAndScheduleDate(doctor, date);

        DoctorSchedule schedule;
        if (existing.isPresent()) {
            schedule = existing.get();
            logger.info("Updating existing schedule for doctor {} on {}", doctor.getUsername(), date);
        } else {
            schedule = new DoctorSchedule();
            schedule.setDoctor(doctor);
            schedule.setScheduleDate(date);
            schedule.setCreatedBy(createdBy);
            logger.info("Creating new schedule for doctor {} on {}", doctor.getUsername(), date);
        }

        // Set shift type
        schedule.setShiftType(shiftType);

        // Set times based on shift type
        if (shiftType != ShiftType.CUSTOM && shiftType != null) {
            // Use default shift times
            schedule.setStartTime(LocalTime.parse(shiftType.getDefaultStartTime()));
            schedule.setEndTime(LocalTime.parse(shiftType.getDefaultEndTime()));
        } else {
            // Use custom times
            schedule.setStartTime(startTime);
            schedule.setEndTime(endTime);
        }

        // Set slot duration and max patients
        schedule.setSlotDurationMinutes(slotDuration != null ? slotDuration : 15);
        schedule.setMaxPatients(maxPatients);
        schedule.setIsActive(true);
        schedule.setUpdatedBy(createdBy);

        return scheduleRepository.save(schedule);
    }

    /**
     * Get or create today's schedule for a doctor
     */
    public DoctorSchedule getOrCreateTodaySchedule(User doctor, String createdBy) {
        LocalDate today = LocalDate.now();
        Optional<DoctorSchedule> schedule = scheduleRepository.findByDoctorAndScheduleDate(doctor, today);

        if (schedule.isPresent()) {
            return schedule.get();
        }

        // Create default morning schedule
        return createOrUpdateSchedule(doctor, today, ShiftType.MORNING, null, null, 15, null, createdBy);
    }

    /**
     * Get schedule by doctor and date, locking the row. Must be called inside a transaction.
     */
    public Optional<DoctorSchedule> getScheduleByDoctorAndDateForUpdate(User doctor, LocalDate date) {
        return scheduleRepository.findByDoctorAndScheduleDateForUpdate(doctor, date);
    }

    /**
     * Get schedule by doctor and date
     */
    @Transactional(readOnly = true)
    public Optional<DoctorSchedule> getScheduleByDoctorAndDate(User doctor, LocalDate date) {
        return scheduleRepository.findByDoctorAndScheduleDate(doctor, date);
    }

    /**
     * Get active schedule for a doctor on a specific date
     */
    @Transactional(readOnly = true)
    public Optional<DoctorSchedule> getActiveSchedule(User doctor, LocalDate date) {
        return scheduleRepository.findActiveScheduleByDoctorAndDate(doctor, date);
    }

    /**
     * Get all schedules for a doctor
     */
    @Transactional(readOnly = true)
    public List<DoctorSchedule> getDoctorSchedules(User doctor) {
        return scheduleRepository.findByDoctorOrderByScheduleDateDesc(doctor);
    }

    /**
     * Get upcoming schedules for a doctor
     */
    @Transactional(readOnly = true)
    public List<DoctorSchedule> getUpcomingSchedules(User doctor) {
        return scheduleRepository.findUpcomingSchedules(doctor, LocalDate.now());
    }

    /**
     * Get schedules for a doctor within a date range
     */
    @Transactional(readOnly = true)
    public List<DoctorSchedule> getSchedulesInRange(User doctor, LocalDate startDate, LocalDate endDate) {
        return scheduleRepository.findByDoctorAndDateRange(doctor, startDate, endDate);
    }

    /**
     * Add priority work delay to a schedule
     * This delays all upcoming appointments by the specified minutes
     */
    public DoctorSchedule addPriorityWork(Long scheduleId, Integer delayMinutes, String updatedBy) {
        DoctorSchedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new RuntimeException("Schedule not found with id: " + scheduleId));

        int currentDelay = schedule.getPriorityWorkMinutes() != null ? schedule.getPriorityWorkMinutes() : 0;
        schedule.setPriorityWorkMinutes(currentDelay + delayMinutes);
        schedule.setUpdatedBy(updatedBy);

        logger.info("Added {} minutes priority work to schedule {}. Total delay: {} minutes",
                   delayMinutes, scheduleId, schedule.getPriorityWorkMinutes());

        return scheduleRepository.save(schedule);
    }

    /**
     * Deactivate a schedule (soft delete)
     */
    public void deactivateSchedule(Long scheduleId, String updatedBy) {
        DoctorSchedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new RuntimeException("Schedule not found with id: " + scheduleId));

        schedule.setIsActive(false);
        schedule.setUpdatedBy(updatedBy);
        scheduleRepository.save(schedule);

        logger.info("Deactivated schedule {} by {}", scheduleId, updatedBy);
    }

    /**
     * Check if doctor has schedule on a specific date
     */
    @Transactional(readOnly = true)
    public boolean hasScheduleOnDate(User doctor, LocalDate date) {
        return scheduleRepository.existsByDoctorAndScheduleDate(doctor, date);
    }

    /**
     * Get all schedules for a specific date (admin view)
     */
    @Transactional(readOnly = true)
    public List<DoctorSchedule> getSchedulesByDate(LocalDate date) {
        return scheduleRepository.findByScheduleDateOrderByDoctorLastName(date);
    }

    /**
     * Calculate available slots for a schedule
     */
    @Transactional(readOnly = true)
    public int calculateAvailableSlots(Long scheduleId) {
        DoctorSchedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new RuntimeException("Schedule not found"));
        return schedule.calculateTotalSlots();
    }
}
