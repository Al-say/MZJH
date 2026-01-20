package com.alsay.mzjh.repository;

import com.alsay.mzjh.entity.AgentSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgentSeatRepository extends JpaRepository<AgentSeat, String> {

    Optional<AgentSeat> findByAgentKey(String agentKey);

    Optional<AgentSeat> findByAgentKeyAndEnabledTrue(String agentKey);

    List<AgentSeat> findByEnabledTrue();

    @Query("SELECT a FROM AgentSeat a WHERE a.agentKey IN :keys AND a.enabled = true")
    List<AgentSeat> findEnabledByAgentKeys(@Param("keys") List<String> keys);

    boolean existsByAgentKey(String agentKey);
}