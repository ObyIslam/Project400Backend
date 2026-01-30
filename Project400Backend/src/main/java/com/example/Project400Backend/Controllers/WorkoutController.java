package com.example.Project400Backend.Controllers;

import com.example.Project400Backend.Models.Exercise;
import com.example.Project400Backend.Models.Workout;
import com.example.Project400Backend.Repositories.ExerciseRepository;
import com.example.Project400Backend.Repositories.WorkoutRepository;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {

    private final WorkoutRepository workoutRepository;
    private final ExerciseRepository exerciseRepository;

    public WorkoutController(WorkoutRepository workoutRepository, ExerciseRepository exerciseRepository) {
        this.workoutRepository = workoutRepository;
        this.exerciseRepository = exerciseRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initExercises() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = new ClassPathResource("workouts.json").getInputStream();
            Exercise[] exercises = mapper.readValue(is, Exercise[].class);

            for (Exercise e : exercises) {
                if (!exerciseRepository.existsById(e.getId())) {
                    exerciseRepository.save(e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/exercises")
    public List<Exercise> getAllExercises() {
        return exerciseRepository.findAll();
    }

    @PostMapping("/user")
    public Workout addUserWorkout(@RequestBody Workout workout) {
        List<Exercise> savedExercises = workout.getExercises().stream()
                .map(e -> exerciseRepository.findById(e.getId())
                        .orElseThrow(() -> new RuntimeException("Exercise not found: " + e.getId())))
                .collect(Collectors.toList());

        workout.setExercises(savedExercises);
        workout.setFinished(false);
        return workoutRepository.save(workout);
    }


    @GetMapping("/user")
    public List<Workout> getUserWorkouts() {
        return workoutRepository.findAll();
    }

    @PostMapping("/user/{id}/finish")
    @Transactional
    public Workout finishWorkout(@PathVariable Long id) {
        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workout not found"));
        workout.setFinished(true);
        return workoutRepository.save(workout);
    }
}
