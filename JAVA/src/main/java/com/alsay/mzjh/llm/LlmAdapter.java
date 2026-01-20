package com.alsay.mzjh.llm;

import com.alsay.mzjh.protocol.RoundtableEvent;
import reactor.core.publisher.Flux;

/**
 * LLM Adapter interface for unified access to different LLM providers.
 * Converts provider-specific streaming responses to Flux<RoundtableEvent>.
 */
public interface LlmAdapter {

    /**
     * Generate streaming response for a given prompt.
     * @param agentId the agent identifier
     * @param prompt the prompt text
     * @param seqStart the starting sequence number for events
     * @return Flux of RoundtableEvents
     */
    Flux<RoundtableEvent> generateStreaming(String agentId, String prompt, long seqStart);
}