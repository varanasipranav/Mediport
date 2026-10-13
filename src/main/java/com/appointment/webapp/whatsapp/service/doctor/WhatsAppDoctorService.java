package com.appointment.webapp.whatsapp.service.doctor;

import com.appointment.webapp.model.User;
import com.appointment.webapp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * WhatsApp Doctor Service
 * Handles doctor lookup and availability
 */
@Service
public class WhatsAppDoctorService {

    private static final Logger logger = LoggerFactory.getLogger(WhatsAppDoctorService.class);

    @Autowired
    private UserRepository userRepository;

    /**
     * Get all active doctors
     */
    public List<User> getAllActiveDoctors() {
        logger.info("Fetching all active doctors");

        List<User> doctors = userRepository.findByRoleName("DOCTOR").stream()
            .filter(User::isEnabled)
            .collect(Collectors.toList());

        logger.info("Found {} active doctors", doctors.size());
        return doctors;
    }

    /**
     * Get doctor by ID
     */
    public User getDoctorById(Long doctorId) {
        return userRepository.findById(doctorId)
            .filter(User::isEnabled)
            .orElseThrow(() -> new RuntimeException("Doctor not found or inactive"));
    }

    /**
     * Get doctors by specialization
     */
    public List<User> getDoctorsBySpecialization(String specialization) {
        logger.info("Fetching doctors with specialization: {}", specialization);

        return userRepository.findByRoleName("DOCTOR").stream()
            .filter(User::isEnabled)
            .filter(d -> specialization.equalsIgnoreCase(d.getSpecialization()))
            .collect(Collectors.toList());
    }

    /**
     * Format doctor name for display
     */
    public String formatDoctorName(User doctor) {
        return String.format("Dr. %s %s", doctor.getFirstName(), doctor.getLastName());
    }

    /**
     * Format doctor description
     */
    public String formatDoctorDescription(User doctor) {
        StringBuilder desc = new StringBuilder();

        if (doctor.getSpecialization() != null && !doctor.getSpecialization().isEmpty()) {
            desc.append(doctor.getSpecialization());
        }

        if (doctor.getDepartment() != null && !doctor.getDepartment().isEmpty()) {
            if (desc.length() > 0) desc.append(" - ");
            desc.append(doctor.getDepartment());
        }

        return desc.length() > 0 ? desc.toString() : "General Physician";
    }
}
