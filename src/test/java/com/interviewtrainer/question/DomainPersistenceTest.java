package com.interviewtrainer.question;

import com.interviewtrainer.answer.Answer;
import com.interviewtrainer.answer.AnswerRepository;
import com.interviewtrainer.evaluation.Evaluation;
import com.interviewtrainer.evaluation.EvaluationRepository;
import com.interviewtrainer.session.PracticeSession;
import com.interviewtrainer.session.PracticeSessionRepository;
import com.interviewtrainer.topic.Topic;
import com.interviewtrainer.topic.TopicRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

@DataJpaTest
class DomainPersistenceTest {

    @Autowired
    TestEntityManager em;

    @Autowired
    TopicRepository topics;

    @Autowired
    QuestionRepository questions;

    @Autowired
    AnswerRepository answers;

    @Autowired
    EvaluationRepository evaluations;

    @Autowired
    PracticeSessionRepository sessions;

    @Test
    void questionFollowUpAnswerAndEvaluationRoundTrip() {
        Topic kafka = topics.save(new Topic("Kafka", null));
        Question question = questions.save(new Question(kafka, Level.SENIOR,
                "How does Kafka guarantee ordering?", List.of("per-partition ordering", "message keys")));
        Question followUp = questions.save(Question.followUp(question, null,
                "What happens to ordering when retries are enabled?", List.of("max.in.flight.requests")));
        Answer answer = answers.save(new Answer(question, "Ordering is guaranteed within a partition."));
        evaluations.save(new Evaluation(answer, 6, "Correct but shallow.",
                List.of("mentions partitions"), List.of("message keys", "idempotent producer"),
                "Kafka keeps order per partition...", "claude-opus-5-5"));
        em.flush();
        em.clear();

        Question loaded = questions.findById(question.getId()).orElseThrow();
        assertThat(loaded.getKeyPoints()).containsExactly("per-partition ordering", "message keys");
        assertThat(loaded.getCreatedAt()).isNotNull();
        assertThat(questions.findByTopicAndLevelOrderByCreatedAtAsc(loaded.getTopic(), Level.SENIOR))
                .hasSize(2);

        List<Question> followUps = questions.findByParentOrderByCreatedAtAsc(loaded);
        assertThat(followUps).singleElement().satisfies(f -> {
            assertThat(f.getId()).isEqualTo(followUp.getId());
            assertThat(f.isFollowUp()).isTrue();
            assertThat(f.getLevel()).isEqualTo(Level.SENIOR);
        });

        Answer loadedAnswer = answers.findByQuestionOrderByAnsweredAtDesc(loaded).getFirst();
        Evaluation evaluation = evaluations.findByAnswer(loadedAnswer).orElseThrow();
        assertThat(evaluation.getScore()).isEqualTo(6);
        assertThat(evaluation.getStrengths()).containsExactly("mentions partitions");
        assertThat(evaluation.getMissingPoints()).containsExactly("message keys", "idempotent producer");
        assertThat(evaluation.getLlmModel()).isEqualTo("claude-opus-5-5");
    }

    @Test
    void sessionMixesTopicsAndKeepsQuestionOrder() {
        Topic kafka = topics.save(new Topic("Kafka", null));
        Topic springSecurity = topics.save(new Topic("Security", topics.save(new Topic("Spring Boot", null))));
        PracticeSession session = sessions.save(new PracticeSession());

        Question first = questions.save(new Question(session, 1, kafka, Level.MID,
                "What is a consumer group?", List.of("partition assignment")));
        questions.save(Question.followUp(first, 2, "What triggers a rebalance?", List.of("member join/leave")));
        questions.save(new Question(session, 3, springSecurity, Level.SENIOR,
                "How does the SecurityFilterChain work?", List.of("filter order")));
        em.flush();
        em.clear();

        PracticeSession loaded = sessions.findById(session.getId()).orElseThrow();
        List<Question> chat = questions.findBySessionOrderByPositionAsc(loaded);
        assertThat(chat).extracting(Question::getPosition).containsExactly(1, 2, 3);
        assertThat(chat).extracting(q -> q.getTopic().getName()).containsExactly("Kafka", "Kafka", "Security");
        assertThat(chat.get(1).isFollowUp()).isTrue();
        assertThat(chat.get(1).getSession().getId()).isEqualTo(session.getId());
        assertThat(questions.countBySession(loaded)).isEqualTo(3);
        assertThat(sessions.findTop20ByOrderByStartedAtDesc()).singleElement()
                .satisfies(s -> assertThat(s.getStartedAt()).isNotNull());
    }

    @Test
    void invalidValuesAreRejected() {
        Topic java = topics.save(new Topic("Java", null));
        Question question = new Question(java, Level.JUNIOR, "What is the JVM?", List.of());
        Answer answer = new Answer(question, "A virtual machine.");

        assertThatIllegalArgumentException().isThrownBy(() -> new Question(java, Level.MID, " ", List.of()));
        assertThatIllegalArgumentException().isThrownBy(
                () -> new Question(new PracticeSession(), 0, java, Level.MID, "Q?", List.of()));
        assertThatIllegalArgumentException().isThrownBy(() -> new Answer(question, ""));
        assertThatIllegalArgumentException().isThrownBy(
                () -> new Evaluation(answer, 11, "x", List.of(), List.of(), null, "claude-opus-5-5"));
    }
}
