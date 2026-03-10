package com.ezc.aragenPR.webapp.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "EZC_EHS_TRAINING_RECORD")
public class EzcEhsTrainingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ETR_ID")
    private Integer id;

    @Column(name = "ETR_TRAINING_NAME")
    private String trainingName;
    
    @Column(name = "ETR_TRAINING_DATE")
    private Date trainingDate;


    @Column(name = "ETR_TRAINER")
    private String trainer;

    @Column(name = "ETR_NO_OF_PARTICIPANTS")
    private Integer noOfParticipants;

    @Column(name = "ETR_NO_OF_HOURS")
    private Double noOfHours;

    @Column(name = "ETR_TOTAL_HOURS")
    private Double totalHours;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ETR_DOC_ID", nullable = false)
    private EzcEhsHeader header;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTrainingName() {
		return trainingName;
	}

	public void setTrainingName(String trainingName) {
		this.trainingName = trainingName;
	}

	public String getTrainer() {
		return trainer;
	}

	public void setTrainer(String trainer) {
		this.trainer = trainer;
	}

	public Integer getNoOfParticipants() {
		return noOfParticipants;
	}

	public void setNoOfParticipants(Integer noOfParticipants) {
		this.noOfParticipants = noOfParticipants;
	}

	public Double getNoOfHours() {
		return noOfHours;
	}

	public void setNoOfHours(Double noOfHours) {
		this.noOfHours = noOfHours;
	}

	public Double getTotalHours() {
		return totalHours;
	}

	public void setTotalHours(Double totalHours) {
		this.totalHours = totalHours;
	}

	public EzcEhsHeader getHeader() {
		return header;
	}

	public void setHeader(EzcEhsHeader header) {
		this.header = header;
	}

	public Date getTrainingDate() {
		return trainingDate;
	}

	public void setTrainingDate(Date trainingDate) {
		this.trainingDate = trainingDate;
	}
	

    
    
}
