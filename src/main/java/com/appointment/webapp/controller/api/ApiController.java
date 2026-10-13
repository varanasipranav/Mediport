package com.appointment.webapp.controller.api;

import com.appointment.webapp.model.User;
import com.appointment.webapp.repository.RoleRepository;
import com.appointment.webapp.repository.UserRepository;
import com.appointment.webapp.service.AppointmentService;
import com.appointment.webapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST API Controller for AJAX operations
 */
@RestController
@RequestMapping("/api")
@org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ATTENDANT','ADMIN')")
public class ApiController {

    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    /**
     * Search patient by phone number
     */
    @GetMapping("/patients/search-by-phone")
    public Map<String, Object> searchPatientByPhone(@RequestParam String phone) {
        Map<String, Object> response = new HashMap<>();

        // Find all patients with this phone number
        List<User> patients = userRepository.findAllByPhoneNumber(phone);

        if (!patients.isEmpty()) {
            response.put("found", true);
            response.put("multiple", patients.size() > 1);

            if (patients.size() == 1) {
                // Single patient found
                response.put("patient", convertUserToMap(patients.get(0)));
            } else {
                // Multiple patients found
                List<Map<String, Object>> patientList = new ArrayList<>();
                for (User patient : patients) {
                    patientList.add(convertUserToMap(patient));
                }
                response.put("patients", patientList);
            }
        } else {
            response.put("found", false);
            response.put("multiple", false);
        }

        return response;
    }

    /**
     * Book appointment with optional patient creation
     */
    @PostMapping("/attendant/book-appointment-json")
    public Map<String, Object> bookAppointmentJson(@RequestBody Map<String, Object> requestData,
                                                    Authentication authentication) {
        logger.info("API: Received bookAppointmentJson request: {} from user {}", requestData, authentication != null ? authentication.getName() : "anonymous");

        Map<String, Object> response = new HashMap<>();

        try {
            Long doctorId = Long.valueOf(requestData.get("doctorId").toString());
            LocalDate appointmentDate = LocalDate.parse(requestData.get("appointmentDate").toString());

            User patient;

            // Check if patientId exists (existing patient) or create new
            if (requestData.containsKey("patientId")) {
                Long patientId = Long.valueOf(requestData.get("patientId").toString());
                patient = userRepository.findById(patientId)
                    .orElseThrow(() -> new RuntimeException("Patient not found"));
            } else if (requestData.containsKey("newPatient")) {
                // Create new patient
                @SuppressWarnings("unchecked")
                Map<String, String> newPatientData = (Map<String, String>) requestData.get("newPatient");
                patient = createPatient(newPatientData);
            } else {
                throw new RuntimeException("Patient information required");
            }

            User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

            // Book appointment
            var appointment = appointmentService.bookAppointment(
                patient, doctor, appointmentDate, authentication != null ? authentication.getName() : "system"
            );

            response.put("success", true);
            response.put("message", "Appointment booked successfully! Queue #" +
                appointment.getQueueNumber() + " at " + appointment.getScheduledTime());
            response.put("appointmentId", appointment.getId());
            response.put("queueNumber", appointment.getQueueNumber());

            logger.info("API: Booking successful: {}", response);

        } catch (Exception e) {
            logger.error("API: Error booking appointment", e);
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }

        return response;
    }

    /**
     * Create new patient user
     */
    private User createPatient(Map<String, String> patientData) {
        User patient = new User();

        String phoneNumber = patientData.get("phoneNumber");
        String firstName = patientData.get("firstName");
        String lastName = patientData.get("lastName");

        // Generate username from phone number
        String username = "PAT" + phoneNumber;

        // Check if username already exists
        int counter = 1;
        String finalUsername = username;
        while (userRepository.findByUsername(finalUsername).isPresent()) {
            finalUsername = username + counter++;
        }

        patient.setUsername(finalUsername);
        patient.setPassword(passwordEncoder.encode(phoneNumber)); // Default password = phone number
        patient.setFirstName(firstName);
        patient.setLastName(lastName);
        patient.setPhoneNumber(phoneNumber);
        patient.setGender(patientData.get("gender"));

        if (patientData.get("dateOfBirth") != null && !patientData.get("dateOfBirth").isEmpty()) {
            patient.setDateOfBirth(LocalDate.parse(patientData.get("dateOfBirth")));
        }

        if (patientData.get("email") != null && !patientData.get("email").isEmpty()) {
            patient.setEmail(patientData.get("email"));
        }

        if (patientData.get("address") != null && !patientData.get("address").isEmpty()) {
            patient.setAddress(patientData.get("address"));
        }

        patient.setEnabled(true);
        patient.setAccountNonLocked(true);

        // Generate patient ID
        long patientCount = userRepository.countByRoleName("PATIENT");
        patient.setPatientId("PAT" + String.format("%05d", patientCount + 1));

        // Assign PATIENT role
        var patientRole = roleRepository.findByName("PATIENT")
            .orElseThrow(() -> new RuntimeException("PATIENT role not found"));
        patient.setRoles(Collections.singletonList(patientRole));

        return userRepository.save(patient);
    }

    /**
     * Convert User entity to Map for JSON response
     */
    private Map<String, Object> convertUserToMap(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("firstName", user.getFirstName());
        map.put("lastName", user.getLastName());
        map.put("phoneNumber", user.getPhoneNumber());
        map.put("patientId", user.getPatientId());
        map.put("gender", user.getGender());
        map.put("dateOfBirth", user.getDateOfBirth() != null ? user.getDateOfBirth().toString() : null);
        map.put("email", user.getEmail());
        map.put("address", user.getAddress());
        return map;
    }
}
