package com.appointment.webapp.controller;

import com.appointment.webapp.model.User;
import com.appointment.webapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private UserService userService;

    @GetMapping({"/dashboard", "/dashboard/index"})
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // Get current user
        User currentUser = userService.getUserByUsername(username).orElse(null);

        // Get statistics
        long totalUsers = userService.getAllUsers().size();
        long totalDoctors = userService.getUsersByRole("DOCTOR").size();
        long totalPatients = userService.getUsersByRole("PATIENT").size();
        long totalAttendants = userService.getUsersByRole("ATTENDANT").size();
        long totalPharma = userService.getUsersByRole("PHARMA").size();
        long activeUsers = userService.getActiveUsers().size();

        // Get recent users (last 5)
        List<User> allUsers = userService.getAllUsers();
        List<User> recentUsers = allUsers.stream()
                .sorted((u1, u2) -> u2.getCreatedDate().compareTo(u1.getCreatedDate()))
                .limit(5)
                .toList();

        // Add to model
        model.addAttribute("userName", username);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("pageTitle", "Dashboard");

        // Statistics
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalDoctors", totalDoctors);
        model.addAttribute("totalPatients", totalPatients);
        model.addAttribute("totalAttendants", totalAttendants);
        model.addAttribute("totalPharma", totalPharma);
        model.addAttribute("activeUsers", activeUsers);

        // Recent activity
        model.addAttribute("recentUsers", recentUsers);

        return "dashboard/index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Render a friendly logout confirmation page; Spring Security already performs the logout
    @GetMapping("/logout")
    public String logoutPage(@RequestParam(value = "logSucc", required = false) String logSucc, Model model) {
        boolean loggedOut = (logSucc != null);
        model.addAttribute("loggedOut", loggedOut);
        return "logout";
    }

}
