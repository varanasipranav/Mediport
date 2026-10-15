package com.appointment.webapp.service.record;

import com.appointment.webapp.model.MedicalProfile;
import com.appointment.webapp.model.User;
import com.appointment.webapp.model.enums.AppointmentStatus;
import com.appointment.webapp.service.RxDocumentService.MedicineLine;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RuleBasedSummarizerTest {

    private final RuleBasedSummarizer summarizer = new RuleBasedSummarizer();

    private PatientRecord.Visit visit(LocalDate date, AppointmentStatus status, PatientRecord.Rx rx, PatientRecord.Vitals vitals) {
        return new PatientRecord.Visit(1L, date, LocalTime.of(9, 0), "Dr. Asha Rao", "General Medicine", status, null, vitals, rx);
    }

    private PatientRecord.Rx rx(String diagnosis, String... medicines) {
        List<MedicineLine> lines = new java.util.ArrayList<>();
        for (int i = 0; i < medicines.length; i++) lines.add(new MedicineLine(i + 1, medicines[i], "1 tab", "3 days"));
        return new PatientRecord.Rx(1L, "RX-2026-00001", diagnosis, lines, String.join("\n", medicines), "CBC", null, null, "Dr. Asha Rao");
    }

    @Test
    void firstTimePatientIsSaidToBeNew() {
        List<String> lines = summarizer.summarize(new User(), null, List.of());
        assertEquals(1, lines.size());
        assertTrue(lines.get(0).contains("first consultation"));
    }

    @Test
    void allergiesComeFirst() {
        MedicalProfile profile = new MedicalProfile();
        profile.setAllergies("Penicillin");
        profile.setChronicConditions("Type 2 diabetes");
        List<String> lines = summarizer.summarize(new User(), profile, List.of());
        assertTrue(lines.get(0).startsWith("⚠ Allergies: Penicillin"));
        assertTrue(lines.get(1).contains("Type 2 diabetes"));
    }

    @Test
    void repeatedMedicinesAreCountedByBrand() {
        LocalDate today = LocalDate.now();
        List<PatientRecord.Visit> visits = List.of(
            visit(today.minusDays(10), AppointmentStatus.COMPLETED, rx("Viral fever", "Dolo 650mg", "Pantop 40mg"), null),
            visit(today.minusDays(60), AppointmentStatus.COMPLETED, rx("Fever", "Dolo 500mg"), null));
        List<String> lines = summarizer.summarize(new User(), null, visits);

        assertTrue(lines.get(0).startsWith("2 previous visits since"), lines.get(0));
        assertTrue(lines.stream().anyMatch(l -> l.startsWith("Last diagnosis: Viral fever")), lines.toString());
        assertTrue(lines.stream().anyMatch(l -> l.contains("Dolo (2x)")), lines.toString());
    }

    @Test
    void cancelledVisitsDoNotCountAsPreviousVisits() {
        List<PatientRecord.Visit> visits = List.of(visit(LocalDate.now().minusDays(3), AppointmentStatus.CANCELLED, null, null));
        List<String> lines = summarizer.summarize(new User(), null, visits);
        assertTrue(lines.get(0).contains("first consultation"));
    }

    @Test
    void latestVitalsAndWeightChangeAreReported() {
        LocalDate today = LocalDate.now();
        PatientRecord.Vitals now = new PatientRecord.Vitals(130, 85, 80, new BigDecimal("37.0"), new BigDecimal("66.0"), null, 98,
            LocalDateTime.now(), "att1");
        PatientRecord.Vitals before = new PatientRecord.Vitals(120, 80, 72, null, new BigDecimal("71.0"), null, null,
            LocalDateTime.now().minusDays(90), "att1");
        List<PatientRecord.Visit> visits = List.of(
            visit(today.minusDays(1), AppointmentStatus.COMPLETED, rx("Checkup", "Pantop"), now),
            visit(today.minusDays(90), AppointmentStatus.COMPLETED, rx("Checkup", "Pantop"), before));
        List<String> lines = summarizer.summarize(new User(), null, visits);

        assertTrue(lines.stream().anyMatch(l -> l.contains("BP 130/85") && l.contains("66.0 kg")), lines.toString());
        assertTrue(lines.stream().anyMatch(l -> l.startsWith("Weight down 5.0 kg")), lines.toString());
    }

    @Test
    void brandIsTheFirstWord() {
        assertEquals("Dolo", RuleBasedSummarizer.brand("Dolo 650mg"));
        assertEquals("Pantop", RuleBasedSummarizer.brand("Pantop"));
        assertEquals("", RuleBasedSummarizer.brand(null));
    }
}
