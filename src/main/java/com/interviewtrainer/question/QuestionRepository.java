package com.interviewtrainer.question;

import com.interviewtrainer.session.PracticeSession;
import com.interviewtrainer.topic.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    /** Earlier questions for a topic and level; fed to the LLM so it does not repeat them. */
    List<Question> findByTopicAndLevelOrderByCreatedAtAsc(Topic topic, Level level);

    List<Question> findByParentOrderByCreatedAtAsc(Question parent);

    /** The questions of a practice session in the order they were asked: the backbone of the chat view. */
    List<Question> findBySessionOrderByPositionAsc(PracticeSession session);

    int countBySession(PracticeSession session);
}
