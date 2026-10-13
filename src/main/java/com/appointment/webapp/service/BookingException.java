package com.appointment.webapp.service;

/**
 * A booking rule was violated (fully booked, no schedule, duplicate booking...).
 * The message is written for end users and is safe to show in WhatsApp.
 */
public class BookingException extends RuntimeException {
    public BookingException(String message) {
        super(message);
    }
}
