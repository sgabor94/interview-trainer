package com.interviewtrainer.llm;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.errors.AnthropicException;
import com.anthropic.errors.AnthropicServiceException;
import com.anthropic.models.beta.messages.BetaFallbacksParam;
import com.anthropic.models.beta.messages.BetaOutputConfig;
import com.anthropic.models.beta.messages.BetaStopReason;
import com.anthropic.models.beta.messages.BetaTextBlockParam;
import com.anthropic.models.beta.messages.BetaUsage;
import com.anthropic.models.beta.messages.MessageCreateParams;
import com.anthropic.models.beta.messages.StructuredMessage;
import com.anthropic.models.beta.messages.StructuredMessageCreateParams;
import com.anthropic.models.beta.messages.StructuredTextBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

import java.util.List;

/**
 * {@link LlmClient} backed by the Claude API (Anthropic Java SDK).
 *
 * <p>Uses structured outputs, so the answer is parsed into the requested record type, and the
 * server-side refusal fallback: if the model declines a request for safety reasons, the API retries
 * it on a fallback model automatically.
 */
public class AnthropicLlmClient implements LlmClient, DisposableBean {

    /** Beta that enables the {@code fallbacks} request parameter. */
    static final String SERVER_SIDE_FALLBACK_BETA = "server-side-fallback-2026-07-01";

    private static final Logger log = LoggerFactory.getLogger(AnthropicLlmClient.class);

    private final LlmProperties properties;
    private final AnthropicClient client;

    public AnthropicLlmClient(LlmProperties properties) {
        this.properties = properties;
        if (properties.hasApiKey()) {
            this.client = AnthropicOkHttpClient.builder()
                    .apiKey(properties.apiKey())
                    .timeout(properties.timeout())
                    .maxRetries(properties.maxRetries())
                    .build();
            log.info("LLM client ready: model {}", properties.model());
        } else {
            this.client = null;
            log.warn("ANTHROPIC_API_KEY is not set: the app runs, but every LLM call will fail until it is set");
        }
    }

    @Override
    public <T> LlmResult<T> generate(LlmRequest request, Class<T> outputType) {
        if (client == null) {
            throw new LlmNotConfiguredException(
                    "ANTHROPIC_API_KEY is not set; set the environment variable and restart the application");
        }

        StructuredMessage<T> message;
        try {
            message = client.beta().messages().create(buildParams(request, outputType));
        } catch (AnthropicServiceException e) {
            throw new LlmException("Claude API call failed with HTTP " + e.statusCode() + ": " + e.getMessage(), e);
        } catch (AnthropicException e) {
            throw new LlmException("Claude API call failed: " + e.getMessage(), e);
        }

        BetaStopReason stopReason = message.stopReason().orElse(null);
        if (BetaStopReason.REFUSAL.equals(stopReason)) {
            throw new LlmRefusalException("The model declined the request: "
                    + message.stopDetails().map(Object::toString).orElse("no details"));
        }
        if (BetaStopReason.MAX_TOKENS.equals(stopReason)) {
            throw new LlmTruncatedException(
                    "The answer exceeded max_tokens (" + properties.maxTokens() + ") and is incomplete");
        }

        T output;
        try {
            output = message.content().stream()
                    .flatMap(block -> block.text().stream())
                    .map(StructuredTextBlock::text)
                    .findFirst()
                    .orElseThrow(() -> new LlmException("The response contained no structured output"));
        } catch (AnthropicException e) {
            throw new LlmException("Could not parse the structured output as " + outputType.getSimpleName(), e);
        }

        return new LlmResult<>(output, message.model().asString(), toUsage(message.usage()));
    }

    private <T> StructuredMessageCreateParams<T> buildParams(LlmRequest request, Class<T> outputType) {
        MessageCreateParams.Builder builder = MessageCreateParams.builder()
                .model(properties.model())
                .maxTokens(properties.maxTokens())
                .addBeta(SERVER_SIDE_FALLBACK_BETA)
                .fallbacks(BetaFallbacksParam.ofDefault())
                .addUserMessage(request.userPrompt());
        if (request.systemPrompt() != null && !request.systemPrompt().isBlank()) {
            // A text block (not a plain string) so prompt caching can be enabled on it later.
            builder.systemOfBetaTextBlockParams(List.of(
                    BetaTextBlockParam.builder().text(request.systemPrompt()).build()));
        }
        LlmEffort effort = request.effort() != null ? request.effort() : properties.defaultEffort();
        return builder.outputConfig(outputType, toSdkEffort(effort)).build();
    }

    private static BetaOutputConfig.Effort toSdkEffort(LlmEffort effort) {
        return switch (effort) {
            case LOW -> BetaOutputConfig.Effort.LOW;
            case MEDIUM -> BetaOutputConfig.Effort.MEDIUM;
            case HIGH -> BetaOutputConfig.Effort.HIGH;
        };
    }

    private static LlmUsage toUsage(BetaUsage usage) {
        return new LlmUsage(
                usage.inputTokens(),
                usage.outputTokens(),
                usage.cacheReadInputTokens().orElse(0L),
                usage.cacheCreationInputTokens().orElse(0L));
    }

    @Override
    public void destroy() {
        if (client != null) {
            client.close();
        }
    }
}
