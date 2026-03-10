package com.ezc.aragenPR.webapp.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "ezc_master_data")
@IdClass(EzcMasterDataId.class)
@Data
public class EzcMasterData {

    @Id
    @Column(name = "EMD_KEY", length = 10)
    private String emdKey;

    @Id
    @Column(name = "EMD_VALUE1", length = 40)
    private String emdValue1;

    @Column(name = "EMD_VALUE2", length = 100)
    private String emdValue2;

    @Column(name = "EMD_VALUE3", length = 100)
    private String emdValue3;

    @Column(name = "EMD_VALUE4", length = 100)
    private String emdValue4;

    @Column(name = "EMD_CREATED_BY", length = 40)
    private String emdCreatedBy;

    @Column(name = "EMD_CREATED_ON")
    private LocalDateTime emdCreatedOn;

    // Getters and setters
    public String getEmdKey() { return emdKey; }
    public void setEmdKey(String emdKey) { this.emdKey = emdKey; }

    public String getEmdValue1() { return emdValue1; }
    public void setEmdValue1(String emdValue1) { this.emdValue1 = emdValue1; }

    public String getEmdValue2() { return emdValue2; }
    public void setEmdValue2(String emdValue2) { this.emdValue2 = emdValue2; }

    public String getEmdCreatedBy() { return emdCreatedBy; }
    public void setEmdCreatedBy(String emdCreatedBy) { this.emdCreatedBy = emdCreatedBy; }

    public LocalDateTime getEmdCreatedOn() { return emdCreatedOn; }
    public void setEmdCreatedOn(LocalDateTime emdCreatedOn) { this.emdCreatedOn = emdCreatedOn; }

    public String getEmdValue3() { return emdValue3; }
    public void setEmdValue3(String emdValue3) { this.emdValue3 = emdValue3; }


}
