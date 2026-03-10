package com.ezc.aragenPR.webapp.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "ezc_upload_document")
@IdClass(EzUploadDocumentKey.class)
public class EzUploadDocument implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Composite key fields
    @Id
    @Column(name = "EUD_DOC_NO", nullable = false, length = 15)
    private Integer docNo;

    @Id
    @Column(name = "EUD_ITEM_NO", nullable = false, length = 50)
    private String itemNo;

    @Id
    @Column(name = "EUD_DOC_TYPE", nullable = false, length = 100)
    private String docType;

    @Id
    @Column(name = "EUD_FILE_NAME", nullable = false, length = 255)
    private String fileName;

    // Other fields
    @Column(name = "EUD_SERVER_FILE_NAME", length = 255)
    private String serverFileName;

    @Column(name = "EUD_EXT1", length = 100) 
    private String ext1;

    @Column(name = "EUD_EXT2", length = 100)
    private String ext2;

    @Column(name = "EUD_UPLOADED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    private Date uploadedOn;

    @Column(name = "EUD_UPLOADED_BY", length = 12)
    private String uploadedBy;
    
    @Column(name = "EUD_COMMENT", length = 200)
    private String comment;

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

    public String getServerFileName() {
        return serverFileName;
    }

    public void setServerFileName(String serverFileName) {
        this.serverFileName = serverFileName;
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

    public Date getUploadedOn() {
        return uploadedOn;
    }

    public void setUploadedOn(Date uploadedOn) {
        this.uploadedOn = uploadedOn;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	// Equals and hashCode (for composite key fields)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EzUploadDocument that = (EzUploadDocument) o;

        if (!docNo.equals(that.docNo)) return false;
        if (!itemNo.equals(that.itemNo)) return false;
        if (!docType.equals(that.docType)) return false;
        return fileName.equals(that.fileName);
    }

    @Override
    public int hashCode() {
        int result = docNo.hashCode();
        result = 31 * result + itemNo.hashCode();
        result = 31 * result + docType.hashCode();
        result = 31 * result + fileName.hashCode();
        return result;
    }
}

