package com.appointment.webapp.repository;

import com.appointment.webapp.model.DoctorWeeklyTemplate;
import com.appointment.webapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing doctor weekly schedule templates
 */
@Repository
public interface DoctorWeeklyTemplateRepository extends JpaRepository<DoctorWeeklyTemplate, Long> {

    /**
     * Find all weekly templates for a specific doctor
     */
    List<DoctorWeeklyTemplate> findByDoctorOrderByDayOfWeek(User doctor);

    /**
     * Find template for a specific doctor and day of week
     */
    Optional<DoctorWeeklyTemplate> findByDoctorAndDayOfWeek(User doctor, Integer dayOfWeek);

    /**
     * Find all active templates for a doctor
     */
    @Query("SELECT t FROM DoctorWeeklyTemplate t WHERE t.doctor = :doctor AND t.isActive = true ORDER BY t.dayOfWeek")
    List<DoctorWeeklyTemplate> findActiveDaysByDoctor(@Param("doctor") User doctor);

    /**
     * Check if doctor has any weekly template configured
     */
    boolean existsByDoctor(User doctor);

    /**
     * Check if doctor has template for specific day
     */
    boolean existsByDoctorAndDayOfWeek(User doctor, Integer dayOfWeek);

    /**
     * Count active days for a doctor (working days)
     */
    @Query("SELECT COUNT(t) FROM DoctorWeeklyTemplate t WHERE t.doctor = :doctor AND t.isActive = true")
    Long countActiveDays(@Param("doctor") User doctor);

    /**
     * Get all weekend templates (Saturday & Sunday)
     */
    @Query("SELECT t FROM DoctorWeeklyTemplate t WHERE t.dayOfWeek IN (6, 7) AND t.isActive = true")
    List<DoctorWeeklyTemplate> findActiveWeekendTemplates();

    /**
     * Get all weekday templates (Monday to Friday)
     */
    @Query("SELECT t FROM DoctorWeeklyTemplate t WHERE t.doctor = :doctor AND t.dayOfWeek BETWEEN 1 AND 5 ORDER BY t.dayOfWeek")
    List<DoctorWeeklyTemplate> findWeekdayTemplates(@Param("doctor") User doctor);

    /**
     * Delete all templates for a doctor (used when resetting schedule)
     */
    void deleteByDoctor(User doctor);

    /**
     * Find doctors who have active schedule on a specific day of week
     */
    @Query("SELECT DISTINCT t.doctor FROM DoctorWeeklyTemplate t WHERE t.dayOfWeek = :dayOfWeek AND t.isActive = true")
    List<User> findDoctorsAvailableOnDay(@Param("dayOfWeek") Integer dayOfWeek);
}
