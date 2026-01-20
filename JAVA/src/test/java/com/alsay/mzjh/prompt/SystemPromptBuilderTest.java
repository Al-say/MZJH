package com.alsay.mzjh.prompt;

import com.alsay.mzjh.entity.AgentSeat;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SystemPromptBuilderTest {

    private final SystemPromptBuilder builder = new SystemPromptBuilder();

    @Test
    void testSystemPromptOverride() {
        // Given
        AgentSeat seat = new AgentSeat();
        seat.setSystemPrompt("Custom override prompt");

        // When
        String result = builder.build(seat);

        // Then
        assertEquals("Custom override prompt", result);
    }

    @Test
    void testStructuredFieldsBuild() {
        // Given
        AgentSeat seat = new AgentSeat();
        seat.setCodename("钢铁侠");
        seat.setRole("科技富豪");
        seat.setGoal("提供创新技术解决方案");
        seat.setThinkingStyle("前瞻性创新思维");
        seat.setReasoningLogic("基于技术趋势和市场需求的推理");
        seat.setPromptTemplateVersion(1);

        // When
        String result = builder.build(seat);

        // Then
        assertTrue(result.contains("钢铁侠"));
        assertTrue(result.contains("科技富豪"));
        assertTrue(result.contains("前瞻性创新思维"));
        assertTrue(result.contains("基于技术趋势和市场需求的推理"));
        assertTrue(result.contains("核心结论"));
        assertTrue(result.contains("分析依据"));
        assertTrue(result.contains("潜在风险"));
        assertTrue(result.contains("建议行动"));
    }

    @Test
    void testFallbackValues() {
        // Given
        AgentSeat seat = new AgentSeat();
        seat.setAgentKey("test-agent");
        seat.setDisplayName("测试智能体");
        // Leave other fields null

        // When
        String result = builder.build(seat);

        // Then
        assertTrue(result.contains("测试智能体")); // Should use displayName as codename
        assertTrue(result.contains("顾问")); // Default role
        assertTrue(result.contains("提供可执行、可验证的深度分析")); // Default goal
    }

    @Test
    void testLengthLimitEnforcement() {
        // Given
        AgentSeat seat = new AgentSeat();
        String longPrompt = "a".repeat(9000);
        seat.setSystemPrompt(longPrompt);

        // When
        String result = builder.build(seat);

        // Then
        assertTrue(result.length() <= 8000 + 100); // Allow some margin for truncation message
        assertTrue(result.contains("[提示词已截断"));
    }

    @Test
    void testTemplateVersioning() {
        // Given
        AgentSeat seat = new AgentSeat();
        seat.setCodename("测试");
        seat.setPromptTemplateVersion(1);

        // When
        String result = builder.build(seat);

        // Then
        assertTrue(result.contains("测试"));
        // For now, all versions use V1 template
    }
}