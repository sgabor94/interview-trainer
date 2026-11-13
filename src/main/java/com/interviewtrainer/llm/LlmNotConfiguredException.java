package com.interviewtrainer.llm;

/** An LLM call was attempted, but no API key is configured. */
public class LlmNotConfiguredException extends LlmException {

    public LlmNotConfiguredException(String message) {
        super(message);
    }
}
