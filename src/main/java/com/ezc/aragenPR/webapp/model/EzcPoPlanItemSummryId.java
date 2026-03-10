package com.ezc.aragenPR.webapp.model;
import java.io.Serializable;
import java.util.Objects;

public class EzcPoPlanItemSummryId implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private EzcPoPlanHeader poPlanHeader;
    private Integer lineNo;
	public EzcPoPlanHeader getPoPlanHeader() {
		return poPlanHeader;
	}
	public void setPoPlanHeader(EzcPoPlanHeader poPlanHeader) {
		this.poPlanHeader = poPlanHeader;
	}
	public Integer getLineNo() {
		return lineNo;
	}
	public void setLineNo(Integer lineNo) {
		this.lineNo = lineNo;
	}
	@Override
	public int hashCode() {
		return Objects.hash(lineNo, poPlanHeader);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EzcPoPlanItemSummryId other = (EzcPoPlanItemSummryId) obj;
		return Objects.equals(lineNo, other.lineNo) && Objects.equals(poPlanHeader, other.poPlanHeader);
	}
	public EzcPoPlanItemSummryId() {
		super();
		// TODO Auto-generated constructor stub
	}
	public EzcPoPlanItemSummryId(EzcPoPlanHeader poPlanHeader, Integer lineNo) {
		super();
		this.poPlanHeader = poPlanHeader;
		this.lineNo = lineNo;
	}
   
}