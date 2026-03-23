package com.example.Project400Backend.Models;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class WorkoutExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Exercise exercise;

    @OneToMany(cascade = CascadeType.ALL)
    private List<WorkoutSet> sets;

    public WorkoutExercise() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public List<WorkoutSet> getSets() {
        return sets;
    }

    public void setSets(List<WorkoutSet> sets) {
        this.sets = new java.util.ArrayList<>(sets);
    }
}