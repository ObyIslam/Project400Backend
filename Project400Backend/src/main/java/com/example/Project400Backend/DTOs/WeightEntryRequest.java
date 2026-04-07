package com.example.Project400Backend.DTOs;

import java.time.LocalDate;

public class WeightEntryRequest {
    private Double weightKg;
    private LocalDate entryDate;
    private String note;

    public Double getWeightKg() { return weightKg; }
    public void setWeightKg(Double weightKg) { this.weightKg = weightKg; }

    public LocalDate getEntryDate() { return entryDate; }
    public void setEntryDate(LocalDate entryDate) { this.entryDate = entryDate; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}