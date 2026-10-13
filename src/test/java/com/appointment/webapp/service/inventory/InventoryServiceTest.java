package com.appointment.webapp.service.inventory;

import com.appointment.webapp.config.ClinicProperties;
import com.appointment.webapp.model.Prescription;
import com.appointment.webapp.model.inventory.DispenseRecord;
import com.appointment.webapp.model.inventory.Medicine;
import com.appointment.webapp.model.inventory.StockBatch;
import com.appointment.webapp.repository.PrescriptionRepository;
import com.appointment.webapp.repository.inventory.DispenseRecordRepository;
import com.appointment.webapp.repository.inventory.MedicineRepository;
import com.appointment.webapp.repository.inventory.StockBatchRepository;
import com.appointment.webapp.repository.inventory.StockMovementRepository;
import com.appointment.webapp.service.BookingException;
import com.appointment.webapp.service.RxDocumentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InventoryServiceTest {

    private MedicineRepository medicines;
    private StockBatchRepository batches;
    private DispenseRecordRepository dispenses;
    private PrescriptionRepository prescriptions;
    private InventoryService service;
    private final RxDocumentService rx = new RxDocumentService(new ClinicProperties(), "http://x", "k");

    @BeforeEach
    void setUp() {
        medicines = mock(MedicineRepository.class);
        batches = mock(StockBatchRepository.class);
        dispenses = mock(DispenseRecordRepository.class);
        prescriptions = mock(PrescriptionRepository.class);
        service = new InventoryService(medicines, batches, mock(StockMovementRepository.class), dispenses, prescriptions, rx);
        when(dispenses.save(any())).thenAnswer(i -> i.getArgument(0));
        when(batches.save(any())).thenAnswer(i -> i.getArgument(0));
        when(dispenses.findByPrescriptionId(anyLong())).thenReturn(Optional.empty());
    }

    private RxDocumentService.MedicineLine line(String name, String instructions, String duration) {
        return new RxDocumentService.MedicineLine(1, name, instructions, duration);
    }

    private Medicine medicine(long id, String name, String strength) {
        Medicine m = new Medicine();
        m.setId(id);
        m.setName(name);
        m.setStrength(strength);
        return m;
    }

    private StockBatch batch(Medicine m, String no, LocalDate expiry, int qty) {
        StockBatch b = new StockBatch();
        b.setMedicine(m);
        b.setBatchNo(no);
        b.setExpiryDate(expiry);
        b.setQuantityReceived(qty);
        b.setQuantityAvailable(qty);
        return b;
    }

    // ---------------------------------------------------------------- quantity suggestion

    @Test
    void quantityFromDoseFrequencyAndDays() {
        assertEquals(6, service.suggestQuantity(line("Paracetamol", "1 tab twice daily after meals", "3 days")));
        assertEquals(15, service.suggestQuantity(line("Cetirizine", "1 tab three times daily", "5 days")));
        assertEquals(7, service.suggestQuantity(line("Pantop", "1 tab once daily", "1 week")));
        assertEquals(4, service.suggestQuantity(line("Syrup", "2 tabs once daily", "2 days")));
    }

    @Test
    void quantityFromMorningNoonNightPattern() {
        assertEquals(10, service.suggestQuantity(line("Metformin", "1-0-1", "5 days")));
        assertEquals(21, service.suggestQuantity(line("Vitamin", "1-1-1", "1 week")));
    }

    @Test
    void quantityUnknownWhenDurationOrFrequencyMissing() {
        assertNull(service.suggestQuantity(line("Dolo 650", "twice a day", "")));
        assertNull(service.suggestQuantity(line("Pantop", "as advised", "5 days")));
    }

    // ---------------------------------------------------------------- matching

    @Test
    void matchesBrandNameAndPrefersMatchingStrength() {
        Medicine dolo500 = medicine(1, "Dolo", "500mg");
        Medicine dolo650 = medicine(2, "Dolo", "650mg");
        Medicine pantop = medicine(3, "Pantop", "40mg");
        List<Medicine> catalogue = List.of(dolo500, dolo650, pantop);

        assertSame(dolo650, service.bestMatch("Dolo 650mg", catalogue));
        assertSame(pantop, service.bestMatch("Pantop", catalogue));
        assertNull(service.bestMatch("Unknownium", catalogue));
    }

    // ---------------------------------------------------------------- dispensing

    private Prescription prescription() {
        Prescription p = new Prescription();
        p.setId(10L);
        return p;
    }

    @Test
    void dispensesEarliestExpiryFirst() {
        Medicine m = medicine(1, "Dolo", "650mg");
        StockBatch soon = batch(m, "A", LocalDate.now().plusDays(30), 5);
        StockBatch later = batch(m, "B", LocalDate.now().plusDays(300), 10);
        when(prescriptions.findById(10L)).thenReturn(Optional.of(prescription()));
        when(medicines.findById(1L)).thenReturn(Optional.of(m));
        when(batches.findSellableForUpdate(anyLong(), any())).thenReturn(List.of(soon, later));

        var result = service.dispense(10L, List.of(1L), List.of(8), null, "phr");

        assertEquals(DispenseRecord.Status.DISPENSED, result.status());
        assertEquals(0, soon.getQuantityAvailable());
        assertEquals(7, later.getQuantityAvailable());
    }

    @Test
    void shortStockGivesWhatIsAvailableAndMarksPartial() {
        Medicine m = medicine(1, "Dolo", "650mg");
        StockBatch only = batch(m, "A", LocalDate.now().plusDays(30), 5);
        when(prescriptions.findById(10L)).thenReturn(Optional.of(prescription()));
        when(medicines.findById(1L)).thenReturn(Optional.of(m));
        when(batches.findSellableForUpdate(anyLong(), any())).thenReturn(List.of(only));

        var result = service.dispense(10L, List.of(1L), List.of(12), null, "phr");

        assertEquals(DispenseRecord.Status.PARTIAL, result.status());
        assertEquals(0, only.getQuantityAvailable());
    }

    @Test
    void refusesWhenNothingCanBeGiven() {
        Medicine m = medicine(1, "Dolo", "650mg");
        when(prescriptions.findById(10L)).thenReturn(Optional.of(prescription()));
        when(medicines.findById(1L)).thenReturn(Optional.of(m));
        when(batches.findSellableForUpdate(anyLong(), any())).thenReturn(List.of());

        assertThrows(BookingException.class, () -> service.dispense(10L, List.of(1L), List.of(3), null, "phr"));
    }

    @Test
    void cannotDispenseTheSamePrescriptionTwice() {
        DispenseRecord done = new DispenseRecord();
        done.setStatus(DispenseRecord.Status.DISPENSED);
        when(prescriptions.findById(10L)).thenReturn(Optional.of(prescription()));
        when(dispenses.findByPrescriptionId(10L)).thenReturn(Optional.of(done));

        assertThrows(BookingException.class, () -> service.dispense(10L, List.of(1L), List.of(3), null, "phr"));
    }

    @Test
    void receivingStockValidatesInput() {
        Medicine m = medicine(1, "Dolo", "650mg");
        when(medicines.findById(1L)).thenReturn(Optional.of(m));
        LocalDate future = LocalDate.now().plusMonths(6);

        assertThrows(BookingException.class, () -> service.receiveStock(1L, "", future, 10, null, null, null, "u"));
        assertThrows(BookingException.class, () -> service.receiveStock(1L, "B1", future, 0, null, null, null, "u"));
        assertThrows(BookingException.class, () -> service.receiveStock(1L, "B1", LocalDate.now().minusDays(1), 10, null, null, null, "u"));
    }
}
