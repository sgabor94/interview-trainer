package com.interviewtrainer.llm;

/**
 * The single entry point for every LLM call in the application. Production code uses
 * {@link AnthropicLlmClient}; tests use a fake, so they never call the real API.
 */
public interface LlmClient {

    /**
     * Sends the request and returns the model's answer parsed into {@code outputType}
     * (a Java record describing the expected JSON structure).
     *
     * @throws LlmException if the call fails, is refused, is cut off, or no API key is configured
     */
    <T> LlmResult<T> generate(LlmRequest request, Class<T> outputType);
}
