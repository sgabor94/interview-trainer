package com.interviewtrainer.llm;

/** The answer hit the {@code max_tokens} limit and is incomplete. */
public class LlmTruncatedException extends LlmException {

    public LlmTruncatedException(String message) {
        super(message);
    }
}
