package com.alsay.mzjh.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "template_seats")
public class TemplateSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private RoundtableTemplate template;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_seat_id", nullable = false)
    private AgentSeat agentSeat;

    @Column(name = "seat_order", nullable = false)
    private Integer seatOrder;

    // Constructors
    public TemplateSeat() {}

    public TemplateSeat(RoundtableTemplate template, AgentSeat agentSeat, Integer seatOrder) {
        this.template = template;
        this.agentSeat = agentSeat;
        this.seatOrder = seatOrder;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public RoundtableTemplate getTemplate() { return template; }
    public void setTemplate(RoundtableTemplate template) { this.template = template; }

    public AgentSeat getAgentSeat() { return agentSeat; }
    public void setAgentSeat(AgentSeat agentSeat) { this.agentSeat = agentSeat; }

    public Integer getSeatOrder() { return seatOrder; }
    public void setSeatOrder(Integer seatOrder) { this.seatOrder = seatOrder; }
}