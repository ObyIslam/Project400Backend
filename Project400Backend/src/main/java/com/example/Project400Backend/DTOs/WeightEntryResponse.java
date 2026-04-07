package com.example.Project400Backend.DTOs;

import java.time.LocalDate;

public class WeightEntryResponse {
    private Long id;
    private Double weightKg;
    private LocalDate entryDate;
    private String note;

    public WeightEntryResponse(Long id, Double weightKg, LocalDate entryDate, String note) {
        this.id = id;
        this.weightKg = weightKg;
        this.entryDate = entryDate;
        this.note = note;
    }

    public Long getId() { return id; }
    public Double getWeightKg() { return weightKg; }
    public LocalDate getEntryDate() { return entryDate; }
    public String getNote() { return note; }
}