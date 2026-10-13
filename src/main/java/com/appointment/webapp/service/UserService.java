package com.appointment.webapp.service;

import com.appointment.webapp.model.Role;
import com.appointment.webapp.model.User;
import com.appointment.webapp.repository.RoleRepository;
import com.appointment.webapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(User user, String roleName) {
        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Assign role
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
        user.setRoles(Arrays.asList(role));

        // Copy role privileges to user
        if (role.getPrivileges() != null && !role.getPrivileges().isEmpty()) {
            user.setPrivileges(new java.util.HashSet<>(role.getPrivileges()));
        }

        // Ensure enabled
        user.setEnabled(true);

        // Generate IDs based on role
        if ("PATIENT".equals(roleName)) {
            user.setPatientId(generatePatientId());
        } else {
            user.setEmployeeId(generateEmployeeId(roleName));
        }

        return userRepository.save(user);
    }

    public User updateUser(Long userId, User userDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setPhoneNumber(userDetails.getPhoneNumber());
        user.setEmail(userDetails.getEmail());
        user.setAddress(userDetails.getAddress());
        user.setDateOfBirth(userDetails.getDateOfBirth());
        user.setGender(userDetails.getGender());
        user.setDepartment(userDetails.getDepartment());
        user.setSpecialization(userDetails.getSpecialization());
        user.setLicenseNumber(userDetails.getLicenseNumber());

        return userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public void toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getUsersByRole(String roleName) {
        return userRepository.findByRoleName(roleName);
    }

    public List<User> getActiveUsers() {
        return userRepository.findByEnabledTrue();
    }

    public List<User> searchUsers(String keyword) {
        return userRepository.searchUsers(keyword);
    }

    public List<User> searchUsersByRoleAndTerm(String roleName, String searchTerm) {
        return userRepository.findByRoleAndSearch(roleName, searchTerm);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    public void changePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // Helper methods to generate IDs
    private String generatePatientId() {
        long count = userRepository.findByRoleName("PATIENT").size();
        return String.format("PAT%05d", count + 1);
    }

    private String generateEmployeeId(String roleName) {
        String prefix = switch (roleName) {
            case "DOCTOR" -> "DOC";
            case "ATTENDANT" -> "ATT";
            case "PHARMA" -> "PHR";
            case "ADMIN" -> "ADM";
            default -> "EMP";
        };

        long count = userRepository.findByRoleName(roleName).size();
        return String.format("%s%05d", prefix, count + 1);
    }
}
