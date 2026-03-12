package com.example.Project400Backend.Repositories;

import com.example.Project400Backend.Models.WorkoutSet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutSetRepository extends JpaRepository<WorkoutSet, Long> {
}