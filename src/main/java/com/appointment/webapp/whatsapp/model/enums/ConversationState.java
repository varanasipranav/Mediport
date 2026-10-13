package com.appointment.webapp.whatsapp.model.enums;

/**
 * Conversation State Machine for WhatsApp Bot
 * Tracks where the user is in the appointment booking flow
 */
public enum ConversationState {

    // ========================================
    // Initial States
    // ========================================
    /** User just sent first message or session expired */
    INITIAL("Initial State"),

    /** Showing main menu options */
    MAIN_MENU("Main Menu"),

    // ========================================
    // Patient Identification Flow
    // ========================================
    /** Checking if patient exists by phone number */
    CHECKING_PATIENT("Checking Patient"),

    /** Multiple patients found with same number, asking to select */
    SELECTING_EXISTING_PATIENT("Selecting Existing Patient"),

    /** Asking if user wants to create new patient or select existing */
    ASKING_NEW_OR_EXISTING("New or Existing Patient"),

    // ========================================
    // New Patient Registration Flow
    // ========================================
    /** Collecting patient's full name */
    COLLECTING_PATIENT_NAME("Collecting Patient Name"),

    /** Collecting patient's gender */
    COLLECTING_PATIENT_GENDER("Collecting Patient Gender"),

    /** Collecting patient's date of birth */
    COLLECTING_PATIENT_DOB("Collecting Patient DOB"),

    /** Collecting patient's age (alternative to DOB) */
    COLLECTING_PATIENT_AGE("Collecting Patient Age"),

    // ========================================
    // Appointment Booking Flow
    // ========================================
    /** Showing list of available doctors */
    SELECTING_DOCTOR("Selecting Doctor"),

    /** Showing date options (Today/Tomorrow/Custom) */
    SELECTING_DATE("Selecting Date"),

    /** User is entering custom date */
    ENTERING_CUSTOM_DATE("Entering Custom Date"),

    /** Showing available time slots */
    SELECTING_TIME_SLOT("Selecting Time Slot"),

    /** Final confirmation before booking */
    CONFIRMING_APPOINTMENT("Confirming Appointment"),

    /** Appointment successfully booked */
    APPOINTMENT_BOOKED("Appointment Booked"),

    // ========================================
    // View Appointments Flow
    // ========================================
    /** Showing user's upcoming appointments */
    VIEWING_APPOINTMENTS("Viewing Appointments"),

    // ========================================
    // Cancel Appointment Flow
    // ========================================
    /** Showing appointments that can be cancelled */
    SELECTING_APPOINTMENT_TO_CANCEL("Selecting Appointment to Cancel"),

    /** Asking for cancellation confirmation */
    CONFIRMING_CANCELLATION("Confirming Cancellation"),

    /** Appointment cancelled successfully */
    APPOINTMENT_CANCELLED("Appointment Cancelled"),

    // ========================================
    // Reschedule Appointment Flow
    // ========================================
    /** Showing appointments that can be rescheduled */
    SELECTING_APPOINTMENT_TO_RESCHEDULE("Selecting Appointment to Reschedule"),

    /** After selecting appointment, choosing new doctor */
    RESCHEDULE_SELECTING_DOCTOR("Reschedule - Selecting Doctor"),

    /** Choosing new date for rescheduled appointment */
    RESCHEDULE_SELECTING_DATE("Reschedule - Selecting Date"),

    /** Choosing new time slot */
    RESCHEDULE_SELECTING_TIME("Reschedule - Selecting Time"),

    /** Confirming reschedule */
    CONFIRMING_RESCHEDULE("Confirming Reschedule"),

    /** Appointment rescheduled successfully */
    APPOINTMENT_RESCHEDULED("Appointment Rescheduled"),

    // ========================================
    // Special States
    // ========================================
    /** Error occurred, showing error message */
    ERROR_STATE("Error State"),

    /** Conversation idle for too long */
    SESSION_EXPIRED("Session Expired"),

    /** User can send any query/question */
    HELP_MODE("Help Mode");

    private final String displayName;

    ConversationState(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if state is part of booking flow
     */
    public boolean isBookingFlow() {
        return this == SELECTING_DOCTOR ||
               this == SELECTING_DATE ||
               this == ENTERING_CUSTOM_DATE ||
               this == SELECTING_TIME_SLOT ||
               this == CONFIRMING_APPOINTMENT;
    }

    /**
     * Check if state is part of patient registration
     */
    public boolean isRegistrationFlow() {
        return this == COLLECTING_PATIENT_NAME ||
               this == COLLECTING_PATIENT_GENDER ||
               this == COLLECTING_PATIENT_DOB ||
               this == COLLECTING_PATIENT_AGE ||
               this == ASKING_NEW_OR_EXISTING ||
               this == SELECTING_EXISTING_PATIENT;
    }

    /**
     * Check if state is part of cancellation flow
     */
    public boolean isCancellationFlow() {
        return this == SELECTING_APPOINTMENT_TO_CANCEL ||
               this == CONFIRMING_CANCELLATION;
    }

    /**
     * Check if state is part of reschedule flow
     */
    public boolean isRescheduleFlow() {
        return this == SELECTING_APPOINTMENT_TO_RESCHEDULE ||
               this == RESCHEDULE_SELECTING_DOCTOR ||
               this == RESCHEDULE_SELECTING_DATE ||
               this == RESCHEDULE_SELECTING_TIME ||
               this == CONFIRMING_RESCHEDULE;
    }
}
