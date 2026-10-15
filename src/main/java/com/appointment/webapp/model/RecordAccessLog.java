package com.appointment.webapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/** Privacy audit trail: who opened or changed whose medical record, and when. */
@Getter
@Setter
@Entity
@Table(name = "record_access_log")
public class RecordAccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @Column(name = "accessed_by", nullable = false, length = 50)
    private String accessedBy;

    @Column(name = "action", nullable = false, length = 40)
    private String action;

    @Column(name = "accessed_at", nullable = false)
    private LocalDateTime accessedAt = LocalDateTime.now();
}
