package com.ezc.aragenPR.webapp.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "EZC_ENVIRONMENT_REPORT")
public class EzEnvironmentReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @Column(name = "DOC_ID")
    private Long docId;

    @Column(name = "VENDOR_ID")
    private Long vendorId;

    @Column(name = "FIELD_ID")
    private Long fieldId;

    @Column(name = "ACTUAL_VALUE")
    private String actual;
    
    @Column(name = "STATUS")
    private String status;

    @Column(name = "REMARKS", columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "REPORT_MONTH") // Format: 04/2025
    private String reportMonth;

    @Column(name = "TIMESTAMP")
    private LocalDateTime timestamp;
    
    @Column(name = "SUBMISSION_STATUS")
    private String submissionStatus; // e.g., "SAVED", "SUBMITTED"
    
    @Column(name = "FILE_NAME")
    private String fileName;
    
    @Column(name = "REVIEWED_BY")
    private String reviewedBy;

    @Column(name = "REVIEW_COMMENTS", columnDefinition = "TEXT")
    private String reviewComments;


    // getters/setters...
}
