package com.example.Project400Backend.Controllers;

import com.example.Project400Backend.ExerciseDBAPI.ExerciseDBExercise;
import com.example.Project400Backend.ExerciseDBAPI.ExerciseDBService;
import com.example.Project400Backend.Models.Exercise;
import com.example.Project400Backend.Models.Workout;
import com.example.Project400Backend.Repositories.ExerciseRepository;
import com.example.Project400Backend.Repositories.WorkoutRepository;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {

    private final WorkoutRepository workoutRepository;
    private final ExerciseRepository exerciseRepository;
    private final ExerciseDBService exerciseDBService;

    public WorkoutController(WorkoutRepository workoutRepository,
                             ExerciseRepository exerciseRepository,
                             ExerciseDBService exerciseDBService) {
        this.workoutRepository = workoutRepository;
        this.exerciseRepository = exerciseRepository;
        this.exerciseDBService = exerciseDBService;
    }

    @GetMapping("/exercises")
    public List<ExerciseDBExercise> getAllExercises() {
        return exerciseDBService.fetchAllExercises();
    }

    @GetMapping("/exercises/{id}/image")
    public ResponseEntity<byte[]> getExerciseImage(@PathVariable String id) {
        String url = "https://exercisedb.p.rapidapi.com/image?resolution=720&exerciseId=" + id;
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", exerciseDBService.getRapidApiKey());
        headers.set("X-RapidAPI-Host", "exercisedb.p.rapidapi.com");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<byte[]> response = exerciseDBService.getRestTemplate().exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    byte[].class
            );
            HttpHeaders respHeaders = new HttpHeaders();
            respHeaders.setContentType(MediaType.IMAGE_GIF);
            return new ResponseEntity<>(response.getBody(), respHeaders, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/user")
    public Workout addUserWorkout(@RequestBody Workout workout) {
        List<Exercise> savedExercises = workout.getExercises().stream()
                .map(e -> {
                    // Save exercise if it doesn’t exist in DB
                    return exerciseRepository.findById(e.getId())
                            .orElseGet(() -> exerciseRepository.save(e));
                })
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
    public Workout finishWorkout(@PathVariable Long id) {
        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workout not found"));
        workout.setFinished(true);
        return workoutRepository.save(workout);
    }
}