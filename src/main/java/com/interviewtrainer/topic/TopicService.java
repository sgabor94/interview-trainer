package com.interviewtrainer.topic;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Turns the user's free-text topic (and optional subtopic) into a stored {@link Topic},
 * reusing an existing one when the names match case- and whitespace-insensitively.
 */
@Service
public class TopicService {

    private final TopicRepository topics;

    public TopicService(TopicRepository topics) {
        this.topics = topics;
    }

    @Transactional
    public Topic resolve(String topicName, String subtopicName) {
        Topic root = topics.findByParentIsNullAndNormalizedName(TopicNames.normalize(topicName))
                .orElseGet(() -> topics.save(new Topic(topicName, null)));

        if (subtopicName == null || subtopicName.isBlank()) {
            return root;
        }
        return topics.findByParentAndNormalizedName(root, TopicNames.normalize(subtopicName))
                .orElseGet(() -> topics.save(new Topic(subtopicName, root)));
    }
}
