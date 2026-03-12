package com.example.Project400Backend.Repositories;

import com.example.Project400Backend.Models.WorkoutExercise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutExerciseRepository extends JpaRepository<WorkoutExercise, Long> {
}