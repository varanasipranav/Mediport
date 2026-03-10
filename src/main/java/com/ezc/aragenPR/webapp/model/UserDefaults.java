package com.ezc.aragenPR.webapp.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Table(name="EZC_USER_DEFAULTS")
@Data
@AllArgsConstructor
@IdClass(UserDefaultsKey.class)
public class UserDefaults implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="EUD_USER_ID",length=20)
	private long userId;
	
	@Id
	@Column(name="EUD_KEY",length=10)
	private String key;
	
	@ManyToOne
    @JoinColumns({
            @JoinColumn(name="EUD_USER_ID", referencedColumnName="id",insertable = false,updatable = false),
    })
    
    private Users users;
	
	
	@Column(name="EUD_VALUE",length=100)
	private String value;
		
	public UserDefaults() {
		
		
	}
		
}
