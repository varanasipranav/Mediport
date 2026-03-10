package com.ezc.aragenPR.webapp.scheduler;

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class QuartzConfig {

    @Autowired
    private Scheduler scheduler;

    @PostMapping("/schedule-job")
    public String scheduleJob(@RequestParam String jobClassName,
                              @RequestParam String cronExpression,
                              RedirectAttributes redirectAttributes) {
        try {
            Class<? extends Job> jobClass = (Class<? extends Job>) Class.forName(jobClassName);
            String jobName = jobClass.getSimpleName();  // consistent job name
            String groupName = "dynamicGroup";

            JobKey jobKey = new JobKey(jobName, groupName);
            TriggerKey triggerKey = new TriggerKey("Trigger-" + jobName, groupName);

            if (scheduler.checkExists(jobKey)) {
                // If job exists, just update the trigger
                scheduler.unscheduleJob(triggerKey);
            } else {
                // Create the job if it doesn't exist
                JobDetail jobDetail = JobBuilder.newJob(jobClass)
                        .withIdentity(jobKey)
                        .storeDurably()
                        .build();
                scheduler.addJob(jobDetail, true);
            }

            // Create a new trigger with updated cron
            Trigger newTrigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .forJob(jobKey)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                    .build();

            scheduler.scheduleJob(newTrigger);

            redirectAttributes.addFlashAttribute("success", "Job scheduled successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to schedule job: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/jobs";
    }

    @PostMapping("/delete-job")
    public String deleteJob(@RequestParam String jobName, RedirectAttributes redirectAttributes) {
        String groupName = "dynamicGroup";
        try {
            JobKey jobKey = new JobKey(jobName, groupName);
            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
                redirectAttributes.addFlashAttribute("success", "Job deleted successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Job not found.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete job: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/jobs";
    }
}