package com.appointment.webapp.whatsapp.service.appointment;

import com.appointment.webapp.model.Appointment;
import com.appointment.webapp.model.DoctorSchedule;
import com.appointment.webapp.model.User;
import com.appointment.webapp.repository.AppointmentRepository;
import com.appointment.webapp.service.AppointmentService;
import com.appointment.webapp.service.DoctorScheduleService;
import com.appointment.webapp.whatsapp.config.ClinicClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * WhatsApp booking rules, driven entirely by the doctor's real schedule
 * (start/end time, slot length, max patients, priority work) - the same rules the web app uses.
 *
 * Appointments are queue based: the next patient gets the next queue number, and the time is
 * start + (queue - 1) * slot length. So WhatsApp offers "the next available slot" per day
 * rather than letting patients pick arbitrary clock times that don't exist in the schedule.
 */
@Service
public class WhatsAppAppointmentService {

    private static final Logger logger = LoggerFactory.getLogger(WhatsAppAppointmentService.class);

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /** How many days ahead patients can book over WhatsApp (including today). */
    private static final int BOOKING_WINDOW_DAYS = 7;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private DoctorScheduleService scheduleService;

    @Autowired
    private ClinicClock clock;

    /**
     * One bookable day for a doctor: the slot the next patient would get.
     */
    public record BookableDay(LocalDate date, int queueNumber, LocalTime time, LocalTime startTime,
                              LocalTime endTime, int slotsLeft) {
        public String timeText() {
            return time.format(TIME_FORMATTER);
        }

        public String hoursText() {
            return startTime.format(TIME_FORMATTER) + " - " + endTime.format(TIME_FORMATTER);
        }
    }

    /**
     * Days (within the booking window) on which this doctor has an active schedule with a free slot.
     */
    @Transactional(readOnly = true)
    public List<BookableDay> getBookableDays(User doctor, User patient) {
        List<BookableDay> days = new ArrayList<>();
        LocalDate today = clock.today();

        for (int i = 0; i < BOOKING_WINDOW_DAYS; i++) {
            findBookableDay(doctor, patient, today.plusDays(i)).ifPresent(days::add);
        }
        return days;
    }

    /**
     * The slot the next patient would get on a date, if the doctor can take one.
     * Empty when: no/inactive schedule, fully booked, day is over, or the patient already has a booking that day.
     */
    @Transactional(readOnly = true)
    public Optional<BookableDay> findBookableDay(User doctor, User patient, LocalDate date) {
        LocalDate today = clock.today();
        if (date.isBefore(today) || date.isAfter(today.plusDays(BOOKING_WINDOW_DAYS - 1))) {
            return Optional.empty();
        }

        Optional<DoctorSchedule> scheduleOpt = scheduleService.getScheduleByDoctorAndDate(doctor, date);
        if (scheduleOpt.isEmpty() || !Boolean.TRUE.equals(scheduleOpt.get().getIsActive())) {
            return Optional.empty();
        }
        DoctorSchedule schedule = scheduleOpt.get();

        if (patient != null && appointmentRepository.findByPatientAndDate(patient, date).isPresent()) {
            return Optional.empty();
        }

        Integer maxQueue = appointmentRepository.findMaxQueueNumberByScheduleId(schedule.getId());
        int nextQueue = (maxQueue != null ? maxQueue : 0) + 1;

        if (schedule.getMaxPatients() != null && nextQueue > schedule.getMaxPatients()) {
            return Optional.empty();
        }
        if (!schedule.hasSlotFor(nextQueue)) {
            return Optional.empty();
        }

        LocalTime time = schedule.calculateScheduledTime(nextQueue);

        // A time that has already passed today isn't a real slot to offer
        if (date.equals(today) && time.isBefore(clock.now().toLocalTime())) {
            return Optional.empty();
        }

        int slotsLeft = remainingSlots(schedule, nextQueue);
        return Optional.of(new BookableDay(date, nextQueue, time, schedule.getStartTime(), schedule.getEndTime(), slotsLeft));
    }

    private int remainingSlots(DoctorSchedule schedule, int nextQueue) {
        int left = 0;
        for (int q = nextQueue; q < nextQueue + 500; q++) {
            if (schedule.getMaxPatients() != null && q > schedule.getMaxPatients()) {
                break;
            }
            if (!schedule.hasSlotFor(q)) {
                break;
            }
            left++;
        }
        return left;
    }

    /**
     * Book the next slot on a date. The schedule row is locked inside
     * AppointmentService.bookAppointment, so two patients can never get the same queue number.
     */
    public Appointment bookAppointment(User patient, User doctor, LocalDate date, String bookedBy) {
        logger.info("Booking appointment for patient {} with doctor {} on {}", patient.getId(), doctor.getId(), date);
        Appointment appointment = appointmentService.bookAppointment(patient, doctor, date, bookedBy);
        logger.info("Appointment {} booked: queue #{} at {}", appointment.getId(),
                appointment.getQueueNumber(), appointment.getScheduledTime());
        return appointment;
    }

    public LocalDate today() {
        return clock.today();
    }

    /**
     * Format date for display
     */
    public String formatDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }

    /**
     * Parse date from DD/MM/YYYY format
     */
    public LocalDate parseDate(String dateString) {
        return LocalDate.parse(dateString, DATE_FORMATTER);
    }

    /**
     * Today / Tomorrow / weekday name (e.g. "Friday")
     */
    public String getDayName(LocalDate date) {
        LocalDate today = clock.today();

        if (date.equals(today)) {
            return "Today";
        } else if (date.equals(today.plusDays(1))) {
            return "Tomorrow";
        }
        String name = date.getDayOfWeek().toString();
        return name.charAt(0) + name.substring(1).toLowerCase();
    }
}
