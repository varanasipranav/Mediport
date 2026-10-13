package com.appointment.webapp.whatsapp.dto.webhook;

/**
 * Simplified Parsed Message DTO for internal processing
 * Extracted from complex WhatsApp webhook payload
 */
public class ParsedMessage {

    private String messageId;          // WhatsApp message ID (wamid.xxx)
    private String from;                // Sender phone number (E.164 format)
    private String senderName;          // Sender's WhatsApp name
    private String timestamp;           // Message timestamp
    private MessageType messageType;    // TEXT, BUTTON_REPLY, LIST_REPLY
    private String textContent;         // For text messages
    private String buttonId;            // For button replies
    private String buttonText;          // For button replies
    private String listId;              // For list replies
    private String listTitle;           // For list replies

    public enum MessageType {
        TEXT,
        BUTTON_REPLY,
        LIST_REPLY,
        UNKNOWN
    }

    // Getters and Setters
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public String getButtonId() {
        return buttonId;
    }

    public void setButtonId(String buttonId) {
        this.buttonId = buttonId;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public String getListTitle() {
        return listTitle;
    }

    public void setListTitle(String listTitle) {
        this.listTitle = listTitle;
    }

    @Override
    public String toString() {
        return "ParsedMessage{" +
                "messageId='" + messageId + '\'' +
                ", from='" + from + '\'' +
                ", senderName='" + senderName + '\'' +
                ", messageType=" + messageType +
                ", textContent='" + textContent + '\'' +
                ", buttonId='" + buttonId + '\'' +
                ", listId='" + listId + '\'' +
                '}';
    }
}
