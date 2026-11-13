package com.interviewtrainer.llm;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Calls the real Claude API (costs about a cent). Excluded from {@code mvn verify};
 * run explicitly with {@code mvn test -Dgroups=live}. Skipped when ANTHROPIC_API_KEY is not set.
 */
@Tag("live")
@EnabledIfEnvironmentVariable(named = "ANTHROPIC_API_KEY", matches = ".+")
class AnthropicLlmClientLiveTest {

    public record JavaTopics(List<String> topics) {
    }

    @Test
    void returnsStructuredOutputFromClaude() {
        LlmProperties properties = new LlmProperties("anthropic", "claude-opus-5-5",
                System.getenv("ANTHROPIC_API_KEY"), 2000, LlmEffort.LOW, Duration.ofSeconds(120), 2);
        AnthropicLlmClient client = new AnthropicLlmClient(properties);
        try {
            LlmResult<JavaTopics> result = client.generate(new LlmRequest(
                    "You suggest topics for practising Java. Answer only with the requested structure.",
                    "List exactly 2 short Java topic names suitable for a senior developer."), JavaTopics.class);

            System.out.printf("Live LLM call: model=%s, topics=%s, usage=%s%n",
                    result.model(), result.output().topics(), result.usage());
            assertThat(result.output().topics()).hasSize(2).allSatisfy(t -> assertThat(t).isNotBlank());
            assertThat(result.model()).startsWith("claude-");
            assertThat(result.usage().inputTokens()).isPositive();
            assertThat(result.usage().outputTokens()).isPositive();
        } finally {
            client.destroy();
        }
    }
}
