package com.interviewtrainer.topic;

import java.util.Locale;

/**
 * Normalizes free-text topic names so that "Kafka", " kafka " and "KAFKA" resolve to the same topic.
 */
public final class TopicNames {

    public static final int MAX_LENGTH = 100;

    private TopicNames() {
    }

    /** Trims, collapses inner whitespace to single spaces. Keeps the original casing for display. */
    public static String clean(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Topic name must not be blank");
        }
        String cleaned = raw.strip().replaceAll("\\s+", " ");
        if (cleaned.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Topic name must be at most " + MAX_LENGTH + " characters");
        }
        return cleaned;
    }

    /** The lookup key: cleaned and lower-cased. */
    public static String normalize(String raw) {
        return clean(raw).toLowerCase(Locale.ROOT);
    }
}
