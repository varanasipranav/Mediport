package com.appointment.webapp.controller.doctor;

import com.appointment.webapp.model.Appointment;
import com.appointment.webapp.model.User;
import com.appointment.webapp.service.BookingException;
import com.appointment.webapp.service.record.MedicalRecordService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * The doctor's view of a patient: allergies and conditions, measurements, and every earlier visit and
 * prescription (from any doctor of the clinic). Also where vitals are entered, by doctors or the front desk.
 */
@Controller
public class PatientRecordController {

    private final MedicalRecordService records;

    public PatientRecordController(MedicalRecordService records) {
        this.records = records;
    }

    @GetMapping("/doctor/patients/{patientId}/record")
    @PreAuthorize("hasRole('DOCTOR')")
    public String record(@PathVariable Long patientId,
                         @RequestParam(required = false) Long appointmentId,
                         Authentication auth, Model model) {
        requireAccess(auth, patientId);
        User patient = records.getPatient(patientId);
        records.logView(patientId, auth.getName());

        model.addAttribute("record", records.getRecord(patient));
        model.addAttribute("patient", patient);
        if (appointmentId != null) {
            Appointment a = records.getAppointment(appointmentId);
            if (a.getPatient().getId().equals(patientId) && a.getDoctor().getUsername().equals(auth.getName())) {
                model.addAttribute("currentAppointment", a);
            }
        }
        return "doctor/patient-record";
    }

    @PostMapping("/doctor/patients/{patientId}/profile")
    @PreAuthorize("hasRole('DOCTOR')")
    public String saveProfile(@PathVariable Long patientId,
                              @RequestParam(required = false) String bloodGroup,
                              @RequestParam(required = false) String allergies,
                              @RequestParam(required = false) String chronicConditions,
                              @RequestParam(required = false) String notes,
                              @RequestParam(required = false) Long appointmentId,
                              Authentication auth, RedirectAttributes redirect) {
        requireAccess(auth, patientId);
        try {
            records.saveProfile(patientId, bloodGroup, allergies, chronicConditions, notes, auth.getName());
            redirect.addFlashAttribute("success", "Medical profile saved.");
        } catch (BookingException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/doctor/patients/" + patientId + "/record" + (appointmentId != null ? "?appointmentId=" + appointmentId : "");
    }

    /** Measurements for one visit: entered by the doctor in charge or by the front desk. */
    @PostMapping("/vitals/{appointmentId}")
    @PreAuthorize("hasAnyRole('DOCTOR','ATTENDANT')")
    public String saveVitals(@PathVariable Long appointmentId,
                             @RequestParam(required = false) Integer systolic,
                             @RequestParam(required = false) Integer diastolic,
                             @RequestParam(required = false) Integer pulse,
                             @RequestParam(required = false) BigDecimal temperature,
                             @RequestParam(required = false) BigDecimal weight,
                             @RequestParam(required = false) BigDecimal height,
                             @RequestParam(required = false) Integer spo2,
                             Authentication auth, RedirectAttributes redirect, HttpServletRequest request) {
        Appointment appointment = records.getAppointment(appointmentId);
        boolean doctor = hasRole(auth, "ROLE_DOCTOR");
        if (doctor && !appointment.getDoctor().getUsername().equals(auth.getName())) {
            throw new AccessDeniedException("Not your appointment");
        }
        try {
            records.saveVitals(appointmentId, systolic, diastolic, pulse, temperature, weight, height, spo2, auth.getName());
            redirect.addFlashAttribute("success", "Vitals saved for " + appointment.getPatient().getFirstName() + ".");
        } catch (BookingException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        if (doctor) {
            return "redirect:/doctor/patients/" + appointment.getPatient().getId() + "/record?appointmentId=" + appointmentId;
        }
        return "redirect:/attendant/queue?doctorId=" + appointment.getDoctor().getId() + "&date=" + appointment.getAppointmentDate();
    }

    private void requireAccess(Authentication auth, Long patientId) {
        if (!records.doctorMayView(auth.getName(), patientId)) {
            throw new AccessDeniedException("This patient has no appointment with you");
        }
    }

    private boolean hasRole(Authentication auth, String role) {
        for (GrantedAuthority a : auth.getAuthorities()) {
            if (role.equals(a.getAuthority())) return true;
        }
        return false;
    }
}
