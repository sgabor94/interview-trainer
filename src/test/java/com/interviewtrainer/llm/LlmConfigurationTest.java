package com.interviewtrainer.llm;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LlmConfigurationTest {

    record Topics(List<String> topics) {
    }

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(LlmConfiguration.class);

    @Test
    void defaultsToTheAnthropicClientWithOpus() {
        runner.run(context -> {
            assertThat(context).hasSingleBean(LlmClient.class).hasSingleBean(AnthropicLlmClient.class);
            LlmProperties properties = context.getBean(LlmProperties.class);
            assertThat(properties.model()).isEqualTo("claude-opus-5-5");
            assertThat(properties.defaultEffort()).isEqualTo(LlmEffort.MEDIUM);
            assertThat(properties.timeout()).isEqualTo(Duration.ofSeconds(120));
        });
    }

    @Test
    void startsWithoutApiKeyButCallsFailWithAClearMessage() {
        runner.withPropertyValues("llm.api-key=").run(context -> {
            assertThat(context).hasNotFailed();
            LlmClient client = context.getBean(LlmClient.class);

            assertThatThrownBy(() -> client.generate(new LlmRequest(null, "Hi"), Topics.class))
                    .isInstanceOf(LlmNotConfiguredException.class)
                    .hasMessageContaining("ANTHROPIC_API_KEY");
        });
    }

    @Test
    void createsTheClientWhenAKeyIsSetWithoutCallingTheApi() {
        runner.withPropertyValues("llm.api-key=sk-ant-test-not-real").run(context ->
                assertThat(context).hasNotFailed().hasSingleBean(AnthropicLlmClient.class));
    }

    @Test
    void fakeProviderDisablesTheRealClient() {
        runner.withPropertyValues("llm.provider=fake").run(context ->
                assertThat(context).doesNotHaveBean(AnthropicLlmClient.class));
    }

    @Test
    void propertiesNeverPrintTheApiKey() {
        runner.withPropertyValues("llm.api-key=sk-ant-secret-value").run(context ->
                assertThat(context.getBean(LlmProperties.class).toString())
                        .doesNotContain("sk-ant-secret-value")
                        .contains("apiKey=***"));
    }

    @Test
    void requestNeedsAUserPrompt() {
        assertThatIllegalArgumentException().isThrownBy(() -> new LlmRequest("system", " "));
    }
}
