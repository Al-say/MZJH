package com.alsay.mzjh.orchestrator;

import com.alsay.mzjh.entity.AgentSeat;
import com.alsay.mzjh.entity.RoundtableTemplate;
import com.alsay.mzjh.protocol.ClientMessage;
import com.alsay.mzjh.repository.AgentSeatRepository;
import com.alsay.mzjh.repository.RoundtableTemplateRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SeatResolver {

    private final AgentSeatRepository seatRepo;
    private final RoundtableTemplateRepository templateRepo;

    public SeatResolver(AgentSeatRepository seatRepo, RoundtableTemplateRepository templateRepo) {
        this.seatRepo = seatRepo;
        this.templateRepo = templateRepo;
    }

    public List<AgentSeat> resolve(ClientMessage msg) {
        if (msg.getAgentKeys() != null && !msg.getAgentKeys().isEmpty()) {
            if (msg.getAgentKeys().size() != 5) {
                throw new IllegalArgumentException("agentKeys 必须为 5 个");
            }
            List<AgentSeat> seats = seatRepo.findEnabledByAgentKeys(msg.getAgentKeys());
            if (seats.size() != 5) {
                throw new IllegalArgumentException("agentKeys 存在无效或未启用席位");
            }
            return orderByInputKeys(seats, msg.getAgentKeys());
        }

        if (msg.getTemplateId() == null || msg.getTemplateId().isBlank()) {
            throw new IllegalArgumentException("templateId 或 agentKeys 必须提供一个");
        }

        RoundtableTemplate t = templateRepo.findByIdAndEnabledTrue(msg.getTemplateId())
                .orElseThrow(() -> new IllegalArgumentException("模板不存在或未启用"));

        List<AgentSeat> seats = templateRepo.findOrderedSeatsByTemplateId(t.getId());
        if (seats.size() != 5) {
            throw new IllegalArgumentException("模板席位必须为 5 个且全部启用");
        }
        return seats;
    }

    private List<AgentSeat> orderByInputKeys(List<AgentSeat> seats, List<String> keys) {
        Map<String, AgentSeat> map = seats.stream().collect(Collectors.toMap(AgentSeat::getAgentKey, s -> s));
        List<AgentSeat> ordered = new ArrayList<>();
        for (String k : keys) ordered.add(map.get(k));
        return ordered;
    }

    /**
     * 单席位调试模式：解析单个 agentKey 对应的席位
     */
    public AgentSeat resolveSingle(ClientMessage msg) {
        String agentKey = msg.getAgentKey();
        if (agentKey == null || agentKey.isBlank()) {
            throw new IllegalArgumentException("agentKey 不能为空");
        }
        return seatRepo.findByAgentKeyAndEnabledTrue(agentKey.trim())
                .orElseThrow(() -> new IllegalArgumentException("席位不存在或未启用: " + agentKey));
    }
}