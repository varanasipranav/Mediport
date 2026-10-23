package com.appointment.webapp.tenancy;

import com.appointment.webapp.model.Role;
import com.appointment.webapp.model.User;
import com.appointment.webapp.repository.RoleRepository;
import com.appointment.webapp.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

/** Creates and resets the first administrator of a clinic. Must run inside a tenant context. */
@Component
public class TenantSeeder {

    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder encoder;

    public TenantSeeder(UserRepository users, RoleRepository roles, PasswordEncoder encoder) {
        this.users = users;
        this.roles = roles;
        this.encoder = encoder;
    }

    public static String randomPassword() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        return sb.toString();
    }

    @Transactional
    public void createAdmin(String rawPassword, String email, String clinicName) {
        if (users.existsByUsername("admin")) return;
        Role admin = roles.findByName("ADMIN").orElseThrow(() -> new IllegalStateException("ADMIN role missing"));
        User u = new User();
        u.setUsername("admin");
        u.setPassword(encoder.encode(rawPassword));
        u.setFirstName("System");
        u.setLastName("Administrator");
        u.setPhoneNumber("0000000000");
        u.setEmail(email == null || email.isBlank() ? "admin@example.com" : email);
        u.setEnabled(true);
        u.setAccountNonLocked(true);
        u.setEmployeeId("EMP001");
        u.setDepartment("Administration");
        u.setRoles(List.of(admin));
        u.setCreatedBy("system");
        u.setUpdatedBy("system");
        users.save(u);
    }

    @Transactional
    public boolean resetAdminPassword(String rawPassword) {
        return users.findByUsername("admin").map(u -> {
            u.setPassword(encoder.encode(rawPassword));
            u.setAccountNonLocked(true);
            u.setEnabled(true);
            users.save(u);
            return true;
        }).orElse(false);
    }
}
