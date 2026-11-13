package com.interviewtrainer.llm;

/**
 * How much the model should think before answering. Higher effort means better answers on hard
 * tasks, but more tokens (cost) and latency.
 */
public enum LlmEffort {
    LOW,
    MEDIUM,
    HIGH
}
