package com.appointment.webapp.repository;

import com.appointment.webapp.model.DoctorSchedule;
import com.appointment.webapp.model.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {

    /**
     * Find schedule by doctor and date (unique constraint)
     */
    Optional<DoctorSchedule> findByDoctorAndScheduleDate(User doctor, LocalDate scheduleDate);

    /**
     * Same lookup but takes a row lock (SELECT ... FOR UPDATE) so concurrent bookings
     * for one doctor/day are serialized until the transaction commits.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ds FROM DoctorSchedule ds WHERE ds.doctor = :doctor AND ds.scheduleDate = :date")
    Optional<DoctorSchedule> findByDoctorAndScheduleDateForUpdate(@Param("doctor") User doctor, @Param("date") LocalDate date);

    /**
     * Find all schedules for a doctor
     */
    List<DoctorSchedule> findByDoctorOrderByScheduleDateDesc(User doctor);

    /**
     * Find schedules for a doctor within a date range
     */
    @Query("SELECT ds FROM DoctorSchedule ds WHERE ds.doctor = :doctor " +
           "AND ds.scheduleDate BETWEEN :startDate AND :endDate " +
           "ORDER BY ds.scheduleDate ASC")
    List<DoctorSchedule> findByDoctorAndDateRange(
        @Param("doctor") User doctor,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Find active schedule for a doctor on a specific date
     */
    @Query("SELECT ds FROM DoctorSchedule ds WHERE ds.doctor = :doctor " +
           "AND ds.scheduleDate = :date AND ds.isActive = true")
    Optional<DoctorSchedule> findActiveScheduleByDoctorAndDate(
        @Param("doctor") User doctor,
        @Param("date") LocalDate date
    );

    /**
     * Find all schedules for a specific date (for admin view)
     */
    List<DoctorSchedule> findByScheduleDateOrderByDoctorLastName(LocalDate scheduleDate);

    /**
     * Check if doctor has schedule on a specific date
     */
    boolean existsByDoctorAndScheduleDate(User doctor, LocalDate scheduleDate);

    /**
     * Find upcoming schedules for a doctor
     */
    @Query("SELECT ds FROM DoctorSchedule ds WHERE ds.doctor = :doctor " +
           "AND ds.scheduleDate >= :date AND ds.isActive = true " +
           "ORDER BY ds.scheduleDate ASC")
    List<DoctorSchedule> findUpcomingSchedules(
        @Param("doctor") User doctor,
        @Param("date") LocalDate date
    );
}
