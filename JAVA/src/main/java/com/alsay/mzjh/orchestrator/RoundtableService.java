package com.alsay.mzjh.orchestrator;

import com.alsay.mzjh.entity.AgentSeat;
import com.alsay.mzjh.prompt.SystemPromptBuilder;
import com.alsay.mzjh.protocol.ClientMessage;
import com.alsay.mzjh.protocol.ServerMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class RoundtableService {

    private final SeatResolver seatResolver;
    private final SimpMessagingTemplate wsTemplate;
    private final SystemPromptBuilder promptBuilder;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final ConcurrentHashMap<String, AtomicBoolean> cancellationTokens = new ConcurrentHashMap<>();
    private final Semaphore topicConcurrencyLimit = new Semaphore(10); // 限制最大10个并发topic

    @Value("${roundtable.single-seat-enabled:false}")
    private boolean singleSeatEnabled;

    // Metrics
    private final Counter topicStartedCounter;
    private final Counter topicCancelledCounter;
    private final Counter wsEventsSentCounter;
    private final Timer seatFirstTokenTimer;
    private final Timer seatTotalDurationTimer;

    private static final DateTimeFormatter TOPIC_FMT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public RoundtableService(SeatResolver seatResolver, SimpMessagingTemplate wsTemplate, SystemPromptBuilder promptBuilder, MeterRegistry meterRegistry) {
        this.seatResolver = seatResolver;
        this.wsTemplate = wsTemplate;
        this.promptBuilder = promptBuilder;

        // Initialize metrics
        this.topicStartedCounter = Counter.builder("roundtable_topics_started_total")
                .description("Total number of topics started")
                .register(meterRegistry);

        this.topicCancelledCounter = Counter.builder("roundtable_topics_cancelled_total")
                .description("Total number of topics cancelled")
                .register(meterRegistry);

        this.wsEventsSentCounter = Counter.builder("roundtable_ws_events_sent_total")
                .description("Total WebSocket events sent")
                .tag("event_type", "all")
                .register(meterRegistry);

        this.seatFirstTokenTimer = Timer.builder("roundtable_seat_first_token_duration")
                .description("Time to first token for each seat")
                .register(meterRegistry);

        this.seatTotalDurationTimer = Timer.builder("roundtable_seat_total_duration")
                .description("Total duration for each seat")
                .register(meterRegistry);
    }

    public void startDiscussion(ClientMessage msg) {
        String topicId = msg.getTopicId();
        String topic = msg.getTopic();

        if (topicId == null || topicId.isBlank() || topic == null || topic.isBlank()) {
            return;
        }

        // 检查并发限制
        if (!topicConcurrencyLimit.tryAcquire()) {
            // 发送并发限制错误到控制频道
            Map<String, Object> errorPayload = Map.of(
                "errorCode", "CONCURRENCY_LIMIT_EXCEEDED",
                "retryable", true,
                "message", "系统并发限制已达上限，请稍后重试"
            );
            wsTemplate.convertAndSend("/topic/roundtable/control",
                new ServerMessage(topicId, "system", "", true, "ERROR", 1, errorPayload));
            wsEventsSentCounter.increment();
            return;
        }

        cancellationTokens.put(topicId, new AtomicBoolean(false));
        topicStartedCounter.increment();

        // 发送主题准备就绪事件到控制频道
        Map<String, Object> readyPayload = Map.of(
            "topic", topic,
            "status", "READY"
        );
        wsTemplate.convertAndSend("/topic/roundtable/control",
            new ServerMessage(topicId, "system", "Topic ready for discussion", false, "TOPIC_READY", 1, readyPayload));
        wsEventsSentCounter.increment();

        List<AgentSeat> seats;
        try {
            seats = seatResolver.resolve(msg);
        } catch (Exception ex) {
            Map<String, Object> errorPayload = Map.of(
                "errorCode", "SEAT_RESOLUTION_FAILED",
                "retryable", false,
                "message", "席位解析失败: " + ex.getMessage()
            );
            wsTemplate.convertAndSend("/topic/roundtable/" + topicId,
                new ServerMessage(topicId, "system", "", true, "ERROR", 1, errorPayload));
            wsEventsSentCounter.increment();
            cleanupTopic(topicId);
            return;
        }

        for (AgentSeat seat : seats) {
            executor.submit(() -> streamOneSeat(topicId, seat, topic));
        }
    }

    public void cancelDiscussion(ClientMessage msg) {
        String topicId = msg.getTopicId();
        if (topicId == null || topicId.isBlank()) return;

        AtomicBoolean cancelled = cancellationTokens.get(topicId);
        if (cancelled != null) {
            cancelled.set(true);
            topicCancelledCounter.increment();
            // 发送取消确认消息到专属频道
            wsTemplate.convertAndSend("/topic/roundtable/" + topicId,
                new ServerMessage(topicId, "system", "Discussion cancelled", true, "CANCELLED", 1));
            wsEventsSentCounter.increment();
            cleanupTopic(topicId);
        }
    }

    /**
     * 单席位调试模式：仅启动一个智能体进行讨论
     * 用于 Prompt 打磨与联调，不破坏五席模板体系
     */
    public void startSingleDiscussion(ClientMessage msg) {
        // 检查单席位开关
        if (!singleSeatEnabled) {
            Map<String, Object> errorPayload = Map.of(
                "errorCode", "SINGLE_SEAT_DISABLED",
                "retryable", false,
                "message", "单席位调试模式未启用"
            );
            wsTemplate.convertAndSend("/topic/roundtable/control",
                new ServerMessage("", "system", "", true, "ERROR", 1, errorPayload));
            wsEventsSentCounter.increment();
            return;
        }

        String topic = msg.getTopic();
        if (topic == null || topic.isBlank()) {
            return;
        }

        // 生成 topicId
        String topicId = "single_" + LocalDateTime.now().format(TOPIC_FMT) + "_" + UUID.randomUUID().toString().substring(0, 8);

        // 检查并发限制
        if (!topicConcurrencyLimit.tryAcquire()) {
            Map<String, Object> errorPayload = Map.of(
                "errorCode", "CONCURRENCY_LIMIT_EXCEEDED",
                "retryable", true,
                "message", "系统并发限制已达上限，请稍后重试"
            );
            wsTemplate.convertAndSend("/topic/roundtable/control",
                new ServerMessage(topicId, "system", "", true, "ERROR", 1, errorPayload));
            wsEventsSentCounter.increment();
            return;
        }

        cancellationTokens.put(topicId, new AtomicBoolean(false));
        topicStartedCounter.increment();

        AgentSeat seat;
        try {
            seat = seatResolver.resolveSingle(msg);
        } catch (Exception ex) {
            Map<String, Object> errorPayload = Map.of(
                "errorCode", "SEAT_RESOLUTION_FAILED",
                "retryable", false,
                "message", "席位解析失败: " + ex.getMessage()
            );
            wsTemplate.convertAndSend("/topic/roundtable/" + topicId,
                new ServerMessage(topicId, "system", "", true, "ERROR", 1, errorPayload));
            wsEventsSentCounter.increment();
            cleanupTopic(topicId);
            return;
        }

        // 发送主题准备就绪事件（包含 topicId 供前端订阅）
        Map<String, Object> readyPayload = Map.of(
            "topic", topic.trim(),
            "topicId", topicId,
            "agentKey", seat.getAgentKey(),
            "mode", "single",
            "status", "READY"
        );
        wsTemplate.convertAndSend("/topic/roundtable/control",
            new ServerMessage(topicId, "system", "Single seat ready", false, "TOPIC_READY", 1, readyPayload));
        wsEventsSentCounter.increment();

        executor.submit(() -> streamOneSeat(topicId, seat, topic.trim()));
    }

    private void streamOneSeat(String topicId, AgentSeat seat, String topic) {
        AtomicLong seq = new AtomicLong(0);
        AtomicBoolean cancelled = cancellationTokens.get(topicId);

        // Send START event with seat metadata
        Map<String, Object> startPayload = Map.of(
            "codename", nvl(seat.getCodename(), seat.getDisplayName()),
            "role", nvl(seat.getRole(), "顾问"),
            "avatarUrl", nvl(seat.getAvatarUrl(), "")
        );
        send(topicId, seat.getId(), "", false, "START", seq.incrementAndGet(), startPayload);

        try {
            String systemPrompt = promptBuilder.build(seat);
            String content = mockOrCallLlm(seat, topic, systemPrompt);

            // 模拟发送内容，包含段落分隔
            String[] sections = content.split("\n");
            for (String section : sections) {
                if (cancelled != null && cancelled.get()) {
                    send(topicId, seat.getId(), "", true, "CANCELLED", seq.incrementAndGet());
                    return;
                }

                // 发送段落分隔符
                if (!section.trim().isEmpty()) {
                    Map<String, Object> sectionPayload = Map.of(
                        "sectionTitle", section.length() > 20 ? section.substring(0, 20) + "..." : section,
                        "sectionType", "analysis",
                        "content", section
                    );
                    send(topicId, seat.getId(), "", false, "SECTION", seq.incrementAndGet(), sectionPayload);
                }

                // 发送段落内容
                for (int i = 0; i < section.length(); i++) {
                    if (cancelled != null && cancelled.get()) {
                        send(topicId, seat.getId(), "", true, "CANCELLED", seq.incrementAndGet());
                        return;
                    }
                    send(topicId, seat.getId(), String.valueOf(section.charAt(i)), false, "DELTA", seq.incrementAndGet());
                    sleepSilently(15);
                }
            }

            send(topicId, seat.getId(), "", true, "END", seq.incrementAndGet());

        } catch (Exception ex) {
            Map<String, Object> errorPayload = Map.of(
                "errorCode", "LLM_TIMEOUT",
                "retryable", true,
                "message", "智能体响应异常: " + ex.getMessage()
            );
            send(topicId, seat.getId(), "", true, "ERROR", seq.incrementAndGet(), errorPayload);
        }
    }

    private void send(String topicId, String agentId, String content, boolean isFinish, String eventType, long seq) {
        send(topicId, agentId, content, isFinish, eventType, seq, null);
    }

    private void send(String topicId, String agentId, String content, boolean isFinish, String eventType, long seq, Map<String, Object> payload) {
        ServerMessage msg = new ServerMessage(topicId, agentId, content, isFinish, eventType, seq, payload);
        wsTemplate.convertAndSend("/topic/roundtable/" + topicId, msg);
        wsEventsSentCounter.increment();

        // 清理终态事件后的资源
        if (isFinish && ("END".equals(eventType) || "CANCELLED".equals(eventType) || "ERROR".equals(eventType))) {
            cleanupTopic(topicId);
        }
    }

    private void cleanupTopic(String topicId) {
        cancellationTokens.remove(topicId);
        topicConcurrencyLimit.release(); // 释放并发许可
        // 可以在这里添加其他清理逻辑，如停止相关任务等
    }

    private String mockOrCallLlm(AgentSeat seat, String topic, String systemPrompt) {
        // In mock mode, simulate different responses based on seat characteristics
        String codename = nvl(seat.getCodename(), seat.getDisplayName());
        String role = nvl(seat.getRole(), "顾问");
        String thinkingStyle = nvl(seat.getThinkingStyle(), "分析性");

        return String.format("""
                ### %s的分析视角

                **身份背景**: 作为%s，我从%s角度审视这个问题。

                **核心观点**: 这个问题需要系统性解决方案，不能头痛医头脚痛医脚。

                **关键洞察**: %s

                **建议方案**: 需要建立长期机制，而非临时抱佛脚。

                **潜在风险**: 执行过程中可能遇到资源不足和技术挑战。

                **行动建议**:
                1. 立即组建专项小组
                2. 制定详细执行计划
                3. 设立里程碑和监控机制
                4. 定期review和调整策略

                议题：%s
                """, codename, role, thinkingStyle, topic.substring(0, Math.min(50, topic.length())), topic);
    }

    private String nvl(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return "";
    }

    private void sleepSilently(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}