package com.appointment.webapp.service;

import com.appointment.webapp.model.Role;
import com.appointment.webapp.model.User;
import com.appointment.webapp.model.enums.ShiftType;
import com.appointment.webapp.repository.RoleRepository;
import com.appointment.webapp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Sample doctors for demos and first-time setup: different specialities and shifts, each with a weekly
 * schedule and the next 30 days of daily schedules, so they can be booked straight away.
 */
@Service
public class DemoDataService {

    private static final Logger logger = LoggerFactory.getLogger(DemoDataService.class);

    private record Sample(String username, String first, String last, String speciality, String department,
                           ShiftType shift, int slotMinutes) {}

    private static final List<Sample> DOCTORS = List.of(
        new Sample("dr.sharma", "Anita", "Sharma", "General Medicine", "OPD", ShiftType.MORNING, 15),
        new Sample("dr.reddy", "Rajesh", "Reddy", "Cardiology", "Cardiology", ShiftType.FULL_DAY, 20),
        new Sample("dr.khan", "Farah", "Khan", "Pediatrics", "Children's Care", ShiftType.MORNING, 15),
        new Sample("dr.iyer", "Suresh", "Iyer", "Orthopedics", "Orthopedics", ShiftType.EVENING, 20),
        new Sample("dr.nair", "Meera", "Nair", "Dermatology", "Skin Clinic", ShiftType.EVENING, 15),
        new Sample("dr.das", "Kavita", "Das", "Gynecology", "Women's Health", ShiftType.MORNING, 20)
    );

    public record Result(List<String> created, String password) {}

    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder encoder;
    private final DoctorWeeklyTemplateService templates;

    public DemoDataService(UserRepository users, RoleRepository roles, PasswordEncoder encoder,
                           DoctorWeeklyTemplateService templates) {
        this.users = users;
        this.roles = roles;
        this.encoder = encoder;
        this.templates = templates;
    }

    /** Adds any of the sample doctors that don't exist yet. They all share one generated password. */
    @Transactional
    public Result seedDoctors(String createdBy) {
        Role doctorRole = roles.findByName("DOCTOR").orElseThrow(() -> new IllegalStateException("DOCTOR role missing"));
        String password = randomPassword();
        List<String> created = new ArrayList<>();

        for (Sample s : DOCTORS) {
            if (users.findByUsername(s.username()).isPresent()) {
                continue;
            }
            User d = new User();
            d.setUsername(s.username());
            d.setPassword(encoder.encode(password));
            d.setFirstName(s.first());
            d.setLastName(s.last());
            d.setSpecialization(s.speciality());
            d.setDepartment(s.department());
            d.setEmail(s.username() + "@example.com");
            d.setPhoneNumber(String.valueOf(9000000100L + DOCTORS.indexOf(s)));
            d.setLicenseNumber("DEMO-" + (1000 + DOCTORS.indexOf(s)));
            d.setEmployeeId(String.format("DOC%05d", users.countByEmployeeIdStartingWith("DOC") + 1));
            d.setEnabled(true);
            d.setCreatedBy(createdBy);
            d.setUpdatedBy(createdBy);
            d.setRoles(Collections.singletonList(doctorRole));
            d = users.save(d);

            templates.setupDefaultWeeklySchedule(d, s.shift(), LocalTime.of(9, 0), LocalTime.of(13, 0),
                LocalTime.of(14, 0), LocalTime.of(18, 0), s.slotMinutes(), createdBy);
            templates.generateSchedulesFromTemplates(d, LocalDate.now(), LocalDate.now().plusDays(30), createdBy);
            created.add(s.username());
            logger.info("Added sample doctor {} ({}, {})", s.username(), s.speciality(), s.shift());
        }
        return new Result(created, created.isEmpty() ? null : password);
    }

    private static String randomPassword() {
        String alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
        SecureRandom r = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) sb.append(alphabet.charAt(r.nextInt(alphabet.length())));
        return sb.toString();
    }
}
