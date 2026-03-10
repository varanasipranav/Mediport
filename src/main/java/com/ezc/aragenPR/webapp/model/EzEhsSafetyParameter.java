package com.ezc.aragenPR.webapp.model;

import jakarta.persistence.*;

@Entity
@Table(
    name = "EZC_EHS_SAFETY_PARAMETER",
    uniqueConstraints = @UniqueConstraint(name = "uq_eesp_key_doc_id", columnNames = {"EESP_KEY", "EESP_DOC_ID"}),
    indexes = { @Index(name = "idx_eesp_doc_id", columnList = "EESP_DOC_ID") }
)
public class EzEhsSafetyParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EESP_ID") // Unique identifier for this table
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EESP_DOC_ID", nullable = false)
    private EzcEhsHeader header; // Correct mapping to the header

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EESP_KEY", nullable = false)
    private EzEhsSafetyMaterial safetyMaterial; // Correct mapping to material

    @Column(name = "EESP_DESCRIPTION", length = 255)
    private String description;

    @Column(name = "EESP_VALUE", length = 100)
    private String value;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public EzcEhsHeader getHeader() {
		return header;
	}

	public void setHeader(EzcEhsHeader header) {
		this.header = header;
	}

	public EzEhsSafetyMaterial getSafetyMaterial() {
		return safetyMaterial;
	}

	public void setSafetyMaterial(EzEhsSafetyMaterial safetyMaterial) {
		this.safetyMaterial = safetyMaterial;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

    
}





//package com.ezc.outsrc.webapp.model;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//
//@Entity
//@Table(name = "EZC_EHS_SAFETY_PARAMETER")
//@Getter
//@Setter
//public class EzEhsSafetyParameter {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "EESP_ID")
//    private Integer id;
//
//    @Column(name = "EESP_DOC_ID", nullable = false)
//    private Integer docId;
//
//    @Column(name = "EESP_VENDOR", nullable = false)
//    private String vendor;
//
//    @Column(name = "EESP_MONTH_YR", nullable = false)
//    private String monthYear;
//
//    @Column(name = "EESP_KEY", nullable = false)
//    private Integer key;
//
//    @Column(name = "EESP_DESCRIPTION", nullable = false, columnDefinition = "TEXT")
//    private String description;
//
//    @Column(name = "EESP_VALUE", nullable = false)
//    private String value;
//}
