package com.appointment.webapp.service.record;

import com.appointment.webapp.config.ClinicProperties;
import com.appointment.webapp.model.Appointment;
import com.appointment.webapp.model.MedicalProfile;
import com.appointment.webapp.model.User;
import com.appointment.webapp.repository.AppointmentRepository;
import com.appointment.webapp.repository.MedicalProfileRepository;
import com.appointment.webapp.repository.PrescriptionRepository;
import com.appointment.webapp.repository.RecordAccessLogRepository;
import com.appointment.webapp.repository.UserRepository;
import com.appointment.webapp.repository.VisitVitalsRepository;
import com.appointment.webapp.service.BookingException;
import com.appointment.webapp.service.RxDocumentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MedicalRecordServiceTest {

    private MedicalProfileRepository profiles;
    private VisitVitalsRepository vitals;
    private RecordAccessLogRepository accessLog;
    private AppointmentRepository appointments;
    private UserRepository users;
    private MedicalRecordService service;

    @BeforeEach
    void setUp() {
        profiles = mock(MedicalProfileRepository.class);
        vitals = mock(VisitVitalsRepository.class);
        accessLog = mock(RecordAccessLogRepository.class);
        appointments = mock(AppointmentRepository.class);
        users = mock(UserRepository.class);
        service = new MedicalRecordService(profiles, vitals, accessLog, appointments, mock(PrescriptionRepository.class), users,
            new RxDocumentService(new ClinicProperties(), "http://x", "k"), new RuleBasedSummarizer());

        User patient = new User();
        patient.setId(5L);
        patient.setFirstName("Priya");
        User doctor = new User();
        doctor.setFirstName("Asha");
        Appointment a = new Appointment();
        a.setId(9L);
        a.setPatient(patient);
        a.setDoctor(doctor);
        when(appointments.findById(9L)).thenReturn(Optional.of(a));
        when(users.findById(5L)).thenReturn(Optional.of(patient));
        when(vitals.findByAppointmentId(9L)).thenReturn(Optional.empty());
        when(profiles.findById(5L)).thenReturn(Optional.empty());
    }

    @Test
    void doctorsMaySeeOnlyTheirOwnPatients() {
        when(appointments.existsByDoctorUsernameAndPatientId("dr.sharma", 5L)).thenReturn(true);
        assertTrue(service.doctorMayView("dr.sharma", 5L));
        assertEquals(false, service.doctorMayView("dr.other", 5L));
    }

    @Test
    void savesAValidSetOfVitalsAndLogsIt() {
        service.saveVitals(9L, 120, 80, 72, new BigDecimal("37.0"), new BigDecimal("68"), null, 98, "att1");
        verify(vitals).save(any());
        verify(accessLog).save(any());
    }

    @Test
    void rejectsImpossibleOrIncompleteVitals() {
        assertThrows(BookingException.class, () -> service.saveVitals(9L, 400, 80, null, null, null, null, null, "x"));      // BP too high
        assertThrows(BookingException.class, () -> service.saveVitals(9L, 120, null, null, null, null, null, null, "x"));    // one BP number only
        assertThrows(BookingException.class, () -> service.saveVitals(9L, 80, 120, null, null, null, null, null, "x"));      // diastolic above systolic
        assertThrows(BookingException.class, () -> service.saveVitals(9L, null, null, null, new BigDecimal("50"), null, null, null, "x")); // 50 degrees
        assertThrows(BookingException.class, () -> service.saveVitals(9L, null, null, null, null, null, null, null, "x"));   // nothing entered
        verify(vitals, never()).save(any());
    }

    @Test
    void profileRejectsAnUnknownBloodGroup() {
        assertThrows(BookingException.class, () -> service.saveProfile(5L, "Z+", null, null, null, "dr"));
        service.saveProfile(5L, "o+", "Penicillin", " ", null, "dr");
        verify(profiles).save(any(MedicalProfile.class));
    }
}
