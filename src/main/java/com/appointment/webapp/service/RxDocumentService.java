package com.appointment.webapp.service;

import com.appointment.webapp.config.ClinicProperties;
import com.appointment.webapp.model.Prescription;
import com.appointment.webapp.model.User;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Everything needed to present a prescription as a document: parsing the free-text medicine list into
 * table rows, a signed QR code for verification, and a PDF rendering.
 */
@Service
public class RxDocumentService {

    private static final Logger logger = LoggerFactory.getLogger(RxDocumentService.class);

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final Pattern LIST_PREFIX = Pattern.compile("^\\s*(\\d+\\s*[.)]|[-•*])\\s*");
    private static final Pattern SEPARATOR = Pattern.compile("\\s+[—–-]\\s+|\\s*\\|\\s*|\\s*;\\s*");

    private static final Color BRAND = new Color(0x0B, 0x4F, 0xB8);
    private static final Color INK = new Color(0x0F, 0x1B, 0x2D);
    private static final Color MUTED = new Color(0x5B, 0x6B, 0x85);
    private static final Color LINE = new Color(0xD5, 0xDC, 0xE7);
    private static final Color TINT = new Color(0xEA, 0xF1, 0xFC);

    private final ClinicProperties clinic;
    private final String publicUrl;
    private final byte[] signingKey;

    public RxDocumentService(ClinicProperties clinic,
                             @Value("${app.public-url:http://localhost:8081}") String publicUrl,
                             @Value("${app.rx.signing-key:}") String signingKey) {
        this.clinic = clinic;
        this.publicUrl = publicUrl.endsWith("/") ? publicUrl.substring(0, publicUrl.length() - 1) : publicUrl;
        if (signingKey == null || signingKey.isBlank()) {
            logger.warn("app.rx.signing-key is not set - prescription QR codes will stop verifying after a restart");
            signingKey = UUID.randomUUID().toString();
        }
        this.signingKey = signingKey.getBytes(StandardCharsets.UTF_8);
    }

    /** One row of the medicine table. */
    public record MedicineLine(int no, String name, String instructions, String duration) {}

    /**
     * Turns the doctor's free text ("1. Paracetamol 500mg — 1 tab twice daily — 3 days") into table rows.
     * Lines that don't follow the pattern are kept whole in the name column so nothing is ever lost.
     */
    public List<MedicineLine> parseMedicines(String text) {
        List<MedicineLine> lines = new ArrayList<>();
        if (text == null) return lines;
        int no = 1;
        for (String raw : text.split("\\R")) {
            String line = LIST_PREFIX.matcher(raw).replaceFirst("").trim();
            if (line.isEmpty()) continue;
            String[] parts = SEPARATOR.split(line);
            String name = parts[0].trim();
            String instructions = "";
            String duration = "";
            if (parts.length == 2) {
                instructions = parts[1].trim();
            } else if (parts.length >= 3) {
                duration = parts[parts.length - 1].trim();
                StringBuilder mid = new StringBuilder();
                for (int i = 1; i < parts.length - 1; i++) {
                    if (mid.length() > 0) mid.append(", ");
                    mid.append(parts[i].trim());
                }
                instructions = mid.toString();
            }
            lines.add(new MedicineLine(no++, name, instructions, duration));
        }
        return lines;
    }

    // ---------------------------------------------------------------- verification

