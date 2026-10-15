package com.appointment.webapp.whatsapp.i18n;

import com.appointment.webapp.model.User;
import com.appointment.webapp.repository.UserRepository;
import com.appointment.webapp.whatsapp.model.WhatsAppConversation;
import com.appointment.webapp.whatsapp.service.conversation.WhatsAppConversationService;
import com.appointment.webapp.whatsapp.service.patient.WhatsAppPatientService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Remembers each patient's language. The choice is stored on the chat and on every patient profile that uses
 * that WhatsApp number (a parent and their children), so reminders and alerts that are sent later still arrive
 * in the right language.
 */
@Service
public class LanguageService {

    private final WhatsAppConversationService conversations;
    private final WhatsAppPatientService patients;
    private final UserRepository users;

    public LanguageService(WhatsAppConversationService conversations, WhatsAppPatientService patients, UserRepository users) {
        this.conversations = conversations;
        this.patients = patients;
        this.users = users;
    }

    /** True once the patient has picked a language in this chat (or one was inherited from their profile). */
    public boolean isChosen(WhatsAppConversation conversation) {
        return conversation.getPreferredLanguage() != null && !conversation.getPreferredLanguage().isBlank();
    }

    public Lang of(WhatsAppConversation conversation) {
        return Lang.fromCode(conversation.getPreferredLanguage());
    }

    public Lang of(User patient) {
        return Lang.fromCode(patient == null ? null : patient.getPreferredLanguage());
    }

    /** A profile on this number that already has a non-English preference means the patient chose before (e.g. on the web). */
    public Lang inheritedFromProfiles(String whatsappPhone) {
        for (User u : patients.findPatientsByPhone(whatsappPhone)) {
            Lang l = Lang.fromCode(u.getPreferredLanguage());
            if (l != Lang.EN) {
                return l;
            }
        }
        return null;
    }

    public void set(WhatsAppConversation conversation, Lang lang) {
        conversations.saveLanguage(conversation, lang.code());
        List<User> onThisNumber = patients.findPatientsByPhone(conversation.getWhatsappPhone());
        for (User u : onThisNumber) {
            u.setPreferredLanguage(lang.code());
        }
        users.saveAll(onThisNumber);
        if (conversation.getUser() != null) {
            conversation.getUser().setPreferredLanguage(lang.code());
            users.save(conversation.getUser());
        }
    }
}
