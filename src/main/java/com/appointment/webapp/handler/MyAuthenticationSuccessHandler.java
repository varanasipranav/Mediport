package com.appointment.webapp.handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@Component
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @org.springframework.beans.factory.annotation.Autowired
    private com.appointment.webapp.security.LoginThrottle throttle;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        throttle.succeeded(request.getRemoteAddr(), request.getParameter("username"));
        HttpSession session = request.getSession();
        session.setAttribute("username", authentication.getName());

        // Redirect to default success URL
        response.sendRedirect(request.getContextPath() + "/dashboard/index");
    }
}
