package com.example.Project400Backend.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class ExerciseAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false, unique = true)
    @JsonIgnore
    private ExerciseSubmission submission;

    private Integer overallScore;

    @Column(length = 4000)
    private String strengthsJson;

    @Column(length = 4000)
    private String improvementsJson;

    @Column(length = 4000)
    private String rawMetricsJson;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public ExerciseAnalysis() {
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ExerciseSubmission getSubmission() {
        return submission;
    }

    public void setSubmission(ExerciseSubmission submission) {
        this.submission = submission;
    }

    public Integer getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(Integer overallScore) {
        this.overallScore = overallScore;
    }

    public String getStrengthsJson() {
        return strengthsJson;
    }

    public void setStrengthsJson(String strengthsJson) {
        this.strengthsJson = strengthsJson;
    }

    public String getImprovementsJson() {
        return improvementsJson;
    }

    public void setImprovementsJson(String improvementsJson) {
        this.improvementsJson = improvementsJson;
    }

    public String getRawMetricsJson() {
        return rawMetricsJson;
    }

    public void setRawMetricsJson(String rawMetricsJson) {
        this.rawMetricsJson = rawMetricsJson;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
