package com.appointment.webapp.service.record;

import com.appointment.webapp.model.Appointment;
import com.appointment.webapp.model.MedicalProfile;
import com.appointment.webapp.model.Prescription;
import com.appointment.webapp.model.RecordAccessLog;
import com.appointment.webapp.model.User;
import com.appointment.webapp.model.VisitVitals;
import com.appointment.webapp.repository.AppointmentRepository;
import com.appointment.webapp.repository.MedicalProfileRepository;
import com.appointment.webapp.repository.PrescriptionRepository;
import com.appointment.webapp.repository.RecordAccessLogRepository;
import com.appointment.webapp.repository.UserRepository;
import com.appointment.webapp.repository.VisitVitalsRepository;
import com.appointment.webapp.service.BookingException;
import com.appointment.webapp.service.RxDocumentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A patient's medical record: profile (allergies, conditions, blood group), measurements per visit, and the
 * timeline of past visits with their prescriptions - across all doctors of the clinic.
 */
@Service
@Transactional
public class MedicalRecordService {

    private static final Set<String> BLOOD_GROUPS = Set.of("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");

    private final MedicalProfileRepository profiles;
    private final VisitVitalsRepository vitals;
    private final RecordAccessLogRepository accessLog;
    private final AppointmentRepository appointments;
    private final PrescriptionRepository prescriptions;
    private final UserRepository users;
    private final RxDocumentService rxDocuments;
    private final RecordSummarizer summarizer;

    public MedicalRecordService(MedicalProfileRepository profiles, VisitVitalsRepository vitals,
                                RecordAccessLogRepository accessLog, AppointmentRepository appointments,
                                PrescriptionRepository prescriptions, UserRepository users,
                                RxDocumentService rxDocuments, RecordSummarizer summarizer) {
        this.profiles = profiles;
        this.vitals = vitals;
        this.accessLog = accessLog;
        this.appointments = appointments;
        this.prescriptions = prescriptions;
        this.users = users;
        this.rxDocuments = rxDocuments;
        this.summarizer = summarizer;
    }

    // ------------------------------------------------------------------ who may read a record

    /**
     * A doctor may read the record of any patient who has (or had) an appointment with them. That is also what
     * lets a doctor see earlier visits with colleagues when a patient books again.
     */
    @Transactional(readOnly = true)
    public boolean doctorMayView(String doctorUsername, Long patientId) {
        return appointments.existsByDoctorUsernameAndPatientId(doctorUsername, patientId);
    }

    // ------------------------------------------------------------------ read

    @Transactional(readOnly = true)
    public PatientRecord getRecord(User patient) {
        MedicalProfile profile = profiles.findById(patient.getId()).orElse(null);

        Map<Long, Prescription> rxByAppointment = new HashMap<>();
        for (Prescription p : prescriptions.findByPatientOrderByCreatedAtDesc(patient)) {
            if (p.getAppointment() != null) {
                rxByAppointment.put(p.getAppointment().getId(), p);
            }
        }
        Map<Long, VisitVitals> vitalsByAppointment = new HashMap<>();
        for (VisitVitals v : vitals.findForPatient(patient.getId())) {
            vitalsByAppointment.put(v.getAppointment().getId(), v);
        }

        List<PatientRecord.Visit> visits = new ArrayList<>();
        for (Appointment a : appointments.findByPatientOrderByAppointmentDateDesc(patient)) {
            visits.add(toVisit(a, rxByAppointment.get(a.getId()), vitalsByAppointment.get(a.getId())));
        }

        return new PatientRecord(patient, profile, visits, summarizer.summarize(patient, profile, visits));
    }

    private PatientRecord.Visit toVisit(Appointment a, Prescription rx, VisitVitals v) {
        String doctor = "Dr. " + a.getDoctor().getFirstName() + " " + a.getDoctor().getLastName();
        PatientRecord.Vitals vit = v == null ? null : new PatientRecord.Vitals(v.getBpSystolic(), v.getBpDiastolic(),
            v.getPulse(), v.getTemperatureC(), v.getWeightKg(), v.getHeightCm(), v.getSpo2(), v.getRecordedAt(), v.getRecordedBy());
        PatientRecord.Rx rxView = rx == null ? null : new PatientRecord.Rx(rx.getId(), rxDocuments.rxNumber(rx),
            rx.getObservations(), rxDocuments.parseMedicines(rx.getMedicines()), rx.getMedicines(), rx.getTests(),
            rx.getAdditionalNotes(), rx.getNextVisitDate(), doctor);
        return new PatientRecord.Visit(a.getId(), a.getAppointmentDate(), a.getScheduledTime(), doctor,
            a.getDoctor().getSpecialization(), a.getStatus(), a.getNotes(), vit, rxView);
    }

