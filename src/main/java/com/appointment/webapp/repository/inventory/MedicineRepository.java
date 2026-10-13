package com.appointment.webapp.repository.inventory;

import com.appointment.webapp.model.inventory.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    List<Medicine> findByActiveTrueOrderByNameAsc();

    boolean existsByNameIgnoreCaseAndStrengthIgnoreCaseAndFormIgnoreCase(String name, String strength, String form);

    @Query("SELECT m FROM Medicine m WHERE m.active = true AND " +
           "(LOWER(m.name) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(m.genericName) LIKE LOWER(CONCAT('%', :q, '%'))) " +
           "ORDER BY m.name ASC")
    List<Medicine> search(@Param("q") String q);
}
