package com.example.Project400Backend.Repositories;

import com.example.Project400Backend.Models.Routine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoutineRepository extends JpaRepository<Routine, Long> {
    List<Routine> findByUserEmail(String email);
    Optional<Routine> findByIdAndUserEmail(Long id, String email);
}