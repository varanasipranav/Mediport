package com.appointment.webapp.whatsapp.i18n;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessageCatalogTest {

    private final MessageCatalog catalog = new MessageCatalog();
    private static final Pattern PLACEHOLDER = Pattern.compile("\\{(\\d+)}");

    private Set<String> placeholders(String text) {
        Set<String> found = new TreeSet<>();
        Matcher m = PLACEHOLDER.matcher(text);
        while (m.find()) found.add(m.group(1));
        return found;
    }

    private int length(String s) {
        return s.codePointCount(0, s.length());
    }

    @Test
    void everyLanguageHasExactlyTheEnglishKeys() {
        Set<String> english = catalog.keys(Lang.EN);
        for (Lang lang : Lang.values()) {
            Set<String> missing = new TreeSet<>(english);
            missing.removeAll(catalog.keys(lang));
            Set<String> extra = new TreeSet<>(catalog.keys(lang));
            extra.removeAll(english);
            assertTrue(missing.isEmpty(), lang + " is missing: " + missing);
            assertTrue(extra.isEmpty(), lang + " has keys English does not: " + extra);
        }
    }

    @Test
    void translationsUseTheSamePlaceholdersAsEnglish() {
        List<String> problems = new ArrayList<>();
        for (String key : catalog.keys(Lang.EN)) {
            Set<String> expected = placeholders(catalog.t(Lang.EN, key));
            for (Lang lang : Lang.values()) {
                Set<String> actual = placeholders(catalog.t(lang, key));
                if (!expected.equals(actual)) {
                    problems.add(lang + " / " + key + ": expected " + expected + " but found " + actual);
                }
            }
        }
        assertTrue(problems.isEmpty(), String.join("\n", problems));
    }

    @Test
    void textShownInButtonsAndListsFitsWhatsAppLimits() {
        List<String> problems = new ArrayList<>();
        for (Lang lang : Lang.values()) {
            for (String key : catalog.keys(lang)) {
                String text = catalog.t(lang, key, "x", "x", "x", "x", "x", "x", "x");
                int limit = -1;
                if (key.startsWith("btn.") || key.startsWith("lbtn.")) limit = 20;
                else if (key.startsWith("hdr.")) limit = 60;
                else if (key.startsWith("sec.")) limit = 24;
                else if (key.startsWith("row.") && key.endsWith(".t")) limit = 24;
                else if (key.startsWith("row.") && key.endsWith(".d")) limit = 72;
                if (limit > 0 && length(text) > limit) {
                    problems.add(lang + " / " + key + " is " + length(text) + " characters (max " + limit + "): " + text);
                }
            }
        }
        assertTrue(problems.isEmpty(), String.join("\n", problems));
    }

    @Test
    void placeholdersAreFilledAndMissingKeysAreVisible() {
        assertEquals("1. *Asha* - 34 years", catalog.t(Lang.EN, "who.line", 1, "Asha", " - 34 years"));
        assertEquals("no.such.key", catalog.t(Lang.TE, "no.such.key"));
        assertTrue(catalog.t(Lang.TA, "q.near.2", "ரவி", "டாக். குமார்", "10:30 AM").contains("ரவி"));
    }

    @Test
    void dayNamesAreLocalised() {
        LocalDate today = LocalDate.of(2026, 10, 14);
        assertEquals("Today", catalog.dayName(today, today, Lang.EN));
        assertEquals("आज", catalog.dayName(today, today, Lang.HI));
        assertEquals("రేపు", catalog.dayName(today.plusDays(1), today, Lang.TE));
        // 2026-10-19 is a Monday
        assertTrue(catalog.dayName(today.plusDays(5), today, Lang.TA).startsWith("திங்"));
        assertTrue(catalog.dayName(today.plusDays(5), today, Lang.HI).contains("सोम"));
    }

    @Test
    void languageInputIsRecognised() {
        assertEquals(Lang.TE, Lang.fromInput("lang_te"));
        assertEquals(Lang.HI, Lang.fromInput("Hindi"));
        assertEquals(Lang.TA, Lang.fromInput("தமிழ்"));
        assertEquals(Lang.EN, Lang.fromInput("1"));
        assertEquals(Lang.EN, Lang.fromCode(null));
        assertEquals(Lang.TE, Lang.fromCode("te"));
    }
}
