package com.appointment.webapp.controller;

import com.appointment.webapp.model.Role;
import com.appointment.webapp.model.User;
import com.appointment.webapp.repository.RoleRepository;
import com.appointment.webapp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Collections;

@Controller
@RequestMapping("/admin/users")
public class UserCreationEditController {

    private static final Logger logger = LoggerFactory.getLogger(UserCreationEditController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // NOTE: AdminUserController already handles:
    // - GET /admin/users/list
    // - GET /admin/users/create-doctor, create-patient, create-attendant, create-pharma
    // - GET /admin/users/edit/{id}
    // - POST /admin/users/toggle-status/{id}
    // - POST /admin/users/delete/{id}
    //
    // This controller ONLY handles POST creation/update with role-specific logic

    // ================ CREATE DOCTOR (POST) ================

    @PostMapping("/create-doctor")
    public String createDoctor(@ModelAttribute User user,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        logger.info("createDoctor called; auth present={}, principal={}",
                authentication != null, authentication != null ? authentication.getName() : "null");
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warn("createDoctor invoked without authenticated principal; redirecting to login");
            redirectAttributes.addFlashAttribute("error", "Session expired. Please login again.");
            return "redirect:/login?message=Session%20expired.%20Please%20login%20again.";
        }

        try {
            if (isBlank(user.getUsername()) || isBlank(user.getPassword()) ||
                    isBlank(user.getFirstName()) || isBlank(user.getLastName()) ||
                    isBlank(user.getPhoneNumber())) {
                logger.debug("createDoctor validation failed - missing required fields: username present={}, firstName present={}, lastName present={}, phone present={}",
                        !isBlank(user.getUsername()), !isBlank(user.getFirstName()), !isBlank(user.getLastName()), !isBlank(user.getPhoneNumber()));
                redirectAttributes.addFlashAttribute("error", "Please fill all required fields");
                return "redirect:/admin/users/create-doctor";
            }

            if (userRepository.findByUsername(user.getUsername()).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Username already exists");
                return "redirect:/admin/users/create-doctor";
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            String employeeId = generateEmployeeId("DOC");
            user.setEmployeeId(employeeId);
            user.setCreatedBy(authentication.getName());
            user.setUpdatedBy(authentication.getName());
            user.setEnabled(true);

            Role doctorRole = roleRepository.findByName("DOCTOR")
                    .orElseThrow(() -> new RuntimeException("DOCTOR role not found"));
            user.setRoles(Collections.singletonList(doctorRole));

            userRepository.save(user);
            logger.info("Doctor created by {} with id {}", authentication.getName(), employeeId);
            redirectAttributes.addFlashAttribute("success", "Doctor created successfully with ID: " + employeeId);
            return "redirect:/admin/users/list";
        } catch (Exception e) {
            logger.error("Error creating doctor", e);
            redirectAttributes.addFlashAttribute("error", "Error creating doctor: " + e.getMessage());
            return "redirect:/admin/users/create-doctor";
        }
    }

    // ================ CREATE PATIENT (POST) ================

    @PostMapping("/create-patient")
    public String createPatient(@ModelAttribute User user,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        logger.info("createPatient called; auth present={}, principal={}",
                authentication != null, authentication != null ? authentication.getName() : "null");
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warn("createPatient invoked without authenticated principal; redirecting to login");
            redirectAttributes.addFlashAttribute("error", "Session expired. Please login again.");
            return "redirect:/login?message=Session%20expired.%20Please%20login%20again.";
        }

        try {
            if (isBlank(user.getUsername()) || isBlank(user.getPassword()) ||
                    isBlank(user.getFirstName()) || isBlank(user.getLastName()) ||
                    isBlank(user.getPhoneNumber())) {
                logger.debug("createPatient validation failed - missing required fields: username present={}, firstName present={}, lastName present={}, phone present={}",
                        !isBlank(user.getUsername()), !isBlank(user.getFirstName()), !isBlank(user.getLastName()), !isBlank(user.getPhoneNumber()));
                redirectAttributes.addFlashAttribute("error", "Please fill all required fields");
                return "redirect:/admin/users/create-patient";
            }

            if (userRepository.findByUsername(user.getUsername()).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Username already exists");
                return "redirect:/admin/users/create-patient";
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            String patientId = generatePatientId();
            user.setPatientId(patientId);
            user.setCreatedBy(authentication.getName());
            user.setUpdatedBy(authentication.getName());
            user.setEnabled(true);

            Role patientRole = roleRepository.findByName("PATIENT")
                    .orElseThrow(() -> new RuntimeException("PATIENT role not found"));
            user.setRoles(Collections.singletonList(patientRole));

            userRepository.save(user);
            logger.info("Patient created by {} with id {}", authentication.getName(), patientId);
            redirectAttributes.addFlashAttribute("success", "Patient created successfully with ID: " + patientId);
            return "redirect:/admin/users/list";
        } catch (Exception e) {
            logger.error("Error creating patient", e);
            redirectAttributes.addFlashAttribute("error", "Error creating patient: " + e.getMessage());
            return "redirect:/admin/users/create-patient";
        }
    }

    // ================ CREATE ATTENDANT (POST) ================

    @PostMapping("/create-attendant")
    public String createAttendant(@ModelAttribute User user,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes) {
        logger.info("createAttendant called; auth present={}, principal={}",
                authentication != null, authentication != null ? authentication.getName() : "null");
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warn("createAttendant invoked without authenticated principal; redirecting to login");
            redirectAttributes.addFlashAttribute("error", "Session expired. Please login again.");
            return "redirect:/login?message=Session%20expired.%20Please%20login%20again.";
        }

        try {
            if (isBlank(user.getUsername()) || isBlank(user.getPassword()) ||
                    isBlank(user.getFirstName()) || isBlank(user.getLastName()) ||
                    isBlank(user.getPhoneNumber())) {
                logger.debug("createAttendant validation failed - missing required fields: username present={}, firstName present={}, lastName present={}, phone present={}",
                        !isBlank(user.getUsername()), !isBlank(user.getFirstName()), !isBlank(user.getLastName()), !isBlank(user.getPhoneNumber()));
                redirectAttributes.addFlashAttribute("error", "Please fill all required fields");
                return "redirect:/admin/users/create-attendant";
            }

            if (userRepository.findByUsername(user.getUsername()).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Username already exists");
                return "redirect:/admin/users/create-attendant";
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            String employeeId = generateEmployeeId("ATT");
            user.setEmployeeId(employeeId);
            user.setCreatedBy(authentication.getName());
            user.setUpdatedBy(authentication.getName());
            user.setEnabled(true);

            Role attendantRole = roleRepository.findByName("ATTENDANT")
                    .orElseThrow(() -> new RuntimeException("ATTENDANT role not found"));
            user.setRoles(Collections.singletonList(attendantRole));

            userRepository.save(user);
            logger.info("Attendant created by {} with id {}", authentication.getName(), employeeId);
            redirectAttributes.addFlashAttribute("success", "Attendant created successfully with ID: " + employeeId);
            return "redirect:/admin/users/list";
        } catch (Exception e) {
            logger.error("Error creating attendant", e);
            redirectAttributes.addFlashAttribute("error", "Error creating attendant: " + e.getMessage());
            return "redirect:/admin/users/create-attendant";
        }
    }

    // ================ CREATE PHARMA (POST) ================

    @PostMapping("/create-pharma")
    public String createPharma(@ModelAttribute User user,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        logger.info("createPharma called; auth present={}, principal={}",
                authentication != null, authentication != null ? authentication.getName() : "null");
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warn("createPharma invoked without authenticated principal; redirecting to login");
            redirectAttributes.addFlashAttribute("error", "Session expired. Please login again.");
            return "redirect:/login?message=Session%20expired.%20Please%20login%20again.";
        }

        try {
            if (isBlank(user.getUsername()) || isBlank(user.getPassword()) ||
                    isBlank(user.getFirstName()) || isBlank(user.getLastName()) ||
                    isBlank(user.getPhoneNumber())) {
                logger.debug("createPharma validation failed - missing required fields: username present={}, firstName present={}, lastName present={}, phone present={}",
                        !isBlank(user.getUsername()), !isBlank(user.getFirstName()), !isBlank(user.getLastName()), !isBlank(user.getPhoneNumber()));
                redirectAttributes.addFlashAttribute("error", "Please fill all required fields");
                return "redirect:/admin/users/create-pharma";
            }

            if (userRepository.findByUsername(user.getUsername()).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Username already exists");
                return "redirect:/admin/users/create-pharma";
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            String employeeId = generateEmployeeId("PHA");
            user.setEmployeeId(employeeId);
            user.setDepartment("Pharmacy");
            user.setCreatedBy(authentication.getName());
            user.setUpdatedBy(authentication.getName());
            user.setEnabled(true);

            Role pharmaRole = roleRepository.findByName("PHARMA")
                    .orElseThrow(() -> new RuntimeException("PHARMA role not found"));
            user.setRoles(Collections.singletonList(pharmaRole));

            userRepository.save(user);
            logger.info("Pharma user created by {} with id {}", authentication.getName(), employeeId);
            redirectAttributes.addFlashAttribute("success", "Pharmacy user created successfully with ID: " + employeeId);
            return "redirect:/admin/users/list";
        } catch (Exception e) {
            logger.error("Error creating pharmacy user", e);
            redirectAttributes.addFlashAttribute("error", "Error creating pharmacy user: " + e.getMessage());
            return "redirect:/admin/users/create-pharma";
        }
    }

    // ================ UPDATE USER (POST) ================

    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable Long id,
                             @ModelAttribute User updatedUser,
                             @RequestParam(required = false) String newPassword,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        logger.info("updateUser called; auth present={}, principal={}", authentication != null, authentication != null ? authentication.getName() : "null");
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warn("updateUser invoked without authenticated principal; redirecting to login");
            redirectAttributes.addFlashAttribute("error", "Session expired. Please login again.");
            return "redirect:/login?message=Session%20expired.%20Please%20login%20again.";
        }

