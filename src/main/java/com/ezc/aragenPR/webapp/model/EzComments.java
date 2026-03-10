package com.ezc.aragenPR.webapp.model;

import static jakarta.persistence.GenerationType.IDENTITY;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="EZC_COMMENTS")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EzComments{

	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "EC_COM_ID", unique = true, nullable = false)
	private int id;
	
	@Column(name="EC_DOC_ID",length=40)
	private String docId;
	
	@Column(name="EC_DOC_TYPE",length=10)
	private String docType;
	
	@Column(name="EC_COMMENTS",length=2000)  
	private String comments;
	
	@Column(name="EC_CREATED_BY",length=10)
	private String createdBy;
	
	@Column(name="EC_TAB",length=50)
	private String tab;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EC_DATE", length = 19)
	private Date date;
		

		
}
