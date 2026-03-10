package com.ezc.aragenPR.webapp.controller;

import com.ezc.aragenPR.webapp.model.Users;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

@ControllerAdvice
public class GlobalUserControllerAdvice {

    @ModelAttribute
    public void addUserNameToModel(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String loggedUser="",userName="";
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Users userObj = (Users) authentication.getPrincipal();
            loggedUser = (String) userObj.getUserId();
            userName= (String) userObj.getFirstName()+""+(String) userObj.getLastName();
        }catch (Exception e){}
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            model.addAttribute("userName", userName);
        }
    }
}
