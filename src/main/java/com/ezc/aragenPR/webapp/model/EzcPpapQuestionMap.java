package com.ezc.aragenPR.webapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
	
	

	@Entity
	@Table(name = "ezc_ppap_question_map")
	@Getter
	@Setter
	@ToString 
	public class EzcPpapQuestionMap {

	    @Id
	    private Integer id;

	    @Column(name = "question", nullable = false)
	    private String question;

	    @Column(name = "field_name", nullable = false)
	    private String fieldName;

	    @Column(name = "default_answer", length = 3)
	    private String defaultAnswer;

	    @Column(name = "docExist") 
	    private boolean docExist;

	    // Default constructor required by JPA
	    public EzcPpapQuestionMap() {}

	    public EzcPpapQuestionMap(Integer id, String question, String fieldName, String defaultAnswer, boolean docExist) {
	        this.id = id;
	        this.question = question;
	        this.fieldName = fieldName;
	        this.defaultAnswer = defaultAnswer;
	        this.docExist = docExist;
	    }
	}
