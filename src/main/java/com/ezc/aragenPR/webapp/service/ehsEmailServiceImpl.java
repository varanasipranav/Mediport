package com.ezc.aragenPR.webapp.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.ezc.aragenPR.webapp.dto.EmailFormat;
import com.ezc.aragenPR.webapp.model.EqhDocStep;
import com.ezc.aragenPR.webapp.model.EqhRoles;
import com.ezc.aragenPR.webapp.model.EzEHSIncident;
import com.ezc.aragenPR.webapp.model.EzQmisHeader;
import com.ezc.aragenPR.webapp.model.EzValueMapping;
import com.ezc.aragenPR.webapp.model.EzcEhsActionTable;
import com.ezc.aragenPR.webapp.model.EzcEhsHeader;
import com.ezc.aragenPR.webapp.model.UserDefaults;
import com.ezc.aragenPR.webapp.model.Users;
import com.ezc.aragenPR.webapp.persistance.dao.EzcEhsHeaderRepository;
import com.ezc.aragenPR.webapp.persistance.dao.UserDefRepository;
import com.ezc.aragenPR.webapp.persistance.dao.ValueMappingRepo;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class ehsEmailServiceImpl implements ehsEmailService{
	
	    @Autowired
	    private JavaMailSender emailSender;

	    @Value("${site.url}")
	    private String siteUrl="";

	    @Value("${mail.fromaddress}")
	    private String fromEmail="";

	    @Value("${mail.ccmail}")
	    private String ccmail="";
	    
	    @Autowired
	    private UserDefRepository userDefRepository;

	    @Autowired
	    private EzcEhsHeaderRepository headerRepo;

	    @Override
	    public void sendIncidentMail(String subject, String toEmail, EzEHSIncident incident) {
	        String mailSub = subject;
	        String link = "http://10.62.10.138/login";

	        
	        String messageBody = "Incident ID <strong>" + incident.getId() + "</strong> for the observation type <strong>" 
	                             + incident.getTypeOfObservation() + "</strong>, submitted by <strong>" 
	                             + incident.getReportingPerson() + "</strong>, is awaiting your approval.";

	        
	        String mailTxt = EmailFormat.formatEmail(
	            "Incident Approval Required",    
	            "Approver",                 
	            messageBody,                
	            link,                       
	            "",                         
	            "Login to view details"     
	        );

	        MimeMessage mimeMessage = emailSender.createMimeMessage();

	        try {
	            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, false, "UTF-8");
	            message.setFrom(fromEmail);
	            message.setTo(toEmail);
	            if (ccmail != null && !"null".equals(ccmail) && !"".equals(ccmail)) {
	                message.setCc(ccmail);
	            }
	            message.setSubject(mailSub);
	            message.setText(mailTxt, true); // true to enable HTML
	        } catch (MessagingException e) {
	            log.error("Error setting up email message: " + e.getMessage());
	        }

	        try {
	            emailSender.send(mimeMessage);
	            System.out.println(mimeMessage);
	        } catch (Exception e) {
	            log.debug("Error in sending email: " + e.getMessage());
	        }
	    }
	    
	    @Override
	    public void sendIncidentApprovalStatusMail(String toEmail, String action, EzEHSIncident incident) {
	        String subject;
	        String messageBody;
	        String link = "http://10.62.10.138/login";

	        if ("approve".equalsIgnoreCase(action)) {
	            subject = "Incident Approval Notification";
	            messageBody = "Incident ID <strong>" + incident.getId() + "</strong> for the observation type <strong>" 
	                          + incident.getTypeOfObservation() + "</strong>, has been <strong>approved</strong> by the EHS Admin.<br><br>" +
	                          "You can view the approved incident at the following link:";
	        } else if ("reject".equalsIgnoreCase(action)) {
	            subject = "Incident Rejection Notification";
	            messageBody = "Incident ID <strong>" + incident.getId() + "</strong> for the observation type <strong>" 
	                          + incident.getTypeOfObservation() + "</strong>, has been <strong>rejected</strong> by the EHS Admin.<br><br>" +
	                          "Please review the comments provided, make the necessary corrections, and resubmit the incident for approval at the following link:";
	        } else {
	            // Invalid action, optionally log or throw an error
	            return;
	        }

	        // Use central email formatting
	        String mailTxt = EmailFormat.formatEmail(
	            "Incident " + (action.equalsIgnoreCase("approve") ? "Approved" : "Rejected"),  // type/title
	            "Vendor",                 // recipient name
	            messageBody,              // formatted body
	            link,                     // link to incident
	            "",                       // regards
	            "Login to view details"  // button text
	        );

	        // Send the email
	        MimeMessage mimeMessage = emailSender.createMimeMessage();
	        try {
	            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, false, "UTF-8");
	            message.setFrom(fromEmail);
	            message.setTo(toEmail);
	            if (ccmail != null && !"null".equals(ccmail) && !"".equals(ccmail)) {
	                message.setCc(ccmail);
	            }
	            message.setSubject(subject);
	            message.setText(mailTxt, true);
	            emailSender.send(mimeMessage);
	        } catch (Exception e) {
	            log.error("Error sending incident status email: " + e.getMessage());
	        }
	    }
	    
	    @Override
	    public void sendActionCompletionMail(String toEmail, EzcEhsActionTable action) {
	        String subject = "Corrective Action Completed";
	        String link = "http://10.62.10.138/login";

	        String messageBody = "Action ID <strong>" + action.getActionId() + "</strong> is completed by EHS ADMIN.";

	        String mailTxt = EmailFormat.formatEmail(
	            "Corrective Action Completed", // type/title
	            "Vendor",                         // recipient name (can be dynamic if needed)
	            messageBody,                      // main message
	            link,                             // login link
	            "",                               // regards (optional)
	            "Login to view details"           // button text
	        );

	        MimeMessage mimeMessage = emailSender.createMimeMessage();
	        try {
	            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, false, "UTF-8");
	            message.setFrom(fromEmail);
	            message.setTo(toEmail);
	            if (ccmail != null && !"null".equals(ccmail) && !"".equals(ccmail)) {
	                message.setCc(ccmail);
	            }
	            message.setSubject(subject);
	            message.setText(mailTxt, true);
	            emailSender.send(mimeMessage);
	        } catch (Exception e) {
	            log.error("Error sending action completion email: " + e.getMessage());
	        }
	    }
	    
	    @Override
	    public void sendMonthlyReportStatusMail(String toEmail, String action, EzcEhsHeader header, Integer docId) {
	        String subject;
	        String messageBody;
	        String link = "http://10.62.10.138/login";

	        switch (action.toUpperCase()) {
	            case "SUBMITTED":
	                subject = "Monthly Report Submission Notification";
	                messageBody = "The monthly report for <strong>" + header.getMonthYear()
	                            + "</strong> has been <strong>submitted</strong> by vendor <strong>"
	                            + header.getVendor() + "</strong> and is awaiting your approval.<br><br>"
	                            + "You can review the submitted report at the following link:";
	                break;

	            case "APPROVED":
	                subject = "Monthly Report Approval Notification";
	                messageBody = "Your monthly report for <strong>" + header.getMonthYear()
	                            + "</strong> has been <strong>approved</strong> by the EHS Admin.<br><br>"
	                            + "You can view the approved report at the following link:";
	                break;

	            case "REJECTED":
	                subject = "Monthly Report Rejection Notification";
	                messageBody = "Your monthly report for <strong>" + header.getMonthYear()
	                            + "</strong> has been <strong>rejected</strong> by the EHS Admin.<br><br>"
	                            + "Please review the comments, make necessary corrections, and resubmit the report at the following link:";
	                break;

	            default:
	                // Invalid action type
	                return;
	        }

	        // Generate formatted email
	        String mailTxt = EmailFormat.formatEmail(
	            "Monthly Report " + action.substring(0, 1).toUpperCase() + action.substring(1).toLowerCase(), // Capitalize first letter
	            action.equals("SUBMITTED") ? "Approver" : "Vendor", // recipient
	            messageBody,
	            link,
	            "", // regards
	            "View Monthly Reports"
	        );

	        // Send the email
	        MimeMessage mimeMessage = emailSender.createMimeMessage();
	        try {
	            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, false, "UTF-8");
	            message.setFrom(fromEmail);
	            message.setTo(toEmail);
	            if (ccmail != null && !"null".equals(ccmail) && !"".equals(ccmail)) {
	                message.setCc(ccmail);
	            }
	            message.setSubject(subject);
	            message.setText(mailTxt, true);
	            emailSender.send(mimeMessage);
	        } catch (Exception e) {
	            log.error("Error sending monthly report status email: " + e.getMessage());
	        }
	    }

		@Override
		public void sendMonthlyEmail(List<Users> usersList, String subject) {
			
			    String mailSub = subject;
		        String mailTxt = "";
		        String link="http://10.62.10.138/";
		        for(Users user: usersList){
		        	
		        	System.out.print("User loop: " + user);
		            mailTxt= EmailFormat.formatEmail("EHS MONTHLY REPORT", user.getFirstName(),"Please review and Fill all the required data.", link, "","Please click here to Open");
		            MimeMessage mimeMessage = emailSender.createMimeMessage();
		            try {
		                MimeMessageHelper message = new MimeMessageHelper(mimeMessage, false, "UTF-8");
		                message.setFrom(fromEmail);
//		                message.setTo(user.getEmail());
		                message.setTo("arin.jain@answerthink.com");
		                if(ccmail != null && !"null".equals(ccmail)  && !"".equals(ccmail))
		                    message.setCc(ccmail);
		                message.setSubject(mailSub);
		                message.setText(mailTxt,true);
		            } catch (MessagingException e) {

		            }
		            try {
		                 emailSender.send(mimeMessage);
		                insertDocument(user);
		                System.out.println(mimeMessage);
		            } catch (Exception e) {
		                log.debug("Error in sending mails:::"+e);
		            }
		        }
		}
		
	    private void insertDocument(Users user){
	        UserDefaults userDefRecord = userDefRepository.findByuserIdAndKey(user.getId(), "LOCATION");
	        String plant = userDefRecord.getValue();
            EzcEhsHeader header = new EzcEhsHeader();
            header.setPlant(plant);
            header.setVendor(String.valueOf(user.getUserId()).trim());
            header.setInitiatedDate(new Date());
            header.setStatus("New");
            headerRepo.save(header);

            System.out.println("saved: " + plant);
        }

		@Override
		public void sendMonthlyReminderEmail(List<Users> usersList, String subject) {
			  String mailSub = subject;
		        String mailTxt = "";
		        String link="http://10.62.10.138/";
		        LocalDate today = LocalDate.now();
		        LocalDate previousMonth = today.minusMonths(1);

		        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
		        String previousMonthString = previousMonth.format(formatter);
		        for(Users user: usersList){
		        	
		        	mailTxt= EmailFormat.formatEmail("EHS MONTHLY REPORT RMINDER", user.getFirstName(),"Please review and Fill all the required data for "+previousMonthString+ ". If already completed, kindly ignore this email.", link, "","Please click here to Open");
		            MimeMessage mimeMessage = emailSender.createMimeMessage();
		            try {
		                MimeMessageHelper message = new MimeMessageHelper(mimeMessage, false, "UTF-8");
		                message.setFrom(fromEmail);
//		                message.setTo(user.getEmail());
		                message.setTo("arin.jain@answerthink.com");
		                if(ccmail != null && !"null".equals(ccmail)  && !"".equals(ccmail))
		                message.setCc(ccmail);
		                message.setSubject(mailSub);
		                message.setText(mailTxt,true);
		            } catch (MessagingException e) {

		            }
		            try {
		                 emailSender.send(mimeMessage);
		                 System.out.println(mimeMessage);
		            } catch (Exception e) {
		                log.debug("Error in sending mails:::"+e);
		            }
		        }
			
		}





}
