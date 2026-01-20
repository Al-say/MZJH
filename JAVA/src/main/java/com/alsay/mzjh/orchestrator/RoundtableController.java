package com.alsay.mzjh.orchestrator;

import com.alsay.mzjh.protocol.ClientMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class RoundtableController {

    private final RoundtableService roundtableService;

    public RoundtableController(RoundtableService roundtableService) {
        this.roundtableService = roundtableService;
    }

    @MessageMapping("/startDiscussion")
    public void startDiscussion(ClientMessage clientMessage) {
        String topic = clientMessage.getTopic();
        if (topic == null || topic.isBlank()) {
            return;
        }
        roundtableService.startDiscussion(topic.trim());
    }
}