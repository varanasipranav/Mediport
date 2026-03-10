/*
package com.ezc.aragenPR.webapp.job;

import com.ezc.aragenPR.webapp.controller.QmisFormController;
//import com.ezc.outsrc.webapp.persistance.dao.JobHistoryService;
import com.ezc.aragenPR.webapp.model.EqhRoles;
import com.ezc.aragenPR.webapp.model.Users;
import com.ezc.aragenPR.webapp.persistance.dao.EzQmisHeaderRepository;
import com.ezc.aragenPR.webapp.persistance.dao.UserRepository;
import com.ezc.aragenPR.webapp.service.IEmailService;
import com.ezc.aragenPR.webapp.service.JobHistoryService;
import com.ezc.aragenPR.webapp.service.qmisEmailServiceImpl;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ReminderEmailJob implements Job {

    @Autowired
    private qmisEmailServiceImpl emailService;

    @Autowired
    private JobHistoryService jobHistoryService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EzQmisHeaderRepository ezQmisHeaderRepository;


    @Override
    public void execute(JobExecutionContext context) {
        System.out.println("Reminder Email Job started");
        String recipient = "";
        String subject = "QMIS Data Update Reminder";
        String message = "";
        int docId;
        try {
            String role = String.valueOf(EqhRoles.ROLE_VEND);
            List<Users> usersList=userRepository.findUsersByRole(role);
            for(Users user: usersList){
                emailService.sendReminderEmail(usersList, subject);
            }
            System.out.println("Reminder Email sent to: " + recipient);
            jobHistoryService.saveJobExecution(context.getJobDetail().getKey().getName(), "SUCCESS", null);

        } catch (Exception e) {
//            jobHistoryService.saveJobExecution("Monthly Email Job", "FAILURE", e.getMessage());
            System.out.println("Reminder Email sent to failed");
            jobHistoryService.saveJobExecution(context.getJobDetail().getKey().getName(), "FAILURE", e.getMessage());
            e.printStackTrace();

        }
    }
}
*/
