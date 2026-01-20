package com.alsay.mzjh.protocol;

import java.util.List;

public class ClientMessage {
    private String topic;
    private String templateId;      // 可空
    private List<String> agentKeys; // 可空，若非空则长度必须为5

    public ClientMessage() {}

    public ClientMessage(String topic, String templateId, List<String> agentKeys) {
        this.topic = topic;
        this.templateId = templateId;
        this.agentKeys = agentKeys;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public List<String> getAgentKeys() {
        return agentKeys;
    }

    public void setAgentKeys(List<String> agentKeys) {
        this.agentKeys = agentKeys;
    }
}