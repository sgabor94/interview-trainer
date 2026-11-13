 package com.interviewtrainer.llm;

/**
 * One request to the LLM.
 *
 * @param systemPrompt stable instructions (role, rubric, output rules); may be null
 * @param userPrompt   the concrete task for this call
 * @param effort       thinking effort; null means the configured default
 */
public record LlmRequest(String systemPrompt, String userPrompt, LlmEffort effort) {

    public LlmRequest {
        if (userPrompt == null || userPrompt.isBlank()) {
            throw new IllegalArgumentException("User prompt must not be blank");
        }
    }

    public LlmRequest(String systemPrompt, String userPrompt) {
        this(systemPrompt, userPrompt, null);
    }
}
