package com.interviewtrainer.evaluation;

import com.interviewtrainer.answer.Answer;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** The LLM's assessment of one answer: a 0–10 score, feedback, and what was good or missing. */
@Entity
@Table(name = "evaluation")
public class Evaluation {

    public static final int MIN_SCORE = 0;
    public static final int MAX_SCORE = 10;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "answer_id", nullable = false, unique = true)
    private Answer answer;

    @Column(nullable = false)
    private int score;

    @Column(nullable = false, length = 4000)
    private String feedback;

    @ElementCollection
    @CollectionTable(name = "evaluation_strengths", joinColumns = @JoinColumn(name = "evaluation_id"))
    @OrderColumn(name = "position")
    @Column(name = "strength", nullable = false, length = 1000)
    private List<String> strengths = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "evaluation_missing_points", joinColumns = @JoinColumn(name = "evaluation_id"))
    @OrderColumn(name = "position")
    @Column(name = "missing_point", nullable = false, length = 1000)
    private List<String> missingPoints = new ArrayList<>();

    @Column(length = 10000)
    private String modelAnswer;

    /** Id of the LLM model that produced this evaluation, e.g. {@code claude-opus-5-5}. */
    @Column(nullable = false, length = 100)
    private String llmModel;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected Evaluation() {
    }

    public Evaluation(Answer answer, int score, String feedback, List<String> strengths,
                      List<String> missingPoints, String modelAnswer, String llmModel) {
        this.answer = Objects.requireNonNull(answer, "answer");
        if (score < MIN_SCORE || score > MAX_SCORE) {
            throw new IllegalArgumentException("Score must be between " + MIN_SCORE + " and " + MAX_SCORE);
        }
        this.score = score;
        this.feedback = Objects.requireNonNull(feedback, "feedback");
        this.strengths = new ArrayList<>(strengths == null ? List.of() : strengths);
        this.missingPoints = new ArrayList<>(missingPoints == null ? List.of() : missingPoints);
        this.modelAnswer = modelAnswer;
        this.llmModel = Objects.requireNonNull(llmModel, "llmModel");
    }

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Answer getAnswer() {
        return answer;
    }

    public int getScore() {
        return score;
    }

    public String getFeedback() {
        return feedback;
    }

    public List<String> getStrengths() {
        return Collections.unmodifiableList(strengths);
    }

    public List<String> getMissingPoints() {
        return Collections.unmodifiableList(missingPoints);
    }

    public String getModelAnswer() {
        return modelAnswer;
    }

    public String getLlmModel() {
        return llmModel;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
