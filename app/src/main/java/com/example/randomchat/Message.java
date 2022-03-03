package com.example.randomchat;

public class Message {
    String messageText;
    String time;
    String senderID;
    String messageID;

    public Message(String messageText, String time, String senderID) {
        this.messageText = messageText;
        this.time = time;
        this.senderID = senderID;
    }

    public Message() {
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }
}
