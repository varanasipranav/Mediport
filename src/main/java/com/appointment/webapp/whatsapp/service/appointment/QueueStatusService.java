package com.appointment.webapp.whatsapp.service.appointment;

import com.appointment.webapp.model.Appointment;
import com.appointment.webapp.model.User;
import com.appointment.webapp.model.enums.AppointmentStatus;
import com.appointment.webapp.repository.AppointmentRepository;
import com.appointment.webapp.whatsapp.config.ClinicClock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Where is each patient in today's queue? Shared by the WhatsApp "queue status" reply and the alert scheduler,
 * so both always give the same answer.
 */
@Service
public class QueueStatusService {

    /** Patients who still stand between a waiting patient and the doctor. */
    private static final Set<AppointmentStatus> AHEAD = EnumSet.of(
        AppointmentStatus.WAITING, AppointmentStatus.IN_CONSULTATION, AppointmentStatus.DELAYED);

    private final AppointmentRepository appointments;
    private final ClinicClock clock;

    public QueueStatusService(AppointmentRepository appointments, ClinicClock clock) {
        this.appointments = appointments;
        this.clock = clock;
    }

    /**
     * @param ahead          patients before this one who are still waiting or in the room
     * @param estimated      expected time the doctor will see them (never earlier than now or their slot)
     * @param minutesLate    how far past the booked slot "now" is (0 if the slot is still in the future)
     */
    public record QueueStatus(Appointment appointment, int ahead, LocalTime estimated, int minutesLate, int slotMinutes) {

        public boolean isWaiting() {
            AppointmentStatus s = appointment.getStatus();
            return s == AppointmentStatus.WAITING || s == AppointmentStatus.DELAYED;
        }
    }

    /** Today's open appointment for a patient, if any. */
    @Transactional(readOnly = true)
    public Optional<QueueStatus> todayFor(User patient) {
        LocalDate today = clock.today();
        return statusesFor(today).stream()
            .filter(s -> s.appointment().getPatient().getId().equals(patient.getId()))
            .filter(s -> s.appointment().getStatus() != AppointmentStatus.CANCELLED)
            .findFirst();
    }

    /** Queue position of every appointment on a date (all statuses; ahead is only meaningful for waiting ones). */
    @Transactional(readOnly = true)
    public List<QueueStatus> statusesFor(LocalDate date) {
        List<Appointment> all = appointments.findByDateWithDetails(date);
        Map<Long, List<Appointment>> bySchedule = all.stream()
            .collect(Collectors.groupingBy(a -> a.getSchedule().getId()));

        LocalDateTime now = clock.now();
        List<QueueStatus> out = new ArrayList<>();
        for (Appointment a : all) {
            int ahead = (int) bySchedule.get(a.getSchedule().getId()).stream()
                .filter(o -> o.getQueueNumber() < a.getQueueNumber())
                .filter(o -> AHEAD.contains(o.getStatus()))
                .count();
            int slot = a.getSchedule().getSlotDurationMinutes() != null ? a.getSchedule().getSlotDurationMinutes() : 15;

            LocalDateTime booked = LocalDateTime.of(date, a.getScheduledTime());
            int late = (int) Math.max(0, java.time.Duration.between(booked, now).toMinutes());
            LocalDateTime byQueue = now.plusMinutes((long) ahead * slot);
            LocalDateTime estimate = byQueue.isAfter(booked) ? byQueue : booked;
            out.add(new QueueStatus(a, ahead, estimate.toLocalTime().withSecond(0).withNano(0), late, slot));
        }
        return out;
    }
}
