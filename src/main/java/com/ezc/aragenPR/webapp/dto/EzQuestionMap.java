package com.ezc.aragenPR.webapp.dto;

import com.ezc.aragenPR.webapp.model.EzUploadDocument;

public class EzQuestionMap {
	
	private int id;
	private String question;
	private String name;
	private String answer;
	private  EzUploadDocument ezUploadDoc;
	private boolean docExist;
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	
	public EzQuestionMap() {
		super();
		// TODO Auto-generated constructor stub
	}
	public EzQuestionMap(int id,String question,String name, String answer,boolean docExist) {
		super();
		this.id=id;
		this.question = question;
		this.name=name;
		this.answer = answer;
		this.docExist=docExist;
	}
	
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public EzUploadDocument getEzUploadDoc() {
		return ezUploadDoc;
	}
	public void setEzUploadDoc(EzUploadDocument ezUploadDoc) {
		this.ezUploadDoc = ezUploadDoc;
	}
	public boolean isDocExist() {
		return docExist;
	}
	public void setDocExist(boolean docExist) {
		this.docExist = docExist;
	}
	
	
}
