package com.alsay.mzjh.orchestrator;

import com.alsay.mzjh.llm.LlmAdapter;
import com.alsay.mzjh.protocol.RoundtableEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * Orchestrator for roundtable discussions.
 * Manages the flow of rounds and agent interactions.
 */
@Service
public class RoundtableOrchestrator {

    private final LlmAdapter llmAdapter;

    public RoundtableOrchestrator(LlmAdapter llmAdapter) {
        this.llmAdapter = llmAdapter;
    }

    /**
     * Start Round 0: Concurrent position papers.
     * All agents generate their initial positions simultaneously.
     */
    public Flux<RoundtableEvent> startRound0(String topicId, String topicDescription) {
        // Define agents with their roles and prompts
        List<AgentConfig> agents = List.of(
                new AgentConfig("agent-1", "经济学家", "作为经济学家，分析这个议题的经济影响..."),
                new AgentConfig("agent-2", "社会学家", "作为社会学家，分析这个议题的社会影响..."),
                new AgentConfig("agent-3", "技术专家", "作为技术专家，分析这个议题的技术可行性...")
        );

        // Start all agents concurrently
        return Flux.fromIterable(agents)
                .flatMap(agent -> startAgent(topicId, agent, topicDescription));
    }

    private Flux<RoundtableEvent> startAgent(String topicId, AgentConfig agent, String topicDescription) {
        // Create prompt for the agent
        String prompt = String.format("议题：%s\n\n你的角色：%s\n\n请提供你的立场和分析：\n%s",
                topicDescription, agent.role, agent.promptTemplate);

        // Send START event
        var startEvent = RoundtableEvent.start(topicId, agent.id, 0L,
                Map.of("role", agent.role, "round", "Round 0: 立场稿"));

        // Generate streaming response, adjusting seq
        var responseFlux = llmAdapter.generateStreaming(agent.id, prompt, 1L)
                .map(event -> new RoundtableEvent(topicId, event.agentId(), event.eventType(),
                        event.seq(), event.content(), event.payload()));

        return Flux.concat(
                Mono.just(startEvent),
                responseFlux
        );
    }

    private record AgentConfig(String id, String role, String promptTemplate) {}
}