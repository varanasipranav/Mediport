package com.appointment.webapp.whatsapp.service.webhook;

import com.appointment.webapp.whatsapp.dto.webhook.ParsedMessage;
import com.appointment.webapp.whatsapp.dto.webhook.WebhookEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service to parse complex WhatsApp webhook payloads
 * Converts nested JSON structure to simple ParsedMessage objects
 */
@Service
public class WebhookMessageParser {

    private static final Logger logger = LoggerFactory.getLogger(WebhookMessageParser.class);

    /**
     * Parse webhook event and extract all messages
     *
     * @param webhookEvent The webhook event from WhatsApp
     * @return List of parsed messages (usually 1, but can handle multiple)
     */
    public List<ParsedMessage> parseMessages(WebhookEvent webhookEvent) {
        List<ParsedMessage> parsedMessages = new ArrayList<>();

        try {
            if (webhookEvent == null || webhookEvent.getEntry() == null) {
                logger.warn("Received null or empty webhook event");
                return parsedMessages;
            }

            // Iterate through all entries (usually just 1)
            for (WebhookEvent.Entry entry : webhookEvent.getEntry()) {
                if (entry.getChanges() == null) {
                    continue;
                }

                // Iterate through all changes
                for (WebhookEvent.Change change : entry.getChanges()) {
                    if (!"messages".equals(change.getField())) {
                        // Skip non-message changes (e.g., message_deliveries)
                        logger.debug("Skipping non-message change: {}", change.getField());
                        continue;
                    }

                    WebhookEvent.Value value = change.getValue();
                    if (value == null || value.getMessages() == null) {
                        continue;
                    }

                    // Get sender information
                    String senderName = extractSenderName(value);
                    String phoneNumberId = value.getMetadata() != null ? value.getMetadata().getPhoneNumberId() : null;

                    // Parse each message
                    for (WebhookEvent.Message message : value.getMessages()) {
                        ParsedMessage parsed = parseIndividualMessage(message, senderName);
                        if (parsed != null) {
                            parsed.setPhoneNumberId(phoneNumberId);
                            parsedMessages.add(parsed);
                            logger.info("Parsed message: {}", parsed);
                        }
                    }
                }
            }

        } catch (Exception e) {
            logger.error("Error parsing webhook messages", e);
        }

        return parsedMessages;
    }

    /**
     * Parse individual message based on type
     */
    private ParsedMessage parseIndividualMessage(WebhookEvent.Message message, String senderName) {
        ParsedMessage parsed = new ParsedMessage();
        parsed.setMessageId(message.getId());
        parsed.setFrom(message.getFrom());
        parsed.setSenderName(senderName);
        parsed.setTimestamp(message.getTimestamp());

        String messageType = message.getType();
        logger.debug("Parsing message type: {} from {}", messageType, message.getFrom());

        switch (messageType) {
            case "text":
                parseTextMessage(message, parsed);
                break;

            case "interactive":
                parseInteractiveMessage(message, parsed);
                break;

            case "button":
                parseButtonMessage(message, parsed);
                break;

            default:
                logger.warn("Unsupported message type: {}", messageType);
                parsed.setMessageType(ParsedMessage.MessageType.UNKNOWN);
                return null;  // Skip unsupported types
        }

        return parsed;
    }

    /**
     * Parse text message
     */
    private void parseTextMessage(WebhookEvent.Message message, ParsedMessage parsed) {
        if (message.getText() != null && message.getText().getBody() != null) {
            parsed.setMessageType(ParsedMessage.MessageType.TEXT);
            parsed.setTextContent(message.getText().getBody().trim());
            logger.debug("Text message content: {}", parsed.getTextContent());
        }
    }

    /**
     * Parse interactive message (button or list reply)
     */
    private void parseInteractiveMessage(WebhookEvent.Message message, ParsedMessage parsed) {
        if (message.getInteractive() == null) {
            return;
        }

        String interactiveType = message.getInteractive().getType();
        logger.debug("Interactive message type: {}", interactiveType);

        if ("button_reply".equals(interactiveType)) {
            WebhookEvent.ButtonReply buttonReply = message.getInteractive().getButtonReply();
            if (buttonReply != null) {
                parsed.setMessageType(ParsedMessage.MessageType.BUTTON_REPLY);
                parsed.setButtonId(buttonReply.getId());
                parsed.setButtonText(buttonReply.getTitle());
                logger.debug("Button reply - ID: {}, Text: {}", buttonReply.getId(), buttonReply.getTitle());
            }
        } else if ("list_reply".equals(interactiveType)) {
            WebhookEvent.ListReply listReply = message.getInteractive().getListReply();
            if (listReply != null) {
                parsed.setMessageType(ParsedMessage.MessageType.LIST_REPLY);
                parsed.setListId(listReply.getId());
                parsed.setListTitle(listReply.getTitle());
                logger.debug("List reply - ID: {}, Title: {}", listReply.getId(), listReply.getTitle());
            }
        }
    }

    /**
     * Parse button message (quick reply button)
     */
    private void parseButtonMessage(WebhookEvent.Message message, ParsedMessage parsed) {
        if (message.getButton() != null) {
            parsed.setMessageType(ParsedMessage.MessageType.BUTTON_REPLY);
            parsed.setButtonId(message.getButton().getPayload());
            parsed.setButtonText(message.getButton().getText());
            logger.debug("Button message - Payload: {}, Text: {}",
                        message.getButton().getPayload(), message.getButton().getText());
        }
    }

    /**
     * Extract sender name from contacts
     */
    private String extractSenderName(WebhookEvent.Value value) {
        if (value.getContacts() != null && !value.getContacts().isEmpty()) {
            WebhookEvent.Contact contact = value.getContacts().get(0);
            if (contact.getProfile() != null && contact.getProfile().getName() != null) {
                return contact.getProfile().getName();
            }
        }
        return "Unknown";
    }
}
