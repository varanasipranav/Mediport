/*
package com.ezc.aragenPR.webapp.service;

import com.ezc.aragenPR.webapp.dto.EzcInvoiceExcelRowsDTO;
import com.ezc.aragenPR.webapp.model.EzcInvoiceBudgetReport;
import com.ezc.aragenPR.webapp.persistance.dao.EzcInvoiceBudgetReportRepo;

import com.ezc.aragenPR.webapp.service.IEzcInvoiceBudgetReportService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;


import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EzcInvoiceBudgetReportServiceImpl implements IEzcInvoiceBudgetReportService {

    @Autowired
    private EzcInvoiceBudgetReportRepo reportRepo;

    @Override
    public List<EzcInvoiceBudgetReport> getAllReports() {
        return reportRepo.findAll();
    }
    
    @Override
    public List<EzcInvoiceBudgetReport> getReportsByYear(int year) {
        return reportRepo.findByYear(year);
    }


    @Override
    public List<EzcInvoiceBudgetReport> getReportsByMonthAndYear(Integer month, Integer year) {
        return reportRepo.findByMonthAndYear(month, year);
    }

    @Override
    public List<EzcInvoiceBudgetReport> getReportsByPlant(String plant) {
        return reportRepo.findByPlant(plant);
    }

    @Override
    public EzcInvoiceBudgetReport saveReport(EzcInvoiceBudgetReport report) {
        return reportRepo.save(report);
    }

    @Override
    public void deleteReport(Long id) {
        reportRepo.deleteById(id);
    }

    @Override
    public EzcInvoiceBudgetReport getReportById(Long id) {
        Optional<EzcInvoiceBudgetReport> optional = reportRepo.findById(id);
        return optional.orElse(null);
    }
    
    public List<EzcInvoiceExcelRowsDTO> parseExcel(MultipartFile file) {
        List<EzcInvoiceExcelRowsDTO> rows = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);  
                if (row == null) continue;

                EzcInvoiceExcelRowsDTO data = new EzcInvoiceExcelRowsDTO();
                data.setMonth((int) row.getCell(0).getNumericCellValue());
                data.setYear((int) row.getCell(1).getNumericCellValue());
                data.setPlant(row.getCell(2).getStringCellValue());
                data.setMaterialType(row.getCell(3).getStringCellValue());
                data.setBudgetKgs(BigDecimal.valueOf(row.getCell(4).getNumericCellValue()));
                data.setBudgetCC(BigDecimal.valueOf(row.getCell(5).getNumericCellValue()));
                data.setBudgetRt(data.getBudgetKgs().compareTo(BigDecimal.ZERO) != 0
                	    ? data.getBudgetCC().divide(data.getBudgetKgs(), 2, RoundingMode.HALF_UP)
                	    : BigDecimal.ZERO);
                rows.add(data);
            }
		} catch (IOException | InvalidFormatException e) {
            e.printStackTrace(); // optionally log
        }

        return rows;
    }
    
    @Override
    public void saveAllReports(List<EzcInvoiceBudgetReport> reports) {
        reportRepo.saveAll(reports);
    }


}
*/
