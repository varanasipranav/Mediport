package com.appointment.webapp.whatsapp.service.appointment;

import com.appointment.webapp.model.Appointment;
import com.appointment.webapp.model.User;
import com.appointment.webapp.model.enums.AppointmentStatus;
import com.appointment.webapp.repository.AppointmentRepository;
import com.appointment.webapp.service.AppointmentService;
import com.appointment.webapp.service.BookingException;
import com.appointment.webapp.whatsapp.config.ClinicClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * WhatsApp Appointment Management Service
 * Handles view, cancel, reschedule operations
 */
@Service
@Transactional
public class WhatsAppAppointmentManagementService {

    private static final Logger logger = LoggerFactory.getLogger(WhatsAppAppointmentManagementService.class);

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private ClinicClock clock;

    /**
     * Get upcoming appointments for a patient (doctor and schedule already loaded)
     */
    @Transactional(readOnly = true)
    public List<Appointment> getUpcomingAppointments(User patient) {
        logger.info("Fetching upcoming appointments for patient: {}", patient.getId());
        List<Appointment> appointments = appointmentRepository.findUpcomingWithDetails(patient, clock.today());
        logger.info("Found {} upcoming appointments", appointments.size());
        return appointments;
    }

    /**
     * Cancel an appointment
     */
    public boolean cancelAppointment(Long appointmentId, User patient) {
        logger.info("Cancelling appointment {} for patient {}", appointmentId, patient.getId());

        try {
            Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

            // Verify ownership
            if (!appointment.getPatient().getId().equals(patient.getId())) {
                logger.error("Patient {} tried to cancel appointment {} belonging to another patient",
                           patient.getId(), appointmentId);
                throw new BookingException("You can only cancel your own appointments");
            }

            // Check if already cancelled
            if (appointment.getStatus() == com.appointment.webapp.model.enums.AppointmentStatus.CANCELLED) {
                logger.warn("Appointment {} is already cancelled", appointmentId);
                return false;
            }

            // Check if appointment is in the past
            LocalDateTime appointmentDateTime = LocalDateTime.of(
                appointment.getAppointmentDate(),
                appointment.getScheduledTime()
            );

            if (appointmentDateTime.isBefore(clock.now().plusHours(1))) {
                logger.warn("Cannot cancel appointment {} - too close to start time", appointmentId);
                throw new BookingException("Appointments cannot be cancelled less than 1 hour before the scheduled time");
            }

            // Cancel the appointment
            appointment.setStatus(com.appointment.webapp.model.enums.AppointmentStatus.CANCELLED);
            appointment.setUpdatedAt(LocalDateTime.now());
            appointment.setUpdatedBy("whatsapp-bot");

            appointmentRepository.save(appointment);

            logger.info("✅ Appointment {} cancelled successfully", appointmentId);
            return true;

        } catch (Exception e) {
            logger.error("Error cancelling appointment {}", appointmentId, e);
            throw e;
        }
    }

