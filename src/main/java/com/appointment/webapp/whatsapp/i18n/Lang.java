package com.appointment.webapp.whatsapp.i18n;

import java.util.Locale;

/**
 * Languages the WhatsApp assistant speaks. To add one: add a constant here and an
 * {@code i18n/whatsapp_<code>.properties} file with the same keys (a unit test checks they match).
 */
public enum Lang {

    EN("en", "English", Locale.ENGLISH, "english", "eng", "1"),
    HI("hi", "हिन्दी", Locale.forLanguageTag("hi-IN"), "hindi", "हिन्दी", "हिंदी", "2"),
    TE("te", "తెలుగు", Locale.forLanguageTag("te-IN"), "telugu", "తెలుగు", "3"),
    TA("ta", "தமிழ்", Locale.forLanguageTag("ta-IN"), "tamil", "தமிழ்", "4");

    private final String code;
    private final String nativeName;
    private final Locale locale;
    private final String[] aliases;

    Lang(String code, String nativeName, Locale locale, String... aliases) {
        this.code = code;
        this.nativeName = nativeName;
        this.locale = locale;
        this.aliases = aliases;
    }

    public String code() {
        return code;
    }

    /** The language's own name for itself, e.g. "తెలుగు". */
    public String nativeName() {
        return nativeName;
    }

    public Locale locale() {
        return locale;
    }

    /** Stored value ("hi", "te"...) to a language; anything unknown means English. */
    public static Lang fromCode(String code) {
        if (code != null) {
            for (Lang l : values()) {
                if (l.code.equalsIgnoreCase(code.trim())) return l;
            }
        }
        return EN;
    }

    /** What a patient typed or tapped ("lang_hi", "telugu", "3", "தமிழ்") to a language, or null if unclear. */
    public static Lang fromInput(String input) {
        if (input == null) return null;
        String v = input.trim().toLowerCase(Locale.ROOT);
        if (v.startsWith("lang_")) {
            v = v.substring(5);
        }
        for (Lang l : values()) {
            if (l.code.equals(v)) return l;
            for (String a : l.aliases) {
                if (a.equalsIgnoreCase(v)) return l;
            }
        }
        return null;
    }
}
