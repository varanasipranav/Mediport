package com.ezc.aragenPR.webapp.model;
import java.io.Serializable;
import java.util.Objects;

public class EzcPoPlanItemsId implements Serializable {
    
	private EzcPoWeekPlan poWeekPlan;
	private Integer lineNo;
	
		@Override
	public int hashCode() {
		return Objects.hash(lineNo, poWeekPlan);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EzcPoPlanItemsId other = (EzcPoPlanItemsId) obj;
		return Objects.equals(lineNo, other.lineNo) && Objects.equals(poWeekPlan, other.poWeekPlan);
	}

		public EzcPoPlanItemsId() {
		super();
		// TODO Auto-generated constructor stub
	}

    // Default constructor, equals, and hashCode
    
    
}
