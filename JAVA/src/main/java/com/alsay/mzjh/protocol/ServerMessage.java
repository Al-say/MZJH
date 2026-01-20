package com.alsay.mzjh.protocol;

import java.util.Map;

public class ServerMessage {
    private String topicId;
    private Object agentId; // String for "system", Long for seat IDs
    private String content;
    private boolean isFinish;

    // 扩展字段，前端可忽略
    private String eventType; // START, DELTA, SECTION, END, CANCELLED, ERROR, TOPIC_READY
    private long seq;
    private Map<String, Object> payload; // 可选：结构化摘要、风险、行动项等

    public ServerMessage() {}

    public ServerMessage(String topicId, Object agentId, String content, boolean isFinish, String eventType, long seq) {
        this.topicId = topicId;
        this.agentId = agentId;
        this.content = content;
        this.isFinish = isFinish;
        this.eventType = eventType;
        this.seq = seq;
    }

    public ServerMessage(String topicId, Object agentId, String content, boolean isFinish, String eventType, long seq, Map<String, Object> payload) {
        this.topicId = topicId;
        this.agentId = agentId;
        this.content = content;
        this.isFinish = isFinish;
        this.eventType = eventType;
        this.seq = seq;
        this.payload = payload;
    }

    public String getTopicId() { return topicId; }
    public void setTopicId(String topicId) { this.topicId = topicId; }

    public Object getAgentId() { return agentId; }
    public void setAgentId(Object agentId) { this.agentId = agentId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public boolean isFinish() { return isFinish; }
    public void setFinish(boolean finish) { isFinish = finish; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public long getSeq() { return seq; }
    public void setSeq(long seq) { this.seq = seq; }

    public Map<String, Object> getPayload() { return payload; }
    public void setPayload(Map<String, Object> payload) { this.payload = payload; }
}