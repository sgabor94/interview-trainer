package com.interviewtrainer.topic;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Long> {

    Optional<Topic> findByParentIsNullAndNormalizedName(String normalizedName);

    Optional<Topic> findByParentAndNormalizedName(Topic parent, String normalizedName);

    List<Topic> findByParentIsNullOrderByName();

    List<Topic> findByParentOrderByName(Topic parent);
}
