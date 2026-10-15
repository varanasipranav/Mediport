package com.appointment.webapp.whatsapp.service.flow;

import com.appointment.webapp.model.Appointment;
import com.appointment.webapp.model.User;
import com.appointment.webapp.service.BookingException;
import com.appointment.webapp.whatsapp.dto.ConversationContext;
import com.appointment.webapp.whatsapp.dto.webhook.ParsedMessage;
import com.appointment.webapp.whatsapp.i18n.Lang;
import com.appointment.webapp.whatsapp.i18n.LanguageService;
import com.appointment.webapp.whatsapp.i18n.MessageCatalog;
import com.appointment.webapp.whatsapp.model.WhatsAppConversation;
import com.appointment.webapp.whatsapp.model.enums.ConversationState;
import com.appointment.webapp.whatsapp.service.appointment.QueueStatusService;
import com.appointment.webapp.whatsapp.service.appointment.WhatsAppAppointmentManagementService;
import com.appointment.webapp.whatsapp.service.appointment.WhatsAppAppointmentService;
import com.appointment.webapp.whatsapp.service.conversation.WhatsAppConversationService;
import com.appointment.webapp.whatsapp.service.doctor.WhatsAppDoctorService;
import com.appointment.webapp.whatsapp.service.messaging.WhatsAppMessageSender;
import com.appointment.webapp.whatsapp.service.patient.WhatsAppPatientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * The WhatsApp assistant: routes each incoming message to the handler for the conversation's current step.
 * Every sentence it sends comes from {@link MessageCatalog} in the patient's chosen language.
 */
