package com.ezc.aragenPR.webapp.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ezc_qmis_header")
@Data
public class EzQmisHeader {

    @Id
    @SequenceGenerator(
            name = "header_generator",
            sequenceName = "header_seq",
            initialValue = 2000,
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EQH_DOCID")
    private Integer eqhdocId;

    @Column(name = "EQH_TYPE1", nullable = false)
    private String eqhType1;

    @Column(name = "EQH_MONTH", nullable = false)
    private Integer eqhMonth;

    @Column(name = "EQH_MONTH_NAME", nullable = false)
    private String eqhMonthName;

    @Column(name = "EQH_YEAR", nullable = false)
    private Integer eqhYear;

    @Column(name = "EQH_CURRENT_STEP")
    private String eqhCurrentStep;

    @Column(name = "EQH_CURRENT_PARTICIPANT")
    private String eqhCurrentParticipant;

    @Column(name = "EQH_NEXT_PARTICIPANT")
    private String eqhNextParticipant;

    @Column(name = "EQH_NEXT_STEP")
    private String eqhNextStep;

    @Column(name = "EQH_VENDOR")
    private String eqhVendor;

    @Column(name = "EQH_VENDOR_NAME")
    private String eqhVendorName;

    @Column(name = "EQH_PLANT")
    private String eqhPlant;

    @Column(name= "EQH_PLANT_NAME")
    private String eqhPlantName;

    @Column(name = "EQH_CREATED_ON")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate eqhCreatedOn;

    @Column(name = "EQH_MODIFIED_ON")
    private LocalDateTime eqhModifiedOn;

    @Column(name = "EQH_CREATED_BY")
    private String eqhCreatedBy;

    @Column(name = "EQH_MODIFIED_BY")
    private String eqhModifiedBy;

    @Column(name = "EQH_STATUS")
    @Enumerated(EnumType.STRING)
    private EqhDocStatus eqhStatus = EqhDocStatus.PENDING;

    @Column(name = "EQH_VEND_STATUS")
    @Enumerated(EnumType.STRING)
    private EqhDocStatus eqhVendorStatus = EqhDocStatus.PENDING;

    @Column(name = "EQH_QA_STATUS")
    @Enumerated(EnumType.STRING)
    private EqhDocStatus eqhQaStatus = EqhDocStatus.PENDING;

    @PrePersist
    protected void onCreate() {
        this.eqhCreatedOn = LocalDate.now();
        this.eqhModifiedOn = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.eqhModifiedOn = LocalDateTime.now();
    }
}