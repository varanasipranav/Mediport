package com.appointment.webapp.model;

import com.appointment.webapp.model.enums.AppointmentStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Appointment Entity - The Queue System
 * Each appointment represents a patient in the doctor's queue for a specific date
 */
@Entity
@Table(name = "appointments",
       uniqueConstraints = @UniqueConstraint(columnNames = {"schedule_id", "queue_number"}))
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private DoctorSchedule schedule;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "queue_number", nullable = false)
    private Integer queueNumber; // 1, 2, 3... CRITICAL for ordering

    @Column(name = "scheduled_time", nullable = false)
    private LocalTime scheduledTime; // Calculated from queue position

    @Column(name = "actual_start_time")
    private LocalDateTime actualStartTime;

    @Column(name = "actual_end_time")
    private LocalDateTime actualEndTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AppointmentStatus status = AppointmentStatus.WAITING;

    @Column(name = "is_priority")
    private Boolean isPriority = false;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes; // Attendant can add notes

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    // Constructors
    public Appointment() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Business Logic Methods

    /**
     * Check if appointment is active (not completed/cancelled)
     */
    public boolean isActive() {
        return status != AppointmentStatus.COMPLETED &&
               status != AppointmentStatus.CANCELLED;
    }

    /**
     * Check if patient is currently being consulted
     */
    public boolean isInProgress() {
        return status == AppointmentStatus.IN_CONSULTATION;
    }

    /**
     * Start consultation
     */
    public void startConsultation() {
        this.status = AppointmentStatus.IN_CONSULTATION;
        this.actualStartTime = LocalDateTime.now();
    }

    /**
     * Complete consultation
     */
    public void complete() {
        this.status = AppointmentStatus.COMPLETED;
        this.actualEndTime = LocalDateTime.now();
    }

    /**
     * Mark as not attended
     */
    public void markNotAttended() {
        this.status = AppointmentStatus.NOT_ATTENDED;
    }

    /**
     * Mark as delayed
     */
    public void markDelayed() {
        this.status = AppointmentStatus.DELAYED;
    }

    /**
     * Get patient full name
     */
    public String getPatientFullName() {
        return patient != null ? patient.getFirstName() + " " + patient.getLastName() : "N/A";
    }

    /**
     * Get doctor full name
     */
    public String getDoctorFullName() {
        return doctor != null ? doctor.getFirstName() + " " + doctor.getLastName() : "N/A";
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getPatient() {
        return patient;
    }

    public void setPatient(User patient) {
        this.patient = patient;
    }

    public User getDoctor() {
        return doctor;
    }

    public void setDoctor(User doctor) {
        this.doctor = doctor;
    }

    public DoctorSchedule getSchedule() {
        return schedule;
    }

    public void setSchedule(DoctorSchedule schedule) {
        this.schedule = schedule;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public Integer getQueueNumber() {
        return queueNumber;
    }

    public void setQueueNumber(Integer queueNumber) {
        this.queueNumber = queueNumber;
    }

    public LocalTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public LocalDateTime getActualStartTime() {
        return actualStartTime;
    }

    public void setActualStartTime(LocalDateTime actualStartTime) {
        this.actualStartTime = actualStartTime;
    }

    public LocalDateTime getActualEndTime() {
        return actualEndTime;
    }

    public void setActualEndTime(LocalDateTime actualEndTime) {
        this.actualEndTime = actualEndTime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public Boolean getIsPriority() {
        return isPriority;
    }

    public void setIsPriority(Boolean isPriority) {
        this.isPriority = isPriority;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