        try {
            User existingUser = userRepository.findById(id).orElse(null);
            if (existingUser == null) {
                redirectAttributes.addFlashAttribute("error", "User not found");
                return "redirect:/admin/users/list";
            }

            existingUser.setFirstName(updatedUser.getFirstName());
            existingUser.setLastName(updatedUser.getLastName());
            existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setAddress(updatedUser.getAddress());
            existingUser.setGender(updatedUser.getGender());
            existingUser.setDateOfBirth(updatedUser.getDateOfBirth());
            existingUser.setDepartment(updatedUser.getDepartment());
            existingUser.setSpecialization(updatedUser.getSpecialization());
            existingUser.setLicenseNumber(updatedUser.getLicenseNumber());

            if (newPassword != null && !newPassword.trim().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(newPassword));
            }

            existingUser.setUpdatedBy(authentication.getName());
            existingUser.setUpdatedDate(LocalDateTime.now());

            userRepository.save(existingUser);

            redirectAttributes.addFlashAttribute("success", "User updated successfully");
            return "redirect:/admin/users/list";
        } catch (Exception e) {
            logger.error("Error updating user", e);
            redirectAttributes.addFlashAttribute("error", "Error updating user: " + e.getMessage());
            return "redirect:/admin/users/edit/" + id;
        }
    }

    // ================ HELPER METHODS ================

    private String generateEmployeeId(String prefix) {
        long count = userRepository.countByEmployeeIdStartingWith(prefix);
        return String.format("%s%05d", prefix, count + 1);
    }

    private String generatePatientId() {
        long count = userRepository.countByPatientIdNotNull();
        return String.format("PAT%05d", count + 1);
    }

    // Helper to check blank strings
    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
