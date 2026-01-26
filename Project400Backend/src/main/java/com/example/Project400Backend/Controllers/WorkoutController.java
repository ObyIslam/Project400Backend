package com.example.Project400Backend.Controllers;

import com.example.Project400Backend.Models.Workout;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {

    @GetMapping
    public List<Workout> getWorkouts() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = new ClassPathResource("workouts.json").getInputStream();
        return Arrays.asList(mapper.readValue(is, Workout[].class));
    }
}

