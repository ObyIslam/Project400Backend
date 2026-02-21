package com.example.Project400Backend.ExerciseDBAPI;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class ExerciseDBService {

    private final RestTemplate restTemplate = new RestTemplate();

    private final String BASE_URL = "https://exercisedb.p.rapidapi.com/exercises";
    private final String RAPIDAPI_KEY = "";
    private final String RAPIDAPI_HOST = "exercisedb.p.rapidapi.com";


    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public String getRapidApiKey() {
        return RAPIDAPI_KEY;
    }

    public List<ExerciseDBExercise> fetchAllExercises() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", RAPIDAPI_KEY);
        headers.set("X-RapidAPI-Host", RAPIDAPI_HOST);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String[] bodyParts = {
                "back", "cardio", "chest", "lower arms", "lower legs",
                "neck", "shoulders", "upper arms", "upper legs", "waist"
        };

        List<ExerciseDBExercise> allExercises = new ArrayList<>();

        for (String part : bodyParts) {
            String url = BASE_URL + "/bodyPart/" + part;

            try {
                ResponseEntity<ExerciseDBExercise[]> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        ExerciseDBExercise[].class
                );

                if (response.getBody() != null) {
                    allExercises.addAll(Arrays.asList(response.getBody()));
                }
            } catch (Exception e) {
                System.err.println("Failed to fetch exercises for bodyPart=" + part);
                e.printStackTrace();
            }
        }

        Set<String> seenIds = new HashSet<>();
        List<ExerciseDBExercise> uniqueExercises = new ArrayList<>();
        for (ExerciseDBExercise ex : allExercises) {
            if (!seenIds.contains(ex.getId())) {
                seenIds.add(ex.getId());
                ex.setGifUrl(getGifUrl(ex.getId()));

                uniqueExercises.add(ex);
            }
        }

        return uniqueExercises;
    }

    private String getGifUrl(String exerciseId) {
        return "http://localhost:8080/api/workouts/exercises/" + exerciseId + "/image";
    }
}
