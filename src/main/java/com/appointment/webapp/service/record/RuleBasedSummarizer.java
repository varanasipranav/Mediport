package com.appointment.webapp.service.record;

import com.appointment.webapp.model.MedicalProfile;
import com.appointment.webapp.model.User;
import com.appointment.webapp.model.enums.AppointmentStatus;
import com.appointment.webapp.service.RxDocumentService.MedicineLine;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/** Rule-based "at a glance" summary: no external service, nothing leaves the server. */
@Component
public class RuleBasedSummarizer implements RecordSummarizer {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);

    @Override
    public List<String> summarize(User patient, MedicalProfile profile, List<PatientRecord.Visit> visits) {
        List<String> lines = new ArrayList<>();

        if (profile != null && profile.hasAllergies()) {
            lines.add("⚠ Allergies: " + oneLine(profile.getAllergies(), 160));
        }
        if (profile != null && profile.getChronicConditions() != null && !profile.getChronicConditions().isBlank()) {
            lines.add("Ongoing conditions: " + oneLine(profile.getChronicConditions(), 160));
        }

        List<PatientRecord.Visit> done = visits.stream()
            .filter(v -> v.status() == AppointmentStatus.COMPLETED || v.prescription() != null)
            .toList();

        if (done.isEmpty()) {
            lines.add("No previous visits on record - this looks like a first consultation.");
            return lines;
        }

        PatientRecord.Visit last = done.get(0);
        PatientRecord.Visit first = done.get(done.size() - 1);
        lines.add(done.size() + " previous visit" + (done.size() == 1 ? "" : "s") + " since " + first.date().format(DATE)
            + "; last seen " + last.date().format(DATE) + " by " + last.doctorName()
            + (last.speciality() != null && !last.speciality().isBlank() ? " (" + last.speciality() + ")" : "") + ".");

        long recent = done.stream().filter(v -> v.date().isAfter(LocalDate.now().minusDays(90))).count();
        if (recent >= 3) {
            lines.add("Seen " + recent + " times in the last 3 months - frequent visits.");
        }

        PatientRecord.Rx lastRx = done.stream().map(PatientRecord.Visit::prescription).filter(r -> r != null).findFirst().orElse(null);
        if (lastRx != null && lastRx.diagnosis() != null && !lastRx.diagnosis().isBlank()) {
            lines.add("Last diagnosis: " + oneLine(lastRx.diagnosis(), 140));
        }

        Map<String, Integer> counts = new LinkedHashMap<>();
        for (PatientRecord.Visit v : done) {
            if (v.prescription() == null) continue;
            for (MedicineLine m : v.prescription().medicines()) {
                String key = brand(m.name());
                if (!key.isEmpty()) counts.merge(key, 1, Integer::sum);
            }
        }
        String frequent = counts.entrySet().stream()
            .filter(e -> e.getValue() >= 2)
            .sorted((a, b) -> b.getValue() - a.getValue())
            .limit(5)
            .map(e -> e.getKey() + " (" + e.getValue() + "x)")
            .collect(Collectors.joining(", "));
        if (!frequent.isEmpty()) {
            lines.add("Prescribed more than once: " + frequent + ".");
        } else if (lastRx != null && !lastRx.medicines().isEmpty()) {
            lines.add("Last prescription: " + lastRx.medicines().stream().map(MedicineLine::name).limit(4)
                .collect(Collectors.joining(", ")) + ".");
        }

        PatientRecord.Vitals latest = visits.stream().map(PatientRecord.Visit::vitals)
            .filter(v -> v != null && !v.isEmpty()).findFirst().orElse(null);
        if (latest != null) {
            List<String> bits = new ArrayList<>();
            if (latest.bp() != null) bits.add("BP " + latest.bp());
            if (latest.pulse() != null) bits.add("pulse " + latest.pulse());
            if (latest.temperatureC() != null) bits.add(latest.temperatureC() + " °C");
            if (latest.weightKg() != null) bits.add(latest.weightKg() + " kg");
            if (latest.spo2() != null) bits.add("SpO2 " + latest.spo2() + "%");
            lines.add("Latest vitals (" + latest.recordedAt().toLocalDate().format(DATE) + "): " + String.join(", ", bits) + ".");

            var weights = visits.stream().map(PatientRecord.Visit::vitals)
                .filter(v -> v != null && v.weightKg() != null).toList();
            if (weights.size() >= 2) {
                double change = weights.get(0).weightKg().doubleValue() - weights.get(weights.size() - 1).weightKg().doubleValue();
                if (Math.abs(change) >= 2.0) {
                    lines.add(String.format(Locale.ENGLISH, "Weight %s %.1f kg since %s.", change > 0 ? "up" : "down",
                        Math.abs(change), weights.get(weights.size() - 1).recordedAt().toLocalDate().format(DATE)));
                }
            }
        }

        if (lastRx != null && lastRx.nextVisit() != null) {
            lines.add(lastRx.nextVisit().isBefore(LocalDate.now())
                ? "Follow-up was advised for " + lastRx.nextVisit().format(DATE) + "."
                : "Follow-up advised on " + lastRx.nextVisit().format(DATE) + ".");
        }
        if (lastRx != null && lastRx.tests() != null && !lastRx.tests().isBlank()) {
            lines.add("Tests advised last time: " + oneLine(lastRx.tests(), 120));
        }
        return lines;
    }

    /** "Dolo 650mg" -> "Dolo": the brand is what repeats between prescriptions. */
    static String brand(String medicineName) {
        if (medicineName == null) return "";
        String trimmed = medicineName.trim();
        int space = trimmed.indexOf(' ');
        String word = space > 0 ? trimmed.substring(0, space) : trimmed;
        return word.replaceAll("[^\\p{L}\\p{N}-]", "");
    }

    private static String oneLine(String text, int max) {
        String flat = text.replaceAll("\\s+", " ").trim();
        return flat.length() <= max ? flat : flat.substring(0, max - 1) + "…";
    }
}
