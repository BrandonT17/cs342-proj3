package com.example.javafxapp;

public class Message {
    private MessageType type;
    private Object content;
    private String sender;

    public Message(MessageType type, Object content, String sender) {
        this.type = type;
        this.content = content;
        this.sender = sender;
    }

    public String getMessage() {
        return content != null ? content.toString() : null;
    }

    public MessageType getType() {
        return type;
    }

    public String getSender() {
        return sender;
    }
} 