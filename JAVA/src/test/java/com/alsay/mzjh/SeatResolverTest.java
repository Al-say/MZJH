package com.alsay.mzjh;

import com.alsay.mzjh.entity.AgentSeat;
import com.alsay.mzjh.orchestrator.SeatResolver;
import com.alsay.mzjh.protocol.ClientMessage;
import com.alsay.mzjh.repository.AgentSeatRepository;
import com.alsay.mzjh.repository.RoundtableTemplateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class SeatResolverTest {

    @Mock
    private AgentSeatRepository agentSeatRepository;

    @Mock
    private RoundtableTemplateRepository templateRepository;

    private SeatResolver seatResolver;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        seatResolver = new SeatResolver(agentSeatRepository, templateRepository);
    }

    @Test
    void testResolveWithAgentKeys() {
        // Given
        List<String> agentKeys = Arrays.asList("agent1", "agent2", "agent3", "agent4", "agent5");
        ClientMessage msg = new ClientMessage("START", "test topic", null, agentKeys);

        AgentSeat seat1 = new AgentSeat("agent1", "Agent One", "System prompt 1");
        AgentSeat seat2 = new AgentSeat("agent2", "Agent Two", "System prompt 2");
        AgentSeat seat3 = new AgentSeat("agent3", "Agent Three", "System prompt 3");
        AgentSeat seat4 = new AgentSeat("agent4", "Agent Four", "System prompt 4");
        AgentSeat seat5 = new AgentSeat("agent5", "Agent Five", "System prompt 5");

        when(agentSeatRepository.findEnabledByAgentKeys(agentKeys))
                .thenReturn(Arrays.asList(seat1, seat2, seat3, seat4, seat5));

        // When
        List<AgentSeat> result = seatResolver.resolve(msg);

        // Then
        assertEquals(5, result.size());
        assertEquals("agent1", result.get(0).getAgentKey());
        assertEquals("agent2", result.get(1).getAgentKey());
        assertEquals("agent3", result.get(2).getAgentKey());
        assertEquals("agent4", result.get(3).getAgentKey());
        assertEquals("agent5", result.get(4).getAgentKey());
    }

    @Test
    void testResolveWithInvalidAgentKeys() {
        // Given
        List<String> agentKeys = Arrays.asList("agent1", "agent2", "agent3", "agent4"); // Only 4 keys
        ClientMessage msg = new ClientMessage("START", "test topic", null, agentKeys);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            seatResolver.resolve(msg);
        });
        assertEquals("agentKeys 必须为 5 个", exception.getMessage());
    }
}