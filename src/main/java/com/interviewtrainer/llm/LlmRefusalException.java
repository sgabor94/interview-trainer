package com.interviewtrainer.llm;

/** The model declined to answer (stop reason {@code refusal}), even after any fallback. */
public class LlmRefusalException extends LlmException {

    public LlmRefusalException(String message) {
        super(message);
    }
}
