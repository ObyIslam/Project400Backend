package com.example.Project400Backend.Services;

import com.example.Project400Backend.Models.ExerciseAnalysis;
import com.example.Project400Backend.Models.ExerciseSubmission;
import com.example.Project400Backend.Models.ExerciseType;
import com.example.Project400Backend.Models.SubmissionStatus;
import com.example.Project400Backend.Repositories.ExerciseAnalysisRepository;
import com.example.Project400Backend.Repositories.ExerciseSubmissionRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExerciseAnalysisService {

    private final ExerciseSubmissionRepository submissionRepository;
    private final ExerciseAnalysisRepository analysisRepository;
    private final ObjectMapper objectMapper;

    public ExerciseAnalysisService(ExerciseSubmissionRepository submissionRepository,
                                   ExerciseAnalysisRepository analysisRepository,
                                   ObjectMapper objectMapper) {
        this.submissionRepository = submissionRepository;
        this.analysisRepository = analysisRepository;
        this.objectMapper = objectMapper;
    }

    public ExerciseSubmission analyzeSubmission(ExerciseSubmission submission) {
        submission.setStatus(SubmissionStatus.PROCESSING);
        submissionRepository.save(submission);

        try {
            MockAnalysisResult result = createMockResult(submission.getExerciseType());

            ExerciseAnalysis analysis = submission.getAnalysis();
            if (analysis == null) {
                analysis = new ExerciseAnalysis();
                analysis.setSubmission(submission);
            }

            analysis.setOverallScore(result.overallScore());
            analysis.setStrengthsJson(writeJson(result.strengths()));
            analysis.setImprovementsJson(writeJson(result.improvements()));
            analysis.setRawMetricsJson(writeJson(result.metrics()));
            analysisRepository.save(analysis);

            submission.setAnalysis(analysis);
            submission.setProcessedAt(LocalDateTime.now());
            submission.setStatus(SubmissionStatus.COMPLETED);
            return submissionRepository.save(submission);
        } catch (Exception e) {
            submission.setStatus(SubmissionStatus.FAILED);
            submission.setProcessedAt(LocalDateTime.now());
            submissionRepository.save(submission);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Analysis failed", e);
        }
    }

    public List<String> parseStringList(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        return objectMapper.readValue(json, new TypeReference<>() {});
    }

    public Map<String, Object> parseMetrics(String json) {
        if (json == null || json.isBlank()) {
            return Map.of();
        }
        return objectMapper.readValue(json, new TypeReference<>() {});
    }

    private String writeJson(Object value) {
        return objectMapper.writeValueAsString(value);
    }

    private MockAnalysisResult createMockResult(ExerciseType exerciseType) {
        if (exerciseType == ExerciseType.SQUAT) {
            Map<String, Object> metrics = new LinkedHashMap<>();
            metrics.put("depthReached", true);
            metrics.put("torsoLeanAtBottom", 36.4);
            metrics.put("kneeValgusDetected", true);
            metrics.put("heelLiftDetected", false);
            metrics.put("tempo", "controlled");

            return new MockAnalysisResult(
                    78,
                    List.of(
                            "Good squat depth",
                            "Descent looks controlled",
                            "Feet stay planted for most of the rep"
                    ),
                    List.of(
                            "Knees move inward slightly near the bottom",
                            "Chest drops a bit too much on descent",
                            "Try to drive chest and hips up together"
                    ),
                    metrics
            );
        }

        Map<String, Object> metrics = new LinkedHashMap<>();
        metrics.put("analysisMode", "mock");
        metrics.put("exerciseType", exerciseType.name());

        return new MockAnalysisResult(
                72,
                List.of(
                        "Movement is mostly controlled",
                        "Setup appears reasonably stable"
                ),
                List.of(
                        "This exercise still uses placeholder analysis",
                        "Add pose-estimation rules for more accurate feedback"
                ),
                metrics
        );
    }

    private record MockAnalysisResult(
            Integer overallScore,
            List<String> strengths,
            List<String> improvements,
            Map<String, Object> metrics
    ) {
    }
}
