package com.appointment.webapp.model.enums;

public enum AppointmentStatus {
    WAITING("Waiting", "secondary"),
    IN_CONSULTATION("In Consultation", "primary"),
    COMPLETED("Completed", "success"),
    NOT_ATTENDED("Not Attended", "danger"),
    DELAYED("Delayed", "warning"),
    CANCELLED("Cancelled", "dark");

    private final String displayName;
    private final String badgeClass;

    AppointmentStatus(String displayName, String badgeClass) {
        this.displayName = displayName;
        this.badgeClass = badgeClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBadgeClass() {
        return badgeClass;
    }
}
