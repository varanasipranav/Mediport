package com.appointment.webapp.service;

import com.appointment.webapp.model.User;
import com.appointment.webapp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Scheduled service to auto-generate daily schedules from weekly templates
 * Runs daily to ensure doctors always have schedules ready
 */
@Service
public class ScheduleAutoGenerationService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleAutoGenerationService.class);

    @Autowired
    private DoctorWeeklyTemplateService weeklyTemplateService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Auto-generate schedules for all doctors every day at 2 AM
     * Generates schedules for the next 30 days
     */
    @Scheduled(cron = "0 0 2 * * ?") // Every day at 2:00 AM
    public void autoGenerateSchedulesDaily() {
        logger.info("Starting auto-generation of daily schedules from weekly templates");

        try {
            // Get all doctors
            List<User> doctors = userRepository.findByRoleName("DOCTOR");

            LocalDate today = LocalDate.now();
            LocalDate endDate = today.plusDays(30); // Generate for next 30 days

            int totalGenerated = 0;

            for (User doctor : doctors) {
                try {
                    // Check if doctor has weekly templates
                    if (weeklyTemplateService.hasWeeklyTemplates(doctor)) {
                        var schedules = weeklyTemplateService.generateSchedulesFromTemplates(
                            doctor, today, endDate, "system-auto-daily"
                        );

                        totalGenerated += schedules.size();

                        logger.info("Generated {} schedules for doctor {}",
                                   schedules.size(), doctor.getUsername());
                    }
                } catch (Exception e) {
                    logger.error("Error generating schedules for doctor {}: {}",
                               doctor.getUsername(), e.getMessage());
                }
            }

            logger.info("Auto-generation completed. Total schedules created: {}", totalGenerated);

        } catch (Exception e) {
            logger.error("Error in auto-generation process", e);
        }
    }

    /**
     * Alternative: Generate schedules every Sunday at midnight for the upcoming week
     */
    @Scheduled(cron = "0 0 0 * * SUN") // Every Sunday at midnight
    public void weeklyScheduleGeneration() {
        logger.info("Starting weekly schedule generation (Sunday night)");

        try {
            List<User> doctors = userRepository.findByRoleName("DOCTOR");

            LocalDate monday = LocalDate.now().plusDays(1); // Next Monday
            LocalDate sunday = monday.plusDays(6); // Following Sunday

            int totalGenerated = 0;

            for (User doctor : doctors) {
                try {
                    if (weeklyTemplateService.hasWeeklyTemplates(doctor)) {
                        var schedules = weeklyTemplateService.generateSchedulesFromTemplates(
                            doctor, monday, sunday, "system-auto-weekly"
                        );

                        totalGenerated += schedules.size();
                    }
                } catch (Exception e) {
                    logger.error("Error generating weekly schedules for doctor {}: {}",
                               doctor.getUsername(), e.getMessage());
                }
            }

            logger.info("Weekly generation completed. Total schedules created: {}", totalGenerated);

        } catch (Exception e) {
            logger.error("Error in weekly generation process", e);
        }
    }

    /**
     * Manual trigger method (can be called from admin panel or API)
     */
    public int generateSchedulesForAllDoctors(LocalDate startDate, LocalDate endDate) {
        logger.info("Manual schedule generation triggered from {} to {}", startDate, endDate);

        List<User> doctors = userRepository.findByRoleName("DOCTOR");
        int totalGenerated = 0;

        for (User doctor : doctors) {
            try {
                if (weeklyTemplateService.hasWeeklyTemplates(doctor)) {
                    var schedules = weeklyTemplateService.generateSchedulesFromTemplates(
                        doctor, startDate, endDate, "system-manual"
                    );
                    totalGenerated += schedules.size();
                }
            } catch (Exception e) {
                logger.error("Error generating schedules for doctor {}: {}",
                           doctor.getUsername(), e.getMessage());
            }
        }

        logger.info("Manual generation completed. Total schedules created: {}", totalGenerated);
        return totalGenerated;
    }
}
