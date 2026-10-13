package com.appointment.webapp.whatsapp.dto.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Root DTO for WhatsApp Webhook Events
 *
 * Example webhook payload from WhatsApp:
 * {
 *   "object": "whatsapp_business_account",
 *   "entry": [{
 *     "id": "WHATSAPP_BUSINESS_ACCOUNT_ID",
 *     "changes": [{
 *       "value": {
 *         "messaging_product": "whatsapp",
 *         "metadata": {
 *           "display_phone_number": "15550000000",
 *           "phone_number_id": "123456789"
 *         },
 *         "contacts": [{
 *           "profile": {
 *             "name": "John Doe"
 *           },
 *           "wa_id": "919876543210"
 *         }],
 *         "messages": [{
 *           "from": "919876543210",
 *           "id": "wamid.HBgLMTY1MDUw...",
 *           "timestamp": "1234567890",
 *           "type": "text",
 *           "text": {
 *             "body": "Hello"
 *           }
 *         }]
 *       },
 *       "field": "messages"
 *     }]
 *   }]
 * }
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebhookEvent {

    @JsonProperty("object")
    private String object;  // "whatsapp_business_account"

    @JsonProperty("entry")
    private List<Entry> entry;

    // Getters and Setters
    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public List<Entry> getEntry() {
        return entry;
    }

    public void setEntry(List<Entry> entry) {
        this.entry = entry;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Entry {
        @JsonProperty("id")
        private String id;  // WhatsApp Business Account ID

        @JsonProperty("changes")
        private List<Change> changes;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<Change> getChanges() {
            return changes;
        }

        public void setChanges(List<Change> changes) {
            this.changes = changes;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Change {
        @JsonProperty("value")
        private Value value;

        @JsonProperty("field")
        private String field;  // "messages" or "message_deliveries"

        public Value getValue() {
            return value;
        }

        public void setValue(Value value) {
            this.value = value;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Value {
        @JsonProperty("messaging_product")
        private String messagingProduct;  // "whatsapp"

        @JsonProperty("metadata")
        private Metadata metadata;

        @JsonProperty("contacts")
        private List<Contact> contacts;

        @JsonProperty("messages")
        private List<Message> messages;

        @JsonProperty("statuses")
        private List<MessageStatus> statuses;  // For delivery receipts

        public String getMessagingProduct() {
            return messagingProduct;
        }

        public void setMessagingProduct(String messagingProduct) {
            this.messagingProduct = messagingProduct;
        }

        public Metadata getMetadata() {
            return metadata;
        }

        public void setMetadata(Metadata metadata) {
            this.metadata = metadata;
        }

        public List<Contact> getContacts() {
            return contacts;
        }

        public void setContacts(List<Contact> contacts) {
            this.contacts = contacts;
        }

        public List<Message> getMessages() {
            return messages;
        }

        public void setMessages(List<Message> messages) {
            this.messages = messages;
        }

        public List<MessageStatus> getStatuses() {
            return statuses;
        }

        public void setStatuses(List<MessageStatus> statuses) {
            this.statuses = statuses;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Metadata {
        @JsonProperty("display_phone_number")
        private String displayPhoneNumber;

        @JsonProperty("phone_number_id")
        private String phoneNumberId;

        public String getDisplayPhoneNumber() {
            return displayPhoneNumber;
        }

        public void setDisplayPhoneNumber(String displayPhoneNumber) {
            this.displayPhoneNumber = displayPhoneNumber;
        }

        public String getPhoneNumberId() {
            return phoneNumberId;
        }

        public void setPhoneNumberId(String phoneNumberId) {
            this.phoneNumberId = phoneNumberId;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Contact {
        @JsonProperty("profile")
        private Profile profile;

        @JsonProperty("wa_id")
        private String waId;  // WhatsApp ID (phone number)

        public Profile getProfile() {
            return profile;
        }

        public void setProfile(Profile profile) {
            this.profile = profile;
        }

        public String getWaId() {
            return waId;
        }

        public void setWaId(String waId) {
            this.waId = waId;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Profile {
        @JsonProperty("name")
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Message {
        @JsonProperty("from")
        private String from;  // Sender phone number

        @JsonProperty("id")
        private String id;  // Message ID (wamid.xxx)

        @JsonProperty("timestamp")
        private String timestamp;

        @JsonProperty("type")
        private String type;  // "text", "interactive", "button", "image", etc.

        @JsonProperty("text")
        private TextMessage text;

        @JsonProperty("interactive")
        private InteractiveMessage interactive;

        @JsonProperty("button")
        private ButtonMessage button;

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public TextMessage getText() {
            return text;
        }

        public void setText(TextMessage text) {
            this.text = text;
        }

        public InteractiveMessage getInteractive() {
            return interactive;
        }

        public void setInteractive(InteractiveMessage interactive) {
            this.interactive = interactive;
        }

        public ButtonMessage getButton() {
            return button;
        }

        public void setButton(ButtonMessage button) {
            this.button = button;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TextMessage {
        @JsonProperty("body")
        private String body;

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InteractiveMessage {
        @JsonProperty("type")
        private String type;  // "list_reply", "button_reply"

        @JsonProperty("list_reply")
        private ListReply listReply;

        @JsonProperty("button_reply")
        private ButtonReply buttonReply;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public ListReply getListReply() {
            return listReply;
        }

        public void setListReply(ListReply listReply) {
            this.listReply = listReply;
        }

        public ButtonReply getButtonReply() {
            return buttonReply;
        }

        public void setButtonReply(ButtonReply buttonReply) {
            this.buttonReply = buttonReply;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ListReply {
        @JsonProperty("id")
        private String id;  // The ID you set when creating the list

        @JsonProperty("title")
        private String title;

        @JsonProperty("description")
        private String description;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ButtonReply {
        @JsonProperty("id")
        private String id;  // The ID you set when creating the button

        @JsonProperty("title")
        private String title;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ButtonMessage {
        @JsonProperty("payload")
        private String payload;

        @JsonProperty("text")
        private String text;

        public String getPayload() {
            return payload;
        }

        public void setPayload(String payload) {
            this.payload = payload;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MessageStatus {
        @JsonProperty("id")
        private String id;  // Message ID

        @JsonProperty("status")
        private String status;  // "sent", "delivered", "read", "failed"

        @JsonProperty("timestamp")
        private String timestamp;

        @JsonProperty("recipient_id")
        private String recipientId;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getRecipientId() {
            return recipientId;
        }

        public void setRecipientId(String recipientId) {
            this.recipientId = recipientId;
        }
    }
}
