package com.ezc.aragenPR.webapp.job;

import java.time.Year;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ezc.aragenPR.webapp.service.IEhsStatutoryService;
import com.ezc.aragenPR.webapp.service.JobHistoryService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class StatutoryValidationJob implements Job {

    @Autowired
    private IEhsStatutoryService statutoryService;
    
    @Autowired
    private JobHistoryService jobHistoryService;

    @Override
    public void execute(JobExecutionContext context) {
        try {
            int currentFY = Year.now().getValue();
            log.info("Running Statutory Validation Job for Financial Year: {}", currentFY);

            statutoryService.validateRecordsForYear(currentFY);

            log.info("Statutory Validation Job completed successfully for FY: {}", currentFY);
            jobHistoryService.saveJobExecution(context.getJobDetail().getKey().getName(), "SUCCESS", null);
        } catch (Exception e) {
            log.error("Error occurred in Statutory Validation Job: ", e);
            jobHistoryService.saveJobExecution(context.getJobDetail().getKey().getName(), "FAILURE", e.getMessage());
        }
    }
}
