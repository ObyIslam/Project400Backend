package com.example.Project400Backend.DTOs;

import com.example.Project400Backend.Models.ExerciseType;
import com.example.Project400Backend.Models.MediaType;
import com.example.Project400Backend.Models.SubmissionStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record ExerciseSubmissionResponse(
        Long id,
        ExerciseType exerciseType,
        MediaType mediaType,
        SubmissionStatus status,
        String originalFilename,
        String contentType,
        long fileSize,
        String cameraAngle,
        LocalDateTime createdAt,
        LocalDateTime processedAt,
        Integer overallScore,
        List<String> strengths,
        List<String> improvements,
        Map<String, Object> metrics
) {
}
