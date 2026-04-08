package com.example.Project400Backend.Repositories;

import com.example.Project400Backend.Models.ExerciseSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExerciseSubmissionRepository extends JpaRepository<ExerciseSubmission, Long> {
    List<ExerciseSubmission> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<ExerciseSubmission> findByIdAndUserId(Long id, Long userId);
}
