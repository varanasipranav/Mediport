package com.ezc.aragenPR.webapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "EZC_EHS_SUSTAINABILITY_PARAMETER",
    uniqueConstraints = @UniqueConstraint(name = "uq_eesup_key_doc_id", columnNames = {"EESUP_KEY", "EESUP_DOC_ID"}),
    indexes = @Index(name = "idx_eesup_doc_id", columnList = "EESUP_DOC_ID")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EzEhsSustainabilityParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EESUP_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EESUP_DOC_ID", nullable = false)
    private EzcEhsHeader header;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EESUP_KEY", nullable = false)
    private EzEhsSustainabilityMaterial sustainabilityMaterial;

    @Column(name = "EESUP_DESCRIPTION", length = 255)
    private String description;

    @Column(name = "EESUP_VALUE", length = 100)
    private String value;
}