package com.ezc.aragenPR.webapp.model;

import lombok.Data;

import java.util.Date;


@Data
public class JobInfo {
    private String jobName;
    private Date lastExecution;
    private Date nextExecution;

}
