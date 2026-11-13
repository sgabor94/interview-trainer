package com.interviewtrainer.topic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

@DataJpaTest
@Import(TopicService.class)
class TopicServiceTest {

    @Autowired
    TopicService topicService;

    @Autowired
    TopicRepository topicRepository;

    @Test
    void sameNameInDifferentCasingResolvesToOneTopic() {
        Topic first = topicService.resolve("Kafka", null);
        Topic second = topicService.resolve("  kafka ", "  ");

        assertThat(second.getId()).isEqualTo(first.getId());
        assertThat(second.getName()).isEqualTo("Kafka");
        assertThat(topicRepository.count()).isEqualTo(1);
    }

    @Test
    void subtopicIsCreatedUnderItsParent() {
        Topic security = topicService.resolve("Spring Boot", "Security");

        assertThat(security.isSubtopic()).isTrue();
        assertThat(security.getParent().getName()).isEqualTo("Spring Boot");
        assertThat(topicService.resolve("spring boot", "SECURITY").getId()).isEqualTo(security.getId());
        assertThat(topicRepository.count()).isEqualTo(2);
    }

    @Test
    void sameSubtopicNameUnderDifferentParentsIsTwoTopics() {
        Topic springTesting = topicService.resolve("Spring Boot", "Testing");
        Topic kafkaTesting = topicService.resolve("Kafka", "Testing");

        assertThat(springTesting.getId()).isNotEqualTo(kafkaTesting.getId());
        assertThat(topicRepository.findByParentIsNullOrderByName())
                .extracting(Topic::getName)
                .containsExactly("Kafka", "Spring Boot");
    }

    @Test
    void thirdLevelIsRejected() {
        Topic security = topicService.resolve("Spring Boot", "Security");

        assertThatIllegalArgumentException().isThrownBy(() -> new Topic("OAuth2", security));
    }
}
