package com.ezc.aragenPR.webapp.service;

import com.ezc.aragenPR.webapp.model.Users;

import java.util.List;

public interface emailServiceModule {
    void sendMonthlyEmail(List<Users> usersList, String subject);
    void sendReminderEmail(List<Users> usersList, String subject);

}
