package com.interviewtrainer.session;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * One practice conversation: the questions asked, the user's answers and the evaluations, in order.
 * Not bound to a topic or level; those live on each question, so one session can mix topics.
 */
@Entity
@Table(name = "practice_session")
public class PracticeSession {

    public static final int MAX_TITLE_LENGTH = 200;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(length = MAX_TITLE_LENGTH)
    private String title;

    @Column(nullable = false, updatable = false)
    private Instant startedAt;

    private Instant endedAt;

    public PracticeSession() {
    }

    @PrePersist
    void onCreate() {
        startedAt = Instant.now();
    }

    public void end() {
        if (endedAt != null) {
            throw new IllegalStateException("Practice session " + id + " has already ended");
        }
        endedAt = Instant.now();
    }

    public void rename(String newTitle) {
        if (newTitle != null && newTitle.strip().length() > MAX_TITLE_LENGTH) {
            throw new IllegalArgumentException("Title must be at most " + MAX_TITLE_LENGTH + " characters");
        }
        this.title = newTitle == null || newTitle.isBlank() ? null : newTitle.strip();
    }

    public boolean isEnded() {
        return endedAt != null;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getEndedAt() {
        return endedAt;
    }
}
