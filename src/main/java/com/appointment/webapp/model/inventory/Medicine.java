package com.appointment.webapp.model.inventory;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** A medicine in the clinic/pharmacy catalogue. Stock lives in {@link StockBatch}es. */
@Getter
@Setter
@Entity
@Table(name = "medicines")
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "generic_name", length = 150)
    private String genericName;

    @Column(name = "strength", nullable = false, length = 50)
    private String strength = "";

    @Column(name = "form", nullable = false, length = 30)
    private String form = "Tablet";

    @Column(name = "unit", nullable = false, length = 20)
    private String unit = "unit";

    @Column(name = "reorder_level", nullable = false)
    private Integer reorderLevel = 10;

    @Column(name = "default_price", precision = 10, scale = 2)
    private BigDecimal defaultPrice;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /** e.g. "Paracetamol 500mg Tablet" */
    public String getDisplayName() {
        StringBuilder sb = new StringBuilder(name);
        if (strength != null && !strength.isBlank()) sb.append(' ').append(strength);
        if (form != null && !form.isBlank()) sb.append(' ').append(form);
        return sb.toString();
    }
}
