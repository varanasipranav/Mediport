package com.appointment.webapp.model.inventory;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/** Append-only audit trail of every stock change. Quantity is signed (+ in, - out). */
@Getter
@Setter
@Entity
@Table(name = "stock_movements")
public class StockMovement {

    public enum Type { RECEIVE, DISPENSE, ADJUST, EXPIRED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id")
    private StockBatch batch;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 20)
    private Type movementType;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "prescription_id")
    private Long prescriptionId;

    @Column(name = "note", length = 255)
    private String note;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
