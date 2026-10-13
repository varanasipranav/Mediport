package com.appointment.webapp.config;

import com.appointment.webapp.model.Privilege;
import com.appointment.webapp.model.Role;
import com.appointment.webapp.model.User;
import com.appointment.webapp.repository.PrivilegeRepository;
import com.appointment.webapp.repository.RoleRepository;
import com.appointment.webapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @org.springframework.beans.factory.annotation.Value("${app.admin.password:}")
    private String configuredAdminPassword;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Check if data already exists
        if (roleRepository.count() > 0) {
            System.out.println("Data already loaded. Skipping initialization.");
            return;
        }

        System.out.println("Loading initial data...");

        // Create Privileges
        Privilege readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE", "Read access");
        Privilege writePrivilege = createPrivilegeIfNotFound("WRITE_PRIVILEGE", "Write access");
        Privilege deletePrivilege = createPrivilegeIfNotFound("DELETE_PRIVILEGE", "Delete access");
        Privilege manageUsersPrivilege = createPrivilegeIfNotFound("MANAGE_USERS", "Manage users");
        Privilege manageAppointmentsPrivilege = createPrivilegeIfNotFound("MANAGE_APPOINTMENTS", "Manage appointments");
        Privilege managePatientsPrivilege = createPrivilegeIfNotFound("MANAGE_PATIENTS", "Manage patients");
        Privilege managePrescriptionsPrivilege = createPrivilegeIfNotFound("MANAGE_PRESCRIPTIONS", "Manage prescriptions");
        Privilege manageInventoryPrivilege = createPrivilegeIfNotFound("MANAGE_INVENTORY", "Manage pharmacy inventory");
        Privilege viewReportsPrivilege = createPrivilegeIfNotFound("VIEW_REPORTS", "View reports");
        Privilege changePasswordPrivilege = createPrivilegeIfNotFound("CHANGE_PASSWORD_PRIVILEGE", "Change password");

        // Create Roles
        // Admin Role
        Set<Privilege> adminPrivileges = new HashSet<>(Arrays.asList(
            readPrivilege, writePrivilege, deletePrivilege, manageUsersPrivilege,
            manageAppointmentsPrivilege, managePatientsPrivilege, managePrescriptionsPrivilege,
            manageInventoryPrivilege, viewReportsPrivilege, changePasswordPrivilege
        ));
        Role adminRole = createRoleIfNotFound("ADMIN", "System Administrator", adminPrivileges);

        // Doctor Role
        Set<Privilege> doctorPrivileges = new HashSet<>(Arrays.asList(
            readPrivilege, writePrivilege, manageAppointmentsPrivilege,
            managePatientsPrivilege, managePrescriptionsPrivilege, viewReportsPrivilege,
            changePasswordPrivilege
        ));
        Role doctorRole = createRoleIfNotFound("DOCTOR", "Medical Doctor", doctorPrivileges);

        // Patient Role
        Set<Privilege> patientPrivileges = new HashSet<>(Arrays.asList(
            readPrivilege, changePasswordPrivilege
        ));
        Role patientRole = createRoleIfNotFound("PATIENT", "Patient", patientPrivileges);

        // Attendant Role
        Set<Privilege> attendantPrivileges = new HashSet<>(Arrays.asList(
            readPrivilege, writePrivilege, manageAppointmentsPrivilege,
            managePatientsPrivilege, changePasswordPrivilege
        ));
        Role attendantRole = createRoleIfNotFound("ATTENDANT", "Hospital Attendant", attendantPrivileges);

        // Pharmacy Role
        Set<Privilege> pharmaPrivileges = new HashSet<>(Arrays.asList(
            readPrivilege, writePrivilege, managePrescriptionsPrivilege,
            manageInventoryPrivilege, changePasswordPrivilege
        ));
        Role pharmaRole = createRoleIfNotFound("PHARMA", "Pharmacy Staff", pharmaPrivileges);

        // Create Default Admin User
        if (!userRepository.existsByUsername("admin")) {
            boolean generated = configuredAdminPassword == null || configuredAdminPassword.isBlank();
            String adminPassword = generated
                    ? java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 16)
                    : configuredAdminPassword;
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode(adminPassword));
            adminUser.setFirstName("System");
            adminUser.setLastName("Administrator");
            adminUser.setPhoneNumber("1234567890");
            adminUser.setEmail("admin@hospital.com");
            adminUser.setEnabled(true);
            adminUser.setAccountNonLocked(true);
            adminUser.setEmployeeId("EMP001");
            adminUser.setDepartment("Administration");
            adminUser.setRoles(Arrays.asList(adminRole));
            adminUser.setCreatedBy("system");
            adminUser.setUpdatedBy("system");

            userRepository.save(adminUser);
            if (generated) {
                System.out.println("Default admin user created: username=admin, generated password=" + adminPassword
                        + " (shown once - change it after first login or set APP_ADMIN_PASSWORD)");
            } else {
                System.out.println("Default admin user created: username=admin (password from APP_ADMIN_PASSWORD)");
            }
        }

        System.out.println("Data initialization completed successfully!");
        System.out.println("Available Roles: ADMIN, DOCTOR, PATIENT, ATTENDANT, PHARMA");
        System.out.println("Login with: username=admin, password=admin123");
    }

    @Transactional
    Privilege createPrivilegeIfNotFound(String name, String description) {
        return privilegeRepository.findByName(name)
                .orElseGet(() -> {
                    Privilege privilege = new Privilege(name, description);
                    return privilegeRepository.save(privilege);
                });
    }

    @Transactional
    Role createRoleIfNotFound(String name, String description, Set<Privilege> privileges) {
        return roleRepository.findByName(name)
                .orElseGet(() -> {
                    Role role = new Role(name, description);
                    role.setPrivileges(privileges);
                    return roleRepository.save(role);
                });
    }
}
