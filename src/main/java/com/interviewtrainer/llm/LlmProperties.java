package com.interviewtrainer.llm;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;

/**
 * LLM settings ({@code llm.*}). The API key comes from the {@code ANTHROPIC_API_KEY} environment
 * variable via {@code llm.api-key=${ANTHROPIC_API_KEY:}}; it is never stored in a committed file.
 *
 * @param provider      {@code anthropic} (default) or {@code fake} (tests)
 * @param model         Claude model id
 * @param apiKey        Anthropic API key; blank means "not configured"
 * @param maxTokens     upper limit for one answer
 * @param defaultEffort effort used when a request does not set one
 * @param timeout       HTTP timeout of one call
 * @param maxRetries    automatic retries on rate limits, overload and network errors
 */
@ConfigurationProperties("llm")
public record LlmProperties(
        @DefaultValue("anthropic") String provider,
        @DefaultValue("claude-opus-5-5") String model,
        String apiKey,
        @DefaultValue("16000") long maxTokens,
        @DefaultValue("MEDIUM") LlmEffort defaultEffort,
        @DefaultValue("120s") Duration timeout,
        @DefaultValue("2") int maxRetries) {

    public boolean hasApiKey() {
        return apiKey != null && !apiKey.isBlank();
    }

    /** Never prints the API key. */
    @Override
    public String toString() {
        return "LlmProperties[provider=" + provider + ", model=" + model + ", apiKey="
                + (hasApiKey() ? "***" : "<not set>") + ", maxTokens=" + maxTokens + ", defaultEffort="
                + defaultEffort + ", timeout=" + timeout + ", maxRetries=" + maxRetries + "]";
    }
}
