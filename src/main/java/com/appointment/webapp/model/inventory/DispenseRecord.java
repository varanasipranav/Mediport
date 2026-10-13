package com.appointment.webapp.model.inventory;

import com.appointment.webapp.model.Prescription;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/** Pharmacy outcome for a prescription: fully or partially dispensed. */
@Getter
@Setter
@Entity
@Table(name = "dispense_records")
public class DispenseRecord {

    public enum Status { DISPENSED, PARTIAL }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prescription_id", nullable = false, unique = true)
    private Prescription prescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status;

    @Column(name = "dispensed_by", length = 50)
    private String dispensedBy;

    @Column(name = "dispensed_at", nullable = false)
    private LocalDateTime dispensedAt = LocalDateTime.now();

    @Column(name = "notes", length = 500)
    private String notes;
}
