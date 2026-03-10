package com.ezc.aragenPR.webapp.model;

import java.io.Serializable;
import java.util.Objects;

public class EzcMasterDataId implements Serializable {

    private String emdKey;
    private String emdValue1;

    // Default constructor
    public EzcMasterDataId() {}

    public EzcMasterDataId(String emdKey, String emdValue1) {
        this.emdKey = emdKey;
        this.emdValue1 = emdValue1;
    }

    // Getters & Setters
    public String getEmdKey() { return emdKey; }
    public void setEmdKey(String emdKey) { this.emdKey = emdKey; }

    public String getEmdValue1() { return emdValue1; }
    public void setEmdValue1(String emdValue1) { this.emdValue1 = emdValue1; }

    // equals() and hashCode() — required for IdClass
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EzcMasterDataId)) return false;
        EzcMasterDataId that = (EzcMasterDataId) o;
        return Objects.equals(emdKey, that.emdKey) &&
                Objects.equals(emdValue1, that.emdValue1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(emdKey, emdValue1);
    }
}
