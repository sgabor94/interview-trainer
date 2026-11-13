package com.interviewtrainer.evaluation;

import com.interviewtrainer.answer.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

    Optional<Evaluation> findByAnswer(Answer answer);
}
