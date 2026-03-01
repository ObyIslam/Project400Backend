package com.example.Project400Backend.Controllers;

import com.example.Project400Backend.Models.Exercise;
import com.example.Project400Backend.Models.Workout;
import com.example.Project400Backend.Repositories.ExerciseRepository;
import com.example.Project400Backend.Repositories.WorkoutRepository;
import com.example.Project400Backend.freeexercisedb.FreeExerciseDbService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {

    private final WorkoutRepository workoutRepository;
    private final ExerciseRepository exerciseRepository;
    private final FreeExerciseDbService freeExerciseDbService;

    public WorkoutController(WorkoutRepository workoutRepository,
                             ExerciseRepository exerciseRepository,
                             FreeExerciseDbService freeExerciseDbService) {
        this.workoutRepository = workoutRepository;
        this.exerciseRepository = exerciseRepository;
        this.freeExerciseDbService = freeExerciseDbService;
    }

    @GetMapping("/exercises")
    public com.example.Project400Backend.freeexercisedb.PagedResponse<Exercise> getExercises(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "80") int limit,
            @RequestParam(required = false) String q
    ) {
        var list = freeExerciseDbService.list(page, limit, q);
        long count = freeExerciseDbService.totalCount(q);

        List<Exercise> mapped = list.stream().map(dto ->
                exerciseRepository.findByExternalId(dto.id())
                        .orElseGet(() -> {
                            Exercise e = new Exercise();
                            e.setExternalId(dto.id());
                            e.setName(dto.name());

                            if (dto.primaryMuscles() != null && !dto.primaryMuscles().isEmpty()) {
                                e.setCategory(dto.primaryMuscles().get(0));
                            }

                            if (dto.images() != null && !dto.images().isEmpty()) {
                                e.setImageUrl(dto.images().get(0));
                            }

                            return exerciseRepository.save(e);
                        })
        ).toList();

        return new com.example.Project400Backend.freeexercisedb.PagedResponse<>(count, page, limit, mapped);
    }

    @PostMapping("/user")
    public Workout addUserWorkout(@RequestBody Workout workout) {
        List<Exercise> savedExercises = workout.getExercises().stream()
                .map(e -> exerciseRepository.findById(e.getId())
                        .orElseGet(() -> exerciseRepository.save(e)))
                .toList();

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