package com.example.Project400Backend.Models;

public class Workout {
    private int id;
    private String name;
    private String type; // muscle group

    public Workout(int id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    // Getters (needed for JSON serialization)
    public int getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
}
