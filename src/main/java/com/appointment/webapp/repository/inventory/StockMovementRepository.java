package com.appointment.webapp.repository.inventory;

import com.appointment.webapp.model.inventory.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    @Query("SELECT m FROM StockMovement m JOIN FETCH m.medicine LEFT JOIN FETCH m.batch ORDER BY m.createdAt DESC LIMIT 100")
    List<StockMovement> findRecent();

    @Query("SELECT m FROM StockMovement m JOIN FETCH m.medicine LEFT JOIN FETCH m.batch WHERE m.medicine.id = :medicineId ORDER BY m.createdAt DESC LIMIT 50")
    List<StockMovement> findRecentForMedicine(@Param("medicineId") Long medicineId);
}
