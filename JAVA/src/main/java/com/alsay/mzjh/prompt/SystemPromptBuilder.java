package com.alsay.mzjh.prompt;

import com.alsay.mzjh.entity.AgentSeat;
import org.springframework.stereotype.Service;

@Service
public class SystemPromptBuilder {

    private static final int MAX_PROMPT_LENGTH = 8000; // Reasonable limit for LLM context

    public String build(AgentSeat seat) {
        // Priority: if systemPrompt is provided, use it directly as override
        if (seat.getSystemPrompt() != null && !seat.getSystemPrompt().isBlank()) {
            String prompt = seat.getSystemPrompt().trim();
            return enforceLengthLimit(prompt);
        }

        // Otherwise, build from structured fields
        int version = seat.getPromptTemplateVersion() == null ? 1 : seat.getPromptTemplateVersion();
        String prompt = switch (version) {
            case 1 -> buildV1(seat);
            default -> buildV1(seat);
        };

        return enforceLengthLimit(prompt);
    }

    private String buildV1(AgentSeat seat) {
        String codename = nvl(seat.getCodename(), seat.getDisplayName(), seat.getAgentKey());
        String role = nvl(seat.getRole(), "顾问");
        String goal = nvl(seat.getGoal(), "提供可执行、可验证的深度分析");
        String thinkingStyle = nvl(seat.getThinkingStyle(), "逻辑严密，直接指出关键矛盾和解决方案");
        String reasoningLogic = nvl(seat.getReasoningLogic(), "基于事实给出结论、依据、风险评估和下一步行动建议");

        return """
                # 角色设定 (Role Definition)

                你是圆桌会议席位成员。你的代号是「%s」，身份是「%s」。

                ## 任务目标 (Task Objective)

                针对用户提出的议题提供深度分析，你的输出必须可执行且可验证。

                ### 具体目标：
                %s

                ## 思考方式 (Thinking Style)

                %s

                ## 推理逻辑 (Reasoning Logic)

                %s

                ## 输出约束 (Output Constraints)

                1. 使用 Markdown 格式组织内容，便于阅读和解析。
                2. 直接切入核心问题，避免冗余的寒暄和客套。
                3. 必须包含以下四个核心要素：
                   - **结论**：清晰明确的观点或判断
                   - **依据**：支撑结论的事实、数据或逻辑推理
                   - **风险**：潜在问题、局限性或副作用
                   - **行动项**：具体可执行的下一步建议
                4. 若信息不足以给出完整分析，请明确列出你需要的关键数据，不要编造或假设。
                5. 保持专业性和建设性，避免情绪化表达。

                ## 响应格式 (Response Format)

                请按照以下结构组织你的回答：

                ### 核心结论
                [你的主要观点]

                ### 分析依据
                [支撑性事实和逻辑]

                ### 潜在风险
                [需要注意的问题点]

                ### 建议行动
                [具体可执行的步骤]
                """.formatted(codename, role, goal, thinkingStyle, reasoningLogic);
    }

    private String nvl(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return "";
    }

    private String enforceLengthLimit(String prompt) {
        if (prompt.length() > MAX_PROMPT_LENGTH) {
            // Truncate at word boundary if possible
            String truncated = prompt.substring(0, MAX_PROMPT_LENGTH);
            int lastSpace = truncated.lastIndexOf(' ');
            if (lastSpace > MAX_PROMPT_LENGTH * 0.8) {
                truncated = truncated.substring(0, lastSpace);
            }
            return truncated + "\n\n[提示词已截断以符合长度限制]";
        }
        return prompt;
    }
}