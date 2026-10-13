package com.appointment.webapp.whatsapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Conversation Context DTO
 * Stores temporary data during conversation flow
 * Serialized to JSON and stored in whatsapp_conversations.context_data
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConversationContext {

    // Patient identification
    private Long userId;
    private List<Long> multiplePatientIds; // When multiple patients found with same phone

    // Appointment management
    private List<Long> cancellableAppointmentIds; // Appointments that can be cancelled
    private List<Long> reschedulableAppointmentIds; // Appointments that can be rescheduled

    // Patient registration data (temporary)
    private String tempPatientName;
    private String tempPatientGender;
    private String tempPatientDob;
    private Integer tempPatientAge;

    // Appointment booking data
    private Long selectedDoctorId;
    private String selectedDoctorName;
    private String selectedDate; // ISO format: 2026-02-01
    private String selectedTimeSlot; // Format: 09:00
    private Long selectedAppointmentId; // For cancel/reschedule

    // Additional metadata
    private Map<String, Object> extraData = new HashMap<>();

    // Constructors
    public ConversationContext() {
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Long> getMultiplePatientIds() {
        return multiplePatientIds;
    }

    public void setMultiplePatientIds(List<Long> multiplePatientIds) {
        this.multiplePatientIds = multiplePatientIds;
    }

    public List<Long> getCancellableAppointmentIds() {
        return cancellableAppointmentIds;
    }

    public void setCancellableAppointmentIds(List<Long> cancellableAppointmentIds) {
        this.cancellableAppointmentIds = cancellableAppointmentIds;
    }

    public List<Long> getReschedulableAppointmentIds() {
        return reschedulableAppointmentIds;
    }

    public void setReschedulableAppointmentIds(List<Long> reschedulableAppointmentIds) {
        this.reschedulableAppointmentIds = reschedulableAppointmentIds;
    }

    public String getTempPatientName() {
        return tempPatientName;
    }

    public void setTempPatientName(String tempPatientName) {
        this.tempPatientName = tempPatientName;
    }

    public String getTempPatientGender() {
        return tempPatientGender;
    }

    public void setTempPatientGender(String tempPatientGender) {
        this.tempPatientGender = tempPatientGender;
    }

    public String getTempPatientDob() {
        return tempPatientDob;
    }

    public void setTempPatientDob(String tempPatientDob) {
        this.tempPatientDob = tempPatientDob;
    }

    public Integer getTempPatientAge() {
        return tempPatientAge;
    }

    public void setTempPatientAge(Integer tempPatientAge) {
        this.tempPatientAge = tempPatientAge;
    }

    public Long getSelectedDoctorId() {
        return selectedDoctorId;
    }

    public void setSelectedDoctorId(Long selectedDoctorId) {
        this.selectedDoctorId = selectedDoctorId;
    }

    public String getSelectedDoctorName() {
        return selectedDoctorName;
    }

    public void setSelectedDoctorName(String selectedDoctorName) {
        this.selectedDoctorName = selectedDoctorName;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    public String getSelectedTimeSlot() {
        return selectedTimeSlot;
    }

    public void setSelectedTimeSlot(String selectedTimeSlot) {
        this.selectedTimeSlot = selectedTimeSlot;
    }

    public Long getSelectedAppointmentId() {
        return selectedAppointmentId;
    }

    public void setSelectedAppointmentId(Long selectedAppointmentId) {
        this.selectedAppointmentId = selectedAppointmentId;
    }

    public Map<String, Object> getExtraData() {
        return extraData;
    }

    public void setExtraData(Map<String, Object> extraData) {
        this.extraData = extraData;
    }

    // Helper methods
    public void putExtra(String key, Object value) {
        this.extraData.put(key, value);
    }

    public Object getExtra(String key) {
        return this.extraData.get(key);
    }

    public void clearTempPatientData() {
        this.tempPatientName = null;
        this.tempPatientGender = null;
        this.tempPatientDob = null;
        this.tempPatientAge = null;
    }

    public void clearBookingData() {
        this.selectedDoctorId = null;
        this.selectedDoctorName = null;
        this.selectedDate = null;
        this.selectedTimeSlot = null;
    }

    @Override
    public String toString() {
        return "ConversationContext{" +
                "userId=" + userId +
                ", selectedDoctorId=" + selectedDoctorId +
                ", selectedDate='" + selectedDate + '\'' +
                ", selectedTimeSlot='" + selectedTimeSlot + '\'' +
                '}';
    }
}
