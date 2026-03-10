package com.ezc.aragenPR.webapp.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public class EzPPAPQuestionnaireIdKey implements Serializable {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 8081639238317012379L;
	private int ecfdDocId;
    private String ecfdField;
    private String ecfdTabName;

    // Getters and Setters
    public int getEcfdDocId() {
        return ecfdDocId;
    }

    public void setEcfdDocId(int ecfdDocId) {
        this.ecfdDocId = ecfdDocId;
    }

    public String getEcfdField() {
        return ecfdField;
    }

    public void setEcfdField(String ecfdField) {
        this.ecfdField = ecfdField;
    }

    public String getEcfdTabName() {
        return ecfdTabName;
    }

    public void setEcfdTabName(String ecfdTabName) {
        this.ecfdTabName = ecfdTabName;
    }

    // Override equals() and hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EzPPAPQuestionnaireIdKey that = (EzPPAPQuestionnaireIdKey) o;
        return ecfdDocId == that.ecfdDocId &&
               ecfdField.equals(that.ecfdField) &&
               ecfdTabName.equals(that.ecfdTabName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ecfdDocId, ecfdField, ecfdTabName);
    }
}
