package com.appointment.webapp.service;

/**
 * A booking rule was violated (fully booked, no schedule, duplicate booking...).
 * The English message is safe to show on the web. Over WhatsApp the {@link #getKey() key} and
 * {@link #getArgs() args} are translated into the patient's language.
 */
public class BookingException extends RuntimeException {

    private final String key;
    private final Object[] args;

    public BookingException(String message) {
        super(message);
        this.key = null;
        this.args = new Object[0];
    }

    public BookingException(String key, String englishMessage, Object... args) {
        super(englishMessage);
        this.key = key;
        this.args = args == null ? new Object[0] : args;
    }

    /** Translation key (see i18n/whatsapp_*.properties), or null if only the English text exists. */
    public String getKey() {
        return key;
    }

    public Object[] getArgs() {
        return args;
    }
}
