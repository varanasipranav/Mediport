package com.appointment.webapp.service;

import com.appointment.webapp.config.ClinicProperties;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RxDocumentServiceTest {

    private final RxDocumentService service = new RxDocumentService(new ClinicProperties(), "http://clinic.test", "unit-test-key");

    @Test
    void parsesNumberedLinesIntoColumns() {
        List<RxDocumentService.MedicineLine> lines = service.parseMedicines(
            "1. Paracetamol 500mg — 1 tab twice daily after meals — 3 days\n" +
            "2) Cetirizine 10mg — 1 tab at bedtime — 5 days");

        assertEquals(2, lines.size());
        assertEquals("Paracetamol 500mg", lines.get(0).name());
        assertEquals("1 tab twice daily after meals", lines.get(0).instructions());
        assertEquals("3 days", lines.get(0).duration());
        assertEquals(2, lines.get(1).no());
        assertEquals("Cetirizine 10mg", lines.get(1).name());
    }

    @Test
    void keepsLinesThatDoNotFollowThePattern() {
        List<RxDocumentService.MedicineLine> lines = service.parseMedicines("Pantop\n\nDolo 650 - twice a day\n");
        assertEquals(2, lines.size());
        assertEquals("Pantop", lines.get(0).name());
        assertEquals("", lines.get(0).instructions());
        assertEquals("Dolo 650", lines.get(1).name());
        assertEquals("twice a day", lines.get(1).instructions());
    }

    @Test
    void emptyTextGivesNoRows() {
        assertTrue(service.parseMedicines(null).isEmpty());
        assertTrue(service.parseMedicines("  \n ").isEmpty());
    }

    @Test
    void verificationCodeIsBoundToThePrescription() {
        String sig = service.sign(7L);
        assertTrue(service.verify(7L, sig));
        assertFalse(service.verify(8L, sig));
        assertFalse(service.verify(7L, "deadbeefdeadbeef"));
        assertFalse(service.verify(7L, null));
    }

    @Test
    void shareableLinkExpires() {
        long today = LocalDate.now().toEpochDay();
        String valid = service.sign(7L + ":" + (today + 3));
        assertTrue(service.verifyPdfLink(7L, today + 3, valid));
        // same signature, but pretend the link is from the past
        String old = service.sign(7L + ":" + (today - 1));
        assertFalse(service.verifyPdfLink(7L, today - 1, old));
        // tampering with the expiry invalidates the signature
        assertFalse(service.verifyPdfLink(7L, today + 30, valid));
    }
}
