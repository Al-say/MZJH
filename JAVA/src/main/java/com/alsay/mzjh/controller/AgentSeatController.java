package com.alsay.mzjh.controller;

import com.alsay.mzjh.entity.AgentSeat;
import com.alsay.mzjh.repository.AgentSeatRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/agent-seats")
public class AgentSeatController {

    private final AgentSeatRepository agentSeatRepository;

    public AgentSeatController(AgentSeatRepository agentSeatRepository) {
        this.agentSeatRepository = agentSeatRepository;
    }

    @GetMapping
    public ResponseEntity<List<AgentSeat>> getAllEnabledSeats() {
        List<AgentSeat> seats = agentSeatRepository.findByEnabledTrue();
        return ResponseEntity.ok(seats);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgentSeat> getSeatById(@PathVariable String id) {
        Optional<AgentSeat> seat = agentSeatRepository.findById(id);
        return seat.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AgentSeat> createSeat(@Valid @RequestBody AgentSeat seat) {
        if (agentSeatRepository.existsByAgentKey(seat.getAgentKey())) {
            return ResponseEntity.badRequest().build(); // Agent key already exists
        }
        AgentSeat savedSeat = agentSeatRepository.save(seat);
        return ResponseEntity.ok(savedSeat);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgentSeat> updateSeat(@PathVariable String id, @Valid @RequestBody AgentSeat seatDetails) {
        Optional<AgentSeat> optionalSeat = agentSeatRepository.findById(id);
        if (optionalSeat.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        AgentSeat seat = optionalSeat.get();

        // Check if agentKey is being changed and if it conflicts
        if (!seat.getAgentKey().equals(seatDetails.getAgentKey()) &&
            agentSeatRepository.existsByAgentKey(seatDetails.getAgentKey())) {
            return ResponseEntity.badRequest().build(); // New agent key already exists
        }

        seat.setAgentKey(seatDetails.getAgentKey());
        seat.setDisplayName(seatDetails.getDisplayName());
        seat.setSystemPrompt(seatDetails.getSystemPrompt());
        seat.setModelProvider(seatDetails.getModelProvider());
        seat.setModelName(seatDetails.getModelName());
        seat.setTemperature(seatDetails.getTemperature());
        seat.setMaxTokens(seatDetails.getMaxTokens());
        seat.setEnabled(seatDetails.getEnabled());

        AgentSeat updatedSeat = agentSeatRepository.save(seat);
        return ResponseEntity.ok(updatedSeat);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeat(@PathVariable String id) {
        Optional<AgentSeat> optionalSeat = agentSeatRepository.findById(id);
        if (optionalSeat.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        AgentSeat seat = optionalSeat.get();
        seat.setEnabled(false); // Soft delete
        agentSeatRepository.save(seat);
        return ResponseEntity.noContent().build();
    }
}