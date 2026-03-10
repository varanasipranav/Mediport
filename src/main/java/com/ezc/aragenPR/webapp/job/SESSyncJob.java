package com.ezc.aragenPR.webapp.job;

import com.ezc.aragenPR.webapp.service.SESService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

public class SESSyncJob implements Job {
    @Autowired
    private SESService sesService;
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("SES Sync JOB Job started");
        try{
            sesService.syncFromSAP();
            System.out.println("SES Sync JOB Job completed successfully");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
