package com.interviewtrainer.llm;

/**
 * A typed LLM response.
 *
 * @param output the structured output, already parsed into the requested type
 * @param model  id of the model that actually answered (may differ from the requested one after a fallback)
 * @param usage  token usage of the call
 */
public record LlmResult<T>(T output, String model, LlmUsage usage) {
}
