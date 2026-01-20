package com.alsay.mzjh.repository;

import com.alsay.mzjh.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository extends JpaRepository<Topic, String> {
}