package com.appointment.webapp.repository.inventory;

import com.appointment.webapp.model.inventory.StockBatch;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface StockBatchRepository extends JpaRepository<StockBatch, Long> {

    boolean existsByMedicineIdAndBatchNoIgnoreCase(Long medicineId, String batchNo);

    /** Sellable batches, earliest expiry first (FEFO), locked so two dispenses can't take the same stock. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM StockBatch b WHERE b.medicine.id = :medicineId AND b.quantityAvailable > 0 " +
           "AND b.expiryDate >= :today ORDER BY b.expiryDate ASC, b.id ASC")
    List<StockBatch> findSellableForUpdate(@Param("medicineId") Long medicineId, @Param("today") LocalDate today);

    @Query("SELECT b FROM StockBatch b JOIN FETCH b.medicine WHERE b.medicine.id = :medicineId ORDER BY b.expiryDate ASC")
    List<StockBatch> findByMedicine(@Param("medicineId") Long medicineId);

    /** Batches that still hold stock and expire on or before the cutoff (includes already expired). */
    @Query("SELECT b FROM StockBatch b JOIN FETCH b.medicine WHERE b.quantityAvailable > 0 AND b.expiryDate <= :cutoff ORDER BY b.expiryDate ASC")
    List<StockBatch> findExpiringBy(@Param("cutoff") LocalDate cutoff);

    /** Total sellable (unexpired) quantity per medicine id. */
    @Query("SELECT b.medicine.id, SUM(b.quantityAvailable) FROM StockBatch b WHERE b.expiryDate >= :today GROUP BY b.medicine.id")
    List<Object[]> sellableTotals(@Param("today") LocalDate today);
}
