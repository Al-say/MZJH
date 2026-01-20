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
        roundtableService.startDiscussion(clientMessage);
    }

    @MessageMapping("/startSingleDiscussion")
    public void startSingleDiscussion(ClientMessage clientMessage) {
        roundtableService.startSingleDiscussion(clientMessage);
    }

    @MessageMapping("/cancelDiscussion")
    public void cancelDiscussion(ClientMessage clientMessage) {
        if ("CANCEL".equals(clientMessage.getAction()) && clientMessage.getTopicId() != null) {
            roundtableService.cancelDiscussion(clientMessage);
        }
    }
}