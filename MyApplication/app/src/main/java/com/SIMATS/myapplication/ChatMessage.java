package com.SIMATS.myapplication;

public class ChatMessage {
    private String message;
    private boolean isSentByMe;
    private String timestamp;

    public ChatMessage(String message, boolean isSentByMe, String timestamp) {
        this.message = message;
        this.isSentByMe = isSentByMe;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSentByMe() {
        return isSentByMe;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
