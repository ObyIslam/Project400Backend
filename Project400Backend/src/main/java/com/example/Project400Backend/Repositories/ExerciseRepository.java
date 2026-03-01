package com.example.Project400Backend.Repositories;

import com.example.Project400Backend.Models.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    Optional<Exercise> findByExternalId(String externalId);
}