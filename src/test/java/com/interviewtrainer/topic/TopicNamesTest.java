package com.interviewtrainer.topic;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class TopicNamesTest {

    @Test
    void cleanTrimsAndCollapsesWhitespaceButKeepsCasing() {
        assertThat(TopicNames.clean("  Spring \t  Boot \n")).isEqualTo("Spring Boot");
    }

    @Test
    void normalizeIsCaseInsensitive() {
        assertThat(TopicNames.normalize(" KAFKA  Streams ")).isEqualTo("kafka streams");
        assertThat(TopicNames.normalize("Kafka Streams")).isEqualTo(TopicNames.normalize("kafka   streams"));
    }

    @Test
    void rejectsBlankAndTooLongNames() {
        assertThatIllegalArgumentException().isThrownBy(() -> TopicNames.normalize("   "));
        assertThatIllegalArgumentException().isThrownBy(() -> TopicNames.normalize(null));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> TopicNames.normalize("x".repeat(TopicNames.MAX_LENGTH + 1)));
    }
}
