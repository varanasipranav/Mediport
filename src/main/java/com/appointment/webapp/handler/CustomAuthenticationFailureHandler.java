package com.appointment.webapp.handler;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
            HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {

        // Don't echo the exception text into the URL; the login page shows a fixed message
        if (exception instanceof com.appointment.webapp.tenancy.SiteRequiredException
                || exception.getCause() instanceof com.appointment.webapp.tenancy.SiteRequiredException) {
            response.sendRedirect(request.getContextPath() + "/login?error=site");
            return;
        }
        response.sendRedirect(request.getContextPath() + "/login?error=true");
    }
}
