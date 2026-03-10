package com.ezc.aragenPR.webapp.model;

import static jakarta.persistence.GenerationType.IDENTITY;

import com.ezc.aragenPR.webapp.dto.MaterialDto;
import com.ezc.aragenPR.webapp.dto.PlaceMasterDto;

import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@SqlResultSetMapping(
	    name="PlaceMasterDtoMapping",
	    classes={ 
	        @ConstructorResult(
	            targetClass=PlaceMasterDto.class,
	            columns={
	            	@ColumnResult(name="EPM_ID"),
	                @ColumnResult(name="EPM_CITY"),
	                @ColumnResult(name="EPM_STATE"),
	                @ColumnResult(name="EPM_COUNTRY")
	                
	            }
	        )
	    }
	)

@NamedNativeQuery(name="EzPlaceMaster.cityDetails", query="SELECT * FROM EZC_PLACE_MASTER mm " 
							+  " WHERE mm.EPM_CITY = :city",  
							resultSetMapping="PlaceMasterDtoMapping")




@Entity
@Table(name="EZC_PLACE_MASTER")
@Data
@AllArgsConstructor
public class EzPlaceMaster{

	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "EPM_ID", unique = true, nullable = false)
	private int id;
	
	@Column(name="EPM_CITY",length=40)
	private String city;
	
	@Column(name="EPM_STATE",length=40)
	private String state;
	
	@Column(name="EPM_COUNTRY",length=40)
	private String country;
		
	public EzPlaceMaster() {
		 
		
	}
		
}
