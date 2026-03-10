/*
package com.ezc.aragenPR.webapp.job;

import com.ezc.aragenPR.webapp.controller.QmisFormController;
import com.ezc.aragenPR.webapp.model.EqhDocStep;
import com.ezc.aragenPR.webapp.model.EqhRoles;
import com.ezc.aragenPR.webapp.model.EzQmisHeader;
import com.ezc.aragenPR.webapp.model.Users;
import com.ezc.aragenPR.webapp.persistance.dao.EzQmisHeaderRepository;
import com.ezc.aragenPR.webapp.persistance.dao.UserRepository;
import com.ezc.aragenPR.webapp.service.JobHistoryService;
import com.ezc.aragenPR.webapp.service.qmisEmailServiceImpl;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
public class MonthlyEmailJob implements Job {

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
        System.out.println("Monthly Email Job started");
        String recipient = "";
        String subject = "QMIS Data Update";
        try {
            String role = String.valueOf(EqhRoles.ROLE_VEND);
            List<Users> usersList=userRepository.findUsersByRole(role);
            emailService.sendMonthlyEmail(usersList, subject);
            System.out.println("Monthly Email sent to: " + recipient);
            jobHistoryService.saveJobExecution(context.getJobDetail().getKey().getName(), "SUCCESS", null);

        } catch (Exception e) {
//            jobHistoryService.saveJobExecution("Monthly Email Job", "FAILURE", e.getMessage());
            System.out.println("Monthly Email sent to failed");
            jobHistoryService.saveJobExecution(context.getJobDetail().getKey().getName(), "FAILURE", e.getMessage());
            e.printStackTrace();

        }
    }
}

*/
