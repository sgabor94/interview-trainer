package com.interviewtrainer.answer;

import com.interviewtrainer.question.Question;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.Objects;

/** The user's free-text answer to a question. A question can be answered several times (repetition). */
@Entity
@Table(name = "answer")
public class Answer {

    public static final int MAX_TEXT_LENGTH = 10000;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(nullable = false, length = MAX_TEXT_LENGTH)
    private String text;

    @Column(nullable = false, updatable = false)
    private Instant answeredAt;

    protected Answer() {
    }

    public Answer(Question question, String text) {
        this.question = Objects.requireNonNull(question, "question");
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Answer text must not be blank");
        }
        this.text = text.strip();
    }

    @PrePersist
    void onCreate() {
        answeredAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Question getQuestion() {
        return question;
    }

    public String getText() {
        return text;
    }

    public Instant getAnsweredAt() {
        return answeredAt;
    }
}
