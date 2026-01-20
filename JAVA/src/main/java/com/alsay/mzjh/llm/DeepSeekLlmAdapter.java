package com.alsay.mzjh.llm;

import com.alsay.mzjh.protocol.RoundtableEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * DeepSeek LLM Adapter implementation using Spring AI OpenAI client.
 * DeepSeek API is OpenAI-compatible, so we use the OpenAI client with custom base-url.
 */
@Component
@Primary
@ConditionalOnProperty(name = "roundtable.llm.mode", havingValue = "deepseek")
public class DeepSeekLlmAdapter implements LlmAdapter {

    private static final Logger log = LoggerFactory.getLogger(DeepSeekLlmAdapter.class);

    private final OpenAiChatClient chatClient;

    @Value("${spring.ai.openai.chat.options.model:deepseek-chat}")
    private String model;

    public DeepSeekLlmAdapter(OpenAiChatClient chatClient) {
        this.chatClient = chatClient;
        log.info("DeepSeekLlmAdapter 已初始化");
    }

    @Override
    public Flux<RoundtableEvent> generateStreaming(String agentId, String prompt, long seqStart) {
        AtomicLong seq = new AtomicLong(seqStart);
        StringBuilder contentBuilder = new StringBuilder();

        log.debug("开始调用 DeepSeek API - agentId: {}, prompt 长度: {}", agentId, prompt.length());

        Prompt aiPrompt = new Prompt(List.of(new UserMessage(prompt)));

        return chatClient.stream(aiPrompt)
                .doOnSubscribe(s -> log.debug("DeepSeek 流式响应开始"))
                .map(response -> {
                    String chunk = response.getResult() != null && response.getResult().getOutput() != null
                            ? response.getResult().getOutput().getContent()
                            : "";
                    if (chunk != null && !chunk.isEmpty()) {
                        contentBuilder.append(chunk);
                    }
                    return RoundtableEvent.delta("topic-placeholder", agentId, seq.incrementAndGet(), chunk != null ? chunk : "");
                })
                .filter(event -> event.content() != null && !event.content().isEmpty())
                .doOnComplete(() -> log.info("DeepSeek 流式响应完成 - agentId: {}, 总长度: {}", agentId, contentBuilder.length()))
                .concatWith(Flux.defer(() -> {
                    String summary = generateBulletSummary(contentBuilder.toString());
                    return Flux.just(RoundtableEvent.end(
                            "topic-placeholder",
                            agentId,
                            seq.incrementAndGet(),
                            Map.of("bullet_summary", summary, "total_length", contentBuilder.length())
                    ));
                }))
                .onErrorResume(e -> {
                    log.error("DeepSeek API 调用失败 - agentId: {}, 错误: {}", agentId, e.getMessage(), e);
                    return Flux.just(RoundtableEvent.error(
                            "topic-placeholder",
                            agentId,
                            seq.incrementAndGet(),
                            "LLM_ERROR",
                            "DeepSeek API 调用失败: " + e.getMessage()
                    ));
                })
                .timeout(java.time.Duration.ofSeconds(60));
    }

    /**
     * 从完整响应中提取要点摘要（简单实现）
     */
    private String generateBulletSummary(String content) {
        if (content == null || content.length() < 50) {
            return "分析完成";
        }
        // 简单截取前100字作为摘要
        return content.length() > 100 ? content.substring(0, 100) + "..." : content;
    }
}
