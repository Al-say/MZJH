package com.alsay.mzjh.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "agent_seats")
public class AgentSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank(message = "Agent key cannot be blank")
    @Size(max = 64, message = "Agent key must be less than 64 characters")
    @Column(name = "agent_key", unique = true, nullable = false, length = 64)
    private String agentKey;

    @NotBlank(message = "Display name cannot be blank")
    @Size(max = 64, message = "Display name must be less than 64 characters")
    @Column(name = "display_name", nullable = false, length = 64)
    private String displayName;

    @Size(max = 2000, message = "System prompt must be less than 2000 characters")
    @Column(name = "system_prompt", columnDefinition = "TEXT")
    private String systemPrompt;

    @Size(max = 64)
    @Column(name = "codename", length = 64)
    private String codename;

    @Size(max = 128)
    @Column(name = "role", length = 128)
    private String role;

    @Size(max = 4000)
    @Column(name = "goal", columnDefinition = "TEXT")
    private String goal;

    @Size(max = 256)
    @Column(name = "thinking_style", length = 256)
    private String thinkingStyle;

    @Size(max = 256)
    @Column(name = "reasoning_logic", length = 256)
    private String reasoningLogic;

    @Column(name = "avatar_url", columnDefinition = "TEXT")
    private String avatarUrl;

    @Column(name = "prompt_template_version", nullable = false)
    private Integer promptTemplateVersion = 1;

    @Column(name = "model_provider", length = 32)
    private String modelProvider;

    @Column(name = "model_name", length = 64)
    private String modelName;

    @Column(name = "temperature", precision = 3, scale = 2)
    private BigDecimal temperature;

    @Column(name = "max_tokens")
    private Integer maxTokens;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public AgentSeat() {}

    public AgentSeat(String agentKey, String displayName, String systemPrompt) {
        this.agentKey = agentKey;
        this.displayName = displayName;
        this.systemPrompt = systemPrompt;
        this.enabled = true;
        this.promptTemplateVersion = 1;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAgentKey() { return agentKey; }
    public void setAgentKey(String agentKey) { this.agentKey = agentKey; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getSystemPrompt() { return systemPrompt; }
    public void setSystemPrompt(String systemPrompt) { this.systemPrompt = systemPrompt; }

    public String getCodename() { return codename; }
    public void setCodename(String codename) { this.codename = codename; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }

    public String getThinkingStyle() { return thinkingStyle; }
    public void setThinkingStyle(String thinkingStyle) { this.thinkingStyle = thinkingStyle; }

    public String getReasoningLogic() { return reasoningLogic; }
    public void setReasoningLogic(String reasoningLogic) { this.reasoningLogic = reasoningLogic; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public Integer getPromptTemplateVersion() { return promptTemplateVersion; }
    public void setPromptTemplateVersion(Integer promptTemplateVersion) { this.promptTemplateVersion = promptTemplateVersion; }

    public String getModelProvider() { return modelProvider; }
    public void setModelProvider(String modelProvider) { this.modelProvider = modelProvider; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public BigDecimal getTemperature() { return temperature; }
    public void setTemperature(BigDecimal temperature) { this.temperature = temperature; }

    public Integer getMaxTokens() { return maxTokens; }
    public void setMaxTokens(Integer maxTokens) { this.maxTokens = maxTokens; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}