@Service
public class WhatsAppMessageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(WhatsAppMessageProcessor.class);

    private static final Set<String> MENU_IDS = Set.of(
        "book_appointment", "queue_status", "view_appointments", "reschedule", "cancel_appointment", "change_language");

    /** Greetings and "go back to the start" words in every supported language. */
    private static final Set<String> RESTART_WORDS = Set.of(
        "hi", "hii", "hiii", "hello", "hey", "hlo", "menu", "main menu", "home", "start", "restart", "start over",
        "namaste", "नमस्ते", "हैलो", "मेन्यू", "నమస్తే", "హలో", "మెనూ", "வணக்கம்", "ஹலோ", "மெனு");

    private static final Set<String> YES_WORDS = Set.of(
        "confirm", "yes", "y", "ok", "okay", "haan", "ha", "हाँ", "हां", "ठीक है", "అవును", "సరే", "ஆம்", "சரி");
    private static final Set<String> NO_WORDS = Set.of(
        "cancel", "no", "n", "nahi", "नहीं", "కాదు", "వద్దు", "இல்லை", "வேண்டாம்");

    @Autowired private WhatsAppConversationService conversationService;
    @Autowired private WhatsAppMessageSender messageSender;
    @Autowired private WhatsAppPatientService patientService;
    @Autowired private WhatsAppDoctorService doctorService;
    @Autowired private WhatsAppAppointmentService appointmentService;
    @Autowired private WhatsAppAppointmentManagementService appointmentManagementService;
    @Autowired private QueueStatusService queueStatusService;
    @Autowired private com.appointment.webapp.config.ClinicProperties clinic;
    @Autowired private MessageCatalog messages;
    @Autowired private LanguageService languages;

    // ====================================================================== helpers

    private Lang lang(WhatsAppConversation c) {
        return languages.of(c);
    }

    private String t(WhatsAppConversation c, String key, Object... args) {
        return messages.t(lang(c), key, args);
    }

    /** Send a plain text message from the catalog. */
    private void say(WhatsAppConversation c, String key, Object... args) {
        messageSender.sendTextMessage(c.getWhatsappPhone(), t(c, key, args));
    }

    /** The reason a booking rule failed, in the patient's language when we have a translation. */
    private String reason(WhatsAppConversation c, BookingException e) {
        return e.getKey() != null ? t(c, e.getKey(), e.getArgs()) : e.getMessage();
    }

    private void goToMainMenu(WhatsAppConversation c) {
        conversationService.updateState(c, ConversationState.MAIN_MENU);
        sendMainMenu(c);
    }

    private String timeText(String hhmm, Lang lang) {
        return messages.time(LocalTime.parse(hhmm));
    }

    private String genderLabel(WhatsAppConversation c, String gender) {
        if (gender == null) return "";
        return switch (gender.trim().toLowerCase(Locale.ROOT)) {
            case "male" -> t(c, "gender.male");
            case "female" -> t(c, "gender.female");
            default -> t(c, "gender.other");
        };
    }

    private String extractUserInput(ParsedMessage message) {
        return switch (message.getMessageType()) {
            case TEXT -> message.getTextContent();
            case BUTTON_REPLY -> message.getButtonText();
            case LIST_REPLY -> message.getListTitle();
            default -> null;
        };
    }

    /** List id, then button id, then typed text. */
    private String pickSelection(ParsedMessage message) {
        if (message.getListId() != null) return message.getListId();
        if (message.getButtonId() != null) return message.getButtonId();
        String text = extractUserInput(message);
        return text != null ? text.trim() : null;
    }

    private boolean isBackCommand(String input) {
        String v = input.trim().toLowerCase(Locale.ROOT);
        return v.equals("back") || v.equals("menu") || v.equals("0") || RESTART_WORDS.contains(v);
    }

    private boolean isYes(String input) {
        return input != null && YES_WORDS.contains(input.trim().toLowerCase(Locale.ROOT));
    }

    private boolean isNo(String input) {
        return input != null && NO_WORDS.contains(input.trim().toLowerCase(Locale.ROOT));
    }

    // ====================================================================== entry point

    public void processMessage(ParsedMessage message) {
        logger.info("Processing {} message {} from {}", message.getMessageType(), message.getMessageId(), message.getFrom());

        try {
            WhatsAppConversation conversation = conversationService.getOrCreateConversation(
                message.getFrom(), message.getSenderName());
            ConversationState state = conversation.getCurrentState();
            logger.info("Conversation {} is in state {}", conversation.getId(), state);

            // Picking a language works from anywhere
            if (message.getListId() != null && message.getListId().startsWith("lang_")) {
                handleSelectingLanguage(conversation, message);
                return;
            }

            if (state == ConversationState.SELECTING_LANGUAGE) {
                handleSelectingLanguage(conversation, message);
                return;
            }

            if (isMenuChoice(conversation, message)) {
                conversationService.updateState(conversation, ConversationState.MAIN_MENU);
                handleMainMenu(conversation, message);
                return;
            }

            if (isRestartCommand(conversation, message)) {
                goToMainMenu(conversation);
                return;
            }

            switch (state) {
                case INITIAL -> handleInitialState(conversation);
                case MAIN_MENU -> handleMainMenu(conversation, message);
                case CHECKING_PATIENT, ASKING_NEW_OR_EXISTING -> goToMainMenu(conversation);
                case SELECTING_EXISTING_PATIENT -> handleSelectingExistingPatient(conversation, message);
                case COLLECTING_PATIENT_NAME -> handleCollectingPatientName(conversation, message);
                case COLLECTING_PATIENT_GENDER -> handleCollectingPatientGender(conversation, message);
                case COLLECTING_PATIENT_DOB -> handleCollectingPatientDob(conversation, message);
                case SELECTING_DOCTOR -> handleSelectingDoctor(conversation, message);
                case SELECTING_DATE -> handleSelectingDate(conversation, message);
                case SELECTING_TIME_SLOT -> {   // legacy state: slots now come from the doctor's schedule
                    conversationService.updateState(conversation, ConversationState.SELECTING_DATE);
                    showDateSelection(conversation);
                }
                case CONFIRMING_APPOINTMENT -> handleConfirmingAppointment(conversation, message);
                case SELECTING_APPOINTMENT_TO_CANCEL -> handleSelectingAppointmentToCancel(conversation, message);
                case CONFIRMING_CANCELLATION -> handleConfirmingCancellation(conversation, message);
                case SELECTING_APPOINTMENT_TO_RESCHEDULE -> handleSelectingAppointmentToReschedule(conversation, message);
                case RESCHEDULE_SELECTING_DATE -> handleRescheduleSelectingDate(conversation, message);
                case CONFIRMING_RESCHEDULE -> handleConfirmingReschedule(conversation, message);
                default -> {
                    logger.warn("Unknown state {}", state);
                    say(conversation, "unknown.state");
                    goToMainMenu(conversation);
                }
            }
        } catch (Exception e) {
            logger.error("Error processing WhatsApp message from {}", message.getFrom(), e);
            sendGenericError(message.getFrom());
        }
    }

    private void sendGenericError(String phone) {
        // we may not know the language here; use the stored one if we can find it
        Lang lang = conversationService.getConversationByPhone(phone)
            .map(languages::of).orElse(Lang.EN);
        messageSender.sendTextMessage(phone, messages.t(lang, "error.generic"));
    }

    /** The patient tapped an item from the main-menu list while in the middle of another flow. */
    private boolean isMenuChoice(WhatsAppConversation c, ParsedMessage message) {
        ConversationState state = c.getCurrentState();
        if (state == ConversationState.INITIAL || state == ConversationState.MAIN_MENU) {
            return false;
        }
        return message.getListId() != null && MENU_IDS.contains(message.getListId());
    }

    /** A greeting or "menu" returns to the main menu from any step, except while the patient is typing details. */
    private boolean isRestartCommand(WhatsAppConversation c, ParsedMessage message) {
        ConversationState state = c.getCurrentState();
        if (state == ConversationState.INITIAL || state == ConversationState.MAIN_MENU
            || state == ConversationState.COLLECTING_PATIENT_NAME || state == ConversationState.COLLECTING_PATIENT_GENDER
            || state == ConversationState.COLLECTING_PATIENT_DOB) {
            return false;
        }
        if (message.getMessageType() != ParsedMessage.MessageType.TEXT || message.getTextContent() == null) {
            return false;
        }
        return RESTART_WORDS.contains(message.getTextContent().trim().toLowerCase(Locale.ROOT));
    }

    // ====================================================================== language

    private void showLanguagePicker(WhatsAppConversation c) {
        Lang ui = Lang.EN;  // the picker itself is deliberately shown in all four languages at once
        var section = new WhatsAppMessageSender.ListSection(messages.t(ui, "sec.lang"));
        for (Lang l : Lang.values()) {
            section.addRow(new WhatsAppMessageSender.ListRow("lang_" + l.code(), l.nativeName(), null));
        }
        conversationService.updateState(c, ConversationState.SELECTING_LANGUAGE);
        messageSender.sendListMessage(c.getWhatsappPhone(), messages.t(ui, "hdr.lang"), messages.t(ui, "lang.body"),
            messages.t(ui, "lbtn.lang"), List.of(section));
    }

    private void handleSelectingLanguage(WhatsAppConversation c, ParsedMessage message) {
        Lang chosen = Lang.fromInput(pickSelection(message));
        if (chosen == null) {
            showLanguagePicker(c);   // didn't recognise it - ask again
            return;
        }
        boolean firstTime = !languages.isChosen(c);
        languages.set(c, chosen);
        say(c, "lang.set");
        if (firstTime) {
            sendWelcome(c);
        }
        goToMainMenu(c);
    }

    // ====================================================================== start and menu

    private void handleInitialState(WhatsAppConversation c) {
        if (!languages.isChosen(c)) {
            Lang inherited = languages.inheritedFromProfiles(c.getWhatsappPhone());
            if (inherited != null) {
                languages.set(c, inherited);
            } else {
                showLanguagePicker(c);
                return;
            }
        }
        sendWelcome(c);
        goToMainMenu(c);
    }

    private void sendWelcome(WhatsAppConversation c) {
        say(c, "welcome", clinic.getName(), c.getSenderName() != null ? c.getSenderName() : "");
    }

    private void handleMainMenu(WhatsAppConversation c, ParsedMessage message) {
        String input = extractUserInput(message);
        if (input == null) {
            sendMainMenu(c);
            return;
        }

        String choice = pickSelection(message).toLowerCase(Locale.ROOT);

        switch (choice) {
            case "book_appointment", "1", "book" -> {
                conversationService.updateState(c, ConversationState.CHECKING_PATIENT);
                checkPatientExists(c);
            }
            case "view_appointments", "2", "view" -> {
                conversationService.updateState(c, ConversationState.VIEWING_APPOINTMENTS);
                showUserAppointments(c);
            }
            case "cancel_appointment", "3", "cancel" -> {
                conversationService.updateState(c, ConversationState.SELECTING_APPOINTMENT_TO_CANCEL);
                showAppointmentsToCancel(c);
            }
            case "reschedule", "4" -> {
                conversationService.updateState(c, ConversationState.SELECTING_APPOINTMENT_TO_RESCHEDULE);
                showAppointmentsToReschedule(c);
            }
            case "queue_status", "status", "queue", "5" -> showQueueStatus(c);
            case "change_language", "language", "lang", "भाषा", "భాష", "மொழி" -> showLanguagePicker(c);
            default -> {
                if (!RESTART_WORDS.contains(choice)) {
                    say(c, "menu.unknown");
                }
                sendMainMenu(c);
            }
        }
    }

    private void sendMainMenu(WhatsAppConversation c) {
        Lang lang = lang(c);
        var section = new WhatsAppMessageSender.ListSection(messages.t(lang, "sec.menu"));
        section.addRow(menuRow(lang, "book_appointment", "book"));
        section.addRow(menuRow(lang, "queue_status", "queue"));
        section.addRow(menuRow(lang, "view_appointments", "view"));
        section.addRow(menuRow(lang, "reschedule", "resched"));
        section.addRow(menuRow(lang, "cancel_appointment", "cancel"));
        section.addRow(menuRow(lang, "change_language", "language"));

        String result = messageSender.sendListMessage(c.getWhatsappPhone(), clinic.getName(),
            messages.t(lang, "menu.body"), messages.t(lang, "lbtn.menu"), List.of(section));
        if (result == null) {
            logger.error("Main menu could not be sent to {}", c.getWhatsappPhone());
        }
    }

    private WhatsAppMessageSender.ListRow menuRow(Lang lang, String id, String keyPart) {
        return new WhatsAppMessageSender.ListRow(id, messages.t(lang, "row." + keyPart + ".t"), messages.t(lang, "row." + keyPart + ".d"));
    }

    // ====================================================================== who is the appointment for

    private void checkPatientExists(WhatsAppConversation c) {
        try {
            var patients = patientService.findPatientsByPhone(c.getWhatsappPhone());

            if (patients.isEmpty()) {
                say(c, "reg.not_found");
                conversationService.updateState(c, ConversationState.COLLECTING_PATIENT_NAME);
                return;
            }

            // One or more profiles on this number: always ask who the appointment is for, so a parent with a
            // single profile can also book for (or register) a child or other family member
            ConversationContext context = conversationService.getContext(c);
            context.setMultiplePatientIds(patients.stream().map(User::getId).toList());
            conversationService.saveContext(c, context);

            say(c, patients.size() == 1 ? "who.intro.one" : "who.intro.many", buildPatientsListText(c, patients));
            sendPatientSelectionList(c, patients);
            conversationService.updateState(c, ConversationState.SELECTING_EXISTING_PATIENT);

        } catch (Exception e) {
            logger.error("Error checking patient existence", e);
            say(c, "err.retry");
            goToMainMenu(c);
        }
    }

    private String patientDetails(WhatsAppConversation c, User patient) {
        StringBuilder sb = new StringBuilder();
        if (patient.getAge() != null) {
            sb.append(" - ").append(t(c, "age.years", patient.getAge()));
        }
        if (patient.getGender() != null && !patient.getGender().isBlank()) {
            sb.append(" - ").append(genderLabel(c, patient.getGender()));
        }
        return sb.toString();
    }

    private String buildPatientsListText(WhatsAppConversation c, List<User> patients) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < patients.size(); i++) {
            User p = patients.get(i);
            sb.append(t(c, "who.line", i + 1, p.getFirstName() + " " + p.getLastName(), patientDetails(c, p))).append("\n");
        }
        return sb.toString();
    }

    private void sendPatientSelectionList(WhatsAppConversation c, List<User> patients) {
        var section = new WhatsAppMessageSender.ListSection(t(c, "sec.who"));
        for (User p : patients) {
            String details = patientDetails(c, p);
            section.addRow(new WhatsAppMessageSender.ListRow(
                "patient_" + p.getId(),
                p.getFirstName() + " " + p.getLastName(),
                details.isEmpty() ? t(c, "who.row.default") : details.substring(3)));
        }
        section.addRow(new WhatsAppMessageSender.ListRow("new_patient", t(c, "row.newpatient.t"), t(c, "row.newpatient.d")));

        messageSender.sendListMessage(c.getWhatsappPhone(), t(c, "hdr.who"), t(c, "who.body"), t(c, "lbtn.who"), List.of(section));
    }

    private void handleSelectingExistingPatient(WhatsAppConversation c, ParsedMessage message) {
        String selected = pickSelection(message);
        if (selected == null) {
            say(c, "who.pick");
            return;
        }

        try {
            if ("new_patient".equals(selected) || "new".equalsIgnoreCase(selected)) {
                say(c, "who.new");
                conversationService.updateState(c, ConversationState.COLLECTING_PATIENT_NAME);
                return;
            }

            Long patientId = null;
            if (selected.startsWith("patient_")) {
                patientId = Long.parseLong(selected.substring("patient_".length()));
            } else {
                try {
                    int index = Integer.parseInt(selected.trim()) - 1;
                    ConversationContext context = conversationService.getContext(c);
                    if (context.getMultiplePatientIds() != null && index >= 0 && index < context.getMultiplePatientIds().size()) {
                        patientId = context.getMultiplePatientIds().get(index);
                    }
                } catch (NumberFormatException ignored) {
                    // falls through to "invalid"
                }
            }

            if (patientId == null) {
                say(c, "who.invalid");
                return;
            }

            final Long chosenId = patientId;
            User patient = patientService.findPatientsByPhone(c.getWhatsappPhone()).stream()
                .filter(p -> p.getId().equals(chosenId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Patient not found"));

            conversationService.linkUser(c, patient);
            say(c, "who.selected", patient.getFirstName() + " " + patient.getLastName());

            conversationService.updateState(c, ConversationState.SELECTING_DOCTOR);
            showDoctorSelection(c);

        } catch (Exception e) {
            logger.error("Error selecting patient", e);
            say(c, "err.retry");
            goToMainMenu(c);
        }
    }

    // ====================================================================== registration

    private void handleCollectingPatientName(WhatsAppConversation c, ParsedMessage message) {
        String name = extractUserInput(message);

        if (name == null || name.trim().isEmpty()) {
            say(c, "reg.name_empty");
            return;
        }
        if (!patientService.isValidName(name)) {
            say(c, "reg.name_invalid");
            return;
        }

        ConversationContext context = conversationService.getContext(c);
        context.setTempPatientName(name.trim());
        conversationService.saveContext(c, context);

        say(c, "reg.thanks", name.trim());
        messageSender.sendButtonMessage(c.getWhatsappPhone(), t(c, "reg.gender_prompt"), List.of(
            new WhatsAppMessageSender.Button("male", t(c, "btn.male")),
            new WhatsAppMessageSender.Button("female", t(c, "btn.female")),
            new WhatsAppMessageSender.Button("other", t(c, "btn.other"))));

        conversationService.updateState(c, ConversationState.COLLECTING_PATIENT_GENDER);
    }

    private void handleCollectingPatientGender(WhatsAppConversation c, ParsedMessage message) {
        String gender = message.getButtonId() != null ? message.getButtonId() : extractUserInput(message);

        if (gender == null) {
            say(c, "reg.gender_pick");
            return;
        }

        gender = gender.trim().toLowerCase(Locale.ROOT);
        // the buttons send male/female/other; people may also type the word in their own language
        if (gender.equals(t(c, "gender.male").toLowerCase(Locale.ROOT))) gender = "male";
        else if (gender.equals(t(c, "gender.female").toLowerCase(Locale.ROOT))) gender = "female";
        else if (gender.equals(t(c, "gender.other").toLowerCase(Locale.ROOT))) gender = "other";

        if (!gender.equals("male") && !gender.equals("female") && !gender.equals("other")) {
            say(c, "reg.gender_invalid");
            return;
        }

        ConversationContext context = conversationService.getContext(c);
        context.setTempPatientGender(gender.toUpperCase(Locale.ROOT));
        conversationService.saveContext(c, context);

        say(c, "reg.dob_prompt");
        conversationService.updateState(c, ConversationState.COLLECTING_PATIENT_DOB);
    }

    private void handleCollectingPatientDob(WhatsAppConversation c, ParsedMessage message) {
        String dob = extractUserInput(message);

        if (dob == null || dob.trim().isEmpty()) {
            say(c, "reg.dob_empty");
            return;
        }
        dob = dob.trim();
        if (!patientService.isValidDateOfBirth(dob)) {
            say(c, "reg.dob_invalid");
            return;
        }

        ConversationContext context = conversationService.getContext(c);
        context.setTempPatientDob(dob);
        conversationService.saveContext(c, context);

        int age = patientService.calculateAge(patientService.parseDateOfBirth(dob));

        say(c, "reg.summary", context.getTempPatientName(), genderLabel(c, context.getTempPatientGender()), dob, age);

        try {
            User patient = patientService.createPatientFromContext(c, context);
            // the new profile speaks the same language as this chat
            patient.setPreferredLanguage(lang(c).code());

            context.clearTempPatientData();
            conversationService.saveContext(c, context);

            say(c, "reg.success", patient.getFirstName(), patient.getPatientId() != null ? patient.getPatientId() : "-");

            conversationService.updateState(c, ConversationState.SELECTING_DOCTOR);
            showDoctorSelection(c);

        } catch (Exception e) {
            logger.error("Error creating patient", e);
            say(c, "reg.failed");
            goToMainMenu(c);
        }
    }

    // ====================================================================== doctor

    private void showDoctorSelection(WhatsAppConversation c) {
        try {
            var doctors = doctorService.getAllActiveDoctors();

            if (doctors.isEmpty()) {
                say(c, "doc.none");
                goToMainMenu(c);
                return;
            }

            StringBuilder text = new StringBuilder(t(c, "doc.list_title"));
            for (int i = 0; i < doctors.size(); i++) {
                var d = doctors.get(i);
                text.append(t(c, "doc.list_item", i + 1, doctorService.formatDoctorName(d), doctorService.formatDoctorDescription(d)));
            }
            messageSender.sendTextMessage(c.getWhatsappPhone(), text.toString());

            var section = new WhatsAppMessageSender.ListSection(t(c, "sec.doctor"));
            for (var d : doctors) {
                section.addRow(new WhatsAppMessageSender.ListRow("doctor_" + d.getId(),
                    doctorService.formatDoctorName(d), doctorService.formatDoctorDescription(d)));
            }
            messageSender.sendListMessage(c.getWhatsappPhone(), t(c, "hdr.doctor"), t(c, "doc.body"), t(c, "lbtn.doctor"), List.of(section));

        } catch (Exception e) {
            logger.error("Error showing doctor selection", e);
            say(c, "doc.error");
            goToMainMenu(c);
        }
    }

    private void handleSelectingDoctor(WhatsAppConversation c, ParsedMessage message) {
        String selected = pickSelection(message);
        if (selected == null) {
            say(c, "doc.pick");
            return;
        }

        try {
            Long doctorId;
            if (selected.startsWith("doctor_")) {
                doctorId = Long.parseLong(selected.substring("doctor_".length()));
            } else {
                int index = Integer.parseInt(selected.trim()) - 1;
                var doctors = doctorService.getAllActiveDoctors();
                if (index < 0 || index >= doctors.size()) {
                    throw new NumberFormatException("Invalid index");
                }
                doctorId = doctors.get(index).getId();
            }

            var doctor = doctorService.getDoctorById(doctorId);

            ConversationContext context = conversationService.getContext(c);
            context.setSelectedDoctorId(doctorId);
            context.setSelectedDoctorName(doctorService.formatDoctorName(doctor));
            conversationService.saveContext(c, context);

            say(c, "doc.chosen", doctorService.formatDoctorName(doctor), doctorService.formatDoctorDescription(doctor));

            conversationService.updateState(c, ConversationState.SELECTING_DATE);
            showDateSelection(c);

        } catch (Exception e) {
            logger.info("Doctor selection not understood: {}", selected);
            say(c, "doc.invalid");
        }
    }

    // ====================================================================== date

    /**
     * Shows the days on which this doctor really has a free slot (from the doctor's schedule).
     * Used for new bookings and for rescheduling (the old appointment's date is left out).
     */
    private void showDateSelection(WhatsAppConversation c) {
        try {
            ConversationContext context = conversationService.getContext(c);
            User doctor = doctorService.getDoctorById(context.getSelectedDoctorId());
            boolean rescheduling = c.getCurrentState() == ConversationState.RESCHEDULE_SELECTING_DATE;

            LocalDate skipDate = null;
            if (rescheduling && context.getSelectedAppointmentId() != null) {
                skipDate = appointmentManagementService
                    .getOwnedAppointment(context.getSelectedAppointmentId(), c.getUser()).getAppointmentDate();
            }
            final LocalDate skip = skipDate;

            var days = appointmentService.getBookableDays(doctor, c.getUser()).stream()
                .filter(d -> !d.date().equals(skip))
                .limit(10)
                .toList();

            if (days.isEmpty()) {
                if (rescheduling) {
                    say(c, "date.none_resched", context.getSelectedDoctorName());
                    goToMainMenu(c);
                } else {
                    say(c, "date.none_new", context.getSelectedDoctorName());
                    conversationService.updateState(c, ConversationState.SELECTING_DOCTOR);
                    showDoctorSelection(c);
                }
                return;
            }

            Lang lang = lang(c);
            LocalDate today = appointmentService.today();
            var section = new WhatsAppMessageSender.ListSection(t(c, "sec.date"));
            for (var day : days) {
                String title = messages.dayName(day.date(), today, lang) + ", " + messages.shortDate(day.date(), lang);
                section.addRow(new WhatsAppMessageSender.ListRow("date_" + day.date(), title,
                    t(c, "date.row.d", messages.time(day.time()), day.queueNumber(), day.slotsLeft())));
            }

            var first = days.get(0);
            messageSender.sendListMessage(c.getWhatsappPhone(),
                t(c, rescheduling ? "hdr.date_new" : "hdr.date"),
                t(c, "date.body", context.getSelectedDoctorName(),
                    messages.time(first.startTime()) + " - " + messages.time(first.endTime())),
                t(c, "lbtn.date"), List.of(section));

        } catch (Exception e) {
            logger.error("Error showing date selection", e);
            say(c, "date.load_error");
        }
    }

    private LocalDate parseSelectedDate(String selected) {
        String v = selected.trim();
        if (v.startsWith("date_")) {
            return LocalDate.parse(v.substring(5));
        }
        String lower = v.toLowerCase(Locale.ROOT);
        if (lower.equals("today") || v.equals(messages.t(Lang.HI, "day.today")) || v.equals(messages.t(Lang.TE, "day.today")) || v.equals(messages.t(Lang.TA, "day.today"))) {
            return appointmentService.today();
        }
        if (lower.equals("tomorrow") || v.equals(messages.t(Lang.HI, "day.tomorrow")) || v.equals(messages.t(Lang.TE, "day.tomorrow")) || v.equals(messages.t(Lang.TA, "day.tomorrow"))) {
            return appointmentService.today().plusDays(1);
        }
        return appointmentService.parseDate(v);
    }

    private void handleSelectingDate(WhatsAppConversation c, ParsedMessage message) {
        String selected = pickSelection(message);
        if (selected == null) {
            say(c, "date.pick");
            return;
        }
        if (isBackCommand(selected)) {
            goToMainMenu(c);
            return;
        }

        try {
            LocalDate date = parseSelectedDate(selected);
            ConversationContext context = conversationService.getContext(c);
            User doctor = doctorService.getDoctorById(context.getSelectedDoctorId());

            var day = appointmentService.findBookableDay(doctor, c.getUser(), date);
            if (day.isEmpty()) {
                say(c, "date.gone");
                showDateSelection(c);
                return;
            }

            context.setSelectedDate(date.toString());
            context.setSelectedTimeSlot(day.get().timeText());
            conversationService.saveContext(c, context);

            conversationService.updateState(c, ConversationState.CONFIRMING_APPOINTMENT);
            showAppointmentConfirmation(c);

        } catch (DateTimeParseException | IllegalArgumentException e) {
            say(c, "date.invalid");
            showDateSelection(c);
        } catch (Exception e) {
            logger.error("Error selecting date", e);
            say(c, "date.error");
            showDateSelection(c);
        }
    }

    // ====================================================================== confirm and book

    private void showAppointmentConfirmation(WhatsAppConversation c) {
        try {
            ConversationContext context = conversationService.getContext(c);
            LocalDate date = LocalDate.parse(context.getSelectedDate());
            User doctor = doctorService.getDoctorById(context.getSelectedDoctorId());
            var day = appointmentService.findBookableDay(doctor, c.getUser(), date);

            String queueLine = day.map(d -> t(c, "confirm.queue_line", d.queueNumber())).orElse("");
            String text = t(c, "confirm.summary",
                context.getSelectedDoctorName(),
                messages.dayName(date, appointmentService.today(), lang(c)),
                messages.date(date),
                timeText(context.getSelectedTimeSlot(), lang(c)),
                queueLine);

            messageSender.sendButtonMessage(c.getWhatsappPhone(), text, List.of(
                new WhatsAppMessageSender.Button("confirm", t(c, "btn.confirm")),
                new WhatsAppMessageSender.Button("cancel", t(c, "btn.cancel"))));

        } catch (Exception e) {
            logger.error("Error showing confirmation", e);
            say(c, "confirm.error");
        }
    }

    private void handleConfirmingAppointment(WhatsAppConversation c, ParsedMessage message) {
        String answer = message.getButtonId() != null ? message.getButtonId() : extractUserInput(message);

        if (answer == null) {
            say(c, "confirm.prompt");
        } else if (isYes(answer)) {
            bookAppointment(c);
        } else if (isNo(answer)) {
            say(c, "confirm.cancelled");
            goToMainMenu(c);
        } else {
            say(c, "confirm.use_buttons");
        }
    }

    /** Books the slot. The time shown to the patient is always the one the system actually assigned. */
    private void bookAppointment(WhatsAppConversation c) {
        ConversationContext context = conversationService.getContext(c);
        try {
            var patient = c.getUser();
            if (patient == null) {
                throw new IllegalStateException("Patient not found in conversation");
            }

            var doctor = doctorService.getDoctorById(context.getSelectedDoctorId());
            LocalDate date = LocalDate.parse(context.getSelectedDate());
            String confirmedTime = context.getSelectedTimeSlot();

            Appointment appointment = appointmentService.bookAppointment(patient, doctor, date, "whatsapp-bot");

            String actual = appointment.getScheduledTime().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
            String note = actual.equals(confirmedTime) ? "" : t(c, "book.slot_taken");

            say(c, "book.success",
                note,
                appointment.getId(),
                context.getSelectedDoctorName(),
                messages.dayName(date, appointmentService.today(), lang(c)),
                messages.date(date),
                messages.time(appointment.getScheduledTime()),
                appointment.getQueueNumber());

            context.clearBookingData();
            conversationService.saveContext(c, context);
            conversationService.updateState(c, ConversationState.APPOINTMENT_BOOKED);
            goToMainMenu(c);

        } catch (BookingException e) {
            logger.warn("Booking rejected: {}", e.getMessage());
            say(c, "book.rejected", reason(c, e));
            conversationService.updateState(c, ConversationState.SELECTING_DATE);
            showDateSelection(c);
        } catch (Exception e) {
            logger.error("Error booking appointment", e);
            say(c, "book.failed");
            goToMainMenu(c);
        }
    }

    // ====================================================================== queue status

    private void showQueueStatus(WhatsAppConversation c) {
        User patient = c.getUser();
        if (patient == null) {
            say(c, "status.need_booking");
            sendMainMenu(c);
            return;
        }

        var status = queueStatusService.todayFor(patient);
        if (status.isEmpty()) {
            say(c, "status.none");
        } else {
            var s = status.get();
            var a = s.appointment();
            String doctor = "Dr. " + a.getDoctor().getFirstName() + " " + a.getDoctor().getLastName();
            String text = switch (a.getStatus()) {
                case IN_CONSULTATION -> t(c, "status.in_consult", doctor);
                case COMPLETED -> t(c, "status.completed", doctor);
                case NOT_ATTENDED -> t(c, "status.missed");
                case CANCELLED -> t(c, "status.cancelled");
                default -> s.ahead() == 0
                    ? t(c, "status.next", doctor)
                    : t(c, "status.detail", doctor, a.getQueueNumber(), messages.time(a.getScheduledTime()), s.ahead(),
                        messages.time(s.estimated()),
                        s.minutesLate() >= 20 && s.minutesLate() <= 240 ? t(c, "status.late", s.minutesLate()) : "");
            };
            messageSender.sendTextMessage(c.getWhatsappPhone(), text);
        }
        goToMainMenu(c);
    }

    // ====================================================================== view / cancel / reschedule

    private List<Appointment> openAppointmentsOrExplain(WhatsAppConversation c, boolean cancellableOnly, String noneKey) {
        User patient = c.getUser();
        if (patient == null) {
            say(c, "list.need_registration");
            goToMainMenu(c);
            return null;
        }
        List<Appointment> appointments = appointmentManagementService.getUpcomingAppointments(patient);
        if (cancellableOnly) {
            appointments = appointments.stream().filter(appointmentManagementService::canCancelAppointment).toList();
        }
        if (appointments.isEmpty()) {
            say(c, noneKey);
            goToMainMenu(c);
            return null;
        }
        return appointments;
    }

    private void showUserAppointments(WhatsAppConversation c) {
        try {
            var appointments = openAppointmentsOrExplain(c, false, "view.none");
            if (appointments == null) return;

            StringBuilder text = new StringBuilder(t(c, "view.title"));
            for (int i = 0; i < Math.min(appointments.size(), 5); i++) {
                text.append(i + 1).append(". ").append(appointmentManagementService.formatAppointmentDetails(appointments.get(i), lang(c))).append("\n\n");
            }
            if (appointments.size() > 5) {
                text.append(t(c, "view.more", appointments.size() - 5));
            }
            messageSender.sendTextMessage(c.getWhatsappPhone(), text.toString());
            goToMainMenu(c);

        } catch (Exception e) {
            logger.error("Error showing appointments", e);
            say(c, "view.error");
            goToMainMenu(c);
        }
    }

    private String appointmentChoices(WhatsAppConversation c, List<Appointment> appointments) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < appointments.size(); i++) {
            sb.append(appointmentManagementService.formatAppointmentShort(appointments.get(i), i + 1, lang(c))).append("\n\n");
        }
        return sb.toString();
    }

    private void showAppointmentsToCancel(WhatsAppConversation c) {
        try {
            var appointments = openAppointmentsOrExplain(c, true, "cancel.none");
            if (appointments == null) return;

            ConversationContext context = conversationService.getContext(c);
            context.setCancellableAppointmentIds(appointments.stream().map(Appointment::getId).toList());
            conversationService.saveContext(c, context);

            say(c, "cancel.select", appointmentChoices(c, appointments));
        } catch (Exception e) {
            logger.error("Error showing cancellable appointments", e);
            say(c, "list.error");
            goToMainMenu(c);
        }
    }

    private void showAppointmentsToReschedule(WhatsAppConversation c) {
        try {
            var appointments = openAppointmentsOrExplain(c, true, "resched.none");
            if (appointments == null) return;

            ConversationContext context = conversationService.getContext(c);
            context.setReschedulableAppointmentIds(appointments.stream().map(Appointment::getId).toList());
            conversationService.saveContext(c, context);

            say(c, "resched.select", appointmentChoices(c, appointments));
        } catch (Exception e) {
            logger.error("Error showing reschedulable appointments", e);
            say(c, "list.error");
            goToMainMenu(c);
        }
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

    // ---------- cancel

    private void handleSelectingAppointmentToCancel(WhatsAppConversation c, ParsedMessage message) {
        String input = pickSelection(message);
        if (input == null || isBackCommand(input)) {
            goToMainMenu(c);
            return;
        }
        try {
            ConversationContext context = conversationService.getContext(c);
            Long id = resolveAppointmentChoice(input, context.getCancellableAppointmentIds());
            if (id == null) {
                say(c, "pick.number");
                return;
            }
            var appointment = appointmentManagementService.getOwnedAppointment(id, c.getUser());
            context.setSelectedAppointmentId(id);
            conversationService.saveContext(c, context);

            conversationService.updateState(c, ConversationState.CONFIRMING_CANCELLATION);
            messageSender.sendButtonMessage(c.getWhatsappPhone(),
                t(c, "cancel.confirm", appointmentManagementService.formatAppointmentDetails(appointment, lang(c))),
                List.of(new WhatsAppMessageSender.Button("confirm_cancel", t(c, "btn.yes_cancel")),
                        new WhatsAppMessageSender.Button("keep", t(c, "btn.keep"))));
        } catch (Exception e) {
            logger.error("Error selecting appointment to cancel", e);
            say(c, "err.something");
            goToMainMenu(c);
        }
    }

    private void handleConfirmingCancellation(WhatsAppConversation c, ParsedMessage message) {
        String input = pickSelection(message);
        if ("confirm_cancel".equalsIgnoreCase(input) || isYes(input)) {
            try {
                ConversationContext context = conversationService.getContext(c);
                appointmentManagementService.cancelAppointment(context.getSelectedAppointmentId(), c.getUser());
                context.setSelectedAppointmentId(null);
                conversationService.saveContext(c, context);
                say(c, "cancel.done");
            } catch (BookingException e) {
                messageSender.sendTextMessage(c.getWhatsappPhone(), "❌ " + reason(c, e));
            } catch (Exception e) {
                logger.error("Error cancelling appointment", e);
                say(c, "cancel.failed");
            }
        } else {
            say(c, "cancel.unchanged");
        }
        goToMainMenu(c);
    }

    // ---------- reschedule

    private void handleSelectingAppointmentToReschedule(WhatsAppConversation c, ParsedMessage message) {
        String input = pickSelection(message);
        if (input == null || isBackCommand(input)) {
            goToMainMenu(c);
            return;
        }
        try {
            ConversationContext context = conversationService.getContext(c);
            Long id = resolveAppointmentChoice(input, context.getReschedulableAppointmentIds());
            if (id == null) {
                say(c, "pick.number");
                return;
            }
            var appointment = appointmentManagementService.getOwnedAppointment(id, c.getUser());
            context.setSelectedAppointmentId(id);
            context.setSelectedDoctorId(appointment.getDoctor().getId());
            context.setSelectedDoctorName(doctorService.formatDoctorName(appointment.getDoctor()));
            conversationService.saveContext(c, context);

            conversationService.updateState(c, ConversationState.RESCHEDULE_SELECTING_DATE);
            showDateSelection(c);
        } catch (Exception e) {
            logger.error("Error selecting appointment to reschedule", e);
            say(c, "err.something");
            goToMainMenu(c);
        }
    }

    private void handleRescheduleSelectingDate(WhatsAppConversation c, ParsedMessage message) {
        String selected = pickSelection(message);
        if (selected == null) {
            say(c, "date.pick_new");
            return;
        }
        if (isBackCommand(selected)) {
            goToMainMenu(c);
            return;
        }
        try {
            LocalDate date = parseSelectedDate(selected);
            ConversationContext context = conversationService.getContext(c);
            User doctor = doctorService.getDoctorById(context.getSelectedDoctorId());

            var day = appointmentService.findBookableDay(doctor, c.getUser(), date);
            if (day.isEmpty()) {
                say(c, "date.gone_resched");
                showDateSelection(c);
                return;
            }
            context.setSelectedDate(date.toString());
            context.setSelectedTimeSlot(day.get().timeText());
            conversationService.saveContext(c, context);

            var old = appointmentManagementService.getOwnedAppointment(context.getSelectedAppointmentId(), c.getUser());
            Lang lang = lang(c);
            LocalDate today = appointmentService.today();
            conversationService.updateState(c, ConversationState.CONFIRMING_RESCHEDULE);
            messageSender.sendButtonMessage(c.getWhatsappPhone(),
                t(c, "resched.summary",
                    context.getSelectedDoctorName(),
                    messages.date(old.getAppointmentDate()), messages.time(old.getScheduledTime()),
                    messages.dayName(date, today, lang), messages.date(date),
                    messages.time(day.get().time()), day.get().queueNumber()),
                List.of(new WhatsAppMessageSender.Button("confirm", t(c, "btn.confirm")),
                        new WhatsAppMessageSender.Button("cancel", t(c, "btn.keep_old"))));
        } catch (DateTimeParseException | IllegalArgumentException e) {
            say(c, "date.invalid");
            showDateSelection(c);
        } catch (Exception e) {
            logger.error("Error selecting reschedule date", e);
            say(c, "err.something");
            goToMainMenu(c);
        }
    }

    private void handleConfirmingReschedule(WhatsAppConversation c, ParsedMessage message) {
        String input = pickSelection(message);
        ConversationContext context = conversationService.getContext(c);

        if (isYes(input)) {
            try {
                var fresh = appointmentManagementService.rescheduleAppointment(
                    context.getSelectedAppointmentId(), c.getUser(), LocalDate.parse(context.getSelectedDate()));
                say(c, "resched.done",
                    context.getSelectedDoctorName(),
                    messages.dayName(fresh.getAppointmentDate(), appointmentService.today(), lang(c)),
                    messages.date(fresh.getAppointmentDate()),
                    messages.time(fresh.getScheduledTime()),
                    fresh.getQueueNumber());
            } catch (BookingException e) {
                say(c, "resched.error", reason(c, e));
            } catch (Exception e) {
                logger.error("Error rescheduling appointment", e);
                say(c, "resched.failed");
            }
        } else {
            say(c, "resched.keep");
        }
        context.setSelectedAppointmentId(null);
        context.clearBookingData();
        conversationService.saveContext(c, context);
        goToMainMenu(c);
    }
}
