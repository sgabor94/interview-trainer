package com.interviewtrainer.llm;

/** Token usage of one LLM call; the basis of cost tracking. */
public record LlmUsage(long inputTokens, long outputTokens, long cacheReadInputTokens, long cacheCreationInputTokens) {

    public static final LlmUsage NONE = new LlmUsage(0, 0, 0, 0);
}
