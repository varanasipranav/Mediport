package com.ezc.aragenPR.webapp.model;

import java.io.Serializable;

public class EzPriceReqItemCompKey implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private EzPriceRequestHeader ezPriceRequestHeader;
	private int lineNo;
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ezPriceRequestHeader == null) ? 0 : ezPriceRequestHeader.hashCode());
		result = prime * result + lineNo;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EzPriceReqItemCompKey other = (EzPriceReqItemCompKey) obj;
		if (ezPriceRequestHeader == null) {
			if (other.ezPriceRequestHeader != null)
				return false;
		} else if (!ezPriceRequestHeader.equals(other.ezPriceRequestHeader))
			return false;
		if (lineNo != other.lineNo)
			return false;
		return true;
	}
	public EzPriceReqItemCompKey(EzPriceRequestHeader ezPriceRequestHeader, int lineNo) {
		super();
		this.ezPriceRequestHeader = ezPriceRequestHeader;
		this.lineNo = lineNo;
	}
	
	public EzPriceReqItemCompKey()
	{
		
	}
	
	}
