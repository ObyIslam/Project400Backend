package com.example.Project400Backend.freeexercisedb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FreeExerciseDto(
        String id,
        String name,
        List<String> primaryMuscles,
        List<String> images
) {}