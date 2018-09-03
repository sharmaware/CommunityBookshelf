package com.google.android.gms.samples.vision.barcodereader;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by reale on 18/11/2016.
 */

public class ChatMessage {
    private String messageText;
    private String messageUser;
    private long messageTime;

    public ChatMessage(String messageText, String messageUser) {
        this.messageText = messageText;
        this.messageUser = messageUser;

        messageTime = new Date().getTime() - 7*60*60*1000;
    }

    public ChatMessage() {
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
    public Map<String, Object> messageMap(){
        Map<String, Object> message = new HashMap<>();
        message.put("text", messageText);
        message.put("user", messageUser);
        message.put("time", messageTime);
        return message;
    }
}

