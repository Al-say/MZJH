package com.alsay.mzjh.controller;

import com.alsay.mzjh.entity.RoundtableTemplate;
import com.alsay.mzjh.repository.RoundtableTemplateRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/templates")
public class RoundtableTemplateController {

    private final RoundtableTemplateRepository templateRepository;

    public RoundtableTemplateController(RoundtableTemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    @GetMapping
    public ResponseEntity<List<RoundtableTemplate>> getAllEnabledTemplates() {
        List<RoundtableTemplate> templates = templateRepository.findByEnabledTrue();
        return ResponseEntity.ok(templates);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoundtableTemplate> getTemplateById(@PathVariable String id) {
        Optional<RoundtableTemplate> template = templateRepository.findById(id);
        return template.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RoundtableTemplate> createTemplate(@Valid @RequestBody RoundtableTemplate template) {
        RoundtableTemplate savedTemplate = templateRepository.save(template);
        return ResponseEntity.ok(savedTemplate);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoundtableTemplate> updateTemplate(@PathVariable String id, @Valid @RequestBody RoundtableTemplate templateDetails) {
        Optional<RoundtableTemplate> optionalTemplate = templateRepository.findById(id);
        if (optionalTemplate.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        RoundtableTemplate template = optionalTemplate.get();
        template.setName(templateDetails.getName());
        template.setEnabled(templateDetails.getEnabled());
        template.setTemplateSeats(templateDetails.getTemplateSeats());

        RoundtableTemplate updatedTemplate = templateRepository.save(template);
        return ResponseEntity.ok(updatedTemplate);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable String id) {
        Optional<RoundtableTemplate> optionalTemplate = templateRepository.findById(id);
        if (optionalTemplate.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        RoundtableTemplate template = optionalTemplate.get();
        template.setEnabled(false); // Soft delete
        templateRepository.save(template);
        return ResponseEntity.noContent().build();
    }
}