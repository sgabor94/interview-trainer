package com.interviewtrainer.session;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PracticeSessionRepository extends JpaRepository<PracticeSession, Long> {

    /** Most recent sessions first, for the history list. */
    List<PracticeSession> findTop20ByOrderByStartedAtDesc();
}
