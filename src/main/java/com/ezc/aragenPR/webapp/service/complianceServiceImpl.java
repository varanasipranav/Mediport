/*
package com.ezc.aragenPR.webapp.service;

import com.ezc.aragenPR.webapp.dto.EzcEhsFormFieldMasterDto;
import com.ezc.aragenPR.webapp.dto.EzcEhsVendorFormConfigDto;
import com.ezc.aragenPR.webapp.model.EzComments;
import com.ezc.aragenPR.webapp.model.EzEnvironmentReport;
import com.ezc.aragenPR.webapp.model.EzcEhsFormFieldMaster;
import com.ezc.aragenPR.webapp.model.EzcEhsVendorFormConfig;
import com.ezc.aragenPR.webapp.persistance.dao.CommentsRepo;
import com.ezc.aragenPR.webapp.persistance.dao.EzEnvironmentReportRepository;
import com.ezc.aragenPR.webapp.persistance.dao.EzcEhsVendorFormConfigRepo;
import com.ezc.aragenPR.webapp.persistance.dao.EzcEhsVendorFormConfigRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class complianceServiceImpl implements IComplianceService {

    @Autowired
    private EzcEhsVendorFormConfigRepository ezcEhsVendorConfigFieldRepository;

    @Autowired
    private EzEnvironmentReportRepository reportRepo;

    @Autowired
    private EzcEhsVendorFormConfigRepo vendorFormConfigRepo;

    @Autowired
    private CommentsRepo commentsRepo;

    @Override
    public List<EzcEhsVendorFormConfigDto> getAllVendorConfigs() {
        List<EzcEhsVendorFormConfig> configs = vendorFormConfigRepo.findAll();
        return configs.stream().map(config -> {
            EzcEhsVendorFormConfigDto dto = new EzcEhsVendorFormConfigDto();
            dto.setEvfmConfigId(config.getEvfmConfigId());
            dto.setEvfmVendorId(config.getEvfmVendorId());
            dto.setMonthYear("04/2025");
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<EzcEhsVendorFormConfigDto> getVendorConfigsByVendorId(String vendorId) {
        List<EzcEhsVendorFormConfig> configs = vendorFormConfigRepo.findAllByEvfmVendorId(vendorId);

        if (configs.isEmpty()) {
            throw new EntityNotFoundException("No config found for vendorId: " + vendorId);
        }

        return configs.stream().map(config -> {
            EzcEhsVendorFormConfigDto dto = new EzcEhsVendorFormConfigDto();
            dto.setEvfmConfigId(config.getEvfmConfigId());
            dto.setEvfmVendorId(config.getEvfmVendorId());
            dto.setMonthYear(config.getConfigMonthYear()); // Assuming you now store month-year in DB

            if (config.getFields() != null) {
                List<EzcEhsFormFieldMasterDto> fieldDtos = config.getFields().stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList());
                dto.setFields(fieldDtos);
            }

            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public void saveComments(String comments, Integer docId, String userId, String tab) {
            if (comments == null || comments.trim().isEmpty()) return;

            EzComments commentObj = new EzComments();
            commentObj.setComments(comments);
            commentObj.setCreatedBy(userId);
            commentObj.setDate(new Date());
            commentObj.setDocType("EHSCompliance"); // Or pass this as a parameter if needed
            commentObj.setTab(tab);

            if (docId != null)
                commentObj.setDocId(docId.toString());

            commentsRepo.save(commentObj);
    }


    @Override
    public List<EzcEhsFormFieldMasterDto> getFormFieldsByVendorId(String vendorId) {
        List<EzcEhsVendorFormConfig> vendorConfigs = ezcEhsVendorConfigFieldRepository.findByEvfmVendorId(vendorId);

        return vendorConfigs.stream()
                .flatMap(config -> config.getFields().stream())
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private EzcEhsFormFieldMasterDto convertToDto(EzcEhsFormFieldMaster entity) {
        EzcEhsFormFieldMasterDto dto = new EzcEhsFormFieldMasterDto();
        dto.setEffmFieldId(entity.getFieldId());
        dto.setEffmSerialNo(entity.getSerialNo());
        dto.setEffmParticulars(entity.getParticulars());
        dto.setEffmConsentConditions(entity.getConsentConditions());
        dto.setEffmUom(entity.getUom());
        dto.setEffmAttachment(entity.getAttachment());
        dto.setEffmNormChar(entity.getNormChar());
        dto.setEffmNorms(entity.getNorms());
        dto.setEffmActual(entity.getActual());
        dto.setEffmStatus(entity.getStatus());
        dto.setEffmRemarks(entity.getRemarks());
        dto.setEffmIsHeader(entity.getIsHeader());
        dto.setEffmInputType(entity.getInputType());
        dto.setEffmIsMandatory(entity.getIsMandatory());
        return dto;
    }

    public EzEnvironmentReport saveFieldEntry(Long docId, Long vendorId, Long fieldId, String actual,
            String remarks, String status, String reportMonth, String submissionStatus, String fileName) {

        EzEnvironmentReport report = reportRepo.findByDocIdAndVendorIdAndFieldIdAndReportMonth(docId, vendorId, fieldId, reportMonth)
                .orElse(new EzEnvironmentReport());

        report.setDocId(docId);
        report.setVendorId(vendorId);
        report.setFieldId(fieldId);
        report.setActual(actual);
        report.setRemarks(remarks);
        report.setStatus(status);
        report.setReportMonth(reportMonth);
        report.setTimestamp(LocalDateTime.now());
        report.setSubmissionStatus(submissionStatus);
        report.setFileName(fileName);

        return reportRepo.save(report);
    }

    @Override
    public List<EzEnvironmentReport> getSavedReports(Long docId, String vendorId, String reportMonth) {
        return reportRepo.findByDocIdAndVendorIdAndReportMonth(docId, Long.valueOf(vendorId), reportMonth);
    }
}
*/
