package com.appointment.webapp.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Prescription Entity
 * Stores doctor's prescription after consultation
 */
@Entity
@Table(name = "prescriptions")
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations; // Doctor's notes/diagnosis

    @Column(name = "medicines", nullable = false, columnDefinition = "TEXT")
    private String medicines; // Prescribed medicines (can be JSON or formatted text)

    @Column(name = "tests", columnDefinition = "TEXT")
    private String tests; // Lab tests recommended

    @Column(name = "additional_notes", columnDefinition = "TEXT")
    private String additionalNotes; // Follow-up instructions

    @Column(name = "next_visit_date")
    private LocalDate nextVisitDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "printed_at")
    private LocalDateTime printedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "printed_by")
    private User printedBy;

    @Column(name = "print_count")
    private Integer printCount = 0;

    // Constructors
    public Prescription() {
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
     * Mark prescription as printed
     */
    public void markPrinted(User printedBy) {
        this.printedAt = LocalDateTime.now();
        this.printedBy = printedBy;
        this.printCount++;
    }

    /**
     * Check if prescription has been printed
     */
    public boolean isPrinted() {
        return printedAt != null;
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

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
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

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public String getMedicines() {
        return medicines;
    }

    public void setMedicines(String medicines) {
        this.medicines = medicines;
    }

    public String getTests() {
        return tests;
    }

    public void setTests(String tests) {
        this.tests = tests;
    }

    public String getAdditionalNotes() {
        return additionalNotes;
    }

    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }

    public LocalDate getNextVisitDate() {
        return nextVisitDate;
    }

    public void setNextVisitDate(LocalDate nextVisitDate) {
        this.nextVisitDate = nextVisitDate;
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

    public LocalDateTime getPrintedAt() {
        return printedAt;
    }

    public void setPrintedAt(LocalDateTime printedAt) {
        this.printedAt = printedAt;
    }

    public User getPrintedBy() {
        return printedBy;
    }

    public void setPrintedBy(User printedBy) {
        this.printedBy = printedBy;
    }

    public Integer getPrintCount() {
        return printCount;
    }

    public void setPrintCount(Integer printCount) {
        this.printCount = printCount;
    }
}
