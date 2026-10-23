package com.appointment.webapp.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** "/": signed-in users go to their dashboard; visitors see the product page (multi-clinic mode) or the login page. */
@Controller
public class HomeController {

    @Value("${app.tenancy.enabled:false}")
    private boolean tenancy;

    @GetMapping("/")
    public String home(Authentication auth) {
        boolean signedIn = auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken);
        if (signedIn) return "redirect:/dashboard/index";
        return tenancy ? "redirect:/site" : "redirect:/login";
    }
}
