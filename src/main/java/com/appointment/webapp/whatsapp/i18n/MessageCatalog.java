package com.appointment.webapp.whatsapp.i18n;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Every sentence the WhatsApp assistant says, in every supported language.
 * Placeholders are {0}, {1}... A key missing in a language falls back to English; unknown keys show the key
 * itself (so a gap is visible instead of silent).
 */
@Component
public class MessageCatalog {

    private static final Pattern PLACEHOLDER = Pattern.compile("\\{(\\d+)}");
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);

    private final Map<Lang, Properties> bundles = new EnumMap<>(Lang.class);

    public MessageCatalog() {
        for (Lang lang : Lang.values()) {
            bundles.put(lang, load(lang));
        }
    }

    private static Properties load(Lang lang) {
        String path = "i18n/whatsapp_" + lang.code() + ".properties";
        try (InputStream in = MessageCatalog.class.getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalStateException("Missing translation file " + path);
            }
            Properties p = new Properties();
            p.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            return p;
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read " + path, e);
        }
    }

    /** The text for a key in a language, with {0}, {1}... filled in. */
    public String t(Lang lang, String key, Object... args) {
        String text = bundles.get(lang).getProperty(key);
        if (text == null) {
            text = bundles.get(Lang.EN).getProperty(key);
        }
        if (text == null) {
            return key;
        }
        if (args == null || args.length == 0) {
            return text;
        }
        Matcher m = PLACEHOLDER.matcher(text);
        StringBuilder out = new StringBuilder();
        while (m.find()) {
            int i = Integer.parseInt(m.group(1));
            String value = i < args.length && args[i] != null ? String.valueOf(args[i]) : "";
            m.appendReplacement(out, Matcher.quoteReplacement(value));
        }
        m.appendTail(out);
        return out.toString();
    }

    public boolean has(Lang lang, String key) {
        return bundles.get(lang).getProperty(key) != null;
    }

    public Set<String> keys(Lang lang) {
        return bundles.get(lang).stringPropertyNames();
    }

    /** "Today", "Tomorrow", or the weekday in the patient's language. */
    public String dayName(LocalDate date, LocalDate today, Lang lang) {
        if (date.equals(today)) return t(lang, "day.today");
        if (date.equals(today.plusDays(1))) return t(lang, "day.tomorrow");
        return date.getDayOfWeek().getDisplayName(TextStyle.FULL, lang.locale());
    }

    /** "15 Oct" style, month name in the patient's language. */
    public String shortDate(LocalDate date, Lang lang) {
        return date.getDayOfMonth() + " " + date.getMonth().getDisplayName(TextStyle.SHORT, lang.locale());
    }

    public String date(LocalDate date) {
        return date.format(DATE);
    }

    /** 12-hour clock with AM/PM in plain letters, understood in every language. */
    public String time(java.time.LocalTime time) {
        return time.format(TIME);
    }
}
