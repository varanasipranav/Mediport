package com.ezc.aragenPR.webapp.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "ezc_customer_form_details")
@IdClass(EzCustomerFormDetailsKey.class)
public class EzCustomerFormDetails implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	
	
	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ECFD_DOC_ID")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private EzCustomerFormHeader ezCustomerFormHeader;

    @Id
    @Column(name = "ECFD_FIELD", nullable = false, length = 20)
    private String field;

    @Id
    @Column(name = "ECFD_TAB_NAME", nullable = false, length = 50)
    private String tabName;

    @Column(name = "ECFD_EXT1", length = 10)
    private String ext1;

    @Column(name = "ECFD_EXT2", length = 10)
    private String ext2;

    @Column(name = "ECFD_VALUE", length = 500)
    private String value;

    // Getters and Setters


    public String getField() {
        return field;
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
    
    

    public EzCustomerFormHeader getEzCustomerFormHeader() {
		return ezCustomerFormHeader;
	}

	public void setEzCustomerFormHeader(EzCustomerFormHeader ezCustomerFormHeader) {
		this.ezCustomerFormHeader = ezCustomerFormHeader;
	}

	// Equals and HashCode (needed for composite key)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EzCustomerFormDetails that = (EzCustomerFormDetails) o;
        return Objects.equals(ezCustomerFormHeader, that.ezCustomerFormHeader) &&
                Objects.equals(field, that.field) &&
                Objects.equals(tabName, that.tabName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ezCustomerFormHeader, field, tabName);
    }

    // toString Method
    @Override
    public String toString() {
        return "EzcCustomerFormDetails{" +
                "docId='" + ezCustomerFormHeader + '\'' +
                ", field='" + field + '\'' +
                ", tabName='" + tabName + '\'' +
                ", ext1='" + ext1 + '\'' +
                ", ext2='" + ext2 + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
