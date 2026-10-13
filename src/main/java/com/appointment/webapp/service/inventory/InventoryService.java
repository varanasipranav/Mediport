package com.appointment.webapp.service.inventory;

import com.appointment.webapp.model.Prescription;
import com.appointment.webapp.model.inventory.DispenseRecord;
import com.appointment.webapp.model.inventory.Medicine;
import com.appointment.webapp.model.inventory.StockBatch;
import com.appointment.webapp.model.inventory.StockMovement;
import com.appointment.webapp.repository.PrescriptionRepository;
import com.appointment.webapp.repository.inventory.DispenseRecordRepository;
import com.appointment.webapp.repository.inventory.MedicineRepository;
import com.appointment.webapp.repository.inventory.StockBatchRepository;
import com.appointment.webapp.repository.inventory.StockMovementRepository;
import com.appointment.webapp.service.BookingException;
import com.appointment.webapp.service.RxDocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Stock for the clinic pharmacy: catalogue, batches with expiry, FEFO dispensing against prescriptions,
 * and an audit trail of every movement.
 */
@Service
@Transactional
public class InventoryService {

    private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);

    private final MedicineRepository medicines;
    private final StockBatchRepository batches;
    private final StockMovementRepository movements;
    private final DispenseRecordRepository dispenses;
    private final PrescriptionRepository prescriptions;
    private final RxDocumentService rxDocumentService;

    public InventoryService(MedicineRepository medicines, StockBatchRepository batches, StockMovementRepository movements,
                            DispenseRecordRepository dispenses, PrescriptionRepository prescriptions,
                            RxDocumentService rxDocumentService) {
        this.medicines = medicines;
        this.batches = batches;
        this.movements = movements;
        this.dispenses = dispenses;
        this.prescriptions = prescriptions;
        this.rxDocumentService = rxDocumentService;
    }

    // ------------------------------------------------------------------ views

    public enum Level { OK, LOW, OUT }

    /** A catalogue row with its live stock figures. */
    public record StockRow(Medicine medicine, int available, int expiringSoon, LocalDate nextExpiry, Level level) {}

    @Transactional(readOnly = true)
    public List<StockRow> stockOverview() {
        LocalDate today = LocalDate.now();
        Map<Long, Integer> totals = new HashMap<>();
        for (Object[] row : batches.sellableTotals(today)) {
            totals.put((Long) row[0], ((Number) row[1]).intValue());
        }
        Map<Long, LocalDate> nextExpiry = new HashMap<>();
        Map<Long, Integer> soon = new HashMap<>();
        for (StockBatch b : batches.findExpiringBy(today.plusDays(60))) {
            if (b.isExpired(today)) continue;
            nextExpiry.merge(b.getMedicine().getId(), b.getExpiryDate(), (a, c) -> a.isBefore(c) ? a : c);
            soon.merge(b.getMedicine().getId(), b.getQuantityAvailable(), Integer::sum);
        }

        List<StockRow> rows = new ArrayList<>();
        for (Medicine m : medicines.findByActiveTrueOrderByNameAsc()) {
            int available = totals.getOrDefault(m.getId(), 0);
            Level level = available <= 0 ? Level.OUT : (available <= m.getReorderLevel() ? Level.LOW : Level.OK);
            rows.add(new StockRow(m, available, soon.getOrDefault(m.getId(), 0), nextExpiry.get(m.getId()), level));
        }
        return rows;
    }

    @Transactional(readOnly = true)
    public Medicine getMedicine(Long id) {
        return medicines.findById(id).orElseThrow(() -> new BookingException("Medicine not found"));
    }

    @Transactional(readOnly = true)
    public List<StockBatch> batchesFor(Long medicineId) {
        return batches.findByMedicine(medicineId);
    }

    @Transactional(readOnly = true)
    public List<StockBatch> expiringWithin(int days) {
        return batches.findExpiringBy(LocalDate.now().plusDays(days));
    }

    @Transactional(readOnly = true)
    public List<StockMovement> recentMovements() {
        return movements.findRecent();
    }

    @Transactional(readOnly = true)
    public List<StockMovement> recentMovements(Long medicineId) {
        return movements.findRecentForMedicine(medicineId);
    }

    /** Autocomplete for the prescription form: name plus how many are sellable right now. */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> search(String query) {
        if (query == null || query.isBlank()) return List.of();
        LocalDate today = LocalDate.now();
        Map<Long, Integer> totals = new HashMap<>();
        for (Object[] row : batches.sellableTotals(today)) {
            totals.put((Long) row[0], ((Number) row[1]).intValue());
        }
        List<Map<String, Object>> out = new ArrayList<>();
        for (Medicine m : medicines.search(query.trim())) {
            if (out.size() >= 10) break;
            Map<String, Object> item = new HashMap<>();
            item.put("id", m.getId());
            item.put("name", m.getDisplayName());
            item.put("generic", m.getGenericName() == null ? "" : m.getGenericName());
            item.put("inStock", totals.getOrDefault(m.getId(), 0));
            out.add(item);
        }
        return out;
    }

    // ------------------------------------------------------------------ catalogue

    public Medicine saveMedicine(Long id, String name, String genericName, String strength, String form,
                                 String unit, Integer reorderLevel, BigDecimal defaultPrice) {
        if (name == null || name.isBlank()) throw new BookingException("Medicine name is required");
        String s = strength == null ? "" : strength.trim();
        String f = form == null || form.isBlank() ? "Tablet" : form.trim();

        Medicine m;
        if (id == null) {
            if (medicines.existsByNameIgnoreCaseAndStrengthIgnoreCaseAndFormIgnoreCase(name.trim(), s, f)) {
                throw new BookingException("This medicine already exists in the catalogue");
            }
            m = new Medicine();
        } else {
            m = getMedicine(id);
        }
        m.setName(name.trim());
        m.setGenericName(genericName == null || genericName.isBlank() ? null : genericName.trim());
        m.setStrength(s);
        m.setForm(f);
        m.setUnit(unit == null || unit.isBlank() ? "unit" : unit.trim());
        m.setReorderLevel(reorderLevel == null || reorderLevel < 0 ? 10 : reorderLevel);
        m.setDefaultPrice(defaultPrice);
        return medicines.save(m);
    }

    private static final String[][] STARTER = {
        // name, generic, strength, form, unit, reorder level
        {"Dolo", "Paracetamol", "650mg", "Tablet", "tab", "50"},
        {"Crocin", "Paracetamol", "500mg", "Tablet", "tab", "50"},
        {"Brufen", "Ibuprofen", "400mg", "Tablet", "tab", "30"},
        {"Voveran", "Diclofenac", "50mg", "Tablet", "tab", "30"},
        {"Azithral", "Azithromycin", "500mg", "Tablet", "tab", "30"},
        {"Augmentin", "Amoxicillin + Clavulanic acid", "625mg", "Tablet", "tab", "30"},
        {"Amoxyclav", "Amoxicillin + Clavulanic acid", "625mg", "Tablet", "tab", "30"},
        {"Cifran", "Ciprofloxacin", "500mg", "Tablet", "tab", "20"},
        {"Pantop", "Pantoprazole", "40mg", "Tablet", "tab", "40"},
        {"Omez", "Omeprazole", "20mg", "Capsule", "cap", "40"},
        {"Domstal", "Domperidone", "10mg", "Tablet", "tab", "30"},
        {"Ondem", "Ondansetron", "4mg", "Tablet", "tab", "20"},
        {"Cetzine", "Cetirizine", "10mg", "Tablet", "tab", "40"},
        {"Montair LC", "Montelukast + Levocetirizine", "10mg", "Tablet", "tab", "30"},
        {"Zincovit", "Multivitamin + Zinc", "", "Tablet", "tab", "60"},
        {"Shelcal", "Calcium + Vitamin D3", "500mg", "Tablet", "tab", "40"},
        {"Uprise D3", "Cholecalciferol", "60000 IU", "Capsule", "cap", "20"},
        {"Glycomet", "Metformin", "500mg", "Tablet", "tab", "60"},
        {"Amlong", "Amlodipine", "5mg", "Tablet", "tab", "40"},
        {"Atorva", "Atorvastatin", "10mg", "Tablet", "tab", "40"},
        {"Ecosprin", "Aspirin", "75mg", "Tablet", "tab", "40"},
        {"Benadryl", "Diphenhydramine", "100ml", "Syrup", "bottle", "15"},
        {"Ascoril", "Bromhexine + Terbutaline", "100ml", "Syrup", "bottle", "15"},
        {"ORS", "Oral rehydration salts", "", "Powder", "sachet", "50"},
        {"Betadine", "Povidone iodine", "5%", "Ointment", "tube", "10"},
    };

    /** Adds a common starter list so a new pharmacy has something to work with. Existing entries are skipped. */
    public int seedStarterCatalogue() {
        int added = 0;
        for (String[] r : STARTER) {
            if (medicines.existsByNameIgnoreCaseAndStrengthIgnoreCaseAndFormIgnoreCase(r[0], r[2], r[3])) continue;
            Medicine m = new Medicine();
            m.setName(r[0]);
            m.setGenericName(r[1]);
            m.setStrength(r[2]);
            m.setForm(r[3]);
            m.setUnit(r[4]);
            m.setReorderLevel(Integer.parseInt(r[5]));
            medicines.save(m);
            added++;
        }
        return added;
    }

    public void deactivateMedicine(Long id) {
        Medicine m = getMedicine(id);
        m.setActive(false);
        medicines.save(m);
    }

    // ------------------------------------------------------------------ stock in / adjust

    public StockBatch receiveStock(Long medicineId, String batchNo, LocalDate expiry, Integer quantity,
                                   BigDecimal unitCost, BigDecimal unitPrice, String supplier, String user) {
        Medicine m = getMedicine(medicineId);
        if (batchNo == null || batchNo.isBlank()) throw new BookingException("Batch number is required");
        if (quantity == null || quantity <= 0) throw new BookingException("Quantity must be greater than zero");
        if (expiry == null) throw new BookingException("Expiry date is required");
        if (!expiry.isAfter(LocalDate.now())) throw new BookingException("This batch is already expired");
        if (batches.existsByMedicineIdAndBatchNoIgnoreCase(medicineId, batchNo.trim())) {
            throw new BookingException("Batch " + batchNo.trim() + " already exists for this medicine");
        }

        StockBatch b = new StockBatch();
        b.setMedicine(m);
        b.setBatchNo(batchNo.trim());
        b.setExpiryDate(expiry);
        b.setQuantityReceived(quantity);
        b.setQuantityAvailable(quantity);
        b.setUnitCost(unitCost);
        b.setUnitPrice(unitPrice != null ? unitPrice : m.getDefaultPrice());
        b.setSupplier(supplier == null || supplier.isBlank() ? null : supplier.trim());
        b.setReceivedAt(LocalDateTime.now());
        b.setReceivedBy(user);
        b = batches.save(b);

        record(m, b, StockMovement.Type.RECEIVE, quantity, null, "Received from " + (b.getSupplier() == null ? "supplier" : b.getSupplier()), user);
        logger.info("Received {} x {} (batch {})", quantity, m.getDisplayName(), b.getBatchNo());
        return b;
    }

    /** Set a batch to a counted quantity (stock-take correction, damage, loss). */
    public void adjustBatch(Long batchId, Integer newQuantity, String reason, String user) {
        StockBatch b = batches.findById(batchId).orElseThrow(() -> new BookingException("Batch not found"));
        if (newQuantity == null || newQuantity < 0) throw new BookingException("Quantity cannot be negative");
        if (reason == null || reason.isBlank()) throw new BookingException("Please give a reason for the adjustment");
        int delta = newQuantity - b.getQuantityAvailable();
        if (delta == 0) return;
        b.setQuantityAvailable(newQuantity);
        batches.save(b);
        record(b.getMedicine(), b, StockMovement.Type.ADJUST, delta, null, reason.trim(), user);
    }

    /** Remove all remaining stock of expired batches from the shelf (kept in the audit trail). */
    public int writeOffExpired(String user) {
        LocalDate today = LocalDate.now();
        int count = 0;
        for (StockBatch b : batches.findExpiringBy(today.minusDays(1))) {
            if (b.getQuantityAvailable() > 0) {
                record(b.getMedicine(), b, StockMovement.Type.EXPIRED, -b.getQuantityAvailable(), null, "Expired " + b.getExpiryDate(), user);
                b.setQuantityAvailable(0);
                batches.save(b);
                count++;
            }
        }
        return count;
    }

    // ------------------------------------------------------------------ dispensing

    /** One prescription line with its catalogue match and a suggested quantity. */
    public record DispenseLine(RxDocumentService.MedicineLine line, Medicine match, Integer suggestedQty, int inStock) {}

    @Transactional(readOnly = true)
    public List<DispenseLine> planDispense(Prescription p) {
        LocalDate today = LocalDate.now();
        Map<Long, Integer> totals = new HashMap<>();
        for (Object[] row : batches.sellableTotals(today)) {
            totals.put((Long) row[0], ((Number) row[1]).intValue());
        }
        List<Medicine> catalogue = medicines.findByActiveTrueOrderByNameAsc();
        List<DispenseLine> plan = new ArrayList<>();
        for (RxDocumentService.MedicineLine line : rxDocumentService.parseMedicines(p.getMedicines())) {
            Medicine match = bestMatch(line.name(), catalogue);
            plan.add(new DispenseLine(line, match, suggestQuantity(line),
                match == null ? 0 : totals.getOrDefault(match.getId(), 0)));
        }
        return plan;
    }

    public record DispenseResult(DispenseRecord.Status status, List<String> messages) {}

    /**
     * Dispense against a prescription. medicineIds/quantities are parallel lists, one per prescription line
     * (null id or 0 qty = skip that line). Stock is taken earliest-expiry-first; if stock runs short the line is
     * dispensed in part and the prescription is recorded as PARTIAL.
     */
    public DispenseResult dispense(Long prescriptionId, List<Long> medicineIds, List<Integer> quantities, String notes, String user) {
        Prescription p = prescriptions.findById(prescriptionId).orElseThrow(() -> new BookingException("Prescription not found"));
        if (dispenses.findByPrescriptionId(prescriptionId).filter(d -> d.getStatus() == DispenseRecord.Status.DISPENSED).isPresent()) {
            throw new BookingException("This prescription has already been dispensed");
        }

        LocalDate today = LocalDate.now();
        List<String> messages = new ArrayList<>();
        boolean anyShort = false;
        boolean anyGiven = false;

        for (int i = 0; i < medicineIds.size(); i++) {
            Long medicineId = medicineIds.get(i);
            int wanted = i < quantities.size() && quantities.get(i) != null ? quantities.get(i) : 0;
            if (medicineId == null || wanted <= 0) continue;

            Medicine m = getMedicine(medicineId);
            int remaining = wanted;
            for (StockBatch b : batches.findSellableForUpdate(medicineId, today)) {
                if (remaining == 0) break;
                int take = Math.min(remaining, b.getQuantityAvailable());
                b.setQuantityAvailable(b.getQuantityAvailable() - take);
                batches.save(b);
                record(m, b, StockMovement.Type.DISPENSE, -take, prescriptionId, "Rx " + rxDocumentService.rxNumber(p), user);
                remaining -= take;
            }
            int given = wanted - remaining;
            if (given > 0) anyGiven = true;
            if (remaining > 0) {
                anyShort = true;
                messages.add(m.getDisplayName() + ": gave " + given + " of " + wanted + " (short by " + remaining + ")");
            } else {
                messages.add(m.getDisplayName() + ": " + given + " dispensed");
            }
        }

        if (!anyGiven) throw new BookingException("Nothing was dispensed - check quantities and stock");

        DispenseRecord record = dispenses.findByPrescriptionId(prescriptionId).orElseGet(DispenseRecord::new);
        record.setPrescription(p);
        record.setStatus(anyShort ? DispenseRecord.Status.PARTIAL : DispenseRecord.Status.DISPENSED);
        record.setDispensedBy(user);
        record.setDispensedAt(LocalDateTime.now());
        record.setNotes(notes == null || notes.isBlank() ? null : notes.trim());
        dispenses.save(record);

        return new DispenseResult(record.getStatus(), messages);
    }

    @Transactional(readOnly = true)
    public Optional<DispenseRecord> dispenseStatus(Long prescriptionId) {
        return dispenses.findByPrescriptionId(prescriptionId);
    }

    @Transactional(readOnly = true)
    public Map<Long, DispenseRecord.Status> dispenseStatuses() {
        Map<Long, DispenseRecord.Status> map = new HashMap<>();
        for (DispenseRecord d : dispenses.findAll()) {
            map.put(d.getPrescription().getId(), d.getStatus());
        }
        return map;
    }

    @Transactional(readOnly = true)
    public List<DispenseRecord> recentDispenses() {
        return dispenses.findRecent();
    }

    // ------------------------------------------------------------------ matching / quantity heuristics

    private void record(Medicine m, StockBatch b, StockMovement.Type type, int qty, Long prescriptionId, String note, String user) {
        StockMovement mv = new StockMovement();
        mv.setMedicine(m);
        mv.setBatch(b);
        mv.setMovementType(type);
        mv.setQuantity(qty);
        mv.setPrescriptionId(prescriptionId);
        mv.setNote(note);
        mv.setCreatedBy(user);
        movements.save(mv);
    }

    /** Best catalogue match for the free-text medicine a doctor typed ("Dolo 650", "Paracetamol 500mg"). */
    Medicine bestMatch(String text, List<Medicine> catalogue) {
        if (text == null) return null;
        String t = text.toLowerCase(Locale.ROOT);
        Medicine best = null;
        int bestScore = 0;
        for (Medicine m : catalogue) {
            String name = m.getName().toLowerCase(Locale.ROOT);
            int score = 0;
            if (t.contains(name)) {
                score = name.length() * 2;
            } else if (m.getGenericName() != null && t.contains(m.getGenericName().toLowerCase(Locale.ROOT))) {
                score = m.getGenericName().length();
            }
            if (score == 0) continue;
            String strength = m.getStrength() == null ? "" : m.getStrength().toLowerCase(Locale.ROOT).replace(" ", "");
            if (!strength.isEmpty() && t.replace(" ", "").contains(strength)) score += 10;
            if (score > bestScore) {
                best = m;
                bestScore = score;
            }
        }
        return best;
    }

    private static final Pattern NUMBER = Pattern.compile("(\\d+(?:\\.\\d+)?)");
    private static final Pattern PATTERN_101 = Pattern.compile("\\b(\\d)\\s*-\\s*(\\d)\\s*-\\s*(\\d)(?:\\s*-\\s*(\\d))?\\b");
    private static final Pattern DAYS = Pattern.compile("(\\d+)\\s*(day|days|d|week|weeks|wk|month|months)\\b");

    /**
     * Quantity needed = units per dose x doses per day x days, read from text such as
     * "1 tab twice daily after meals" / "1-0-1" and "5 days". Returns null if it cannot be worked out,
     * in which case the pharmacist types the quantity.
     */
    Integer suggestQuantity(RxDocumentService.MedicineLine line) {
        String ins = (line.instructions() == null ? "" : line.instructions()).toLowerCase(Locale.ROOT);
        String dur = (line.duration() == null ? "" : line.duration()).toLowerCase(Locale.ROOT);

        int days = -1;
        Matcher dm = DAYS.matcher(dur.isEmpty() ? ins : dur);
        if (dm.find()) {
            int n = Integer.parseInt(dm.group(1));
            String u = dm.group(2);
            days = u.startsWith("week") || u.equals("wk") ? n * 7 : u.startsWith("month") ? n * 30 : n;
        }
        if (days <= 0) return null;

        Matcher pm = PATTERN_101.matcher(ins);
        if (pm.find()) {
            int perDay = 0;
            for (int g = 1; g <= 4; g++) {
                if (pm.group(g) != null) perDay += Integer.parseInt(pm.group(g));
            }
            return perDay > 0 ? perDay * days : null;
        }

        double perDose = 1;
        Matcher nm = NUMBER.matcher(ins);
        if (nm.find()) perDose = Double.parseDouble(nm.group(1));
        if (perDose > 10) perDose = 1; // a strength like "500" is not a dose count

        int freq;
        if (ins.contains("four times") || ins.contains("qid") || ins.contains("qds")) freq = 4;
        else if (ins.contains("thrice") || ins.contains("three times") || ins.contains("tds") || ins.contains("tid") || ins.contains("3 times")) freq = 3;
        else if (ins.contains("twice") || ins.contains("bd") || ins.contains("bid") || ins.contains("2 times")) freq = 2;
        else if (ins.contains("once") || ins.contains("daily") || ins.contains("od") || ins.contains("at night") || ins.contains("bedtime")) freq = 1;
        else return null;

        return (int) Math.ceil(perDose * freq * days);
    }
}
