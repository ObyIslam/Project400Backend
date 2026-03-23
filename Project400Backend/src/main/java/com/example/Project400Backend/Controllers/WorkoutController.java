package com.example.Project400Backend.Controllers;

import com.example.Project400Backend.Models.Exercise;
import com.example.Project400Backend.Models.Workout;
import com.example.Project400Backend.Models.WorkoutExercise;
import com.example.Project400Backend.Models.WorkoutSet;
import com.example.Project400Backend.Repositories.ExerciseRepository;
import com.example.Project400Backend.Repositories.WorkoutExerciseRepository;
import com.example.Project400Backend.Repositories.WorkoutRepository;
import com.example.Project400Backend.Repositories.WorkoutSetRepository;
import com.example.Project400Backend.freeexercisedb.FreeExerciseDbService;
import com.example.Project400Backend.freeexercisedb.PagedResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {

    private final WorkoutRepository workoutRepository;
    private final ExerciseRepository exerciseRepository;
    private final WorkoutExerciseRepository workoutExerciseRepository;
    private final WorkoutSetRepository workoutSetRepository;
    private final FreeExerciseDbService freeExerciseDbService;

    public WorkoutController(WorkoutRepository workoutRepository,
                             ExerciseRepository exerciseRepository,
                             WorkoutExerciseRepository workoutExerciseRepository,
                             WorkoutSetRepository workoutSetRepository,
                             FreeExerciseDbService freeExerciseDbService) {
        this.workoutRepository = workoutRepository;
        this.exerciseRepository = exerciseRepository;
        this.workoutExerciseRepository = workoutExerciseRepository;
        this.workoutSetRepository = workoutSetRepository;
        this.freeExerciseDbService = freeExerciseDbService;
    }

    @GetMapping("/exercises")
    public PagedResponse<Exercise> getExercises(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "80") int limit,
            @RequestParam(required = false) String q
    ) {
        var list = freeExerciseDbService.list(page, limit, q);
        long count = freeExerciseDbService.totalCount(q);

        List<Exercise> mapped = list.stream()
                .map(dto -> exerciseRepository.findByExternalId(dto.id())
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
                        }))
                .collect(Collectors.toList());

        return new PagedResponse<>(count, page, limit, mapped);
    }

    @PostMapping("/user")
    public Workout addUserWorkout(@RequestBody Workout workout) {
        List<WorkoutExercise> savedWorkoutExercises = workout.getExercises().stream()
                .map(this::buildWorkoutExercise)
                .collect(Collectors.toList());

        workout.setExercises(savedWorkoutExercises);
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

    @DeleteMapping("/user/{id}")
    public void deleteWorkout(@PathVariable Long id) {
        if (!workoutRepository.existsById(id)) {
            throw new RuntimeException("Workout not found");
        }
        workoutRepository.deleteById(id);
    }

    @PutMapping("/user/{id}")
    public Workout updateWorkout(@PathVariable Long id, @RequestBody Workout updatedWorkout) {
        Workout existingWorkout = workoutRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workout not found"));

        List<WorkoutExercise> savedWorkoutExercises = updatedWorkout.getExercises().stream()
                .map(this::buildWorkoutExercise)
                .collect(Collectors.toList());

        existingWorkout.setName(updatedWorkout.getName());
        existingWorkout.setFinished(updatedWorkout.isFinished());
        existingWorkout.setExercises(savedWorkoutExercises);

        return workoutRepository.save(existingWorkout);
    }

    private WorkoutExercise buildWorkoutExercise(WorkoutExercise we) {
        Exercise incomingExercise = we.getExercise();

        if (incomingExercise == null) {
            throw new RuntimeException("Workout exercise is missing exercise data");
        }

        Exercise savedExercise = resolveExercise(incomingExercise);

        List<WorkoutSet> savedSets = we.getSets().stream()
                .map(this::buildWorkoutSet)
                .collect(Collectors.toList());

        WorkoutExercise entry;
        if (we.getId() != null) {
            entry = workoutExerciseRepository.findById(we.getId()).orElse(new WorkoutExercise());
        } else {
            entry = new WorkoutExercise();
        }

        entry.setExercise(savedExercise);
        entry.setSets(savedSets);

        return workoutExerciseRepository.save(entry);
    }

    private WorkoutSet buildWorkoutSet(WorkoutSet set) {
        WorkoutSet workoutSet;

        if (set.getId() != null) {
            workoutSet = workoutSetRepository.findById(set.getId()).orElse(new WorkoutSet());
        } else {
            workoutSet = new WorkoutSet();
        }

        workoutSet.setReps(set.getReps());
        workoutSet.setWeight(set.getWeight());
        workoutSet.setCompleted(set.isCompleted());

        return workoutSetRepository.save(workoutSet);
    }

    private Exercise resolveExercise(Exercise incomingExercise) {
        Exercise savedExercise = null;

        if (incomingExercise.getId() != null) {
            savedExercise = exerciseRepository.findById(incomingExercise.getId()).orElse(null);
        }

        if (savedExercise == null
                && incomingExercise.getExternalId() != null
                && !incomingExercise.getExternalId().isBlank()) {
            savedExercise = exerciseRepository.findByExternalId(incomingExercise.getExternalId()).orElse(null);
        }

        if (savedExercise == null) {
            Exercise newExercise = new Exercise();
            newExercise.setExternalId(incomingExercise.getExternalId());
            newExercise.setName(incomingExercise.getName());
            newExercise.setCategory(incomingExercise.getCategory());
            newExercise.setDescription(incomingExercise.getDescription());
            newExercise.setImageUrl(incomingExercise.getImageUrl());

            savedExercise = exerciseRepository.save(newExercise);
        }

        return savedExercise;
    }
}