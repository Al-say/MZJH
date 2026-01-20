package com.alsay.mzjh.llm;

import com.alsay.mzjh.protocol.RoundtableEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Map;

/**
 * Mock implementation of LlmAdapter for testing.
 * Simulates streaming response with delayed chunks.
 * Only active when roundtable.llm.mode=mock (default).
 */
@Component
@ConditionalOnProperty(name = "roundtable.llm.mode", havingValue = "mock", matchIfMissing = true)
public class MockLlmAdapter implements LlmAdapter {

    private static final Logger log = LoggerFactory.getLogger(MockLlmAdapter.class);

    public MockLlmAdapter() {
        log.info("MockLlmAdapter 已初始化 (LLM mock 模式)");
    }

    @Override
    public Flux<RoundtableEvent> generateStreaming(String agentId, String prompt, long seqStart) {
        // Simulate a streaming response with chunks
        String response = "作为" + agentId + "，我认为这个议题非常重要。从经济角度来看... 社会影响包括... 技术上可行，但需要考虑...";

        String[] chunks = response.split(" ");

        return Flux.fromArray(chunks)
                .index()
                .delayElements(Duration.ofMillis(100))  // Simulate streaming delay
                .map(tuple -> {
                    long index = tuple.getT1();
                    String chunk = tuple.getT2();
                    return RoundtableEvent.delta("topic-placeholder", agentId, seqStart + index, chunk + " ");
                })
                .concatWith(Flux.just(
                        RoundtableEvent.end("topic-placeholder", agentId, seqStart + chunks.length,
                                Map.of("bullet_summary", "分析完成"))
                ));
    }
}