    @Transactional(readOnly = true)
    public Appointment getAppointment(Long appointmentId) {
        Appointment a = appointments.findById(appointmentId).orElseThrow(() -> new BookingException("Appointment not found"));
        a.getPatient().getFirstName();
        a.getDoctor().getFirstName();
        return a;
    }

    @Transactional(readOnly = true)
    public User getPatient(Long patientId) {
        return users.findById(patientId).orElseThrow(() -> new BookingException("Patient not found"));
    }

    // ------------------------------------------------------------------ write

    public void saveProfile(Long patientId, String bloodGroup, String allergies, String chronic, String notes, String by) {
        getPatient(patientId);
        MedicalProfile p = profiles.findById(patientId).orElseGet(() -> {
            MedicalProfile fresh = new MedicalProfile();
            fresh.setUserId(patientId);
            return fresh;
        });
        String bg = bloodGroup == null ? "" : bloodGroup.trim().toUpperCase();
        if (!bg.isEmpty() && !BLOOD_GROUPS.contains(bg)) {
            throw new BookingException("Blood group must be one of " + String.join(", ", new java.util.TreeSet<>(BLOOD_GROUPS)));
        }
        p.setBloodGroup(bg.isEmpty() ? null : bg);
        p.setAllergies(blankToNull(allergies));
        p.setChronicConditions(blankToNull(chronic));
        p.setNotes(blankToNull(notes));
        p.setUpdatedBy(by);
        p.setUpdatedAt(LocalDateTime.now());
        profiles.save(p);
        log(patientId, by, "UPDATE_PROFILE");
    }

    public void saveVitals(Long appointmentId, Integer systolic, Integer diastolic, Integer pulse, BigDecimal temperature,
                           BigDecimal weight, BigDecimal height, Integer spo2, String by) {
        Appointment a = getAppointment(appointmentId);
        check("Systolic pressure", systolic, 50, 300);
        check("Diastolic pressure", diastolic, 30, 200);
        check("Pulse", pulse, 20, 250);
        check("Oxygen saturation", spo2, 50, 100);
        check("Temperature (°C)", temperature, 30, 45);
        check("Weight (kg)", weight, 1, 400);
        check("Height (cm)", height, 20, 250);
        if ((systolic == null) != (diastolic == null)) {
            throw new BookingException("Enter both blood pressure numbers, or neither");
        }
        if (systolic != null && diastolic >= systolic) {
            throw new BookingException("Diastolic pressure must be lower than systolic");
        }
        if (systolic == null && pulse == null && temperature == null && weight == null && height == null && spo2 == null) {
            throw new BookingException("Enter at least one measurement");
        }

        VisitVitals v = vitals.findByAppointmentId(appointmentId).orElseGet(() -> {
            VisitVitals fresh = new VisitVitals();
            fresh.setAppointment(a);
            return fresh;
        });
        v.setBpSystolic(systolic);
        v.setBpDiastolic(diastolic);
        v.setPulse(pulse);
        v.setTemperatureC(temperature);
        v.setWeightKg(weight);
        v.setHeightCm(height);
        v.setSpo2(spo2);
        v.setRecordedBy(by);
        v.setRecordedAt(LocalDateTime.now());
        vitals.save(v);
        log(a.getPatient().getId(), by, "RECORD_VITALS");
    }

    /** Remember that someone opened this record. */
    public void logView(Long patientId, String by) {
        log(patientId, by, "VIEW_RECORD");
    }

    private void log(Long patientId, String by, String action) {
        RecordAccessLog entry = new RecordAccessLog();
        entry.setPatientId(patientId);
        entry.setAccessedBy(by);
        entry.setAction(action);
        accessLog.save(entry);
    }

    private static void check(String label, Number value, double min, double max) {
        if (value != null && (value.doubleValue() < min || value.doubleValue() > max)) {
            throw new BookingException(label + " must be between " + (long) min + " and " + (long) max);
        }
    }

    private static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }
}
