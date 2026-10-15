package com.appointment.webapp.service;

import com.appointment.webapp.model.Appointment;
import com.appointment.webapp.model.DoctorSchedule;
import com.appointment.webapp.model.User;
import com.appointment.webapp.model.enums.AppointmentStatus;
import com.appointment.webapp.repository.AppointmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing appointments and queue operations
 * This is the CORE business logic of the appointment system
 */
@Service
@Transactional
public class AppointmentService {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentService.class);

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorScheduleService scheduleService;

    /**
     * Book a new appointment for a patient
     * This calculates the queue number and scheduled time automatically
     */
    public Appointment bookAppointment(User patient, User doctor, LocalDate date, String createdBy) {
        // Lock the schedule row so concurrent bookings are serialized: the duplicate-patient check,
        // queue-number calculation and capacity check below are then race-free until commit.
        DoctorSchedule schedule = scheduleService.getScheduleByDoctorAndDateForUpdate(doctor, date)
            .orElseThrow(() -> new BookingException("err.no_schedule", "Doctor has no schedule on " + date, date.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))));

        if (!schedule.getIsActive()) {
            throw new BookingException("err.unavailable", "Doctor is not available on " + date, date.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }

        if (date.isBefore(LocalDate.now())) {
            throw new BookingException("err.past", "Cannot book an appointment in the past");
        }

        // Check if patient already has an appointment on this date
        Optional<Appointment> existingAppointment = appointmentRepository.findByPatientAndDate(patient, date);
        if (existingAppointment.isPresent()) {
            throw new BookingException("err.already_booked", "Patient already has an appointment on " + date, date.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }

        // Calculate next queue number
        Integer maxQueue = appointmentRepository.findMaxQueueNumberByScheduleId(schedule.getId());
        int nextQueueNumber = (maxQueue != null ? maxQueue : 0) + 1;

        // Check capacity
        if (schedule.getMaxPatients() != null && nextQueueNumber > schedule.getMaxPatients()) {
            throw new BookingException("err.fully_booked", "Doctor is fully booked for " + date, date.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }

        // The slot must also fit inside the doctor's working hours
        if (!schedule.hasSlotFor(nextQueueNumber)) {
            throw new BookingException("err.fully_booked", "Doctor is fully booked for " + date, date.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }

        // Calculate scheduled time
        LocalTime scheduledTime = schedule.calculateScheduledTime(nextQueueNumber);

        // Create appointment
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setSchedule(schedule);
        appointment.setAppointmentDate(date);
        appointment.setQueueNumber(nextQueueNumber);
        appointment.setScheduledTime(scheduledTime);
        appointment.setStatus(AppointmentStatus.WAITING);
        appointment.setCreatedBy(createdBy);

        Appointment saved = appointmentRepository.save(appointment);

        logger.info("Booked appointment for patient {} with doctor {} on {} - Queue #{}, Time: {}",
                   patient.getUsername(), doctor.getUsername(), date, nextQueueNumber, scheduledTime);

        return saved;
    }

    /**
     * Get all appointments for a doctor on a specific date
     */
    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsByDoctorAndDate(User doctor, LocalDate date) {
        List<Appointment> appointments = appointmentRepository.findByDoctorAndDate(doctor, date);

        // Eagerly load patient and doctor data to avoid lazy loading issues
        appointments.forEach(apt -> {
            if (apt.getPatient() != null) {
                apt.getPatient().getFirstName(); // Touch to load
            }
            if (apt.getDoctor() != null) {
                apt.getDoctor().getFirstName(); // Touch to load
            }
        });

        return appointments;
    }

    /**
     * Get appointment by ID
     */
    @Transactional(readOnly = true)
    public Appointment getAppointmentById(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + appointmentId));
    }

    /**
     * Get today's appointments for a doctor
     */
    @Transactional(readOnly = true)
    public List<Appointment> getTodaysAppointments(User doctor) {
        List<Appointment> appointments = appointmentRepository.findTodaysAppointmentsByDoctor(doctor);

        // Eagerly load patient and doctor data to avoid lazy loading issues
        appointments.forEach(apt -> {
            if (apt.getPatient() != null) {
                apt.getPatient().getFirstName(); // Touch to load
            }
            if (apt.getDoctor() != null) {
                apt.getDoctor().getFirstName(); // Touch to load
            }
        });

        return appointments;
    }

    /**
     * Get all appointments for a schedule, ordered by queue
     */
    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsBySchedule(Long scheduleId) {
        return appointmentRepository.findByScheduleIdOrderByQueueNumber(scheduleId);
    }

    /**
     * Get upcoming appointments for a patient
     */
    @Transactional(readOnly = true)
    public List<Appointment> getUpcomingAppointmentsByPatient(User patient) {
        return appointmentRepository.findUpcomingAppointmentsByPatient(patient);
    }

    /**
     * Get appointment history for a patient
     */
    @Transactional(readOnly = true)
    public List<Appointment> getPatientAppointmentHistory(User patient) {
        return appointmentRepository.findByPatientOrderByAppointmentDateDesc(patient);
    }

    /**
     * Start consultation (change status to IN_CONSULTATION)
     */
    public Appointment startConsultation(Long appointmentId, String updatedBy) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.startConsultation();
        appointment.setUpdatedBy(updatedBy);

        logger.info("Started consultation for appointment {}", appointmentId);
        return appointmentRepository.save(appointment);
    }

    /**
     * Complete consultation (change status to COMPLETED)
     */
    public Appointment completeConsultation(Long appointmentId, String updatedBy) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.complete();
        appointment.setUpdatedBy(updatedBy);

        logger.info("Completed consultation for appointment {}", appointmentId);
        return appointmentRepository.save(appointment);
    }

    /**
     * Mark appointment as NOT_ATTENDED and move to end of queue
     */
    public Appointment markNotAttended(Long appointmentId, String updatedBy) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new RuntimeException("Appointment not found"));

        int currentQueue = appointment.getQueueNumber();
        Long scheduleId = appointment.getSchedule().getId();

        // Get all active appointments in this schedule
        List<Appointment> allAppointments = appointmentRepository.findByScheduleIdOrderByQueueNumber(scheduleId);
        
        // Filter out completed/cancelled appointments
        List<Appointment> activeAppointments = allAppointments.stream()
            .filter(a -> a.getStatus() != AppointmentStatus.COMPLETED && 
                        a.getStatus() != AppointmentStatus.CANCELLED)
            .collect(Collectors.toList());

        // Remove current appointment from list
        activeAppointments.removeIf(a -> a.getId().equals(appointment.getId()));

        // Add it to the end
        activeAppointments.add(appointment);
        
        // Reassign queue numbers safely (two-phase update to avoid unique constraint collisions)
        reassignQueueNumbersSafely(activeAppointments);

        // Mark as not attended and update metadata
        appointment.markNotAttended();
        appointment.setUpdatedBy(updatedBy);
        appointmentRepository.save(appointment);

        // Recalculate times for affected appointments
        recalculateTimesForSchedule(scheduleId);

        logger.info("Marked appointment {} as NOT_ATTENDED and moved to end (queue #{})",
                   appointmentId, appointment.getQueueNumber());

        return appointment;
    }

    /**
     * Delay appointment by 2 positions
     */
    public Appointment delayAppointment(Long appointmentId, String updatedBy) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new RuntimeException("Appointment not found"));

        int currentQueue = appointment.getQueueNumber();
        Long scheduleId = appointment.getSchedule().getId();

        // Get all active appointments in this schedule
        List<Appointment> allAppointments = appointmentRepository.findByScheduleIdOrderByQueueNumber(scheduleId);
        
        // Filter out completed/cancelled appointments
        List<Appointment> activeAppointments = allAppointments.stream()
            .filter(a -> a.getStatus() != AppointmentStatus.COMPLETED && 
                        a.getStatus() != AppointmentStatus.CANCELLED)
            .sorted(Comparator.comparing(Appointment::getQueueNumber))
            .collect(Collectors.toList());

        // Find current appointment index
        int currentIndex = -1;
        for (int i = 0; i < activeAppointments.size(); i++) {
            if (activeAppointments.get(i).getId().equals(appointmentId)) {
                currentIndex = i;
                break;
            }
        }

        if (currentIndex == -1) {
            throw new RuntimeException("Appointment not found in active queue");
        }

        // Calculate new index (move down 2 positions, but not beyond end)
        int newIndex = Math.min(currentIndex + 2, activeAppointments.size() - 1);
        
        if (newIndex > currentIndex) {
            // Remove from current position and insert at new position
            Appointment removed = activeAppointments.remove(currentIndex);
            activeAppointments.add(newIndex, removed);
            
            // Reassign queue numbers safely
            reassignQueueNumbersSafely(activeAppointments);
        }

        // Mark as delayed and update metadata
        appointment.markDelayed();
        appointment.setUpdatedBy(updatedBy);
        appointmentRepository.save(appointment);

        // Recalculate times
        recalculateTimesForSchedule(scheduleId);

        logger.info("Delayed appointment {} from position {} to position {}",
                   appointmentId, currentIndex + 1, newIndex + 1);

        return appointment;
    }

    /**
     * Helper that safely reassigns queue numbers to a list of appointments.
     * Performs a two-phase update to avoid temporary duplicate (schedule_id, queue_number) unique constraint
     */
    private void reassignQueueNumbersSafely(List<Appointment> appointments) {
        if (appointments == null || appointments.isEmpty()) return;

        // Determine schedule id from first appointment
        Long scheduleId = appointments.get(0).getSchedule() != null ? appointments.get(0).getSchedule().getId() : null;

        int n = appointments.size();
        // Compute current max queue in DB for this schedule to choose an offset that won't collide
        Integer currentMaxQueue = null;
        if (scheduleId != null) {
            try {
                currentMaxQueue = appointmentRepository.findMaxQueueNumberByScheduleId(scheduleId);
            } catch (Exception e) {
                logger.warn("Unable to determine current max queue for schedule {}: {}", scheduleId, e.getMessage());
            }
        }

        int base = (currentMaxQueue != null ? currentMaxQueue : 0) + n + 1000; // safe offset

        // Phase 1: assign temporary unique queue numbers (base + index)
        for (int i = 0; i < appointments.size(); i++) {
            Appointment apt = appointments.get(i);
            apt.setQueueNumber(base + i + 1);
        }
        appointmentRepository.saveAll(appointments);

        // Phase 2: assign final sequential queue numbers starting from 1
        for (int i = 0; i < appointments.size(); i++) {
            Appointment apt = appointments.get(i);
            apt.setQueueNumber(i + 1);
        }
        appointmentRepository.saveAll(appointments);
    }

    /**
     * Recalculate scheduled times for all appointments in a schedule
     * Called after queue manipulation or priority work changes
     */
    public void recalculateTimesForSchedule(Long scheduleId) {
        List<Appointment> appointments = appointmentRepository.findByScheduleIdOrderByQueueNumber(scheduleId);

        if (appointments.isEmpty()) {
            return;
        }

        DoctorSchedule schedule = appointments.get(0).getSchedule();

        for (Appointment appointment : appointments) {
            LocalTime newTime = schedule.calculateScheduledTime(appointment.getQueueNumber());
            appointment.setScheduledTime(newTime);
        }

        appointmentRepository.saveAll(appointments);
        logger.info("Recalculated times for {} appointments in schedule {}", appointments.size(), scheduleId);
    }

    /**
     * Calculate estimated wait time in minutes for a patient
     */
    @Transactional(readOnly = true)
    public int calculateEstimatedWaitMinutes(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Count patients ahead who are WAITING or IN_CONSULTATION
        long patientsAhead = appointmentRepository.countByScheduleIdAndQueueNumberLessThanAndStatusIn(
            appointment.getSchedule().getId(),
            appointment.getQueueNumber(),
            List.of(AppointmentStatus.WAITING, AppointmentStatus.IN_CONSULTATION)
        );

        DoctorSchedule schedule = appointment.getSchedule();
        return (int) (patientsAhead * schedule.getSlotDurationMinutes());
    }

    /**
     * Cancel an appointment
     */
    public void cancelAppointment(Long appointmentId, String updatedBy) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (appointment.getStatus() != AppointmentStatus.WAITING && appointment.getStatus() != AppointmentStatus.DELAYED) {
            throw new BookingException("err.only_waiting", "Only appointments that are still waiting can be cancelled");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setUpdatedBy(updatedBy);
        appointmentRepository.save(appointment);

        logger.info("Cancelled appointment {}", appointmentId);
    }

    /**
     * Add notes to an appointment
     */
    public Appointment addNotes(Long appointmentId, String notes, String updatedBy) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setNotes(notes);
        appointment.setUpdatedBy(updatedBy);

        return appointmentRepository.save(appointment);
    }
}
