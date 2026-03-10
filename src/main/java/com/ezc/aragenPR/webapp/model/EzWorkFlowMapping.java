/*
package com.ezc.aragenPR.webapp.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Entity
@Table(name="EZC_WORKFLOW_MAPPING")
@IdClass(EzWorkFlowMappingKey.class)
@Data @AllArgsConstructor @ToString
public class EzWorkFlowMapping{

	
	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EWM_WF_KEY")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private EzWorkFlowKey ezWorkFlowKey;
 
	@Id
	@Column(name = "EWM_USER_ID",length=10)
	private String userId;
	
	@Column(name="EWM_LEVEL")
	private Integer level;
			
	public EzWorkFlowMapping() {
		 
		
	}
		
}
*/
