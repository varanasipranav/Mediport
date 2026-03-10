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
public class StatutoryCopyJob implements Job {

    @Autowired
    private IEhsStatutoryService statutoryService;
    
    @Autowired
    private JobHistoryService jobHistoryService;

    @Override
    public void execute(JobExecutionContext context) {
        try {
            int currentYear = Year.now().getValue();
            log.info("Running Statutory Copy Job for Current Year: {}", currentYear);

            statutoryService.copyRequirementsForNextYear(currentYear);

            log.info("Statutory Copy Job completed successfully from {} to {}", currentYear, currentYear + 1);
            jobHistoryService.saveJobExecution(context.getJobDetail().getKey().getName(), "SUCCESS", null);
        } catch (Exception e) {
            log.error("Error occurred in Statutory Copy Job: ", e);
            jobHistoryService.saveJobExecution(context.getJobDetail().getKey().getName(), "FAILURE", e.getMessage());
        }
    }
}
