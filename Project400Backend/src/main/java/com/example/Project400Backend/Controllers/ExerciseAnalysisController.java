package com.example.Project400Backend.Controllers;

import com.example.Project400Backend.DTOs.ExerciseSubmissionResponse;
import com.example.Project400Backend.Models.*;
import com.example.Project400Backend.Repositories.ExerciseSubmissionRepository;
import com.example.Project400Backend.Repositories.UserRepository;
import com.example.Project400Backend.Services.ExerciseAnalysisService;
import com.example.Project400Backend.Services.FileStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaTypeFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/exercise-analysis")
public class ExerciseAnalysisController {

    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024;
    private static final long MAX_VIDEO_SIZE = 50 * 1024 * 1024;

    private final ExerciseSubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final ExerciseAnalysisService exerciseAnalysisService;

    public ExerciseAnalysisController(ExerciseSubmissionRepository submissionRepository,
                                      UserRepository userRepository,
                                      FileStorageService fileStorageService,
                                      ExerciseAnalysisService exerciseAnalysisService) {
        this.submissionRepository = submissionRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
        this.exerciseAnalysisService = exerciseAnalysisService;
    }

    @PostMapping(value = "/upload", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ExerciseSubmissionResponse uploadSubmission(@RequestParam ExerciseType exerciseType,
                                                       @RequestParam MediaType mediaType,
                                                       @RequestParam(defaultValue = "SIDE") String cameraAngle,
                                                       @RequestParam("file") MultipartFile file,
                                                       Authentication authentication) {
        System.out.println("UPLOAD CONTROLLER HIT");
        System.out.println("AUTH: " + authentication);
        System.out.println("AUTH NAME: " + (authentication != null ? authentication.getName() : null));
        System.out.println("exerciseType: " + exerciseType);
        System.out.println("mediaType: " + mediaType);
        System.out.println("cameraAngle: " + cameraAngle);
        System.out.println("file null? " + (file == null));
        System.out.println("file empty? " + (file != null && file.isEmpty()));
        System.out.println("file content type: " + (file != null ? file.getContentType() : null));
        System.out.println("file original filename: " + (file != null ? file.getOriginalFilename() : null));
        System.out.println("file size: " + (file != null ? file.getSize() : null));

        User currentUser = getCurrentUser(authentication);
        System.out.println("STEP 1 - current user resolved");

        validateFile(file, mediaType);
        System.out.println("STEP 2 - file validated");

        FileStorageService.StoredFile storedFile = fileStorageService.store(file, "exercise-analysis");
        System.out.println("STEP 3 - file stored: " + storedFile.storagePath());

        ExerciseSubmission submission = new ExerciseSubmission();
        submission.setUser(currentUser);
        submission.setExerciseType(exerciseType);
        submission.setMediaType(mediaType);
        submission.setStatus(SubmissionStatus.UPLOADED);
        submission.setOriginalFilename(storedFile.originalFilename());
        submission.setStoredFilename(storedFile.storedFilename());
        submission.setStoragePath(storedFile.storagePath());
        submission.setContentType(storedFile.contentType());
        submission.setFileSize(storedFile.fileSize());
        submission.setCameraAngle(cameraAngle.toUpperCase());

        submission = submissionRepository.save(submission);
        System.out.println("STEP 4 - submission saved with id " + submission.getId());

        submission = exerciseAnalysisService.analyzeSubmission(submission);
        System.out.println("STEP 5 - analysis completed");

        return toResponse(submission);
    }

    @GetMapping
    public List<ExerciseSubmissionResponse> getUserSubmissions(Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        return submissionRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ExerciseSubmissionResponse getSubmission(@PathVariable Long id, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        ExerciseSubmission submission = submissionRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Submission not found"));
        return toResponse(submission);
    }

    private void validateFile(MultipartFile file, MediaType mediaType) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is required");
        }

        String contentType = file.getContentType();
        if (contentType == null || contentType.isBlank()) {
            contentType = MediaTypeFactory.getMediaType(file.getOriginalFilename()).map(Object::toString).orElse("");
        }

        if (mediaType == MediaType.IMAGE) {
            if (!contentType.startsWith("image/")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File must be an image");
            }
            if (file.getSize() > MAX_IMAGE_SIZE) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image exceeds 10MB limit");
            }
            return;
        }

        if (!contentType.startsWith("video/")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File must be a video");
        }
        if (file.getSize() > MAX_VIDEO_SIZE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Video exceeds 50MB limit");
        }
    }

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private ExerciseSubmissionResponse toResponse(ExerciseSubmission submission) {
        ExerciseAnalysis analysis = submission.getAnalysis();

        return new ExerciseSubmissionResponse(
                submission.getId(),
                submission.getExerciseType(),
                submission.getMediaType(),
                submission.getStatus(),
                submission.getOriginalFilename(),
                submission.getContentType(),
                submission.getFileSize(),
                submission.getCameraAngle(),
                submission.getCreatedAt(),
                submission.getProcessedAt(),
                analysis == null ? null : analysis.getOverallScore(),
                analysis == null ? List.of() : exerciseAnalysisService.parseStringList(analysis.getStrengthsJson()),
                analysis == null ? List.of() : exerciseAnalysisService.parseStringList(analysis.getImprovementsJson()),
                analysis == null ? java.util.Map.of() : exerciseAnalysisService.parseMetrics(analysis.getRawMetricsJson())
        );
    }
}
