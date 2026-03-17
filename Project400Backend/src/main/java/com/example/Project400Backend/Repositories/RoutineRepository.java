package com.example.Project400Backend.Repositories;

import com.example.Project400Backend.Models.Routine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoutineRepository extends JpaRepository<Routine, Long> {
}