    /**
     * Load an appointment and make sure it belongs to this patient
     */
    @Transactional(readOnly = true)
    public Appointment getOwnedAppointment(Long appointmentId, User patient) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new BookingException("Appointment not found"));
        if (patient == null || !appointment.getPatient().getId().equals(patient.getId())) {
            throw new BookingException("Appointment not found");
        }
        appointment.getDoctor().getFirstName(); // load for use outside the transaction
        return appointment;
    }

    /**
     * Check if appointment can be cancelled
     */
    public boolean canCancelAppointment(Appointment appointment) {
        // Cannot cancel if already cancelled or completed
        if (appointment.getStatus() == AppointmentStatus.CANCELLED ||
            appointment.getStatus() == AppointmentStatus.COMPLETED ||
            appointment.getStatus() == AppointmentStatus.IN_CONSULTATION ||
            appointment.getStatus() == AppointmentStatus.NOT_ATTENDED) {
            return false;
        }

        if (appointment.getScheduledTime() == null) {
            return false;
        }

        // Cannot cancel if less than 1 hour before appointment
        LocalDateTime appointmentDateTime = LocalDateTime.of(
            appointment.getAppointmentDate(),
            appointment.getScheduledTime()
        );

        return appointmentDateTime.isAfter(clock.now().plusHours(1));
    }

    /**
     * Reschedule = book the next slot on the new date with the same doctor, then cancel the old one.
     * Runs in one transaction: if the new booking fails (fully booked...), the old appointment is untouched.
     */
    public Appointment rescheduleAppointment(Long appointmentId, User patient, LocalDate newDate) {
        logger.info("Rescheduling appointment {} to {}", appointmentId, newDate);

        Appointment old = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new BookingException("Appointment not found"));

        if (!old.getPatient().getId().equals(patient.getId())) {
            throw new BookingException("You can only reschedule your own appointments");
        }
        if (!canCancelAppointment(old)) {
            throw new BookingException("This appointment can no longer be rescheduled");
        }
        if (old.getAppointmentDate().equals(newDate)) {
            throw new BookingException("Please choose a different date");
        }

        // Cancel first so the "one appointment per day" rule can't trip on the old booking
        old.setStatus(AppointmentStatus.CANCELLED);
        old.setUpdatedAt(LocalDateTime.now());
        old.setUpdatedBy("whatsapp-bot");
        appointmentRepository.save(old);

        Appointment fresh = appointmentService.bookAppointment(patient, old.getDoctor(), newDate, "whatsapp-bot");
        logger.info("Appointment {} rescheduled -> new appointment {}", appointmentId, fresh.getId());
        return fresh;
    }

    /**
     * Format appointment for display
     */
    public String formatAppointmentDetails(Appointment appointment) {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("📅 *Appointment #%d*\n\n", appointment.getId()));
        sb.append(String.format("👨‍⚕️ *Doctor:* Dr. %s %s\n",
            appointment.getDoctor().getFirstName(),
            appointment.getDoctor().getLastName()));

        if (appointment.getDoctor().getSpecialization() != null) {
            sb.append(String.format("   (%s)\n", appointment.getDoctor().getSpecialization()));
        }

        sb.append(String.format("\n📆 *Date:* %s\n", appointment.getAppointmentDate().format(DATE_FORMATTER)));
        sb.append(String.format("⏰ *Time:* %s\n", appointment.getScheduledTime().format(TIME_FORMATTER)));
        sb.append(String.format("🔢 *Queue:* #%d\n", appointment.getQueueNumber()));
        sb.append(String.format("📊 *Status:* %s", formatStatus(appointment.getStatus())));

        return sb.toString();
    }

    /**
     * Format appointment for list
     */
    public String formatAppointmentShort(Appointment appointment, int index) {
        return String.format("%d. Dr. %s - %s %s (Queue #%d)",
            index,
            appointment.getDoctor().getLastName(),
            appointment.getAppointmentDate().format(DATE_FORMATTER),
            appointment.getScheduledTime().format(TIME_FORMATTER),
            appointment.getQueueNumber());
    }

    /**
     * Format status for display
     */
    private String formatStatus(com.appointment.webapp.model.enums.AppointmentStatus status) {
        return switch (status) {
            case WAITING -> "⏳ Waiting";
            case IN_CONSULTATION -> "👨‍⚕️ In Consultation";
            case COMPLETED -> "✅ Completed";
            case CANCELLED -> "❌ Cancelled";
            case DELAYED -> "⏰ Delayed";
            case NOT_ATTENDED -> "❌ Not Attended";
        };
    }

    /**
     * Appointments starting within (now + hoursAhead - 30 min, now + hoursAhead].
     * Reminder jobs run every 30 minutes, so these windows tile exactly: each appointment
     * falls into one window once, instead of being re-sent on every run.
     */
    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsStartingInWindow(int hoursAhead) {
        LocalDateTime now = clock.now();
        LocalDateTime windowEnd = now.plusHours(hoursAhead);
        LocalDateTime windowStart = windowEnd.minusMinutes(30);

        return appointmentRepository.findByDateRangeAndStatusWithDetails(
                windowStart.toLocalDate(), windowEnd.toLocalDate(), AppointmentStatus.WAITING).stream()
            .filter(a -> a.getScheduledTime() != null)
            .filter(a -> {
                LocalDateTime start = LocalDateTime.of(a.getAppointmentDate(), a.getScheduledTime());
                return start.isAfter(windowStart) && !start.isAfter(windowEnd);
            })
            .collect(Collectors.toList());
    }
}
