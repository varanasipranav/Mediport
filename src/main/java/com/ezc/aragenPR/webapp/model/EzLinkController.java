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
import lombok.ToString;


@Entity
@Table(name="EZC_LINK_CONROLLER")
@Data @AllArgsConstructor @ToString
public class EzLinkController{

	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "ELC_ID", unique = true, nullable = false)
	private Integer id;
	
	
	@Column(name="ELC_USER_ID",length=20)
	private String userId;
	
	@Column(name="ELC_USER_ROLE",length=20)
	private String userRole;
	
	@Column(name="ELC_EMAIL",length=80)
	private String email;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "ELC_CREATED_ON")
	private Date createdOn;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "ELC_EXPIRY_DATE")
	private Date expiryDate;
	
	@Column(name="ELC_MSG_SUB",length=256)
	private String mailSubject;
	

	@Column(columnDefinition = "TEXT",name="ELC_MSG_CONTENT")
	private String mailContent;
	
	@Column(name="ELC_DOC_ID",length=40)
	private String docId;
	
	@Column(name="ELC_STATUS",length=1)
	private String status;
			
	public EzLinkController() {
		 
		
	}

	public void setCreatedOn(Date createdOn2) {
		// TODO Auto-generated method stub
		
	}
		
}
	