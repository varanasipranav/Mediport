package com.appointment.webapp.repository;

import com.appointment.webapp.model.Appointment;
import com.appointment.webapp.model.DoctorSchedule;
import com.appointment.webapp.model.User;
import com.appointment.webapp.model.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /**
     * Find all appointments for a schedule, ordered by queue number
     */
    List<Appointment> findByScheduleOrderByQueueNumber(DoctorSchedule schedule);

    /**
     * Find appointments by schedule ID, ordered by queue number
     */
    @Query("SELECT a FROM Appointment a WHERE a.schedule.id = :scheduleId ORDER BY a.queueNumber ASC")
    List<Appointment> findByScheduleIdOrderByQueueNumber(@Param("scheduleId") Long scheduleId);

    /**
     * Find appointments by doctor and date (for WhatsApp booking)
     */
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate = :date")
    List<Appointment> findByDoctorIdAndAppointmentDate(@Param("doctorId") Long doctorId, @Param("date") LocalDate date);

    /**
     * Count active appointments in a schedule (excluding NOT_ATTENDED)
     */
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.schedule.id = :scheduleId " +
           "AND a.status != 'NOT_ATTENDED'")
    long countByScheduleIdAndStatusNot(@Param("scheduleId") Long scheduleId, @Param("status") String status);

    /**
     * Find appointments by schedule and queue number range
     */
    @Query("SELECT a FROM Appointment a WHERE a.schedule.id = :scheduleId " +
           "AND a.queueNumber BETWEEN :startQueue AND :endQueue " +
           "ORDER BY a.queueNumber ASC")
    List<Appointment> findByScheduleIdAndQueueNumberBetweenOrderByQueueNumber(
        @Param("scheduleId") Long scheduleId,
        @Param("startQueue") Integer startQueue,
        @Param("endQueue") Integer endQueue
    );

    /**
     * Find maximum queue number for a schedule
     */
    @Query("SELECT MAX(a.queueNumber) FROM Appointment a WHERE a.schedule.id = :scheduleId")
    Integer findMaxQueueNumberByScheduleId(@Param("scheduleId") Long scheduleId);

    /**
     * Decrement queue numbers for all appointments after a specific position
     */
    @Modifying
    @Query("UPDATE Appointment a SET a.queueNumber = a.queueNumber - 1 " +
           "WHERE a.schedule.id = :scheduleId AND a.queueNumber > :afterQueueNumber")
    void decrementQueueNumbersAfter(
        @Param("scheduleId") Long scheduleId,
        @Param("afterQueueNumber") Integer afterQueueNumber
    );

    /**
     * Find all appointments for a doctor on a specific date
     */
    @Query("SELECT a FROM Appointment a WHERE a.doctor = :doctor " +
           "AND a.appointmentDate = :date ORDER BY a.queueNumber ASC")
    List<Appointment> findByDoctorAndDate(
        @Param("doctor") User doctor,
        @Param("date") LocalDate date
    );

    /**
     * Find all appointments for a patient
     */
    List<Appointment> findByPatientOrderByAppointmentDateDesc(User patient);

    /**
     * Find appointments for a patient on a specific date
     */
    @Query("SELECT a FROM Appointment a WHERE a.patient = :patient " +
           "AND a.appointmentDate = :date AND a.status <> 'CANCELLED'")
    Optional<Appointment> findByPatientAndDate(
        @Param("patient") User patient,
        @Param("date") LocalDate date
    );

    /**
     * Count patients ahead in queue with specific statuses
     */
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.schedule.id = :scheduleId " +
           "AND a.queueNumber < :queueNumber " +
           "AND a.status IN :statuses")
    long countByScheduleIdAndQueueNumberLessThanAndStatusIn(
        @Param("scheduleId") Long scheduleId,
        @Param("queueNumber") Integer queueNumber,
        @Param("statuses") List<AppointmentStatus> statuses
    );

    /**
     * Find today's appointments for a doctor
     */
    @Query("SELECT a FROM Appointment a WHERE a.doctor = :doctor " +
           "AND a.appointmentDate = CURRENT_DATE ORDER BY a.queueNumber ASC")
    List<Appointment> findTodaysAppointmentsByDoctor(@Param("doctor") User doctor);

    /**
     * Find appointments by status for a doctor on a date
     */
    @Query("SELECT a FROM Appointment a WHERE a.doctor = :doctor " +
           "AND a.appointmentDate = :date AND a.status = :status " +
           "ORDER BY a.queueNumber ASC")
    List<Appointment> findByDoctorAndDateAndStatus(
        @Param("doctor") User doctor,
        @Param("date") LocalDate date,
        @Param("status") AppointmentStatus status
    );

    /**
     * Find upcoming appointments for a patient
     */
    @Query("SELECT a FROM Appointment a WHERE a.patient = :patient " +
           "AND a.appointmentDate >= CURRENT_DATE " +
           "AND a.status NOT IN ('COMPLETED', 'CANCELLED') " +
           "ORDER BY a.appointmentDate ASC, a.queueNumber ASC")
    List<Appointment> findUpcomingAppointmentsByPatient(@Param("patient") User patient);

    /**
     * Upcoming appointments for a patient with doctor and schedule loaded (safe to use outside a session)
     */
    @Query("SELECT a FROM Appointment a JOIN FETCH a.doctor JOIN FETCH a.schedule " +
           "WHERE a.patient = :patient AND a.appointmentDate >= :fromDate " +
           "AND a.status NOT IN ('COMPLETED', 'CANCELLED', 'NOT_ATTENDED') " +
           "ORDER BY a.appointmentDate ASC, a.queueNumber ASC")
    List<Appointment> findUpcomingWithDetails(@Param("patient") User patient, @Param("fromDate") LocalDate fromDate);

    /**
     * Every appointment on a date with patient, doctor and schedule loaded (used by queue alerts)
     */
    @Query("SELECT a FROM Appointment a JOIN FETCH a.patient JOIN FETCH a.doctor JOIN FETCH a.schedule " +
           "WHERE a.appointmentDate = :date")
    List<Appointment> findByDateWithDetails(@Param("date") LocalDate date);

    /**
     * Waiting appointments in a date range with patient and doctor loaded (used by reminders)
     */
    @Query("SELECT a FROM Appointment a JOIN FETCH a.patient JOIN FETCH a.doctor " +
           "WHERE a.appointmentDate BETWEEN :fromDate AND :toDate AND a.status = :status")
    List<Appointment> findByDateRangeAndStatusWithDetails(@Param("fromDate") LocalDate fromDate,
                                                          @Param("toDate") LocalDate toDate,
                                                          @Param("status") AppointmentStatus status);

    /** True if this doctor has ever had (or is booked for) an appointment with the patient. */
    boolean existsByDoctorUsernameAndPatientId(String doctorUsername, Long patientId);
}
