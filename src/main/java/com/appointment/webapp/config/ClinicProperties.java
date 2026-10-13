package com.appointment.webapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Clinic identity shown on prescriptions, PDFs and the app shell. Set in application.properties (clinic.*).
 */
@Component
@ConfigurationProperties(prefix = "clinic")
public class ClinicProperties {

    private String name = "Mediport Clinic";
    private String tagline = "Multi-speciality outpatient care";
    private String address = "";
    private String phone = "";
    private String email = "";
    private String registrationNo = "";

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getTagline() { return tagline; }
    public void setTagline(String tagline) { this.tagline = tagline; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRegistrationNo() { return registrationNo; }
    public void setRegistrationNo(String registrationNo) { this.registrationNo = registrationNo; }
}
