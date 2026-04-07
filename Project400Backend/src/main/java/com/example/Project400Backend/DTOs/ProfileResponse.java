package com.example.Project400Backend.DTOs;

import java.time.LocalDate;

public class ProfileResponse {
    private Long userId;
    private String name;
    private String email;
    private Integer age;
    private Double heightCm;
    private Double startingWeightKg;
    private Double goalWeightKg;
    private LocalDate dateOfBirth;
    private Double latestWeightKg;

    public ProfileResponse() {}

    public ProfileResponse(Long userId, String name, String email, Integer age,
                           Double heightCm, Double startingWeightKg,
                           Double goalWeightKg, LocalDate dateOfBirth,
                           Double latestWeightKg) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.age = age;
        this.heightCm = heightCm;
        this.startingWeightKg = startingWeightKg;
        this.goalWeightKg = goalWeightKg;
        this.dateOfBirth = dateOfBirth;
        this.latestWeightKg = latestWeightKg;
    }

    public Long getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public Integer getAge() { return age; }
    public Double getHeightCm() { return heightCm; }
    public Double getStartingWeightKg() { return startingWeightKg; }
    public Double getGoalWeightKg() { return goalWeightKg; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public Double getLatestWeightKg() { return latestWeightKg; }
}
