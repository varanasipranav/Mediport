package com.ezc.aragenPR.webapp.service;

import com.ezc.aragenPR.webapp.dto.VendorPlantResponse;
import com.ezc.aragenPR.webapp.model.EhsStatutoryMaster;

import java.util.List;
import java.util.Optional;

public interface IEhsStatutoryService {
    List<EhsStatutoryMaster> getAll();
    EhsStatutoryMaster getById(Long id);
    List<EhsStatutoryMaster> getByPlantName(String plantName);
    List<EhsStatutoryMaster> getByVendor(String vendor);
    EhsStatutoryMaster save(EhsStatutoryMaster entity);
    List<EhsStatutoryMaster> getMandatoryRequirements(String vendorCode);
    void deleteById(Long id);
    
    List<String> getAllVendors(); // SELECT DISTINCT vendor FROM ehs_statutory_master
    List<String> getAllPlantNames();
    List<String> getAllUniqueRequirements(); // SELECT DISTINCT requirement FROM ehs_statutory_master
    public List<EhsStatutoryMaster> getMandatoryRequirementsForYear(String vendorCode, int financialYearId);
    Optional<EhsStatutoryMaster> findByRequirementAndVendor(String req, String vendor);
    public VendorPlantResponse getVendorAndPlantList();
    public void copyRequirementsForNextYear(int currentYear);
    
    Optional<EhsStatutoryMaster> findByRequirementAndVendorAndFY(String requirement, String vendor, int financialYearId);

    List<EhsStatutoryMaster> getMandatoryRequirementsByFY(String vendorCode, int financialYearId);

    List<String> getAllUniqueRequirementsForFY(int financialYearId);
    
    void validateRecordsForYear(int financialYearId);

}
