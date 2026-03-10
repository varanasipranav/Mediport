package com.ezc.aragenPR.webapp.persistance.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.ezc.aragenPR.webapp.model.EhsStatutoryMaster;

import java.util.List;
import java.util.Optional;

@Repository
public interface EhsStatutoryMasterRepository extends JpaRepository<EhsStatutoryMaster, Long> {
    List<EhsStatutoryMaster> findByPlantName(String plantName);
    List<EhsStatutoryMaster> findByVendor(String vendor);
    List<EhsStatutoryMaster> findByVendorAndIsMandatoryTrue(String vendor);
    
 // Custom query to get unique vendor names
    @Query("SELECT DISTINCT e.vendor FROM EhsStatutoryMaster e WHERE e.vendor IS NOT NULL")
    List<String> findDistinctVendors();

    @Query("SELECT DISTINCT e.plantName FROM EhsStatutoryMaster e")
    List<String> findDistinctPlantNames();
    
    // Custom query to get unique requirement names
    @Query("SELECT DISTINCT e.requirement FROM EhsStatutoryMaster e WHERE e.requirement IS NOT NULL")
    List<String> findDistinctRequirements();

    // Find by requirement and vendor
    Optional<EhsStatutoryMaster> findByRequirementAndVendor(String requirement, String vendor);
    
    List<EhsStatutoryMaster> findByFinancialYear(int financialYear);
    
    @Query("SELECT e FROM EhsStatutoryMaster e WHERE e.requirement = :requirement AND e.vendor = :vendor AND e.financialYear = :financialYear")
    Optional<EhsStatutoryMaster> findByRequirementAndVendorAndFY(@Param("requirement") String requirement,
                                                                 @Param("vendor") String vendor,
                                                                 @Param("financialYear") int financialYear);

    @Query("SELECT e FROM EhsStatutoryMaster e WHERE e.vendor = :vendor AND e.financialYear = :financialYear AND e.isMandatory = true")
    List<EhsStatutoryMaster> findMandatoryRequirementsByVendorAndFY(@Param("vendor") String vendor,
                                                                    @Param("financialYear") int financialYear);

    @Query("SELECT DISTINCT e.requirement FROM EhsStatutoryMaster e WHERE e.financialYear = :financialYear")
    List<String> findDistinctRequirementsByFinancialYear(@Param("financialYear") int financialYear);

    List<EhsStatutoryMaster> findByVendorAndFinancialYear(String vendor, int financialYear);

}
