package com.appointment.webapp.model;

import com.appointment.webapp.model.enums.ShiftType;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "doctor_schedules",
       uniqueConstraints = @UniqueConstraint(columnNames = {"doctor_id", "schedule_date"}))
public class DoctorSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    @Column(name = "schedule_date", nullable = false)
    private LocalDate scheduleDate;

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

    @Column(name = "priority_work_minutes", nullable = false)
    private Integer priorityWorkMinutes = 0;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_template_generated")
    private Boolean isTemplateGenerated = false; // Auto-generated from weekly template

    @Column(name = "is_override")
    private Boolean isOverride = false; // Manual override of weekly template

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    // Constructors
    public DoctorSchedule() {
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
     * Calculate total available slots based on time range and slot duration
     */
    public int calculateTotalSlots() {
        if (maxPatients != null) {
            return maxPatients;
        }

        int totalMinutes = (int) java.time.Duration.between(startTime, endTime).toMinutes();
        return totalMinutes / slotDurationMinutes;
    }

    /**
     * Calculate scheduled time for a given queue number
     */
    public LocalTime calculateScheduledTime(int queueNumber) {
        int totalMinutes = (queueNumber - 1) * slotDurationMinutes + priorityWorkMinutes;
        return startTime.plusMinutes(totalMinutes);
    }

    /**
     * True if the slot for this queue number starts and ends inside the doctor's working hours
     * (start_time .. end_time), taking priority-work delay into account.
     */
    public boolean hasSlotFor(int queueNumber) {
        int offset = (queueNumber - 1) * slotDurationMinutes + priorityWorkMinutes;
        int slotStart = startTime.toSecondOfDay() / 60 + offset;
        int slotEnd = slotStart + slotDurationMinutes;
        return slotEnd <= endTime.toSecondOfDay() / 60;
    }

    /**
     * Check if schedule has capacity for more appointments
     */
    public boolean hasCapacity(int currentAppointmentCount) {
        int totalSlots = calculateTotalSlots();
        return currentAppointmentCount < totalSlots;
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

    public LocalDate getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(LocalDate scheduleDate) {
        this.scheduleDate = scheduleDate;
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

    public Integer getPriorityWorkMinutes() {
        return priorityWorkMinutes;
    }

    public void setPriorityWorkMinutes(Integer priorityWorkMinutes) {
        this.priorityWorkMinutes = priorityWorkMinutes;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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

    public Boolean getIsTemplateGenerated() {
        return isTemplateGenerated;
    }

    public void setIsTemplateGenerated(Boolean isTemplateGenerated) {
        this.isTemplateGenerated = isTemplateGenerated;
    }

    public Boolean getIsOverride() {
        return isOverride;
    }

    public void setIsOverride(Boolean isOverride) {
        this.isOverride = isOverride;
    }
}
