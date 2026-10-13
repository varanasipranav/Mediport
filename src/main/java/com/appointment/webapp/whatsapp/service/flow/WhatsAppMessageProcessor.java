package com.appointment.webapp.whatsapp.service.flow;

import com.appointment.webapp.model.User;
import com.appointment.webapp.whatsapp.dto.ConversationContext;
import com.appointment.webapp.whatsapp.dto.webhook.ParsedMessage;
import com.appointment.webapp.whatsapp.model.WhatsAppConversation;
import com.appointment.webapp.whatsapp.model.enums.ConversationState;
import com.appointment.webapp.whatsapp.service.conversation.WhatsAppConversationService;
import com.appointment.webapp.whatsapp.service.messaging.WhatsAppMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Message Processor
 * Routes messages to appropriate handlers based on conversation state
 */
@Service
public class WhatsAppMessageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(WhatsAppMessageProcessor.class);

    @Autowired
    private WhatsAppConversationService conversationService;

    @Autowired
    private WhatsAppMessageSender messageSender;

    @Autowired
    private com.appointment.webapp.whatsapp.service.patient.WhatsAppPatientService patientService;

    @Autowired
    private com.appointment.webapp.whatsapp.service.doctor.WhatsAppDoctorService doctorService;

    @Autowired
    private com.appointment.webapp.whatsapp.service.appointment.WhatsAppAppointmentService appointmentService;

    @Autowired
    private com.appointment.webapp.whatsapp.service.appointment.WhatsAppAppointmentManagementService appointmentManagementService;

    @Autowired
    private com.appointment.webapp.whatsapp.service.appointment.QueueStatusService queueStatusService;

    /**
     * Main entry point - process incoming message
     */
    public void processMessage(ParsedMessage message) {
        logger.info("════════════════════════════════════════════════════════");
        logger.info("🔄 PROCESSING MESSAGE");
        logger.info("   From: {}", message.getFrom());
        logger.info("   Sender Name: {}", message.getSenderName());
        logger.info("   Message Type: {}", message.getMessageType());

        try {
            logger.info("📞 Step 1: Getting or creating conversation...");

            // Get or create conversation
            WhatsAppConversation conversation = conversationService.getOrCreateConversation(
                message.getFrom(),
                message.getSenderName()
            );

            logger.info("✅ Conversation retrieved: ID={}, State={}",
                       conversation.getId(), conversation.getCurrentState());

            // Route to appropriate handler based on state
            logger.info("📍 Step 2: Routing to handler for state: {}", conversation.getCurrentState());

            switch (conversation.getCurrentState()) {
                case INITIAL -> {
                    logger.info("→ Calling handleInitialState");
                    handleInitialState(conversation, message);
                }
                case MAIN_MENU -> {
                    logger.info("→ Calling handleMainMenu");
                    handleMainMenu(conversation, message);
                }

                // Patient identification flow
                case CHECKING_PATIENT -> {
                    logger.info("→ Calling handleCheckingPatient");
                    handleCheckingPatient(conversation, message);
                }
                case SELECTING_EXISTING_PATIENT -> {
                    logger.info("→ Calling handleSelectingExistingPatient");
                    handleSelectingExistingPatient(conversation, message);
                }
                case ASKING_NEW_OR_EXISTING -> {
                    logger.info("→ Calling handleNewOrExisting");
                    handleNewOrExisting(conversation, message);
                }

                // Registration flow
                case COLLECTING_PATIENT_NAME -> {
                    logger.info("→ Calling handleCollectingPatientName");
                    handleCollectingPatientName(conversation, message);
                }
                case COLLECTING_PATIENT_GENDER -> {
                    logger.info("→ Calling handleCollectingPatientGender");
                    handleCollectingPatientGender(conversation, message);
                }
                case COLLECTING_PATIENT_DOB -> {
                    logger.info("→ Calling handleCollectingPatientDob");
                    handleCollectingPatientDob(conversation, message);
                }

                // Booking flow
                case SELECTING_DOCTOR -> {
                    logger.info("→ Calling handleSelectingDoctor");
                    handleSelectingDoctor(conversation, message);
                }
                case SELECTING_DATE -> {
                    logger.info("→ Calling handleSelectingDate");
                    handleSelectingDate(conversation, message);
                }
                case SELECTING_TIME_SLOT -> {
                    logger.info("→ Calling handleSelectingTimeSlot");
                    handleSelectingTimeSlot(conversation, message);
                }
                case CONFIRMING_APPOINTMENT -> {
                    logger.info("→ Calling handleConfirmingAppointment");
                    handleConfirmingAppointment(conversation, message);
                }

                // Cancel flow
                case SELECTING_APPOINTMENT_TO_CANCEL -> handleSelectingAppointmentToCancel(conversation, message);
                case CONFIRMING_CANCELLATION -> handleConfirmingCancellation(conversation, message);

                // Reschedule flow
                case SELECTING_APPOINTMENT_TO_RESCHEDULE -> handleSelectingAppointmentToReschedule(conversation, message);
                case RESCHEDULE_SELECTING_DATE -> handleRescheduleSelectingDate(conversation, message);
                case CONFIRMING_RESCHEDULE -> handleConfirmingReschedule(conversation, message);

                default -> {
                    logger.warn("⚠️ Unknown state: {}", conversation.getCurrentState());
                    handleUnknownState(conversation, message);
                }
            }

            logger.info("✅ Message processing completed successfully");
            logger.info("════════════════════════════════════════════════════════");

        } catch (Exception e) {
            logger.error("════════════════════════════════════════════════════════");
            logger.error("❌ EXCEPTION IN MESSAGE PROCESSOR!");
            logger.error("   From: {}", message.getFrom());
            logger.error("   Error type: {}", e.getClass().getName());
            logger.error("   Error message: {}", e.getMessage());
            logger.error("   Full stack trace:", e);
            logger.error("════════════════════════════════════════════════════════");
            sendErrorMessage(message.getFrom());
        }
    }

    /**
     * Handle INITIAL state - first message from user
     */
    private void handleInitialState(WhatsAppConversation conversation, ParsedMessage message) {
        logger.info("📝 HANDLING INITIAL STATE");
        logger.info("   Phone: {}", conversation.getWhatsappPhone());
        logger.info("   Sender: {}", conversation.getSenderName());

        try {
            // Send welcome message
            logger.info("📤 Step 1: Preparing welcome message...");

            String welcomeMessage = String.format(
                "🏥 *Welcome to %s!*\n\n" +
                "Hello %s! 👋\n\n" +
                "I'm your virtual assistant. I can help you:\n\n" +
                "📅 Book appointments\n" +
                "👁️ View your appointments\n" +
                "❌ Cancel appointments\n" +
                "🔄 Reschedule appointments\n\n" +
                "Let's get started!",
                "City Hospital", // TODO: Get from config
                conversation.getSenderName()
            );

            logger.info("📤 Step 2: Sending welcome message...");
            String result = messageSender.sendTextMessage(conversation.getWhatsappPhone(), welcomeMessage);

            if (result != null) {
                logger.info("✅ Welcome message sent successfully");
            } else {
                logger.error("❌ Failed to send welcome message (result was null)");
            }

            // Move to main menu
            logger.info("📤 Step 3: Updating state to MAIN_MENU...");
            conversationService.updateState(conversation, ConversationState.MAIN_MENU);
            logger.info("✅ State updated to MAIN_MENU");

            logger.info("📤 Step 4: Sending main menu...");
            sendMainMenu(conversation);
            logger.info("✅ Initial state handling completed");

        } catch (Exception e) {
            logger.error("❌ Exception in handleInitialState!", e);
            throw e; // Re-throw to be caught by main handler
        }
    }

    /**
     * Handle MAIN_MENU state
     */
    private void handleMainMenu(WhatsAppConversation conversation, ParsedMessage message) {
        logger.info("Handling main menu");

        String input = extractUserInput(message);

        if (input == null) {
            sendMainMenu(conversation);
            return;
        }

        // Check button ID or text input
        String choice = message.getListId() != null ? message.getListId()
            : (message.getButtonId() != null ? message.getButtonId() : input.trim());

        switch (choice.toLowerCase()) {
            case "book_appointment", "1", "book" -> {
                // Start booking flow - check if patient exists
                conversationService.updateState(conversation, ConversationState.CHECKING_PATIENT);
                checkPatientExists(conversation);
            }
            case "view_appointments", "2", "view" -> {
                conversationService.updateState(conversation, ConversationState.VIEWING_APPOINTMENTS);
                showUserAppointments(conversation);
            }
            case "cancel_appointment", "3", "cancel" -> {
                conversationService.updateState(conversation, ConversationState.SELECTING_APPOINTMENT_TO_CANCEL);
                showAppointmentsToCancel(conversation);
            }
            case "queue_status", "status", "queue", "5" -> {
                showQueueStatus(conversation);
            }
            case "reschedule", "4" -> {
                conversationService.updateState(conversation, ConversationState.SELECTING_APPOINTMENT_TO_RESCHEDULE);
                showAppointmentsToReschedule(conversation);
            }
            default -> {
                messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                    "Sorry, I didn't understand that. Please select an option:");
                sendMainMenu(conversation);
            }
        }
    }

    /**
     * Send main menu with interactive buttons
     */
    private void sendMainMenu(WhatsAppConversation conversation) {
        logger.info("📋 SENDING MAIN MENU");
        logger.info("   To: {}", conversation.getWhatsappPhone());

        try {
            String menuText = "What would you like to do?\n\nTap the button below to choose an option.";

            var section = new WhatsAppMessageSender.ListSection("Menu");
            section.addRow(new WhatsAppMessageSender.ListRow("book_appointment", "Book appointment", "Pick a doctor and day"));
            section.addRow(new WhatsAppMessageSender.ListRow("queue_status", "Queue status", "Where am I in today's queue?"));
            section.addRow(new WhatsAppMessageSender.ListRow("view_appointments", "My appointments", "See upcoming bookings"));
            section.addRow(new WhatsAppMessageSender.ListRow("reschedule", "Reschedule", "Move to another day"));
            section.addRow(new WhatsAppMessageSender.ListRow("cancel_appointment", "Cancel appointment", "Cancel a booking"));

            logger.info("\uD83D\uDCE4 Sending main menu list...");
            String result = messageSender.sendListMessage(conversation.getWhatsappPhone(), "Mediport", menuText, "Choose", java.util.List.of(section));

            if (result != null) {
                logger.info("✅ Main menu sent successfully");
            } else {
                logger.error("❌ Failed to send main menu (result was null)");
            }
        } catch (Exception e) {
            logger.error("❌ Exception in sendMainMenu!", e);
            throw e;
        }
    }

    /**
     * Check if patient exists by phone number
     */
    private void checkPatientExists(WhatsAppConversation conversation) {
        logger.info("🔍 CHECKING IF PATIENT EXISTS");
        logger.info("   Phone: {}", conversation.getWhatsappPhone());

        try {
            // Search for patients with this phone number
            var patients = patientService.findPatientsByPhone(conversation.getWhatsappPhone());

            if (patients.isEmpty()) {
                // No patient found - start registration
                logger.info("📝 No patient found. Starting registration...");
                messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                    "I don't have your details in our system yet.\n\n" +
                    "Let's create your patient profile! 📋\n\n" +
                    "Please enter your *full name*:");

                conversationService.updateState(conversation, ConversationState.COLLECTING_PATIENT_NAME);

            } else if (patients.size() == 1) {
                // Single patient found - use directly
                logger.info("✅ Found 1 patient: {}", patients.get(0).getFirstName());
                User patient = patients.get(0);

                conversationService.linkUser(conversation, patient);

                messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                    String.format("Welcome back, *%s %s*! 👋\n\n" +
                        "I found your profile in our system.",
                        patient.getFirstName(), patient.getLastName()));

                // Continue to doctor selection
                conversationService.updateState(conversation, ConversationState.SELECTING_DOCTOR);
                showDoctorSelection(conversation);

            } else {
                // Multiple patients found - ask to select
                logger.info("👥 Found {} patients. Asking to select...", patients.size());

                // Save patient IDs in context
                ConversationContext context = conversationService.getContext(conversation);
                context.setMultiplePatientIds(patients.stream().map(User::getId).toList());
                conversationService.saveContext(conversation, context);

                // Send list of patients
                String patientsText = buildPatientsListText(patients);
                messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                    "I found multiple profiles with this phone number:\n\n" + patientsText +
                    "\n\nOr type *'new'* to create a new profile.");

                // Send interactive list
                sendPatientSelectionList(conversation, patients);

                conversationService.updateState(conversation, ConversationState.SELECTING_EXISTING_PATIENT);
            }

        } catch (Exception e) {
            logger.error("Error checking patient existence", e);
            messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                "Sorry, there was an error. Let's try again.");
            conversationService.updateState(conversation, ConversationState.MAIN_MENU);
            sendMainMenu(conversation);
        }
    }

    /**
     * Build text list of patients
     */
    private String buildPatientsListText(java.util.List<com.appointment.webapp.model.User> patients) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < patients.size(); i++) {
            var patient = patients.get(i);
            sb.append(String.format("%d. *%s %s*",
                i + 1,
                patient.getFirstName(),
                patient.getLastName()));

            if (patient.getAge() != null) {
                sb.append(String.format(" - %d years", patient.getAge()));
            }
            if (patient.getGender() != null) {
                sb.append(String.format(" - %s", patient.getGender()));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Send interactive list for patient selection
     */
    private void sendPatientSelectionList(WhatsAppConversation conversation,
                                          java.util.List<com.appointment.webapp.model.User> patients) {
        var section = new WhatsAppMessageSender.ListSection("Select Your Profile");

        for (var patient : patients) {
            String title = patient.getFirstName() + " " + patient.getLastName();
            String description = "";
            if (patient.getAge() != null) {
                description = patient.getAge() + " years";
            }
            if (patient.getGender() != null) {
                description += (description.isEmpty() ? "" : " - ") + patient.getGender();
            }

            section.addRow(new WhatsAppMessageSender.ListRow(
                "patient_" + patient.getId(),
                title,
                description.isEmpty() ? "Select this profile" : description
            ));
        }

        // Add "Create New" option
        section.addRow(new WhatsAppMessageSender.ListRow(
            "new_patient",
            "➕ Create New Profile",
            "Register as a new patient"
        ));

        messageSender.sendListMessage(
            conversation.getWhatsappPhone(),
            "Select Profile",
            "Please select your profile or create a new one:",
            "Select",
            java.util.List.of(section)
        );
    }

    /**
     * Placeholder handlers (to be implemented in Phase 3+)
     */
    private void handleCheckingPatient(WhatsAppConversation conversation, ParsedMessage message) {
        // Already handled by checkPatientExists()
        logger.warn("handleCheckingPatient called - should not reach here");
        conversationService.updateState(conversation, ConversationState.MAIN_MENU);
        sendMainMenu(conversation);
    }

    private void handleSelectingExistingPatient(WhatsAppConversation conversation, ParsedMessage message) {
        logger.info("📋 HANDLING PATIENT SELECTION");

        String selectedId = message.getListId();  // For list replies
        if (selectedId == null) {
            selectedId = extractUserInput(message);  // For text input
        }

        if (selectedId == null) {
            messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                "Please select a profile from the list.");
            return;
        }

        try {
            if ("new_patient".equals(selectedId) || "new".equalsIgnoreCase(selectedId)) {
                // User wants to create new profile
                logger.info("User chose to create new profile");
                messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                    "Great! Let's create your new profile. 📋\n\n" +
                    "Please enter your *full name*:");

                conversationService.updateState(conversation, ConversationState.COLLECTING_PATIENT_NAME);

            } else if (selectedId.startsWith("patient_")) {
                // User selected existing patient
                Long patientId = Long.parseLong(selectedId.replace("patient_", ""));
                logger.info("User selected patient ID: {}", patientId);

                var patient = patientService.findPatientsByPhone(conversation.getWhatsappPhone()).stream()
                    .filter(p -> p.getId().equals(patientId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Patient not found"));

                conversationService.linkUser(conversation, patient);

                messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                    String.format("Welcome back, *%s %s*! 👋\n\n" +
                        "Profile selected successfully.",
                        patient.getFirstName(), patient.getLastName()));

                // Continue to doctor selection
                conversationService.updateState(conversation, ConversationState.SELECTING_DOCTOR);
                showDoctorSelection(conversation);

            } else {
                // Try to parse as number (1, 2, 3...)
                try {
                    int index = Integer.parseInt(selectedId) - 1;
                    ConversationContext context = conversationService.getContext(conversation);

                    if (context.getMultiplePatientIds() != null &&
                        index >= 0 && index < context.getMultiplePatientIds().size()) {

                        Long patientId = context.getMultiplePatientIds().get(index);
                        var patients = patientService.findPatientsByPhone(conversation.getWhatsappPhone());
                        var patient = patients.stream()
                            .filter(p -> p.getId().equals(patientId))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Patient not found"));

                        conversationService.linkUser(conversation, patient);

                        messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                            String.format("Welcome back, *%s %s*! 👋",
                                patient.getFirstName(), patient.getLastName()));

                        // Continue to doctor selection
                        conversationService.updateState(conversation, ConversationState.SELECTING_DOCTOR);
                        showDoctorSelection(conversation);

                    } else {
                        throw new NumberFormatException("Invalid index");
                    }

                } catch (NumberFormatException e) {
                    messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                        "Invalid selection. Please select from the list or type 'new'.");
                }
            }

        } catch (Exception e) {
            logger.error("Error selecting patient", e);
            messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                "Sorry, there was an error. Let's try again.");
            conversationService.updateState(conversation, ConversationState.MAIN_MENU);
            sendMainMenu(conversation);
        }
    }

    private void handleNewOrExisting(WhatsAppConversation conversation, ParsedMessage message) {
        // Not used in current flow
        logger.warn("handleNewOrExisting called - should not reach here");
        conversationService.updateState(conversation, ConversationState.MAIN_MENU);
        sendMainMenu(conversation);
    }

    private void handleCollectingPatientName(WhatsAppConversation conversation, ParsedMessage message) {
        logger.info("📝 COLLECTING PATIENT NAME");

        String name = extractUserInput(message);

        if (name == null || name.trim().isEmpty()) {
            messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                "Please enter a valid name.");
            return;
        }

        // Validate name
        if (!patientService.isValidName(name)) {
            messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                "❌ Invalid name format.\n\n" +
                "Please enter a valid name (only letters and spaces).\n" +
                "Example: John Smith");
            return;
        }

        // Save name in context
        ConversationContext context = conversationService.getContext(conversation);
        context.setTempPatientName(name.trim());
        conversationService.saveContext(conversation, context);

        logger.info("✅ Name saved: {}", name.trim());

        // Ask for gender
        messageSender.sendTextMessage(conversation.getWhatsappPhone(),
            String.format("Thanks, *%s*! 👍\n\nNow, please select your gender:", name.trim()));

        var buttons = java.util.List.of(
            new WhatsAppMessageSender.Button("male", "👨 Male"),
            new WhatsAppMessageSender.Button("female", "👩 Female"),
            new WhatsAppMessageSender.Button("other", "⚧ Other")
        );

        messageSender.sendButtonMessage(conversation.getWhatsappPhone(),
            "Select your gender:", buttons);

        conversationService.updateState(conversation, ConversationState.COLLECTING_PATIENT_GENDER);
    }

    private void handleCollectingPatientGender(WhatsAppConversation conversation, ParsedMessage message) {
        logger.info("👥 COLLECTING PATIENT GENDER");

        String gender = message.getButtonId() != null ? message.getButtonId() : extractUserInput(message);

        if (gender == null) {
            messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                "Please select a gender option from the buttons.");
            return;
        }

        // Normalize gender
        gender = gender.toLowerCase();
        if (!gender.equals("male") && !gender.equals("female") && !gender.equals("other")) {
            messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                "Please select Male, Female, or Other.");
            return;
        }

        // Save gender
        ConversationContext context = conversationService.getContext(conversation);
        context.setTempPatientGender(gender.toUpperCase());
        conversationService.saveContext(conversation, context);

        logger.info("✅ Gender saved: {}", gender.toUpperCase());

        // Ask for date of birth
        messageSender.sendTextMessage(conversation.getWhatsappPhone(),
            "Great! Now, please enter your *date of birth* 📅\n\n" +
            "*Format:* DD/MM/YYYY\n" +
            "*Example:* 15/05/1990\n\n" +
            "Please enter your date of birth:");

        conversationService.updateState(conversation, ConversationState.COLLECTING_PATIENT_DOB);
    }

    private void handleCollectingPatientDob(WhatsAppConversation conversation, ParsedMessage message) {
        logger.info("📅 COLLECTING PATIENT DOB");

        String dobString = extractUserInput(message);

        if (dobString == null || dobString.trim().isEmpty()) {
            messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                "Please enter your date of birth.");
            return;
        }

        // Validate DOB format
        if (!patientService.isValidDateOfBirth(dobString.trim())) {
            messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                "❌ Invalid date of birth.\n\n" +
                "Please use the format *DD/MM/YYYY*\n" +
                "Example: 15/05/1990\n\n" +
                "Make sure:\n" +
                "• Day is between 01-31\n" +
                "• Month is between 01-12\n" +
                "• Year is valid (not in future)\n" +
                "• Age is between 1-120 years");
            return;
        }

        // Save DOB in context
        ConversationContext context = conversationService.getContext(conversation);
        context.setTempPatientDob(dobString.trim());
        conversationService.saveContext(conversation, context);

        // Calculate age
        java.time.LocalDate dob = patientService.parseDateOfBirth(dobString.trim());
        int age = patientService.calculateAge(dob);

        logger.info("✅ DOB saved: {}, Age: {}", dobString.trim(), age);

        // Show summary and create patient
        String summary = String.format(
            "✅ *Registration Summary*\n\n" +
            "📝 *Name:* %s\n" +
            "👥 *Gender:* %s\n" +
            "📅 *Date of Birth:* %s\n" +
            "🎂 *Age:* %d years\n\n" +
            "Creating your profile...",
            context.getTempPatientName(),
            context.getTempPatientGender(),
            dobString.trim(),
            age
        );

        messageSender.sendTextMessage(conversation.getWhatsappPhone(), summary);

        try {
            // Create patient in database
            logger.info("💾 Creating patient in database...");
            var patient = patientService.createPatientFromContext(conversation, context);

            logger.info("✅ Patient created successfully! ID: {}", patient.getId());

            // Clear temp data from context
            context.clearTempPatientData();
            conversationService.saveContext(conversation, context);

            // Send success message
            messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                String.format(
                    "🎉 *Registration Successful!*\n\n" +
                    "Welcome to our hospital, *%s*!\n\n" +
                    "Your patient profile has been created.\n" +
                    "Patient ID: *%s*\n\n" +
                    "Let's book your appointment! 📅",
                    patient.getFirstName(),
                    patient.getPatientId() != null ? patient.getPatientId() : "Generated"
                ));

            // Continue to doctor selection
            conversationService.updateState(conversation, ConversationState.SELECTING_DOCTOR);
            showDoctorSelection(conversation);

        } catch (Exception e) {
            logger.error("❌ Error creating patient", e);
            messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                "❌ Sorry, there was an error creating your profile.\n\n" +
                "Error: " + e.getMessage() + "\n\n" +
                "Please try again later or contact support.");

            conversationService.updateState(conversation, ConversationState.MAIN_MENU);
            sendMainMenu(conversation);
        }
    }

    private void handleSelectingDoctor(WhatsAppConversation conversation, ParsedMessage message) {
        logger.info("👨‍⚕️ HANDLING DOCTOR SELECTION");

        String selectedId = message.getListId();  // For list replies
        if (selectedId == null) {
            selectedId = extractUserInput(message);  // For text input
        }

        if (selectedId == null) {
            messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                "Please select a doctor from the list.");
            return;
        }

        try {
            Long doctorId;

            if (selectedId.startsWith("doctor_")) {
                doctorId = Long.parseLong(selectedId.replace("doctor_", ""));
            } else {
                // Try to parse as number (1, 2, 3...)
                int index = Integer.parseInt(selectedId) - 1;
                var doctors = doctorService.getAllActiveDoctors();

                if (index >= 0 && index < doctors.size()) {
                    doctorId = doctors.get(index).getId();
                } else {
                    throw new NumberFormatException("Invalid index");
                }
            }

            // Get doctor
            var doctor = doctorService.getDoctorById(doctorId);

            // Save doctor in context
            ConversationContext context = conversationService.getContext(conversation);
            context.setSelectedDoctorId(doctorId);
            context.setSelectedDoctorName(doctorService.formatDoctorName(doctor));
            conversationService.saveContext(conversation, context);

            logger.info("✅ Doctor selected: {}", doctor.getFirstName());

            messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                String.format("You selected: *%s*\n%s\n\nGreat choice! 👍",
                    doctorService.formatDoctorName(doctor),
                    doctorService.formatDoctorDescription(doctor)));

            // Show date selection
            conversationService.updateState(conversation, ConversationState.SELECTING_DATE);
            showDateSelection(conversation);

        } catch (Exception e) {
            logger.error("Error selecting doctor", e);
            messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                "Invalid selection. Please select a doctor from the list.");
        }
    }

    private void handleSelectingDate(WhatsAppConversation conversation, ParsedMessage message) {
        logger.info("📅 HANDLING DATE SELECTION");

        String selected = pickSelection(message);
        if (selected == null) {
            messageSender.sendTextMessage(conversation.getWhatsappPhone(), "Please select a date from the list.");
            return;
        }
        if (isBackCommand(selected)) {
            goToMainMenu(conversation);
            return;
        }

        try {
            java.time.LocalDate date = parseSelectedDate(selected);
            ConversationContext context = conversationService.getContext(conversation);
            User doctor = doctorService.getDoctorById(context.getSelectedDoctorId());

            var day = appointmentService.findBookableDay(doctor, conversation.getUser(), date);
            if (day.isEmpty()) {
                messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                    "Sorry, that day is no longer available. Please pick another one.");
                showDateSelection(conversation);
                return;
            }

            context.setSelectedDate(date.toString());
            context.setSelectedTimeSlot(day.get().timeText());
            conversationService.saveContext(conversation, context);

            conversationService.updateState(conversation, ConversationState.CONFIRMING_APPOINTMENT);
            showAppointmentConfirmation(conversation);

        } catch (java.time.format.DateTimeParseException | IllegalArgumentException e) {
            messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                "I didn't understand that date. Please choose one from the list.");
            showDateSelection(conversation);
        } catch (Exception e) {
            logger.error("Error selecting date", e);
            messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                "Sorry, something went wrong. Please try again.");
            showDateSelection(conversation);
        }
    }

    /**
     * Time slots are no longer picked by the patient (they come from the doctor's schedule).
     * Anyone still sitting in this legacy state is moved back to date selection.
     */
    private void handleSelectingTimeSlot(WhatsAppConversation conversation, ParsedMessage message) {
        conversationService.updateState(conversation, ConversationState.SELECTING_DATE);
        showDateSelection(conversation);
    }

    private String pickSelection(ParsedMessage message) {
        if (message.getListId() != null) return message.getListId();
        if (message.getButtonId() != null) return message.getButtonId();
        String text = extractUserInput(message);
        return text != null ? text.trim() : null;
    }

    private boolean isBackCommand(String input) {
        String v = input.trim().toLowerCase();
        return v.equals("back") || v.equals("menu") || v.equals("0");
    }

    private void goToMainMenu(WhatsAppConversation conversation) {
        conversationService.updateState(conversation, ConversationState.MAIN_MENU);
        sendMainMenu(conversation);
    }

    private java.time.LocalDate parseSelectedDate(String selected) {
        String v = selected.trim();
        if (v.startsWith("date_")) {
            return java.time.LocalDate.parse(v.substring(5));
        }
        if ("today".equalsIgnoreCase(v)) {
            return appointmentService.today();
        }
        if ("tomorrow".equalsIgnoreCase(v)) {
            return appointmentService.today().plusDays(1);
        }
        return appointmentService.parseDate(v);
    }

    // ---------- Queue status ----------

    private void showQueueStatus(WhatsAppConversation conversation) {
        User patient = conversation.getUser();
        if (patient == null) {
            messageSender.sendTextMessage(conversation.getWhatsappPhone(), "Please book an appointment first - I'll then be able to track your queue.");
            sendMainMenu(conversation);
            return;
        }
        var fmt = java.time.format.DateTimeFormatter.ofPattern("hh:mm a");
        var status = queueStatusService.todayFor(patient);
        if (status.isEmpty()) {
            messageSender.sendTextMessage(conversation.getWhatsappPhone(), "You don't have an appointment today.");
        } else {
            var s = status.get();
            var a = s.appointment();
            String doctor = "Dr. " + a.getDoctor().getFirstName() + " " + a.getDoctor().getLastName();
            String text = switch (a.getStatus()) {
                case IN_CONSULTATION -> "\uD83E\uDE7A You're with " + doctor + " right now.";
                case COMPLETED -> "\u2705 Your consultation with " + doctor + " is complete. Take care!";
                case NOT_ATTENDED -> "We marked your appointment today as missed. Send \"Hi\" to book a new one.";
                case CANCELLED -> "Your appointment today was cancelled.";
                default -> s.ahead() == 0
                    ? "\uD83D\uDD14 *You're next!* No one is ahead of you with " + doctor + ". Please be ready."
                    : "\uD83D\uDCCD *Your queue status*\n\n" +
                      "\u2022 Doctor: " + doctor + "\n" +
                      "\u2022 Queue number: #" + a.getQueueNumber() + "\n" +
                      "\u2022 Booked slot: " + a.getScheduledTime().format(fmt) + "\n" +
                      "\u2022 Patients ahead of you: *" + s.ahead() + "*\n" +
                      "\u2022 Estimated time: *" + s.estimated().format(fmt) + "*" +
                      (s.minutesLate() >= 20 ? "\n\nThe doctor is running about " + s.minutesLate() + " minutes behind. Thank you for your patience." : "");
            };
            messageSender.sendTextMessage(conversation.getWhatsappPhone(), text);
        }
        goToMainMenu(conversation);
    }

    // ---------- Cancel flow ----------

    private void handleSelectingAppointmentToCancel(WhatsAppConversation conversation, ParsedMessage message) {
        String input = pickSelection(message);
        if (input == null || isBackCommand(input)) {
            goToMainMenu(conversation);
            return;
        }
        try {
            ConversationContext context = conversationService.getContext(conversation);
            Long appointmentId = resolveAppointmentChoice(input, context.getCancellableAppointmentIds());
            if (appointmentId == null) {
                messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                    "Please reply with a number from the list, or 'back' to return to the menu.");
                return;
            }
            var appointment = appointmentManagementService.getOwnedAppointment(appointmentId, conversation.getUser());
            context.setSelectedAppointmentId(appointmentId);
            conversationService.saveContext(conversation, context);

            conversationService.updateState(conversation, ConversationState.CONFIRMING_CANCELLATION);
            messageSender.sendButtonMessage(conversation.getWhatsappPhone(),
                "Cancel this appointment?\n\n" + appointmentManagementService.formatAppointmentDetails(appointment),
                java.util.List.of(
                    new WhatsAppMessageSender.Button("confirm_cancel", "Yes, cancel"),
                    new WhatsAppMessageSender.Button("keep", "No, keep it")));
        } catch (Exception e) {
            logger.error("Error selecting appointment to cancel", e);
            messageSender.sendTextMessage(conversation.getWhatsappPhone(), "Sorry, something went wrong.");
            goToMainMenu(conversation);
        }
    }

    private void handleConfirmingCancellation(WhatsAppConversation conversation, ParsedMessage message) {
        String input = pickSelection(message);
        if (input != null && ("confirm_cancel".equalsIgnoreCase(input) || "yes".equalsIgnoreCase(input))) {
            try {
                ConversationContext context = conversationService.getContext(conversation);
                appointmentManagementService.cancelAppointment(context.getSelectedAppointmentId(), conversation.getUser());
                context.setSelectedAppointmentId(null);
                conversationService.saveContext(conversation, context);
                messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                    "✅ Your appointment has been cancelled.");
            } catch (com.appointment.webapp.service.BookingException e) {
                messageSender.sendTextMessage(conversation.getWhatsappPhone(), "❌ " + e.getMessage());
            } catch (Exception e) {
                logger.error("Error cancelling appointment", e);
                messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                    "❌ Sorry, we couldn't cancel your appointment. Please try again or call the clinic.");
            }
        } else {
            messageSender.sendTextMessage(conversation.getWhatsappPhone(), "👍 Okay, your appointment is unchanged.");
        }
        goToMainMenu(conversation);
    }

    // ---------- Reschedule flow ----------

    private void handleSelectingAppointmentToReschedule(WhatsAppConversation conversation, ParsedMessage message) {
        String input = pickSelection(message);
        if (input == null || isBackCommand(input)) {
            goToMainMenu(conversation);
            return;
        }
        try {
            ConversationContext context = conversationService.getContext(conversation);
            Long appointmentId = resolveAppointmentChoice(input, context.getReschedulableAppointmentIds());
            if (appointmentId == null) {
                messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                    "Please reply with a number from the list, or 'back' to return to the menu.");
                return;
            }
            var appointment = appointmentManagementService.getOwnedAppointment(appointmentId, conversation.getUser());
            context.setSelectedAppointmentId(appointmentId);
            context.setSelectedDoctorId(appointment.getDoctor().getId());
            context.setSelectedDoctorName(doctorService.formatDoctorName(appointment.getDoctor()));
            conversationService.saveContext(conversation, context);

            conversationService.updateState(conversation, ConversationState.RESCHEDULE_SELECTING_DATE);
            showDateSelection(conversation);
        } catch (Exception e) {
            logger.error("Error selecting appointment to reschedule", e);
            messageSender.sendTextMessage(conversation.getWhatsappPhone(), "Sorry, something went wrong.");
            goToMainMenu(conversation);
        }
    }

    private void handleRescheduleSelectingDate(WhatsAppConversation conversation, ParsedMessage message) {
        String selected = pickSelection(message);
        if (selected == null) {
            messageSender.sendTextMessage(conversation.getWhatsappPhone(), "Please select a new date from the list.");
            return;
        }
        if (isBackCommand(selected)) {
            goToMainMenu(conversation);
            return;
        }
        try {
            java.time.LocalDate date = parseSelectedDate(selected);
            ConversationContext context = conversationService.getContext(conversation);
            User doctor = doctorService.getDoctorById(context.getSelectedDoctorId());

            var day = appointmentService.findBookableDay(doctor, conversation.getUser(), date);
            if (day.isEmpty()) {
                messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                    "Sorry, that day is not available. Please pick another one.");
                showDateSelection(conversation);
                return;
            }
            context.setSelectedDate(date.toString());
            context.setSelectedTimeSlot(day.get().timeText());
            conversationService.saveContext(conversation, context);

            var old = appointmentManagementService.getOwnedAppointment(context.getSelectedAppointmentId(), conversation.getUser());
            conversationService.updateState(conversation, ConversationState.CONFIRMING_RESCHEDULE);
            messageSender.sendButtonMessage(conversation.getWhatsappPhone(),
                String.format("🔄 *Reschedule Summary*\n\n" +
                    "👨‍⚕️ *Doctor:* %s\n" +
                    "❌ *From:* %s at %s\n" +
                    "✅ *To:* %s (%s) at *%s* (Queue #%d)\n\n" +
                    "Confirm the change?",
                    context.getSelectedDoctorName(),
                    appointmentService.formatDate(old.getAppointmentDate()), old.getScheduledTime().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")),
                    appointmentService.getDayName(date), appointmentService.formatDate(date),
                    day.get().timeText(), day.get().queueNumber()),
                java.util.List.of(
                    new WhatsAppMessageSender.Button("confirm", "✅ Confirm"),
                    new WhatsAppMessageSender.Button("cancel", "❌ Keep old")));
        } catch (java.time.format.DateTimeParseException | IllegalArgumentException e) {
            messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                "I didn't understand that date. Please choose one from the list.");
            showDateSelection(conversation);
        } catch (Exception e) {
            logger.error("Error selecting reschedule date", e);
            messageSender.sendTextMessage(conversation.getWhatsappPhone(), "Sorry, something went wrong.");
            goToMainMenu(conversation);
        }
    }

    private void handleConfirmingReschedule(WhatsAppConversation conversation, ParsedMessage message) {
        String input = pickSelection(message);
        ConversationContext context = conversationService.getContext(conversation);

        if (input != null && ("confirm".equalsIgnoreCase(input) || "yes".equalsIgnoreCase(input))) {
            try {
                var fresh = appointmentManagementService.rescheduleAppointment(
                    context.getSelectedAppointmentId(), conversation.getUser(),
                    java.time.LocalDate.parse(context.getSelectedDate()));
                messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                    String.format("✅ *Appointment Rescheduled*\n\n" +
                        "• *Doctor:* %s\n• *Date:* %s (%s)\n• *Time:* %s\n• *Queue:* #%d\n\n" +
                        "Please arrive 10 minutes early.",
                        context.getSelectedDoctorName(),
                        appointmentService.getDayName(fresh.getAppointmentDate()),
                        appointmentService.formatDate(fresh.getAppointmentDate()),
                        fresh.getScheduledTime().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")),
                        fresh.getQueueNumber()));
            } catch (com.appointment.webapp.service.BookingException e) {
                messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                    "❌ " + e.getMessage() + "\nYour original appointment is unchanged.");
            } catch (Exception e) {
                logger.error("Error rescheduling appointment", e);
                messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                    "❌ Sorry, we couldn't reschedule. Your original appointment is unchanged.");
            }
        } else {
            messageSender.sendTextMessage(conversation.getWhatsappPhone(), "👍 Okay, your original appointment is unchanged.");
        }
        context.setSelectedAppointmentId(null);
        context.clearBookingData();
        conversationService.saveContext(conversation, context);
        goToMainMenu(conversation);
    }

    /** Accepts "2" (position in the list we showed) and returns that appointment's id. */
    private Long resolveAppointmentChoice(String input, List<Long> ids) {
        if (ids == null) return null;
        try {
            int index = Integer.parseInt(input.trim()) - 1;
            return (index >= 0 && index < ids.size()) ? ids.get(index) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void handleConfirmingAppointment(WhatsAppConversation conversation, ParsedMessage message) {
        logger.info("✅ HANDLING APPOINTMENT CONFIRMATION");

        String confirmation = message.getButtonId();
        if (confirmation == null) {
            confirmation = extractUserInput(message);
        }

        if (confirmation == null) {
            messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                "Please confirm or cancel the appointment.");
            return;
        }

        if ("confirm".equalsIgnoreCase(confirmation) || "yes".equalsIgnoreCase(confirmation)) {
            // Book the appointment
            bookAppointment(conversation);
        } else if ("cancel".equalsIgnoreCase(confirmation) || "no".equalsIgnoreCase(confirmation)) {
            // Cancel and go back to main menu
            messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                "Appointment booking cancelled.");

            conversationService.updateState(conversation, ConversationState.MAIN_MENU);
            sendMainMenu(conversation);
        } else {
            messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                "Please click 'Confirm' or 'Cancel'.");
        }
    }

    /**
     * Show doctor selection list
     */
    private void showDoctorSelection(WhatsAppConversation conversation) {
        logger.info("📋 SHOWING DOCTOR SELECTION");

        try {
            var doctors = doctorService.getAllActiveDoctors();

            if (doctors.isEmpty()) {
                messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                    "Sorry, no doctors are available at the moment.\n\n" +
                    "Please try again later.");

                conversationService.updateState(conversation, ConversationState.MAIN_MENU);
                sendMainMenu(conversation);
                return;
            }

            // Send text list first
            StringBuilder doctorsList = new StringBuilder("*Available Doctors:*\n\n");
            for (int i = 0; i < doctors.size(); i++) {
                var doctor = doctors.get(i);
                doctorsList.append(String.format("%d. *%s*\n   %s\n\n",
                    i + 1,
                    doctorService.formatDoctorName(doctor),
                    doctorService.formatDoctorDescription(doctor)));
            }

            messageSender.sendTextMessage(conversation.getWhatsappPhone(), doctorsList.toString());

            // Send interactive list
            var section = new WhatsAppMessageSender.ListSection("Select a Doctor");

            for (var doctor : doctors) {
                section.addRow(new WhatsAppMessageSender.ListRow(
                    "doctor_" + doctor.getId(),
                    doctorService.formatDoctorName(doctor),
                    doctorService.formatDoctorDescription(doctor)
                ));
            }

            messageSender.sendListMessage(
                conversation.getWhatsappPhone(),
                "Select Doctor",
                "Please select a doctor to continue:",
                "Select Doctor",
                java.util.List.of(section)
            );

        } catch (Exception e) {
            logger.error("Error showing doctor selection", e);
            messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                "Sorry, there was an error loading doctors. Please try again.");

            conversationService.updateState(conversation, ConversationState.MAIN_MENU);
            sendMainMenu(conversation);
        }
    }

    /**
     * Show the days on which this doctor really has a free slot (from the doctor's schedule).
     * Used for both new bookings and rescheduling (the old appointment's date is left out).
     */
    private void showDateSelection(WhatsAppConversation conversation) {
        logger.info("📅 SHOWING DATE SELECTION");

        try {
            ConversationContext context = conversationService.getContext(conversation);
            User doctor = doctorService.getDoctorById(context.getSelectedDoctorId());
            boolean rescheduling = conversation.getCurrentState() == ConversationState.RESCHEDULE_SELECTING_DATE;

            java.time.LocalDate excludeDate = null;
            if (rescheduling && context.getSelectedAppointmentId() != null) {
                excludeDate = appointmentManagementService
                    .getOwnedAppointment(context.getSelectedAppointmentId(), conversation.getUser()).getAppointmentDate();
            }
            final java.time.LocalDate skipDate = excludeDate;

            var days = appointmentService.getBookableDays(doctor, rescheduling ? null : conversation.getUser()).stream()
                .filter(d -> !d.date().equals(skipDate))
                .limit(10)
                .toList();

            if (days.isEmpty()) {
                messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                    "Sorry, *" + context.getSelectedDoctorName() + "* has no free slots in the next 7 days" +
                    (rescheduling ? "." : ", or you already have a booking with the doctor on the available days.") +
                    "\n\nPlease try another doctor or check back later.");
                if (rescheduling) {
                    goToMainMenu(conversation);
                } else {
                    conversationService.updateState(conversation, ConversationState.SELECTING_DOCTOR);
                    showDoctorSelection(conversation);
                }
                return;
            }

            var section = new WhatsAppMessageSender.ListSection("Available Days");
            for (var day : days) {
                String title = appointmentService.getDayName(day.date()) + ", " +
                    day.date().format(java.time.format.DateTimeFormatter.ofPattern("dd MMM"));
                String description = "Your slot: " + day.timeText() + " (Queue #" + day.queueNumber() + ") • " +
                    day.slotsLeft() + " left";
                section.addRow(new WhatsAppMessageSender.ListRow("date_" + day.date(), truncate(title, 24), truncate(description, 72)));
            }

            var first = days.get(0);
            messageSender.sendListMessage(
                conversation.getWhatsappPhone(),
                rescheduling ? "Choose New Date" : "Choose a Date",
                "*" + context.getSelectedDoctorName() + "* sees patients " + first.hoursText() +
                    ".\nAppointments are given in queue order - pick a day and we'll book the next free slot.",
                "Select Date",
                java.util.List.of(section)
            );

        } catch (Exception e) {
            logger.error("Error showing date selection", e);
            messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                "Sorry, there was an error loading dates. Please try again.");
        }
    }

    private String truncate(String text, int max) {
        return text.length() <= max ? text : text.substring(0, max - 1) + "…";
    }

    /**
     * Show appointment confirmation
     */
    private void showAppointmentConfirmation(WhatsAppConversation conversation) {
        logger.info("✅ SHOWING APPOINTMENT CONFIRMATION");

        try {
            ConversationContext context = conversationService.getContext(conversation);
            java.time.LocalDate date = java.time.LocalDate.parse(context.getSelectedDate());
            User doctor = doctorService.getDoctorById(context.getSelectedDoctorId());
            var day = appointmentService.findBookableDay(doctor, conversation.getUser(), date);

            String queueLine = day.map(d -> "🔢 *Queue:* #" + d.queueNumber() + "\n").orElse("");
            String confirmationMessage = String.format(
                "📋 *Appointment Summary*\n\n" +
                "👨‍⚕️ *Doctor:* %s\n" +
                "📅 *Date:* %s (%s)\n" +
                "⏰ *Time:* %s\n" +
                "%s\n" +
                "Please confirm your appointment:",
                context.getSelectedDoctorName(),
                appointmentService.getDayName(date),
                appointmentService.formatDate(date),
                context.getSelectedTimeSlot(),
                queueLine
            );

            var buttons = java.util.List.of(
                new WhatsAppMessageSender.Button("confirm", "✅ Confirm"),
                new WhatsAppMessageSender.Button("cancel", "❌ Cancel")
            );

            messageSender.sendButtonMessage(conversation.getWhatsappPhone(), confirmationMessage, buttons);

        } catch (Exception e) {
            logger.error("Error showing confirmation", e);
            messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                "Sorry, there was an error. Please try again.");
        }
    }

    /**
     * Book the appointment. The time shown to the patient is always the one the system actually assigned.
     */
    private void bookAppointment(WhatsAppConversation conversation) {
        logger.info("💾 BOOKING APPOINTMENT");

        ConversationContext context = conversationService.getContext(conversation);
        try {
            var patient = conversation.getUser();
            if (patient == null) {
                throw new IllegalStateException("Patient not found in conversation");
            }

            var doctor = doctorService.getDoctorById(context.getSelectedDoctorId());
            java.time.LocalDate date = java.time.LocalDate.parse(context.getSelectedDate());
            String confirmedTime = context.getSelectedTimeSlot();

            var appointment = appointmentService.bookAppointment(patient, doctor, date, "whatsapp-bot");

            String actualTime = appointment.getScheduledTime().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
            String note = actualTime.equals(confirmedTime) ? "" :
                "⚠️ The slot you saw was just taken, so you've been given the next free one.\n\n";

            messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                String.format(
                    "🎉 *Appointment Booked!*\n\n" +
                    "%s" +
                    "📋 *Details:*\n" +
                    "• *ID:* #%d\n" +
                    "• *Doctor:* %s\n" +
                    "• *Date:* %s (%s)\n" +
                    "• *Time:* %s\n" +
                    "• *Queue:* #%d\n\n" +
                    "Please arrive 10 minutes early. We'll remind you before your visit and let you know when your turn is near. 👋",
                    note,
                    appointment.getId(),
                    context.getSelectedDoctorName(),
                    appointmentService.getDayName(date),
                    appointmentService.formatDate(date),
                    actualTime,
                    appointment.getQueueNumber()
                ));

            context.clearBookingData();
            conversationService.saveContext(conversation, context);
            conversationService.updateState(conversation, ConversationState.APPOINTMENT_BOOKED);
            goToMainMenu(conversation);

        } catch (com.appointment.webapp.service.BookingException e) {
            logger.warn("Booking rejected: {}", e.getMessage());
            messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                "❌ " + e.getMessage() + "\n\nPlease choose another date.");
            conversationService.updateState(conversation, ConversationState.SELECTING_DATE);
            showDateSelection(conversation);
        } catch (Exception e) {
            logger.error("❌ Error booking appointment", e);
            messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                "❌ Sorry, we couldn't book your appointment right now. Please try again in a moment.");
            goToMainMenu(conversation);
        }
    }

    private void handleUnknownState(WhatsAppConversation conversation, ParsedMessage message) {
        logger.warn("Unknown state: {}", conversation.getCurrentState());
        messageSender.sendTextMessage(conversation.getWhatsappPhone(),
            "Something went wrong. Let's start over.");
        conversationService.updateState(conversation, ConversationState.MAIN_MENU);
        sendMainMenu(conversation);
    }

    /**
     * Extract user input from message
     */
    private String extractUserInput(ParsedMessage message) {
        if (message.getMessageType() == ParsedMessage.MessageType.TEXT) {
            return message.getTextContent();
        } else if (message.getMessageType() == ParsedMessage.MessageType.BUTTON_REPLY) {
            return message.getButtonText();
        } else if (message.getMessageType() == ParsedMessage.MessageType.LIST_REPLY) {
            return message.getListTitle();
        }
        return null;
    }

    /**
     * Send error message
     */
    private void sendErrorMessage(String phone) {
        messageSender.sendTextMessage(phone,
            "❌ Sorry, something went wrong. Please try again later.");
    }

    // ========================================
    // PHASES 6-10: View/Cancel/Reschedule
    // ========================================

    /**
     * Show user's appointments
     */
    private void showUserAppointments(WhatsAppConversation conversation) {
        logger.info("📋 SHOWING USER APPOINTMENTS");

        try {
            User patient = conversation.getUser();
            if (patient == null) {
                messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                    "Please complete your registration first.");
                conversationService.updateState(conversation, ConversationState.MAIN_MENU);
                sendMainMenu(conversation);
                return;
            }

            List<com.appointment.webapp.model.Appointment> appointments =
                appointmentManagementService.getUpcomingAppointments(patient);

            if (appointments.isEmpty()) {
                messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                    "📋 *Your Appointments*\n\n" +
                    "You don't have any upcoming appointments.\n\n" +
                    "Would you like to book one?");

                conversationService.updateState(conversation, ConversationState.MAIN_MENU);
                sendMainMenu(conversation);
                return;
            }

            // Send appointments list
            StringBuilder message = new StringBuilder("📋 *Your Upcoming Appointments*\n\n");

            for (int i = 0; i < Math.min(appointments.size(), 5); i++) {
                var apt = appointments.get(i);
                message.append(String.format("%d. %s\n\n",
                    i + 1,
                    appointmentManagementService.formatAppointmentDetails(apt)));
            }

            if (appointments.size() > 5) {
                message.append(String.format("\n... and %d more\n", appointments.size() - 5));
            }

            messageSender.sendTextMessage(conversation.getWhatsappPhone(), message.toString());

            conversationService.updateState(conversation, ConversationState.MAIN_MENU);
            sendMainMenu(conversation);

        } catch (Exception e) {
            logger.error("Error showing appointments", e);
            messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                "Sorry, there was an error loading your appointments.");
            conversationService.updateState(conversation, ConversationState.MAIN_MENU);
            sendMainMenu(conversation);
        }
    }

    /**
     * Show appointments that can be cancelled
     */
    private void showAppointmentsToCancel(WhatsAppConversation conversation) {
        logger.info("❌ SHOWING APPOINTMENTS TO CANCEL");

        try {
            User patient = conversation.getUser();
            if (patient == null) {
                messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                    "Please complete your registration first.");
                conversationService.updateState(conversation, ConversationState.MAIN_MENU);
                sendMainMenu(conversation);
                return;
            }

            List<com.appointment.webapp.model.Appointment> appointments =
                appointmentManagementService.getUpcomingAppointments(patient);

            // Filter cancellable appointments
            appointments = appointments.stream()
                .filter(appointmentManagementService::canCancelAppointment)
                .collect(java.util.stream.Collectors.toList());

            if (appointments.isEmpty()) {
                messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                    "You don't have any appointments that can be cancelled.\n\n" +
                    "(Appointments cannot be cancelled less than 1 hour before the scheduled time)");

                conversationService.updateState(conversation, ConversationState.MAIN_MENU);
                sendMainMenu(conversation);
                return;
            }

            // Save appointment IDs in context
            ConversationContext context = conversationService.getContext(conversation);
            context.setCancellableAppointmentIds(
                appointments.stream().map(com.appointment.webapp.model.Appointment::getId).collect(java.util.stream.Collectors.toList())
            );
            conversationService.saveContext(conversation, context);

            // Send list
            StringBuilder message = new StringBuilder("Select appointment to cancel:\n\n");
            for (int i = 0; i < appointments.size(); i++) {
                message.append(appointmentManagementService.formatAppointmentShort(appointments.get(i), i + 1));
                message.append("\n\n");
            }
            message.append("Reply with the number to cancel (1, 2, 3...)\n");
            message.append("Or type 'back' to return to main menu.");

            messageSender.sendTextMessage(conversation.getWhatsappPhone(), message.toString());

        } catch (Exception e) {
            logger.error("Error showing cancellable appointments", e);
            messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                "Sorry, there was an error.");
            conversationService.updateState(conversation, ConversationState.MAIN_MENU);
            sendMainMenu(conversation);
        }
    }

    /**
     * Show appointments that can be rescheduled
     */
    private void showAppointmentsToReschedule(WhatsAppConversation conversation) {
        logger.info("🔄 SHOWING APPOINTMENTS TO RESCHEDULE");

        try {
            User patient = conversation.getUser();
            if (patient == null) {
                messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                    "Please complete your registration first.");
                conversationService.updateState(conversation, ConversationState.MAIN_MENU);
                sendMainMenu(conversation);
                return;
            }

            List<com.appointment.webapp.model.Appointment> appointments =
                appointmentManagementService.getUpcomingAppointments(patient);

            // Filter reschedulable appointments (same as cancellable)
            appointments = appointments.stream()
                .filter(appointmentManagementService::canCancelAppointment)
                .collect(java.util.stream.Collectors.toList());

            if (appointments.isEmpty()) {
                messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                    "You don't have any appointments that can be rescheduled.");

                conversationService.updateState(conversation, ConversationState.MAIN_MENU);
                sendMainMenu(conversation);
                return;
            }

            // Save appointment IDs in context
            ConversationContext context = conversationService.getContext(conversation);
            context.setReschedulableAppointmentIds(
                appointments.stream().map(com.appointment.webapp.model.Appointment::getId).collect(java.util.stream.Collectors.toList())
            );
            conversationService.saveContext(conversation, context);

            // Send list
            StringBuilder message = new StringBuilder("Select appointment to reschedule:\n\n");
            for (int i = 0; i < appointments.size(); i++) {
                message.append(appointmentManagementService.formatAppointmentShort(appointments.get(i), i + 1));
                message.append("\n\n");
            }
            message.append("Reply with the number (1, 2, 3...)\n");
            message.append("Or type 'back' to return.");

            messageSender.sendTextMessage(conversation.getWhatsappPhone(), message.toString());

        } catch (Exception e) {
            logger.error("Error showing reschedulable appointments", e);
            messageSender.sendTextMessage(conversation.getWhatsappPhone(),
                "Sorry, there was an error.");
            conversationService.updateState(conversation, ConversationState.MAIN_MENU);
            sendMainMenu(conversation);
        }
    }
}
