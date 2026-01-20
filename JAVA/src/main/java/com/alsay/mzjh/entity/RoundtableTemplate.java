package com.alsay.mzjh.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "roundtable_templates")
public class RoundtableTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank(message = "Template name cannot be blank")
    @Size(max = 64, message = "Template name must be less than 64 characters")
    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("seatOrder ASC")
    private List<TemplateSeat> templateSeats;

    // Constructors
    public RoundtableTemplate() {}

    public RoundtableTemplate(String name) {
        this.name = name;
        this.enabled = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<TemplateSeat> getTemplateSeats() { return templateSeats; }
    public void setTemplateSeats(List<TemplateSeat> templateSeats) { this.templateSeats = templateSeats; }

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