package com.appointment.webapp.service;

import com.appointment.webapp.model.Appointment;
import com.appointment.webapp.model.Prescription;
import com.appointment.webapp.model.User;
import com.appointment.webapp.repository.AppointmentRepository;
import com.appointment.webapp.repository.PrescriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing prescriptions
 * Handles prescription creation, viewing, and PDF generation
 */
@Service
@Transactional
public class PrescriptionService {

    private static final Logger logger = LoggerFactory.getLogger(PrescriptionService.class);

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private org.springframework.context.ApplicationEventPublisher events;

    /**
     * Create a new prescription after consultation
     */
    public Prescription createPrescription(Long appointmentId, String observations, String medicines,
                                          String tests, String additionalNotes, LocalDate nextVisitDate) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Check if prescription already exists for this appointment
        Optional<Prescription> existing = prescriptionRepository.findByAppointment(appointment);
        if (existing.isPresent()) {
            throw new RuntimeException("Prescription already exists for this appointment");
        }

        Prescription prescription = new Prescription();
        prescription.setAppointment(appointment);
        prescription.setPatient(appointment.getPatient());
        prescription.setDoctor(appointment.getDoctor());
        prescription.setObservations(observations);
        prescription.setMedicines(medicines);
        prescription.setTests(tests);
        prescription.setAdditionalNotes(additionalNotes);
        prescription.setNextVisitDate(nextVisitDate);

        Prescription saved = prescriptionRepository.save(prescription);

        logger.info("Created prescription {} for appointment {}", saved.getId(), appointmentId);
        events.publishEvent(new PrescriptionCreatedEvent(saved.getId()));

        return saved;
    }

    /**
     * Update an existing prescription
     */
    public Prescription updatePrescription(Long prescriptionId, String observations, String medicines,
                                          String tests, String additionalNotes, LocalDate nextVisitDate) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
            .orElseThrow(() -> new RuntimeException("Prescription not found"));

        prescription.setObservations(observations);
        prescription.setMedicines(medicines);
        prescription.setTests(tests);
        prescription.setAdditionalNotes(additionalNotes);
        prescription.setNextVisitDate(nextVisitDate);

        Prescription updated = prescriptionRepository.save(prescription);

        logger.info("Updated prescription {}", prescriptionId);

        return updated;
    }

    /**
     * Get prescription by ID
     */
    @Transactional(readOnly = true)
    public Prescription getPrescriptionById(Long prescriptionId) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
            .orElseThrow(() -> new RuntimeException("Prescription not found"));

        // Eagerly load related entities
        if (prescription.getPatient() != null) {
            prescription.getPatient().getFirstName();
        }
        if (prescription.getDoctor() != null) {
            prescription.getDoctor().getFirstName();
        }

        return prescription;
    }

    /**
     * Get prescription for an appointment
     */
    @Transactional(readOnly = true)
    public Optional<Prescription> getPrescriptionByAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new RuntimeException("Appointment not found"));
        return prescriptionRepository.findByAppointment(appointment);
    }

    /**
     * Get all prescriptions for a patient (ordered by date desc)
     */
    @Transactional(readOnly = true)
    public List<Prescription> getPatientPrescriptions(User patient) {
        List<Prescription> prescriptions = prescriptionRepository.findByPatientOrderByCreatedAtDesc(patient);

        // Eagerly load related entities
        prescriptions.forEach(p -> {
            if (p.getDoctor() != null) {
                p.getDoctor().getFirstName();
            }
        });

        return prescriptions;
    }

    /**
     * Get all prescriptions created by a doctor
     */
    @Transactional(readOnly = true)
    public List<Prescription> getDoctorPrescriptions(User doctor) {
        List<Prescription> prescriptions = prescriptionRepository.findByDoctorOrderByCreatedAtDesc(doctor);

        // Eagerly load related entities
        prescriptions.forEach(p -> {
            if (p.getPatient() != null) {
                p.getPatient().getFirstName();
            }
        });

        return prescriptions;
    }


    /**
     * Get prescriptions for a specific date range
     */
    @Transactional(readOnly = true)
    public List<Prescription> getPrescriptionsByDateRange(LocalDate startDate, LocalDate endDate) {
        return prescriptionRepository.findByCreatedAtBetween(startDate, endDate);
    }

    /**
     * Mark prescription as printed
     */
    public Prescription markAsPrinted(Long prescriptionId, User printedBy) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
            .orElseThrow(() -> new RuntimeException("Prescription not found"));

        prescription.markPrinted(printedBy);
        Prescription updated = prescriptionRepository.save(prescription);

        logger.info("Prescription {} marked as printed by {}", prescriptionId, printedBy.getUsername());

        return updated;
    }

    /**
     * Search prescriptions by patient name or phone
     */
    @Transactional(readOnly = true)
    public List<Prescription> searchPrescriptions(String searchTerm) {
        return prescriptionRepository.searchByPatientNameOrPhone(searchTerm);
    }
}
