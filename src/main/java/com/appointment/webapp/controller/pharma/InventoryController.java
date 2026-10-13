package com.appointment.webapp.controller.pharma;

import com.appointment.webapp.model.Prescription;
import com.appointment.webapp.model.inventory.StockBatch;
import com.appointment.webapp.service.BookingException;
import com.appointment.webapp.service.PrescriptionService;
import com.appointment.webapp.service.RxDocumentService;
import com.appointment.webapp.service.inventory.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Pharmacy inventory screens, dispensing against prescriptions, and the medicine search used by the
 * doctor's prescription form.
 */
@Controller
public class InventoryController {

    private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);

    private final InventoryService inventory;
    private final PrescriptionService prescriptionService;
    private final RxDocumentService rxDocumentService;

    public InventoryController(InventoryService inventory, PrescriptionService prescriptionService,
                               RxDocumentService rxDocumentService) {
        this.inventory = inventory;
        this.prescriptionService = prescriptionService;
        this.rxDocumentService = rxDocumentService;
    }

    // ------------------------------------------------------------------ inventory pages

    @GetMapping("/pharma/inventory")
    @PreAuthorize("hasAnyRole('PHARMA','ADMIN')")
    public String overview(Model model) {
        var rows = inventory.stockOverview();
        model.addAttribute("rows", rows);
        model.addAttribute("lowCount", rows.stream().filter(r -> r.level() == InventoryService.Level.LOW).count());
        model.addAttribute("outCount", rows.stream().filter(r -> r.level() == InventoryService.Level.OUT).count());
        List<StockBatch> expiring = inventory.expiringWithin(60);
        LocalDate today = LocalDate.now();
        model.addAttribute("expired", expiring.stream().filter(b -> b.isExpired(today)).toList());
        model.addAttribute("expiring", expiring.stream().filter(b -> !b.isExpired(today)).toList());
        model.addAttribute("movements", inventory.recentMovements());
        model.addAttribute("today", today);
        return "pharma/inventory";
    }

    @GetMapping("/pharma/inventory/medicine/{id}")
    @PreAuthorize("hasAnyRole('PHARMA','ADMIN')")
    public String medicine(@PathVariable Long id, Model model) {
        model.addAttribute("medicine", inventory.getMedicine(id));
        model.addAttribute("batches", inventory.batchesFor(id));
        model.addAttribute("movements", inventory.recentMovements(id));
        model.addAttribute("today", LocalDate.now());
        return "pharma/inventory-medicine";
    }

    @PostMapping("/pharma/inventory/medicine/save")
    @PreAuthorize("hasAnyRole('PHARMA','ADMIN')")
    public String saveMedicine(@RequestParam(required = false) Long id,
                               @RequestParam String name,
                               @RequestParam(required = false) String genericName,
                               @RequestParam(required = false) String strength,
                               @RequestParam(required = false) String form,
                               @RequestParam(required = false) String unit,
                               @RequestParam(required = false) Integer reorderLevel,
                               @RequestParam(required = false) BigDecimal defaultPrice,
                               RedirectAttributes redirect) {
        try {
            var m = inventory.saveMedicine(id, name, genericName, strength, form, unit, reorderLevel, defaultPrice);
            redirect.addFlashAttribute("success", m.getDisplayName() + " saved");
            return id == null ? "redirect:/pharma/inventory/medicine/" + m.getId() : "redirect:/pharma/inventory/medicine/" + id;
        } catch (BookingException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return id == null ? "redirect:/pharma/inventory" : "redirect:/pharma/inventory/medicine/" + id;
        }
    }

    @PostMapping("/pharma/inventory/medicine/{id}/deactivate")
    @PreAuthorize("hasAnyRole('PHARMA','ADMIN')")
    public String deactivate(@PathVariable Long id, RedirectAttributes redirect) {
        inventory.deactivateMedicine(id);
        redirect.addFlashAttribute("success", "Medicine removed from the catalogue");
        return "redirect:/pharma/inventory";
    }

    @PostMapping("/pharma/inventory/receive")
    @PreAuthorize("hasAnyRole('PHARMA','ADMIN')")
    public String receive(@RequestParam Long medicineId,
                          @RequestParam String batchNo,
                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiryDate,
                          @RequestParam Integer quantity,
                          @RequestParam(required = false) BigDecimal unitCost,
                          @RequestParam(required = false) BigDecimal unitPrice,
                          @RequestParam(required = false) String supplier,
                          Authentication auth, RedirectAttributes redirect) {
        try {
            inventory.receiveStock(medicineId, batchNo, expiryDate, quantity, unitCost, unitPrice, supplier, auth.getName());
            redirect.addFlashAttribute("success", "Stock received: " + quantity + " units, batch " + batchNo.trim());
        } catch (BookingException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/pharma/inventory/medicine/" + medicineId;
    }

    @PostMapping("/pharma/inventory/adjust")
    @PreAuthorize("hasAnyRole('PHARMA','ADMIN')")
    public String adjust(@RequestParam Long batchId, @RequestParam Long medicineId, @RequestParam Integer quantity,
                         @RequestParam String reason, Authentication auth, RedirectAttributes redirect) {
        try {
            inventory.adjustBatch(batchId, quantity, reason, auth.getName());
            redirect.addFlashAttribute("success", "Stock adjusted");
        } catch (BookingException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/pharma/inventory/medicine/" + medicineId;
    }

    @PostMapping("/pharma/inventory/seed")
    @PreAuthorize("hasAnyRole('PHARMA','ADMIN')")
    public String seed(RedirectAttributes redirect) {
        int n = inventory.seedStarterCatalogue();
        redirect.addFlashAttribute("success", n == 0 ? "The starter list is already in your catalogue" : n + " common medicines added - now receive stock for them");
        return "redirect:/pharma/inventory";
    }

    @PostMapping("/pharma/inventory/write-off-expired")
    @PreAuthorize("hasAnyRole('PHARMA','ADMIN')")
    public String writeOff(Authentication auth, RedirectAttributes redirect) {
        int n = inventory.writeOffExpired(auth.getName());
        redirect.addFlashAttribute("success", n == 0 ? "No expired stock on the shelf" : n + " expired batch(es) written off");
        return "redirect:/pharma/inventory";
    }

    // ------------------------------------------------------------------ dispensing

    @GetMapping("/pharma/prescriptions/dispense/{id}")
    @PreAuthorize("hasRole('PHARMA')")
    public String dispenseForm(@PathVariable Long id, Model model) {
        Prescription p = prescriptionService.getPrescriptionById(id);
        rxDocumentService.addDocumentModel(model, p);
        model.addAttribute("plan", inventory.planDispense(p));
        model.addAttribute("catalogue", inventory.stockOverview());
        model.addAttribute("dispense", inventory.dispenseStatus(id).orElse(null));
        return "pharma/dispense";
    }

    @PostMapping("/pharma/prescriptions/dispense/{id}")
    @PreAuthorize("hasRole('PHARMA')")
    public String dispense(@PathVariable Long id,
                           @RequestParam(name = "medicineId", required = false) List<String> medicineIds,
                           @RequestParam(name = "quantity", required = false) List<String> quantities,
                           @RequestParam(required = false) String notes,
                           Authentication auth, RedirectAttributes redirect) {
        try {
            List<Long> ids = new ArrayList<>();
            List<Integer> qty = new ArrayList<>();
            if (medicineIds != null) {
                for (int i = 0; i < medicineIds.size(); i++) {
                    String mid = medicineIds.get(i);
                    ids.add(mid == null || mid.isBlank() ? null : Long.valueOf(mid));
                    String q = quantities != null && i < quantities.size() ? quantities.get(i) : "";
                    qty.add(q == null || q.isBlank() ? 0 : Integer.valueOf(q.trim()));
                }
            }
            var result = inventory.dispense(id, ids, qty, notes, auth.getName());
            redirect.addFlashAttribute(result.status().name().equals("PARTIAL") ? "warning" : "success",
                (result.status().name().equals("PARTIAL") ? "Partly dispensed. " : "Dispensed. ") + String.join("; ", result.messages()));
            logger.info("Prescription {} dispensed by {} ({})", id, auth.getName(), result.status());
            return "redirect:/pharma/dashboard";
        } catch (BookingException | NumberFormatException e) {
            redirect.addFlashAttribute("error", e instanceof NumberFormatException ? "Quantities must be whole numbers" : e.getMessage());
            return "redirect:/pharma/prescriptions/dispense/" + id;
        }
    }

    // ------------------------------------------------------------------ JSON for the prescription form

    @GetMapping("/api/medicines/search")
    @PreAuthorize("hasAnyRole('DOCTOR','PHARMA','ADMIN')")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> search(@RequestParam("q") String q) {
        return ResponseEntity.ok(inventory.search(q));
    }
}
