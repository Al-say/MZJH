package com.alsay.mzjh.protocol;

import java.util.List;

public class ClientMessage {
    private String action; // "START" or "CANCEL"
    private String topic;
    private String topicId; // for CANCEL
    private String templateId;      // 可空
    private List<String> agentKeys; // 可空，若非空则长度必须为5
    private String agentKey;        // 单席位模式使用

    public ClientMessage() {}

    public ClientMessage(String action, String topic, String templateId, List<String> agentKeys) {
        this.action = action;
        this.topic = topic;
        this.templateId = templateId;
        this.agentKeys = agentKeys;
    }

    public ClientMessage(String action, String topicId) {
        this.action = action;
        this.topicId = topicId;
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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getAgentKey() {
        return agentKey;
    }

    public void setAgentKey(String agentKey) {
        this.agentKey = agentKey;
    }
}