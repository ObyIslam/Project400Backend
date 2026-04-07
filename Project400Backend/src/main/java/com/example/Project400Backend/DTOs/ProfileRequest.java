package com.example.Project400Backend.DTOs;

import java.time.LocalDate;

public class ProfileRequest {
    private Integer age;
    private Double heightCm;
    private Double startingWeightKg;
    private Double goalWeightKg;
    private LocalDate dateOfBirth;

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public Double getHeightCm() { return heightCm; }
    public void setHeightCm(Double heightCm) { this.heightCm = heightCm; }

    public Double getStartingWeightKg() { return startingWeightKg; }
    public void setStartingWeightKg(Double startingWeightKg) { this.startingWeightKg = startingWeightKg; }

    public Double getGoalWeightKg() { return goalWeightKg; }
    public void setGoalWeightKg(Double goalWeightKg) { this.goalWeightKg = goalWeightKg; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
}
