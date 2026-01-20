package com.alsay.mzjh.orchestrator;

import com.alsay.mzjh.protocol.ServerMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class RoundtableService {

    private final SimpMessagingTemplate wsTemplate;
    private final ExecutorService vthreadExecutor = Executors.newCachedThreadPool();

    private static final DateTimeFormatter TOPIC_FMT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private final Map<String, String> agents = Map.of(
            "agent_ceo", "你是 CEO，关注结果、增长与效率，表达简洁直接。",
            "agent_cto", "你是 CTO，关注技术可行性、风险与演进路径。",
            "agent_pm", "你是 PM，关注用户价值、范围控制与交付节奏。",
            "agent_cfo", "你是 CFO，关注成本、现金流、风险暴露。"
    );

    public RoundtableService(SimpMessagingTemplate wsTemplate) {
        this.wsTemplate = wsTemplate;
    }

    public void startDiscussion(String userTopic) {
        String topicId = "t_" + LocalDateTime.now().format(TOPIC_FMT);

        agents.forEach((agentId, persona) ->
                vthreadExecutor.submit(() -> streamAgent(topicId, agentId, persona, userTopic))
        );
    }

    private void streamAgent(String topicId, String agentId, String persona, String userTopic) {
        AtomicLong seq = new AtomicLong(0);

        send(topicId, agentId, "", false, "START", seq.incrementAndGet());

        String full = mockGenerate(agentId, persona, userTopic);

        // 最小可用：按字符流式推送
        for (int i = 0; i < full.length(); i++) {
            String delta = String.valueOf(full.charAt(i));
            send(topicId, agentId, delta, false, "DELTA", seq.incrementAndGet());

            // 模拟打字机节奏
            sleepSilently(15);
        }

        send(topicId, agentId, "", true, "END", seq.incrementAndGet());
    }

    private void send(String topicId, String agentId, String content, boolean isFinish, String eventType, long seq) {
        ServerMessage msg = new ServerMessage(topicId, agentId, content, isFinish, eventType, seq);
        wsTemplate.convertAndSend("/topic/roundtable", msg);
    }

    private String mockGenerate(String agentId, String persona, String topic) {
        return switch (agentId) {
            case "agent_ceo" -> """
                    先定义目标：裁员是否能带来现金流改善与执行效率提升。
                    核心判断：业务增长曲线、单位经济模型、组织冗余程度。
                    立即行动：冻结非核心项目，按业务线做产出审计，给出两套方案与时间表。
                    """;
            case "agent_cto" -> """
                    先看系统性风险：裁员会引发技术债失控与关键系统无人负责的单点故障。
                    需要一张能力地图：服务清单、Owner、值班与故障演练覆盖率。
                    建议优先做范围收缩与架构减法，再评估人力缺口与必要裁撤。
                    """;
            case "agent_pm" -> """
                    先把问题转成范围与节奏：哪些功能能停，哪些必须继续维护。
                    给出两周内可落地的路线：砍掉低价值需求，保留关键用户路径与合规项。
                    裁员属于组织层决策，需要与产品策略同步发布，避免用户信任受损。
                    """;
            case "agent_cfo" -> """
                    先算账：现金流 runway 还有多久，固定成本占比多少，裁员一次性成本是多少。
                    裁员短期能降成本，长期会放大交付风险与收入波动。
                    我需要三张表：人力成本结构，项目 ROI，未来 3 到 6 个月收款预测。
                    """;
            default -> persona + "\n" + "议题：" + topic;
        };
    }

    private void sleepSilently(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}