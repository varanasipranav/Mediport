package com.ezc.aragenPR.webapp.service;

import com.ezc.aragenPR.webapp.dto.VendorPlantResponse;
import com.ezc.aragenPR.webapp.model.EhsStatutoryMaster;
import com.ezc.aragenPR.webapp.persistance.dao.EhsStatutoryMasterRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EhsStatutoryServiceImpl implements IEhsStatutoryService {

    @Autowired
    private EhsStatutoryMasterRepository repository;

    @Override
    public List<EhsStatutoryMaster> getAll() {
        return repository.findAll();
    }

    @Override
    public EhsStatutoryMaster getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<EhsStatutoryMaster> getByPlantName(String plantName) {
        return repository.findByPlantName(plantName);
    }

    @Override
    public List<EhsStatutoryMaster> getByVendor(String vendor) {
        return repository.findByVendor(vendor);
    }

    @Override
    public EhsStatutoryMaster save(EhsStatutoryMaster entity) {
        return repository.save(entity);
    }
    
    @Override
    public List<EhsStatutoryMaster> getMandatoryRequirements(String vendorCode) {
        return repository.findByVendorAndIsMandatoryTrue(vendorCode);
    }
    
    @Override
    public List<EhsStatutoryMaster> getMandatoryRequirementsForYear(String vendorCode, int financialYearId) {
        return repository.findByVendorAndFinancialYear(vendorCode, financialYearId);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
    
    @Override
    public List<String> getAllVendors() {
        return repository.findDistinctVendors();
    }
    
    @Override
    public List<String> getAllPlantNames() {
        return repository.findDistinctPlantNames();
    }
    
    @Override
    public List<String> getAllUniqueRequirements() {
        return repository.findDistinctRequirements();
    }

    @Override
    public Optional<EhsStatutoryMaster> findByRequirementAndVendor(String req, String vendor) {
        return repository.findByRequirementAndVendor(req, vendor);
    }
    
    public VendorPlantResponse getVendorAndPlantList() {
        List<String> vendors = getAllVendors();
        List<String> plantNames = getAllPlantNames();
        return new VendorPlantResponse(vendors, plantNames);
    }
    
    @Transactional
    public void copyRequirementsForNextYear(int currentYear) {
        int nextYear = currentYear + 1;

        // Check if entries for next year already exist
        List<EhsStatutoryMaster> existingNextYearEntries = repository.findByFinancialYear(nextYear);
        if (!existingNextYearEntries.isEmpty()) {
            log.warn("Entries for financial year " + nextYear + " already exist. Skipping copy operation.");
            return; // or throw an exception if needed
        }

        // Fetch current year entries to copy from
        List<EhsStatutoryMaster> currentYearEntries = repository.findByFinancialYear(currentYear);
        if (currentYearEntries.isEmpty()) {
            log.warn("No entries found for current financial year: " + currentYear);
            return;
        }

        // Prepare next year copies
        List<EhsStatutoryMaster> nextYearEntries = currentYearEntries.stream().map(entry -> {
            EhsStatutoryMaster copy = new EhsStatutoryMaster();
            copy.setRequirement(entry.getRequirement());
            copy.setPlantName(entry.getPlantName());
            copy.setIsMandatory(entry.getIsMandatory());
            copy.setVendor(entry.getVendor());
            copy.setValidFrom(null);
            copy.setValidTill(null);
            copy.setIsValid(false);
            copy.setFinancialYear(nextYear);
            return copy;
        }).collect(Collectors.toList());

        log.info("Copying " + nextYearEntries.size() + " entries to financial year " + nextYear);
        repository.saveAll(nextYearEntries);
    }


    @Override
    public Optional<EhsStatutoryMaster> findByRequirementAndVendorAndFY(String requirement, String vendor,
                                                                         int financialYearId) {
        return repository.findByRequirementAndVendorAndFY(requirement, vendor, financialYearId);
    }

    @Override
    public List<EhsStatutoryMaster> getMandatoryRequirementsByFY(String vendorCode, int financialYearId) {
        return repository.findMandatoryRequirementsByVendorAndFY(vendorCode, financialYearId);
    }

    @Override
    public List<String> getAllUniqueRequirementsForFY(int financialYearId) {
        return repository.findDistinctRequirementsByFinancialYear(financialYearId);
    }
    
    @Override
    public void validateRecordsForYear(int financialYearId) {
        List<EhsStatutoryMaster> allRecords = repository.findByFinancialYear(financialYearId);
        LocalDate today = LocalDate.now();

        for (EhsStatutoryMaster record : allRecords) {
            if (record.getValidTill() != null && record.getValidTill().isBefore(today)) {
                record.setIsValid(false); // Update BIT(1) column
            }
        }

        repository.saveAll(allRecords);
    }



}
