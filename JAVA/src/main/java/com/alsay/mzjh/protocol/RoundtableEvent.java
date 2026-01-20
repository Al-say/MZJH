package com.alsay.mzjh.protocol;

import java.util.Map;

/**
 * RoundtableEvent represents an event in the multi-agent streaming roundtable system.
 * Events are used for real-time communication between backend and frontend.
 */
public record RoundtableEvent(
        String topicId,
        String agentId,
        EventType eventType,
        long seq,
        String content,  // Used for DELTA events
        Map<String, Object> payload  // Used for structured content in START, SECTION, END, ERROR
) {

    public enum EventType {
        START,    // Agent starts, with seat and round strategy summary
        DELTA,    // Streaming incremental content
        SECTION,  // Completed a paragraph block or structured sub-conclusion
        END,      // Agent completes, with structured output and bullet_summary
        ERROR     // Error event, with retryable flag and error code
    }

    // Factory methods for convenience
    public static RoundtableEvent start(String topicId, String agentId, long seq, Map<String, Object> payload) {
        return new RoundtableEvent(topicId, agentId, EventType.START, seq, null, payload);
    }

    public static RoundtableEvent delta(String topicId, String agentId, long seq, String content) {
        return new RoundtableEvent(topicId, agentId, EventType.DELTA, seq, content, null);
    }

    public static RoundtableEvent section(String topicId, String agentId, long seq, Map<String, Object> payload) {
        return new RoundtableEvent(topicId, agentId, EventType.SECTION, seq, null, payload);
    }

    public static RoundtableEvent end(String topicId, String agentId, long seq, Map<String, Object> payload) {
        return new RoundtableEvent(topicId, agentId, EventType.END, seq, null, payload);
    }

    public static RoundtableEvent error(String topicId, String agentId, long seq, Map<String, Object> payload) {
        return new RoundtableEvent(topicId, agentId, EventType.ERROR, seq, null, payload);
    }
}