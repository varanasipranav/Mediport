package com.appointment.webapp.repository;

import com.appointment.webapp.model.Appointment;
import com.appointment.webapp.model.Prescription;
import com.appointment.webapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    /**
     * Find prescription by appointment
     */
    Optional<Prescription> findByAppointment(Appointment appointment);

    /**
     * Find all prescriptions for a patient, ordered by date (newest first)
     */
    List<Prescription> findByPatientOrderByCreatedAtDesc(User patient);

    /**
     * Find prescriptions for a patient within a date range
     */
    @Query("SELECT p FROM Prescription p WHERE p.patient = :patient " +
           "AND DATE(p.createdAt) BETWEEN :startDate AND :endDate " +
           "ORDER BY p.createdAt DESC")
    List<Prescription> findByPatientAndDateRange(
        @Param("patient") User patient,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Find all prescriptions written by a doctor
     */
    List<Prescription> findByDoctorOrderByCreatedAtDesc(User doctor);

    /**
     * Find prescriptions written by a doctor on a specific date
     */
    @Query("SELECT p FROM Prescription p WHERE p.doctor = :doctor " +
           "AND DATE(p.createdAt) = :date ORDER BY p.createdAt DESC")
    List<Prescription> findByDoctorAndDate(
        @Param("doctor") User doctor,
        @Param("date") LocalDate date
    );

    /**
     * Find prescriptions that haven't been printed yet (for pharma)
     */
    @Query("SELECT p FROM Prescription p WHERE p.printedAt IS NULL " +
           "ORDER BY p.createdAt DESC")
    List<Prescription> findUnprintedPrescriptions();

    /**
     * Find prescriptions created on a specific date (for pharma daily view)
     */
    @Query("SELECT p FROM Prescription p WHERE DATE(p.createdAt) = :date " +
           "ORDER BY p.createdAt DESC")
    List<Prescription> findByCreatedDate(@Param("date") LocalDate date);

    /**
     * Find recent prescriptions (last N days) for pharma
     */
    @Query("SELECT p FROM Prescription p WHERE DATE(p.createdAt) >= :sinceDate " +
           "ORDER BY p.createdAt DESC")
    List<Prescription> findRecentPrescriptionsSince(@Param("sinceDate") LocalDate sinceDate);

    /**
     * Find prescriptions between date range
     */
    @Query("SELECT p FROM Prescription p WHERE DATE(p.createdAt) BETWEEN :startDate AND :endDate " +
           "ORDER BY p.createdAt DESC")
    List<Prescription> findByCreatedAtBetween(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Search prescriptions by patient name or phone number
     */
    @Query("SELECT p FROM Prescription p WHERE " +
           "LOWER(p.patient.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.patient.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "p.patient.phoneNumber LIKE CONCAT('%', :searchTerm, '%') " +
           "ORDER BY p.createdAt DESC")
    List<Prescription> searchByPatientNameOrPhone(@Param("searchTerm") String searchTerm);

    /**
     * Count prescriptions for a patient
     */
    long countByPatient(User patient);

    /**
     * Count prescriptions written by a doctor
     */
    long countByDoctor(User doctor);
}
