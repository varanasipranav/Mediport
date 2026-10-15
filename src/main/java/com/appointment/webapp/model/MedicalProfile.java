package com.appointment.webapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/** Long-lived medical facts about a patient: allergies, chronic conditions, blood group. One row per patient. */
@Getter
@Setter
@Entity
@Table(name = "medical_profiles")
public class MedicalProfile {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "blood_group", length = 5)
    private String bloodGroup;

    @Column(name = "allergies", columnDefinition = "TEXT")
    private String allergies;

    @Column(name = "chronic_conditions", columnDefinition = "TEXT")
    private String chronicConditions;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public boolean hasAllergies() {
        return allergies != null && !allergies.isBlank();
    }
}
