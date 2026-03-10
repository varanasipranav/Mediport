package com.ezc.aragenPR.webapp.persistance.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ezc.aragenPR.webapp.model.EzInvoiceItems;
import com.ezc.aragenPR.webapp.model.EzPoItems;
import com.ezc.aragenPR.webapp.model.EzPoItemsKey;

@Repository
public interface EzPoItemsRepo extends JpaRepository<EzPoItems, EzPoItemsKey> {
//    List<EzPoItems> findByPoNo(String poNo);  // ✅ Find items by PO number
	
	@Query("SELECT e FROM EzPoItems e WHERE e.id.poNo = :poNo")
	List<EzPoItems> findByPoNo(@Param("poNo") String poNo);
    
    @Query("SELECT DISTINCT e.id.poNo FROM EzPoItems e") // ✅ Access poNo from e.id
    List<String> findDistinctPoNos();
    
    boolean existsByMaterial(String material);
    
    
    List<EzPoItems> findAllByMaterial(String material);

    
//    List<EzPoItems> findByMaterialOrderByPoNoAsc(String material);
    @Query("SELECT e FROM EzPoItems e WHERE e.material = :material ORDER BY e.id.poNo ASC")
    List<EzPoItems> findByMaterialOrderByPoNoAsc(@Param("material") String material);

    
    Optional<EzPoItems> findByIdAndMaterial(EzPoItemsKey id, String material);
    
//    @Query("SELECT e FROM EzPoItems e WHERE e.id.poNo = :poNo AND e.material = :material AND e.id.poItemNumber = :poItemNumber")
//    EzPoItems findByPoNoAndMaterial(String poNo, String material, Integer poItemNumber);
    
    @Query("SELECT e FROM EzPoItems e WHERE e.id.poNo = :poNo AND e.material = :material AND e.id.poItemNumber = :poItemNumber")
    EzPoItems findByPoNoAndMaterial(@Param("poNo") String poNo, 
                                    @Param("material") String material, 
                                    @Param("poItemNumber") String poItemNumber);

    
//        @Query("SELECT e FROM EzPoItems e WHERE e.poNo = :poNo AND e.material = :material AND e.poItemNumber = :poItemNumber")
//        EzPoItems findByPoNoAndMaterial(@Param("poNo") String poNo, @Param("material") String material, @Param("poItemNumber") String poItemNumber);
}