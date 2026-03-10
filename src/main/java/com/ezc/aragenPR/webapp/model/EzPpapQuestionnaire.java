package com.ezc.aragenPR.webapp.model;

import jakarta.persistence.*; // Use javax.persistence for older versions of JPA
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;


@Entity
@Table(name = "ezc_ppap_questionaire")
public class EzPpapQuestionnaire implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7283009966172412447L;

	/**
	 * 
	 */

	
	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ECPQ_DOC_ID")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private EzCustomerFormHeader ezCustomerFormHeader;

    @Id
    @Column(name = "ECPQ_FIELD", nullable = false, length = 50)
    private String field;

    @Id
    @Column(name = "ECPQ_TAB_NAME", nullable = false, length = 20)
    private String tabName;

    @Column(name = "ECPQ_EXT1", length = 10)
    private String ext1;

    @Column(name = "ECPQ_EXT2", length = 10)
    private String ext2;

    @Column(name = "ECPQ_VALUE", length = 50)
    private String value;

  
    // Getters and Setters

    

    public String getField() {
        return field;
    }

    public EzCustomerFormHeader getEzCustomerFormHeader() {
		return ezCustomerFormHeader;
	}

	public void setEzCustomerFormHeader(EzCustomerFormHeader ezCustomerFormHeader) {
		this.ezCustomerFormHeader = ezCustomerFormHeader;
	}

	public void setField(String field) {
        this.field = field;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public String getExt1() {
        return ext1;
    }

    public void setExt1(String ext1) {
        this.ext1 = ext1;
    }

    public String getExt2() {
        return ext2;
    }

    public void setExt2(String ext2) {
        this.ext2 = ext2;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    // Override equals and hashCode for composite key handling

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EzPpapQuestionnaire that = (EzPpapQuestionnaire) o;

        if (ezCustomerFormHeader != that.ezCustomerFormHeader) return false;
        if (!field.equals(that.field)) return false;
        return tabName.equals(that.tabName);
    }

    @Override
    public int hashCode() {
        int result = ezCustomerFormHeader.getDocId();
        result = 31 * result + field.hashCode();
        result = 31 * result + tabName.hashCode();
        return result;
    }
}
