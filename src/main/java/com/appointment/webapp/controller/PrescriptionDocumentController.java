package com.appointment.webapp.controller;

import com.appointment.webapp.model.Prescription;
import com.appointment.webapp.model.User;
import com.appointment.webapp.repository.UserRepository;
import com.appointment.webapp.service.PrescriptionService;
import com.appointment.webapp.service.RxDocumentService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * PDF download for a prescription (role-checked) and the public verification page the QR code points to.
 */
@Controller
public class PrescriptionDocumentController {

    private final PrescriptionService prescriptionService;
    private final RxDocumentService rxDocumentService;
    private final UserRepository userRepository;

    public PrescriptionDocumentController(PrescriptionService prescriptionService,
                                          RxDocumentService rxDocumentService,
                                          UserRepository userRepository) {
        this.prescriptionService = prescriptionService;
        this.rxDocumentService = rxDocumentService;
        this.userRepository = userRepository;
    }

    @GetMapping("/prescriptions/{id}/pdf")
    public ResponseEntity<byte[]> pdf(@PathVariable Long id, Authentication authentication) {
        Prescription prescription = prescriptionService.getPrescriptionById(id);
        if (!canView(prescription, authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        byte[] pdf = rxDocumentService.buildPdf(prescription);
        String filename = rxDocumentService.rxNumber(prescription) + ".pdf";
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline().filename(filename).build().toString())
            .body(pdf);
    }

    /** Public: scanned from the QR code on a printed prescription. Shows only non-sensitive facts. */
    @GetMapping("/verify/rx/{id}/{signature}")
    public String verify(@PathVariable Long id, @PathVariable String signature, Model model) {
        boolean valid = rxDocumentService.verify(id, signature);
        model.addAttribute("valid", valid);
        if (valid) {
            Prescription p = prescriptionService.getPrescriptionById(id);
            model.addAttribute("rxNumber", rxDocumentService.rxNumber(p));
            model.addAttribute("issuedOn", p.getCreatedAt());
            model.addAttribute("doctorName", "Dr. " + RxDocumentService.fullName(p.getDoctor()));
            String first = p.getPatient() != null && p.getPatient().getFirstName() != null ? p.getPatient().getFirstName() : "";
            model.addAttribute("patientName", first.isEmpty() ? "-" : first.charAt(0) + "*".repeat(Math.max(first.length() - 1, 2)));
        }
        return "rx-verify";
    }

    private boolean canView(Prescription p, Authentication auth) {
        if (auth == null) return false;
        boolean admin = hasRole(auth, "ROLE_ADMIN");
        boolean pharma = hasRole(auth, "ROLE_PHARMA");
        if (admin || pharma) return true;
        User me = userRepository.findByUsername(auth.getName()).orElse(null);
        if (me == null) return false;
        boolean isDoctor = p.getDoctor() != null && p.getDoctor().getId().equals(me.getId());
        boolean isPatient = p.getPatient() != null && p.getPatient().getId().equals(me.getId());
        return isDoctor || isPatient;
    }

    private boolean hasRole(Authentication auth, String role) {
        for (GrantedAuthority a : auth.getAuthorities()) {
            if (role.equals(a.getAuthority())) return true;
        }
        return false;
    }
}
