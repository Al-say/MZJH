package com.alsay.mzjh.protocol;

public class ServerMessage {
    private String topicId;
    private String agentId;
    private String content;
    private boolean isFinish;

    // 扩展字段，前端可忽略
    private String eventType; // START, DELTA, END, ERROR
    private long seq;

    public ServerMessage() {}

    public ServerMessage(String topicId, String agentId, String content, boolean isFinish, String eventType, long seq) {
        this.topicId = topicId;
        this.agentId = agentId;
        this.content = content;
        this.isFinish = isFinish;
        this.eventType = eventType;
        this.seq = seq;
    }

    public String getTopicId() { return topicId; }
    public void setTopicId(String topicId) { this.topicId = topicId; }

    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public boolean isFinish() { return isFinish; }
    public void setFinish(boolean finish) { isFinish = finish; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public long getSeq() { return seq; }
    public void setSeq(long seq) { this.seq = seq; }
}