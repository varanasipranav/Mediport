package com.appointment.webapp.service;

/** Published after a prescription is saved, so other modules (WhatsApp) can react without being called directly. */
public record PrescriptionCreatedEvent(Long prescriptionId) {}
