package com.alsay.mzjh.repository;

import com.alsay.mzjh.entity.AgentSeat;
import com.alsay.mzjh.entity.RoundtableTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoundtableTemplateRepository extends JpaRepository<RoundtableTemplate, String> {

    List<RoundtableTemplate> findByEnabledTrue();

    Optional<RoundtableTemplate> findByIdAndEnabledTrue(String id);

    @Query("SELECT a FROM AgentSeat a JOIN TemplateSeat ts ON a.id = ts.agentSeat.id " +
           "WHERE ts.template.id = :templateId AND a.enabled = true ORDER BY ts.seatOrder ASC")
    List<AgentSeat> findOrderedSeatsByTemplateId(@Param("templateId") String templateId);
}