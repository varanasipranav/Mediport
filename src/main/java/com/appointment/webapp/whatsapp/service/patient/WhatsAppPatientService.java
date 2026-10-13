package com.appointment.webapp.whatsapp.service.patient;

import com.appointment.webapp.model.User;
import com.appointment.webapp.repository.RoleRepository;
import com.appointment.webapp.repository.UserRepository;
import com.appointment.webapp.whatsapp.dto.ConversationContext;
import com.appointment.webapp.whatsapp.model.WhatsAppConversation;
import com.appointment.webapp.whatsapp.service.conversation.WhatsAppConversationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * WhatsApp Patient Service
 * Handles patient lookup, registration, and linking
 */
@Service
@Transactional
public class WhatsAppPatientService {

    private static final Logger logger = LoggerFactory.getLogger(WhatsAppPatientService.class);

    private static final DateTimeFormatter DOB_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private WhatsAppConversationService conversationService;

    /**
     * Find patients by WhatsApp phone number
     */
    public List<User> findPatientsByPhone(String whatsappPhone) {
        logger.info("Searching for patients with phone: {}", whatsappPhone);

        // Try with +
        List<User> users = userRepository.findByWhatsappPhone(whatsappPhone);

        // Try without + if not found
        if (users.isEmpty() && whatsappPhone.startsWith("+")) {
            String phoneWithoutPlus = whatsappPhone.substring(1);
            users = userRepository.findByWhatsappPhone(phoneWithoutPlus);
        }

        // Try adding + if not found
        if (users.isEmpty() && !whatsappPhone.startsWith("+")) {
            String phoneWithPlus = "+" + whatsappPhone;
            users = userRepository.findByWhatsappPhone(phoneWithPlus);
        }

        logger.info("Found {} patient(s) with phone: {}", users.size(), whatsappPhone);
        return users;
    }

    /**
     * Create new patient from WhatsApp conversation context
     */
    public User createPatientFromContext(WhatsAppConversation conversation, ConversationContext context) {
        logger.info("Creating new patient from WhatsApp conversation");
        logger.info("   Phone: {}", conversation.getWhatsappPhone());
        logger.info("   Name: {}", context.getTempPatientName());

        try {
            User patient = new User();

            // Basic info
            patient.setFirstName(extractFirstName(context.getTempPatientName()));
            patient.setLastName(extractLastName(context.getTempPatientName()));
            patient.setEmail(generateEmail(conversation.getWhatsappPhone()));
            patient.setUsername(generateUsername(conversation.getWhatsappPhone()));

            // Password - random for WhatsApp users
            String randomPassword = UUID.randomUUID().toString();
            patient.setPassword(passwordEncoder.encode(randomPassword));

            // Phone numbers - both regular and WhatsApp
            patient.setPhoneNumber(conversation.getWhatsappPhone());  // Required NOT NULL field
            patient.setWhatsappPhone(conversation.getWhatsappPhone());

            // Gender
            if (context.getTempPatientGender() != null) {
                patient.setGender(context.getTempPatientGender());
            }

            // Date of Birth and Age
            if (context.getTempPatientDob() != null) {
                LocalDate dob = parseDateOfBirth(context.getTempPatientDob());
                patient.setDateOfBirth(dob);

                // Calculate age
                int age = calculateAge(dob);
                patient.setAge(age);

                logger.info("   DOB: {}, Age: {}", dob, age);
            } else if (context.getTempPatientAge() != null) {
                patient.setAge(context.getTempPatientAge());
            }

            // Set role as PATIENT
            var patientRole = roleRepository.findByName("PATIENT")
                .orElseThrow(() -> new RuntimeException("PATIENT role not found in database"));
            patient.setRoles(List.of(patientRole));

            // Additional fields
            patient.setEnabled(true);
            patient.setCreatedBy("whatsapp-bot");

            // Save patient
            User savedPatient = userRepository.save(patient);
            logger.info("✅ Patient created successfully! ID: {}", savedPatient.getId());

            // Link to conversation
            conversationService.linkUser(conversation, savedPatient);

            return savedPatient;

        } catch (Exception e) {
            logger.error("Error creating patient from WhatsApp", e);
            throw new RuntimeException("Failed to create patient: " + e.getMessage(), e);
        }
    }

    /**
     * Validate date of birth format (DD/MM/YYYY)
     */
    public boolean isValidDateOfBirth(String dobString) {
        if (dobString == null || dobString.trim().isEmpty()) {
            return false;
        }

        try {
            LocalDate dob = LocalDate.parse(dobString.trim(), DOB_FORMATTER);

            // Check reasonable age range (1 to 120 years)
            LocalDate today = LocalDate.now();
            int age = Period.between(dob, today).getYears();

            if (age < 0 || age > 120) {
                logger.warn("Invalid age calculated: {} years", age);
                return false;
            }

            // DOB cannot be in future
            if (dob.isAfter(today)) {
                logger.warn("DOB is in future: {}", dob);
                return false;
            }

            return true;

        } catch (DateTimeParseException e) {
            logger.warn("Invalid DOB format: {}", dobString);
            return false;
        }
    }

    /**
     * Parse date of birth string to LocalDate
     */
    public LocalDate parseDateOfBirth(String dobString) {
        return LocalDate.parse(dobString.trim(), DOB_FORMATTER);
    }

    /**
     * Calculate age from date of birth
     */
    public int calculateAge(LocalDate dateOfBirth) {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    /**
     * Extract first name from full name
     */
    private String extractFirstName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "Patient";
        }

        String[] parts = fullName.trim().split("\\s+");
        return parts[0];
    }

    /**
     * Extract last name from full name
     */
    private String extractLastName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "";
        }

        String[] parts = fullName.trim().split("\\s+");
        if (parts.length > 1) {
            return String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length));
        }

        return "";
    }

    /**
     * Generate email from phone number (for WhatsApp users)
     */
    private String generateEmail(String phone) {
        String cleanPhone = phone.replace("+", "").replace("-", "");
        return "whatsapp_" + cleanPhone + "@hospital.local";
    }

    /**
     * Generate username from phone number
     * Format: Just the phone number without country code prefix
     * Example: +917794093705 → 7794093705
     */
    private String generateUsername(String phone) {
        String cleanPhone = phone.replace("+", "").replace("-", "").replace(" ", "");

        // Remove country code if present (91 for India)
        if (cleanPhone.startsWith("91") && cleanPhone.length() > 10) {
            cleanPhone = cleanPhone.substring(2); // Remove '91' prefix
        }

        return cleanPhone;
    }

    /**
     * Validate patient name
     */
    public boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        String trimmed = name.trim();

        // Name should be at least 2 characters
        if (trimmed.length() < 2) {
            return false;
        }

        // Name should only contain letters, spaces, and basic punctuation
        if (!trimmed.matches("^[a-zA-Z\\s.'\\-]+$")) {
            return false;
        }

        return true;
    }

    /**
     * Format DOB for display
     */
    public String formatDateOfBirth(LocalDate dob) {
        return dob.format(DOB_FORMATTER);
    }
}
