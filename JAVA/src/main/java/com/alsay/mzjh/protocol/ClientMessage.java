package com.alsay.mzjh.protocol;

public class ClientMessage {
    private String topic;

    public ClientMessage() {}

    public ClientMessage(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}