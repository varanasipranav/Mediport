package com.ezc.aragenPR.webapp.model;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class EzUploadDocumentKey implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "EUD_DOC_NO", nullable = false, length = 15)
    private Integer docNo;

    @Column(name = "EUD_ITEM_NO", nullable = false, length = 50)
    private String itemNo;

    @Column(name = "EUD_DOC_TYPE", nullable = false, length = 100)
    private String docType;

    @Column(name = "EUD_FILE_NAME", nullable = false, length = 255)
    private String fileName;

    // Getters and Setters
    public Integer getDocNo() {
        return docNo;
    }

    public void setDocNo(Integer docId) {
        this.docNo = docId;
    }

    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    // Equals and hashCode
    @Override
    public boolean equals(Object o) { 
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EzUploadDocumentKey that = (EzUploadDocumentKey) o;
        return Objects.equals(docNo, that.docNo) &&
               Objects.equals(itemNo, that.itemNo) &&
               Objects.equals(docType, that.docType) &&
               Objects.equals(fileName, that.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(docNo, itemNo, docType, fileName);
    }
}