    public String sign(Long prescriptionId) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(signingKey, "HmacSHA256"));
            byte[] h = mac.doFinal(("rx:" + prescriptionId).getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 8; i++) sb.append(String.format("%02x", h[i]));
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot sign prescription", e);
        }
    }

    public boolean verify(Long prescriptionId, String signature) {
        return signature != null && MessageDigest.isEqual(
            sign(prescriptionId).getBytes(StandardCharsets.UTF_8), signature.getBytes(StandardCharsets.UTF_8));
    }

    /** Public PDF link that stops working after a week: /rx/pdf/{id}/{expiryEpochDay}/{signature}. */
    public String signedPdfUrl(Long prescriptionId) {
        long expiry = LocalDate.now().plusDays(7).toEpochDay();
        return publicUrl + "/rx/pdf/" + prescriptionId + "/" + expiry + "/" + sign(prescriptionId + ":" + expiry);
    }

    public boolean verifyPdfLink(Long prescriptionId, long expiryEpochDay, String signature) {
        return LocalDate.now().toEpochDay() <= expiryEpochDay
            && signature != null
            && MessageDigest.isEqual(sign(prescriptionId + ":" + expiryEpochDay).getBytes(StandardCharsets.UTF_8),
                                     signature.getBytes(StandardCharsets.UTF_8));
    }

    public String sign(String payload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(signingKey, "HmacSHA256"));
            byte[] h = mac.doFinal(("rx:" + payload).getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 12; i++) sb.append(String.format("%02x", h[i]));
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot sign link", e);
        }
    }

    public String verifyUrl(Long prescriptionId) {
        return publicUrl + "/verify/rx/" + prescriptionId + "/" + sign(prescriptionId);
    }

    // ---------------------------------------------------------------- QR

    private BufferedImage qrImage(String text, int size) {
        try {
            var hints = java.util.Map.of(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M, EncodeHintType.MARGIN, 1);
            BitMatrix m = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size, size, hints);
            BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < size; x++)
                for (int y = 0; y < size; y++)
                    img.setRGB(x, y, m.get(x, y) ? 0x0F1B2D : 0xFFFFFF);
            return img;
        } catch (Exception e) {
            throw new IllegalStateException("QR generation failed", e);
        }
    }

    public String qrDataUri(String text, int size) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            javax.imageio.ImageIO.write(qrImage(text, size), "png", out);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    // ---------------------------------------------------------------- model for the HTML document

    /** Adds everything the shared prescription document fragment needs. */
    public void addDocumentModel(Model model, Prescription p) {
        model.addAttribute("prescription", p);
        model.addAttribute("medicineLines", parseMedicines(p.getMedicines()));
        model.addAttribute("rxNumber", rxNumber(p));
        model.addAttribute("rxQr", qrDataUri(verifyUrl(p.getId()), 220));
    }

    public String rxNumber(Prescription p) {
        return String.format("RX-%s-%05d",
            p.getCreatedAt() != null ? p.getCreatedAt().getYear() : LocalDate.now().getYear(), p.getId());
    }

    public static String fullName(User u) {
        if (u == null) return "";
        return ((u.getFirstName() == null ? "" : u.getFirstName()) + " " + (u.getLastName() == null ? "" : u.getLastName())).trim();
    }

    // ---------------------------------------------------------------- PDF

    private static Font font(float size, int style, Color color) {
        return FontFactory.getFont(FontFactory.HELVETICA, size, style, color);
    }

    /** Helvetica only covers Latin text; swap characters it cannot draw so a prescription never fails to print. */
    private static String pdfSafe(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder(s.length());
        for (char c : s.toCharArray()) {
            sb.append(c <= 0xFF || c == '—' || c == '–' || c == '•' || c == '’' ? c : '?');
        }
        return sb.toString();
    }

    public byte[] buildPdf(Prescription p) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document doc = new Document(PageSize.A4, 40, 40, 36, 40);
            PdfWriter.getInstance(doc, out);
            doc.addTitle(rxNumber(p));
            doc.addAuthor(clinic.getName());
            doc.open();

            User doctor = p.getDoctor();
            User patient = p.getPatient();

            // ----- letterhead
            PdfPTable head = new PdfPTable(new float[] {3f, 2f});
            head.setWidthPercentage(100);
            PdfPCell left = noBorder();
            left.addElement(new Paragraph(pdfSafe(clinic.getName()), font(20, Font.BOLD, BRAND)));
            if (!clinic.getTagline().isBlank())
                left.addElement(new Paragraph(pdfSafe(clinic.getTagline()), font(9, Font.NORMAL, MUTED)));
            String contact = joinNonBlank("  |  ", clinic.getAddress(), clinic.getPhone(), clinic.getEmail());
            if (!contact.isBlank()) left.addElement(new Paragraph(pdfSafe(contact), font(8.5f, Font.NORMAL, MUTED)));
            head.addCell(left);

            PdfPCell right = noBorder();
            right.setHorizontalAlignment(Element.ALIGN_RIGHT);
            Paragraph dn = new Paragraph("Dr. " + pdfSafe(fullName(doctor)), font(13, Font.BOLD, INK));
            dn.setAlignment(Element.ALIGN_RIGHT);
            right.addElement(dn);
            String qual = joinNonBlank(" - ", doctor != null ? doctor.getSpecialization() : null, doctor != null ? doctor.getDepartment() : null);
            if (!qual.isBlank()) { Paragraph q = new Paragraph(pdfSafe(qual), font(9, Font.NORMAL, MUTED)); q.setAlignment(Element.ALIGN_RIGHT); right.addElement(q); }
            if (doctor != null && doctor.getLicenseNumber() != null && !doctor.getLicenseNumber().isBlank()) {
                Paragraph l = new Paragraph("Reg. No: " + pdfSafe(doctor.getLicenseNumber()), font(9, Font.NORMAL, MUTED));
                l.setAlignment(Element.ALIGN_RIGHT);
                right.addElement(l);
            }
            head.addCell(right);
            doc.add(head);

            PdfPTable rule = new PdfPTable(1);
            rule.setWidthPercentage(100);
            rule.setSpacingBefore(8);
            rule.setSpacingAfter(10);
            PdfPCell rc = new PdfPCell(new Phrase(" ", font(1, Font.NORMAL, BRAND)));
            rc.setBorder(Rectangle.BOTTOM);
            rc.setBorderColorBottom(BRAND);
            rc.setBorderWidthBottom(2f);
            rule.addCell(rc);
            doc.add(rule);

            // ----- patient strip
            PdfPTable strip = new PdfPTable(new float[] {2.2f, 1.2f, 1.4f, 1.4f});
            strip.setWidthPercentage(100);
            strip.addCell(infoCell("PATIENT", fullName(patient)));
            strip.addCell(infoCell("AGE / SEX", joinNonBlank(" / ", patient != null && patient.getAge() != null ? patient.getAge() + " yrs" : null,
                patient != null ? patient.getGender() : null)));
            strip.addCell(infoCell("PATIENT ID", patient != null && patient.getPatientId() != null ? patient.getPatientId() : "-"));
            strip.addCell(infoCell("DATE", (p.getCreatedAt() != null ? p.getCreatedAt().toLocalDate() : LocalDate.now()).format(DATE_FMT)));
            doc.add(strip);

            PdfPTable meta = new PdfPTable(1);
            meta.setWidthPercentage(100);
            PdfPCell mc = noBorder();
            mc.setPaddingTop(4);
            mc.addElement(new Paragraph(rxNumber(p), font(8.5f, Font.NORMAL, MUTED)));
            meta.addCell(mc);
            doc.add(meta);

            // ----- diagnosis
            if (notBlank(p.getObservations())) {
                section(doc, "DIAGNOSIS / OBSERVATIONS", p.getObservations());
            }

            // ----- Rx + medicines
            Paragraph rx = new Paragraph("Rx", FontFactory.getFont(FontFactory.TIMES_BOLDITALIC, 26, Font.BOLDITALIC, BRAND));
            rx.setSpacingBefore(10);
            rx.setSpacingAfter(4);
            doc.add(rx);

            PdfPTable meds = new PdfPTable(new float[] {0.5f, 3.2f, 3.2f, 1.4f});
            meds.setWidthPercentage(100);
            for (String h : new String[] {"#", "MEDICINE", "DOSE & INSTRUCTIONS", "DURATION"}) {
                PdfPCell hc = new PdfPCell(new Phrase(h, font(8, Font.BOLD, BRAND)));
                hc.setBackgroundColor(TINT);
                hc.setBorder(Rectangle.NO_BORDER);
                hc.setPadding(6);
                meds.addCell(hc);
            }
            List<MedicineLine> lines = parseMedicines(p.getMedicines());
            for (MedicineLine m : lines) {
                meds.addCell(medCell(String.valueOf(m.no()), Font.NORMAL, MUTED));
                meds.addCell(medCell(m.name(), Font.BOLD, INK));
                meds.addCell(medCell(m.instructions(), Font.NORMAL, INK));
                meds.addCell(medCell(m.duration(), Font.NORMAL, INK));
            }
            doc.add(meds);

            if (notBlank(p.getTests())) section(doc, "TESTS ADVISED", p.getTests());
            if (notBlank(p.getAdditionalNotes())) section(doc, "ADVICE / NOTES", p.getAdditionalNotes());
            if (p.getNextVisitDate() != null) {
                Paragraph nv = new Paragraph();
                nv.setSpacingBefore(10);
                nv.add(new Phrase("Next visit: ", font(10, Font.BOLD, INK)));
                nv.add(new Phrase(p.getNextVisitDate().format(DATE_FMT), font(10, Font.NORMAL, INK)));
                doc.add(nv);
            }

            // ----- signature + QR
            PdfPTable foot = new PdfPTable(new float[] {1.2f, 2f, 1.6f});
            foot.setWidthPercentage(100);
            foot.setSpacingBefore(34);
            PdfPCell qrCell = noBorder();
            Image qr = Image.getInstance(qrImage(verifyUrl(p.getId()), 220), null);
            qr.scaleToFit(74, 74);
            qrCell.addElement(qr);
            qrCell.addElement(new Paragraph("Scan to verify", font(7.5f, Font.NORMAL, MUTED)));
            foot.addCell(qrCell);
            foot.addCell(noBorder());
            PdfPCell sig = noBorder();
            sig.setVerticalAlignment(Element.ALIGN_BOTTOM);
            Paragraph line = new Paragraph("_______________________________", font(10, Font.NORMAL, LINE));
            line.setAlignment(Element.ALIGN_CENTER);
            sig.addElement(line);
            Paragraph sn = new Paragraph("Dr. " + pdfSafe(fullName(doctor)), font(10, Font.BOLD, INK));
            sn.setAlignment(Element.ALIGN_CENTER);
            sig.addElement(sn);
            Paragraph ss = new Paragraph("Signature", font(8, Font.NORMAL, MUTED));
            ss.setAlignment(Element.ALIGN_CENTER);
            sig.addElement(ss);
            foot.addCell(sig);
            doc.add(foot);

            Paragraph disclaimer = new Paragraph(
                "This is a computer-generated prescription from " + pdfSafe(clinic.getName()) +
                ". Take medicines only as directed by your doctor.", font(7.5f, Font.ITALIC, MUTED));
            disclaimer.setSpacingBefore(16);
            disclaimer.setAlignment(Element.ALIGN_CENTER);
            doc.add(disclaimer);

            doc.close();
            return out.toByteArray();
        } catch (DocumentException | IOException e) {
            throw new IllegalStateException("Could not build prescription PDF", e);
        }
    }

    private static boolean notBlank(String s) {
        return s != null && !s.isBlank();
    }

    private static String joinNonBlank(String sep, String... parts) {
        StringBuilder sb = new StringBuilder();
        for (String s : parts) {
            if (s != null && !s.isBlank()) {
                if (sb.length() > 0) sb.append(sep);
                sb.append(s.trim());
            }
        }
        return sb.toString();
    }

    private static PdfPCell noBorder() {
        PdfPCell c = new PdfPCell();
        c.setBorder(Rectangle.NO_BORDER);
        return c;
    }

    private static PdfPCell infoCell(String label, String value) {
        PdfPCell c = new PdfPCell();
        c.setBorder(Rectangle.NO_BORDER);
        c.setBackgroundColor(new Color(0xF7, 0xF9, 0xFC));
        c.setPadding(7);
        c.addElement(new Paragraph(label, font(7, Font.BOLD, MUTED)));
        c.addElement(new Paragraph(pdfSafe(value), font(10.5f, Font.BOLD, INK)));
        return c;
    }

    private static PdfPCell medCell(String text, int style, Color color) {
        PdfPCell c = new PdfPCell(new Phrase(pdfSafe(text), font(10, style, color)));
        c.setBorder(Rectangle.BOTTOM);
        c.setBorderColorBottom(LINE);
        c.setBorderWidthBottom(0.5f);
        c.setPadding(7);
        return c;
    }

    private static void section(Document doc, String title, String body) throws DocumentException {
        Paragraph t = new Paragraph(title, font(8, Font.BOLD, BRAND));
        t.setSpacingBefore(12);
        t.setSpacingAfter(3);
        doc.add(t);
        doc.add(new Paragraph(pdfSafe(body), font(10, Font.NORMAL, INK)));
    }
}
