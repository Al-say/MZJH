package com.alsay.mzjh.controller;

import com.alsay.mzjh.entity.Topic;
import com.alsay.mzjh.repository.TopicRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/topics")
public class TopicController {

    private final TopicRepository topicRepository;

    public TopicController(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    @PostMapping
    public ResponseEntity<TopicResponse> createTopic(@Valid @RequestBody TopicRequest request) {
        Topic topic = new Topic(request.getTopic());
        Topic savedTopic = topicRepository.save(topic);

        TopicResponse response = new TopicResponse(savedTopic.getId(), savedTopic.getCreatedAt());
        return ResponseEntity.ok(response);
    }

    public static class TopicRequest {
        @NotBlank(message = "Topic content cannot be blank")
        private String topic;

        public TopicRequest() {}

        public TopicRequest(String topic) {
            this.topic = topic;
        }

        public String getTopic() { return topic; }
        public void setTopic(String topic) { this.topic = topic; }
    }

    public static class TopicResponse {
        private String topicId;
        private LocalDateTime createdAt;

        public TopicResponse() {}

        public TopicResponse(String topicId, LocalDateTime createdAt) {
            this.topicId = topicId;
            this.createdAt = createdAt;
        }

        public String getTopicId() { return topicId; }
        public void setTopicId(String topicId) { this.topicId = topicId; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }
}