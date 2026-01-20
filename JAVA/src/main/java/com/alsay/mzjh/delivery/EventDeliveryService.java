package com.alsay.mzjh.delivery;

import com.alsay.mzjh.protocol.RoundtableEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * Service for delivering events to WebSocket clients with throttling.
 */
@Service
public class EventDeliveryService {

    private final SimpMessagingTemplate messagingTemplate;

    public EventDeliveryService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Deliver events for a topic with throttling to prevent overwhelming the client.
     * DELTA events are buffered in 50ms windows and merged, others are sent immediately.
     */
    public void deliverEvents(String topicId, Flux<RoundtableEvent> eventFlux) {
        eventFlux
                .groupBy(event -> event.agentId() + ":" + (event.eventType() == RoundtableEvent.EventType.DELTA ? "delta" : "other"))
                .flatMap(group -> {
                    String[] parts = group.key().split(":");
                    String agentId = parts[0];
                    boolean isDelta = "delta".equals(parts[1]);

                    if (isDelta) {
                        // Buffer DELTA events for 50ms and merge content
                        return group
                                .bufferTimeout(10, Duration.ofMillis(50))  // Buffer up to 10 events or 50ms
                                .filter(list -> !list.isEmpty())
                                .map(list -> {
                                    RoundtableEvent first = list.get(0);
                                    String mergedContent = list.stream()
                                            .map(RoundtableEvent::content)
                                            .filter(content -> content != null)
                                            .reduce("", String::concat);
                                    return new RoundtableEvent(first.topicId(), first.agentId(),
                                            first.eventType(), first.seq(), mergedContent, first.payload());
                                });
                    } else {
                        // Send other events immediately
                        return group;
                    }
                })
                .subscribe(event -> {
                    String destination = "/topic/roundtable/" + topicId;
                    messagingTemplate.convertAndSend(destination, event);
                });
    }
}