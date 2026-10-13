package com.appointment.webapp.model.enums;

public enum ShiftType {
    MORNING("Morning", "09:00", "13:00"),
    EVENING("Evening", "14:00", "18:00"),
    FULL_DAY("Full Day", "09:00", "18:00"),
    CUSTOM("Custom", null, null);

    private final String displayName;
    private final String defaultStartTime;
    private final String defaultEndTime;

    ShiftType(String displayName, String defaultStartTime, String defaultEndTime) {
        this.displayName = displayName;
        this.defaultStartTime = defaultStartTime;
        this.defaultEndTime = defaultEndTime;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDefaultStartTime() {
        return defaultStartTime;
    }

    public String getDefaultEndTime() {
        return defaultEndTime;
    }
}
