package com.appointment.webapp.model;

import com.appointment.webapp.model.enums.ShiftType;
import jakarta.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Doctor Weekly Schedule Template Entity
 * Stores recurring weekly schedule patterns for doctors
 * This allows doctors to set their default working hours for each day of the week
 */
@Entity
@Table(name = "doctor_weekly_templates",
       uniqueConstraints = @UniqueConstraint(columnNames = {"doctor_id", "day_of_week"}))
public class DoctorWeeklyTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    /**
     * Day of week (MONDAY=1, TUESDAY=2... SUNDAY=7)
     * We use integer for database compatibility
     */
    @Column(name = "day_of_week", nullable = false)
    private Integer dayOfWeek;

    /**
     * Is this day active? (false = doctor is off on this day)
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "shift_type", length = 20)
    private ShiftType shiftType;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "slot_duration_minutes", nullable = false)
    private Integer slotDurationMinutes = 15;

    @Column(name = "max_patients")
    private Integer maxPatients;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    // Constructors
    public DoctorWeeklyTemplate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Business Logic Methods

    /**
     * Get DayOfWeek enum from integer
     */
    public DayOfWeek getDayOfWeekEnum() {
        return DayOfWeek.of(dayOfWeek);
    }

    /**
     * Set DayOfWeek from enum
     */
    public void setDayOfWeekEnum(DayOfWeek day) {
        this.dayOfWeek = day.getValue();
    }

    /**
     * Get human-readable day name
     */
    public String getDayName() {
        if (dayOfWeek == null) return "N/A";
        return getDayOfWeekEnum().toString();
    }

    /**
     * Calculate total available slots
     */
    public int calculateTotalSlots() {
        if (maxPatients != null) {
            return maxPatients;
        }

        int totalMinutes = (int) java.time.Duration.between(startTime, endTime).toMinutes();
        return totalMinutes / slotDurationMinutes;
    }

    /**
     * Check if this is a weekend day
     */
    public boolean isWeekend() {
        return dayOfWeek != null && (dayOfWeek == 6 || dayOfWeek == 7); // Saturday or Sunday
    }

    /**
     * Get display time range
     */
    public String getTimeRange() {
        if (startTime == null || endTime == null) return "Not set";
        return startTime.toString() + " - " + endTime.toString();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getDoctor() {
        return doctor;
    }

    public void setDoctor(User doctor) {
        this.doctor = doctor;
    }

    public Integer getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public ShiftType getShiftType() {
        return shiftType;
    }

    public void setShiftType(ShiftType shiftType) {
        this.shiftType = shiftType;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Integer getSlotDurationMinutes() {
        return slotDurationMinutes;
    }

    public void setSlotDurationMinutes(Integer slotDurationMinutes) {
        this.slotDurationMinutes = slotDurationMinutes;
    }

    public Integer getMaxPatients() {
        return maxPatients;
    }

    public void setMaxPatients(Integer maxPatients) {
        this.maxPatients = maxPatients;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String toString() {
        return "DoctorWeeklyTemplate{" +
                "id=" + id +
                ", dayOfWeek=" + getDayName() +
                ", isActive=" + isActive +
                ", shiftType=" + shiftType +
                ", timeRange=" + getTimeRange() +
                '}';
    }
}
