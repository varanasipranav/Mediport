package com.ezc.aragenPR.webapp.service;

//import com.ezc.aragenPR.webapp.dto.EzcInvoiceExcelRowsDTO;
import com.ezc.aragenPR.webapp.model.EzcInvoiceBudgetReport;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;


import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface IEzcInvoiceBudgetReportService {

    List<EzcInvoiceBudgetReport> getAllReports();
    
    List<EzcInvoiceBudgetReport> getReportsByYear(int year);

    List<EzcInvoiceBudgetReport> getReportsByMonthAndYear(Integer month, Integer year);

    List<EzcInvoiceBudgetReport> getReportsByPlant(String plant);

  //  EzcInvoiceBudgetReport saveReport(EzcInvoiceBudgetReport report);

    void deleteReport(Long id);

    EzcInvoiceBudgetReport getReportById(Long id);
    
   // List<EzcInvoiceExcelRowsDTO> parseExcel(MultipartFile file) throws IOException;
    
    void saveAllReports(List<EzcInvoiceBudgetReport> reports);
    

}
