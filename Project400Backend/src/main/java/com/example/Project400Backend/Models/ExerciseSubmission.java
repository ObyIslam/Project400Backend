package com.example.Project400Backend.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class ExerciseSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExerciseType exerciseType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaType mediaType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubmissionStatus status;

    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false)
    private String storedFilename;

    @Column(nullable = false)
    private String storagePath;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private long fileSize;

    @Column(nullable = false)
    private String cameraAngle;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime processedAt;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ExerciseAnalysis analysis;

    public ExerciseSubmission() {
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

    public ExerciseType getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(ExerciseType exerciseType) {
        this.exerciseType = exerciseType;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public SubmissionStatus getStatus() {
        return status;
    }

    public void setStatus(SubmissionStatus status) {
        this.status = status;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getStoredFilename() {
        return storedFilename;
    }

    public void setStoredFilename(String storedFilename) {
        this.storedFilename = storedFilename;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getCameraAngle() {
        return cameraAngle;
    }

    public void setCameraAngle(String cameraAngle) {
        this.cameraAngle = cameraAngle;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ExerciseAnalysis getAnalysis() {
        return analysis;
    }

    public void setAnalysis(ExerciseAnalysis analysis) {
        this.analysis = analysis;
    }
}
