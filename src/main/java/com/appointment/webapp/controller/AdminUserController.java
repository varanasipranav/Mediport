package com.appointment.webapp.controller;

import com.appointment.webapp.model.User;
import com.appointment.webapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public String listUsers(
            @RequestParam(required = false) String roleFilter,
            @RequestParam(required = false) String search,
            Model model) {

        List<User> users;

        if (roleFilter != null && !roleFilter.isEmpty() && search != null && !search.isEmpty()) {
            // Both filter and search
            users = userService.searchUsersByRoleAndTerm(roleFilter, search.toLowerCase());
        } else if (roleFilter != null && !roleFilter.isEmpty()) {
            // Only role filter
            users = userService.getUsersByRole(roleFilter);
        } else if (search != null && !search.isEmpty()) {
            // Only search
            users = userService.searchUsers(search);
        } else {
            // No filter or search
            users = userService.getAllUsers();
        }

        model.addAttribute("users", users);
        model.addAttribute("roleFilter", roleFilter);
        model.addAttribute("search", search);
        model.addAttribute("pageTitle", "User Management");
        return "admin/users/list";
    }

    @GetMapping("/create")
    public String showCreateForm() {
        return "redirect:/admin/users/list";
    }

    @PostMapping("/create")
    public String createUser(@ModelAttribute User user,
                            @RequestParam String role,
                            RedirectAttributes redirectAttributes) {
        logger.info("AdminUserController.createUser called: role={} username={}", role, user != null ? user.getUsername() : null);
        try {
            if (userService.existsByUsername(user.getUsername())) {
                redirectAttributes.addFlashAttribute("error", "Username already exists!");
                return "redirect:/admin/users/create";
            }

            userService.createUser(user, role);
            redirectAttributes.addFlashAttribute("success",
                role + " user created successfully! Username: " + user.getUsername());
            return "redirect:/admin/users/list";
        } catch (Exception e) {
            logger.error("Error creating user", e);
            redirectAttributes.addFlashAttribute("error", "Error creating user: " + e.getMessage());
            return "redirect:/admin/users/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getUserById(id);
            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "User not found");
                return "redirect:/admin/users/list";
            }

            model.addAttribute("user", user);
            model.addAttribute("isEdit", true);
            user.getRoles().stream().findFirst().ifPresent(r -> model.addAttribute("role", r.getName()));
            model.addAttribute("pageTitle", "Edit User");

            // Return role-specific form
            if (user.getRoles().stream().anyMatch(r -> r.getName().equals("DOCTOR"))) {
                return "admin/users/create-doctor";
            } else if (user.getRoles().stream().anyMatch(r -> r.getName().equals("PATIENT"))) {
                return "admin/users/create-patient";
            } else if (user.getRoles().stream().anyMatch(r -> r.getName().equals("ATTENDANT"))) {
                return "admin/users/create-attendant";
            } else if (user.getRoles().stream().anyMatch(r -> r.getName().equals("PHARMA"))) {
                return "admin/users/create-pharma";
            }

            // Fallback to generic edit
            return "admin/users/edit";
        } catch (Exception e) {
            logger.error("Error loading edit form", e);
            redirectAttributes.addFlashAttribute("error", "Error loading user: " + e.getMessage());
            return "redirect:/admin/users/list";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable Long id,
                            @ModelAttribute User user,
                            RedirectAttributes redirectAttributes) {
        try {
            userService.updateUser(id, user);
            redirectAttributes.addFlashAttribute("success", "User updated successfully!");
            return "redirect:/admin/users/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating user: " + e.getMessage());
            return "redirect:/admin/users/edit/" + id;
        }
    }

    @PostMapping("/toggle-status/{id}")
    public String toggleUserStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.toggleUserStatus(id);
            redirectAttributes.addFlashAttribute("success", "User status updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating user status: " + e.getMessage());
        }
        return "redirect:/admin/users/list";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "User deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting user: " + e.getMessage());
        }
        return "redirect:/admin/users/list";
    }

    @GetMapping("/search")
    @ResponseBody
    public List<java.util.Map<String, Object>> searchUsers(@RequestParam String keyword) {
        // Only what a picker needs - never the password hash or other account internals
        return userService.searchUsers(keyword).stream().map(u -> {
            java.util.Map<String, Object> m = new java.util.LinkedHashMap<>();
            m.put("id", u.getId());
            m.put("username", u.getUsername());
            m.put("name", (u.getFirstName() + " " + u.getLastName()).trim());
            m.put("phone", u.getPhoneNumber());
            m.put("email", u.getEmail());
            return m;
        }).toList();
    }

    // Role-specific user creation pages
    @GetMapping("/create-doctor")
    public String showCreateDoctorForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("role", "DOCTOR");
        model.addAttribute("pageTitle", "Create Doctor");
        return "admin/users/create-doctor";
    }

    @GetMapping("/create-patient")
    public String showCreatePatientForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("role", "PATIENT");
        model.addAttribute("pageTitle", "Create Patient");
        return "admin/users/create-patient";
    }

    @GetMapping("/create-attendant")
    public String showCreateAttendantForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("role", "ATTENDANT");
        model.addAttribute("pageTitle", "Create Attendant");
        return "admin/users/create-attendant";
    }

    @GetMapping("/create-pharma")
    public String showCreatePharmaForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("role", "PHARMA");
        model.addAttribute("pageTitle", "Create Pharmacy User");
        return "admin/users/create-pharma";
    }
}
