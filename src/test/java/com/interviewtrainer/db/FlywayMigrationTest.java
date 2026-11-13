package com.interviewtrainer.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Checks that the Flyway migrations build the schema and that its constraints guard the data. */
@DataJpaTest
class FlywayMigrationTest {

    @Autowired
    JdbcTemplate jdbc;

    @Test
    void allMigrationsAreApplied() {
        List<String> applied = jdbc.queryForList(
                // version is null for Flyway's own "schema created" marker row
                "select version from flyway_schema_history where success = true and version is not null "
                        + "order by installed_rank",
                String.class);

        assertThat(applied).containsExactly("1");
    }

    @Test
    void levelMustBeAKnownValue() {
        long topicId = insertTopic();

        assertThatThrownBy(() -> jdbc.update(
                "insert into question (id, topic_id, level, text, created_at) values (1, ?, 'EXPERT', 'Q?', current_timestamp)",
                topicId))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void scoreMustBeBetweenZeroAndTen() {
        long topicId = insertTopic();
        jdbc.update("insert into question (id, topic_id, level, text, created_at) values (1, ?, 'MID', 'Q?', current_timestamp)",
                topicId);
        jdbc.update("insert into answer (id, question_id, text, answered_at) values (1, 1, 'A', current_timestamp)");

        assertThatThrownBy(() -> jdbc.update(
                "insert into evaluation (id, answer_id, score, feedback, llm_model, created_at) "
                        + "values (1, 1, 11, 'f', 'm', current_timestamp)"))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    private long insertTopic() {
        jdbc.update("insert into topic (id, name, normalized_name, created_at) values (1, 'Kafka', 'kafka', current_timestamp)");
        return 1L;
    }
}
