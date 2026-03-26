package com.example.Project400Backend.Controllers;

import com.example.Project400Backend.Models.Exercise;
import com.example.Project400Backend.Models.Routine;
import com.example.Project400Backend.Models.User;
import com.example.Project400Backend.Models.WorkoutExercise;
import com.example.Project400Backend.Models.WorkoutSet;
import com.example.Project400Backend.Repositories.ExerciseRepository;
import com.example.Project400Backend.Repositories.RoutineRepository;
import com.example.Project400Backend.Repositories.UserRepository;
import com.example.Project400Backend.Repositories.WorkoutExerciseRepository;
import com.example.Project400Backend.Repositories.WorkoutSetRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routines")
public class RoutineController {

    private final RoutineRepository routineRepository;
    private final ExerciseRepository exerciseRepository;
    private final WorkoutExerciseRepository workoutExerciseRepository;
    private final WorkoutSetRepository workoutSetRepository;
    private final UserRepository userRepository;

    public RoutineController(RoutineRepository routineRepository,
                             ExerciseRepository exerciseRepository,
                             WorkoutExerciseRepository workoutExerciseRepository,
                             WorkoutSetRepository workoutSetRepository,
                             UserRepository userRepository) {
        this.routineRepository = routineRepository;
        this.exerciseRepository = exerciseRepository;
        this.workoutExerciseRepository = workoutExerciseRepository;
        this.workoutSetRepository = workoutSetRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public Routine addRoutine(@RequestBody Routine routine, Authentication authentication) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<WorkoutExercise> savedExercises = routine.getExercises().stream()
                .map(re -> {
                    Exercise incomingExercise = re.getExercise();

                    if (incomingExercise == null) {
                        throw new RuntimeException("Routine exercise is missing exercise data");
                    }

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

                    List<WorkoutSet> savedSets = re.getSets().stream()
                            .map(set -> {
                                WorkoutSet workoutSet = new WorkoutSet();
                                workoutSet.setReps(set.getReps());
                                workoutSet.setWeight(set.getWeight());
                                workoutSet.setCompleted(false);
                                return workoutSetRepository.save(workoutSet);
                            })
                            .toList();

                    WorkoutExercise entry = new WorkoutExercise();
                    entry.setExercise(savedExercise);
                    entry.setSets(savedSets);

                    return workoutExerciseRepository.save(entry);
                })
                .toList();

        routine.setExercises(savedExercises);
        routine.setUser(user);

        return routineRepository.save(routine);
    }

    @GetMapping("/user")
    public List<Routine> getUserRoutines(Authentication authentication) {
        String email = authentication.getName();
        return routineRepository.findByUserEmail(email);
    }

    @GetMapping("/{id}")
    public Routine getRoutine(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();

        return routineRepository.findByIdAndUserEmail(id, email)
                .orElseThrow(() -> new RuntimeException("Routine not found"));
    }

    @DeleteMapping("/{id}")
    public void deleteRoutine(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();

        Routine routine = routineRepository.findByIdAndUserEmail(id, email)
                .orElseThrow(() -> new RuntimeException("Routine not found"));

        routineRepository.delete(routine);
    }
}