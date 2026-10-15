package com.appointment.webapp.service.record;

import com.appointment.webapp.model.MedicalProfile;
import com.appointment.webapp.model.User;
import com.appointment.webapp.model.enums.AppointmentStatus;
import com.appointment.webapp.service.RxDocumentService.MedicineLine;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Everything the clinic knows about one patient, built for reading (plain values, safe to use in a page):
 * long-lived profile, a timeline of visits with their vitals and prescriptions, and an automatic summary.
 */
public record PatientRecord(User patient, MedicalProfile profile, List<Visit> visits, List<String> summary) {

    public record Vitals(Integer bpSystolic, Integer bpDiastolic, Integer pulse, BigDecimal temperatureC,
                         BigDecimal weightKg, BigDecimal heightCm, Integer spo2, LocalDateTime recordedAt, String recordedBy) {

        public String bp() {
            return bpSystolic != null && bpDiastolic != null ? bpSystolic + "/" + bpDiastolic : null;
        }

        public boolean isEmpty() {
            return bpSystolic == null && pulse == null && temperatureC == null && weightKg == null
                && heightCm == null && spo2 == null;
        }
    }

    public record Rx(Long id, String number, String diagnosis, List<MedicineLine> medicines, String rawMedicines,
                     String tests, String advice, LocalDate nextVisit, String doctorName) {}

    public record Visit(Long appointmentId, LocalDate date, LocalTime time, String doctorName, String speciality,
                        AppointmentStatus status, String notes, Vitals vitals, Rx prescription) {}

    public boolean hasVisits() {
        return visits != null && !visits.isEmpty();
    }

    /** The most recent set of measurements, if any. */
    public Vitals latestVitals() {
        return visits.stream().map(Visit::vitals).filter(v -> v != null && !v.isEmpty()).findFirst().orElse(null);
    }

    public Rx latestPrescription() {
        return visits.stream().map(Visit::prescription).filter(r -> r != null).findFirst().orElse(null);
    }
}
