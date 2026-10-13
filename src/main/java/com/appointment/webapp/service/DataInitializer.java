package com.appointment.webapp.service;

import com.appointment.webapp.model.Privilege;
import com.appointment.webapp.model.Role;
import com.appointment.webapp.repository.PrivilegeRepository;
import com.appointment.webapp.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
public class DataInitializer {

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private RoleRepository roleRepository;

    // privilege constants
    public static final String READ = "READ_PRIVILEGE";
    public static final String WRITE = "WRITE_PRIVILEGE";
    public static final String DELETE = "DELETE_PRIVILEGE";
    public static final String MANAGE_USERS = "MANAGE_USERS";
    public static final String MANAGE_APPOINTMENTS = "MANAGE_APPOINTMENTS";
    public static final String MANAGE_PATIENTS = "MANAGE_PATIENTS";
    public static final String MANAGE_PRESCRIPTIONS = "MANAGE_PRESCRIPTIONS";
    public static final String MANAGE_INVENTORY = "MANAGE_INVENTORY";
    public static final String VIEW_REPORTS = "VIEW_REPORTS";
    public static final String CHANGE_PASSWORD = "CHANGE_PASSWORD_PRIVILEGE";

    // role constants
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_DOCTOR = "DOCTOR";
    public static final String ROLE_PATIENT = "PATIENT";
    public static final String ROLE_ATTENDANT = "ATTENDANT";
    public static final String ROLE_PHARMA = "PHARMA";

    @PostConstruct
    @Transactional
    public void init() {
        // create privileges if missing
        Map<String, Privilege> pmap = new HashMap<>();
        List<Privilege> needed = Arrays.asList(
                new Privilege(READ),
                new Privilege(WRITE),
                new Privilege(DELETE),
                new Privilege(MANAGE_USERS),
                new Privilege(MANAGE_APPOINTMENTS),
                new Privilege(MANAGE_PATIENTS),
                new Privilege(MANAGE_PRESCRIPTIONS),
                new Privilege(MANAGE_INVENTORY),
                new Privilege(VIEW_REPORTS),
                new Privilege(CHANGE_PASSWORD)
        );

        for (Privilege p : needed) {
            Privilege db = privilegeRepository.findByName(p.getName()).orElse(null);
            if (db == null) db = privilegeRepository.save(p);
            pmap.put(db.getName(), db);
        }

        // create roles and assign privileges
        createRoleIfNotFound(ROLE_ADMIN, "System Administrator", new String[]{READ, WRITE, DELETE, MANAGE_USERS, MANAGE_APPOINTMENTS, MANAGE_PATIENTS, MANAGE_PRESCRIPTIONS, MANAGE_INVENTORY, VIEW_REPORTS, CHANGE_PASSWORD}, pmap);
        createRoleIfNotFound(ROLE_DOCTOR, "Medical Doctor", new String[]{READ, WRITE, VIEW_REPORTS, MANAGE_PATIENTS, MANAGE_PRESCRIPTIONS, CHANGE_PASSWORD}, pmap);
        createRoleIfNotFound(ROLE_PATIENT, "Patient", new String[]{READ, VIEW_REPORTS, CHANGE_PASSWORD}, pmap);
        createRoleIfNotFound(ROLE_ATTENDANT, "Hospital Attendant", new String[]{READ, MANAGE_APPOINTMENTS, MANAGE_PATIENTS, CHANGE_PASSWORD}, pmap);
        createRoleIfNotFound(ROLE_PHARMA, "Pharmacy Staff", new String[]{READ, MANAGE_INVENTORY, MANAGE_PRESCRIPTIONS, CHANGE_PASSWORD}, pmap);
    }

    private void createRoleIfNotFound(String name, String description, String[] privilegeNames, Map<String, Privilege> pmap) {
        Role role = roleRepository.findByName(name).orElse(null);
        if (role == null) {
            role = new Role(name, description);
        }
        Set<Privilege> set = new HashSet<>();
        for (String pn : privilegeNames) {
            Privilege p = pmap.get(pn);
            if (p != null) set.add(p);
        }
        role.setPrivileges(set);
        roleRepository.save(role);
    }
}
