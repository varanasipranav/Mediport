package com.ezc.aragenPR.webapp.job;

import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import com.ezc.aragenPR.webapp.model.EqhRoles;
import com.ezc.aragenPR.webapp.model.Users;
import com.ezc.aragenPR.webapp.persistance.dao.UserRepository;
import com.ezc.aragenPR.webapp.service.JobHistoryService;
import com.ezc.aragenPR.webapp.service.ehsEmailService;

public class EHSMonthlyReminder implements Job {
	
	@Autowired
    private JobHistoryService jobHistoryService;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
	 private ehsEmailService ehsEmail;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		 System.out.println("EHS Monthly Report Reminder Email Job started");
	        String recipient = "";
	        String subject = "EHS Monthly Report Reminder";
	        try {
	            String role = String.valueOf(EqhRoles.ROLE_VEND);
	            List<Users> usersList=userRepository.findUsersByRole(role);
	            System.out.print("User List : " + usersList);
	            ehsEmail.sendMonthlyReminderEmail(usersList, subject);
	            System.out.println("Monthly Email sent to: " + recipient);
	            jobHistoryService.saveJobExecution(context.getJobDetail().getKey().getName(), "SUCCESS", null);

	        } catch (Exception e) {
	            System.out.println("Monthly Email sent to failed");
	            jobHistoryService.saveJobExecution(context.getJobDetail().getKey().getName(), "FAILURE", e.getMessage());
	            e.printStackTrace();

	        }
		
	}

}
