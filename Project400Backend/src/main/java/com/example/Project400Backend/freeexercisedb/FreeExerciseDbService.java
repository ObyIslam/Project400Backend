package com.example.Project400Backend.freeexercisedb;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

@Service
public class FreeExerciseDbService {

    private static final String IMAGE_PREFIX = "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/";

    private final List<FreeExerciseDto> all;

    public FreeExerciseDbService(ObjectMapper objectMapper) {
        this.all = loadAll(objectMapper);
    }

    private List<FreeExerciseDto> loadAll(ObjectMapper objectMapper) {
        try (InputStream is = new ClassPathResource("free-exercise-db.json").getInputStream()) {
            return objectMapper.readValue(is, new TypeReference<List<FreeExerciseDto>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to load free-exercise-db.json from src/main/resources", e);
        }
    }

    public List<FreeExerciseDto> list(int page, int limit, String q) {
        Stream<FreeExerciseDto> s = all.stream();

        if (q != null && !q.isBlank()) {
            String needle = q.toLowerCase(Locale.ROOT);
            s = s.filter(e -> e.name() != null && e.name().toLowerCase(Locale.ROOT).contains(needle));
        }

        return s.skip((long) (page - 1) * limit)
                .limit(limit)
                .map(this::withFullImagePaths)
                .toList();
    }

    private FreeExerciseDto withFullImagePaths(FreeExerciseDto e) {
        if (e.images() == null || e.images().isEmpty()) return e;

        var full = e.images().stream()
                .map(path -> IMAGE_PREFIX + path)
                .toList();

        return new FreeExerciseDto(e.id(), e.name(), e.primaryMuscles(), full);
    }

    public long totalCount(String q) {
        if (q == null || q.isBlank()) return all.size();
        String needle = q.toLowerCase(Locale.ROOT);
        return all.stream()
                .filter(e -> e.name() != null && e.name().toLowerCase(Locale.ROOT).contains(needle))
                .count();
    }
}