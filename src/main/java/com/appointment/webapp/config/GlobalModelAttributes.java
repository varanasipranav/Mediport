package com.appointment.webapp.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Model attributes every page needs. Thymeleaf 3.1 no longer exposes #httpServletRequest,
 * so the sidebar reads the current path from here to highlight the active link.
 */
@ControllerAdvice
public class GlobalModelAttributes {

    private final ClinicProperties clinic;

    public GlobalModelAttributes(ClinicProperties clinic) {
        this.clinic = clinic;
    }

    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @ModelAttribute("clinic")
    public ClinicProperties clinic() {
        return clinic;
    }
}
