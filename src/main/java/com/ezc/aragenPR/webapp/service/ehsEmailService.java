package com.ezc.aragenPR.webapp.service;

import java.util.List;

import com.ezc.aragenPR.webapp.model.EzEHSIncident;
import com.ezc.aragenPR.webapp.model.EzcEhsActionTable;
import com.ezc.aragenPR.webapp.model.EzcEhsHeader;
import com.ezc.aragenPR.webapp.model.Users;



public interface ehsEmailService { 
	
	void sendIncidentMail(String subject, String toEmail, EzEHSIncident incident);
	void sendIncidentApprovalStatusMail(String toEmail, String action, EzEHSIncident incident);
	void sendActionCompletionMail(String toEmail, EzcEhsActionTable action); 
	void sendMonthlyReportStatusMail(String toEmail, String action, EzcEhsHeader header, Integer docId);
	void sendMonthlyEmail(List<Users> usersList, String subject);
	void sendMonthlyReminderEmail (List<Users> usersList, String subject);
}
