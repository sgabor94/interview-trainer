package com.interviewtrainer.topic;

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
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

/**
 * A topic the user practises, e.g. "Kafka". A topic may have one parent, which makes it a subtopic
 * (e.g. "Spring Boot" -> "Security"). At most two levels are allowed.
 */
@Entity
@Table(name = "topic", uniqueConstraints = @UniqueConstraint(columnNames = {"parent_id", "normalized_name"}))
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, length = TopicNames.MAX_LENGTH)
    private String name;

    @Column(name = "normalized_name", nullable = false, length = TopicNames.MAX_LENGTH)
    private String normalizedName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Topic parent;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected Topic() {
    }

    public Topic(String name, Topic parent) {
        if (parent != null && parent.getParent() != null) {
            throw new IllegalArgumentException(
                    "Topics can be nested only two levels deep; '" + parent.getName() + "' is already a subtopic");
        }
        this.name = TopicNames.clean(name);
        this.normalizedName = TopicNames.normalize(name);
        this.parent = parent;
    }

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }

    public boolean isSubtopic() {
        return parent != null;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNormalizedName() {
        return normalizedName;
    }

    public Topic getParent() {
        return parent;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
