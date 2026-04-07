package com.example.Project400Backend.Controllers;

import com.example.Project400Backend.DTOs.ProfileRequest;
import com.example.Project400Backend.DTOs.ProfileResponse;
import com.example.Project400Backend.DTOs.WeightEntryRequest;
import com.example.Project400Backend.DTOs.WeightEntryResponse;
import com.example.Project400Backend.Models.User;
import com.example.Project400Backend.Models.UserProfile;
import com.example.Project400Backend.Models.WeightEntry;

import com.example.Project400Backend.Repositories.UserProfileRepository;
import com.example.Project400Backend.Repositories.UserRepository;
import com.example.Project400Backend.Repositories.WeightEntryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final WeightEntryRepository weightEntryRepository;

    public ProfileController(UserRepository userRepository,
                             UserProfileRepository userProfileRepository,
                             WeightEntryRepository weightEntryRepository) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.weightEntryRepository = weightEntryRepository;
    }

    private User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    @GetMapping
    public ProfileResponse getProfile(Authentication authentication) {
        User user = getCurrentUser(authentication);

        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseGet(() -> {
                    UserProfile newProfile = new UserProfile();
                    newProfile.setUser(user);
                    return userProfileRepository.save(newProfile);
                });

        Double latestWeight = weightEntryRepository.findByUserOrderByEntryDateDesc(user)
                .stream()
                .findFirst()
                .map(WeightEntry::getWeightKg)
                .orElse(null);

        return new ProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                profile.getAge(),
                profile.getHeightCm(),
                profile.getStartingWeightKg(),
                profile.getGoalWeightKg(),
                profile.getDateOfBirth(),
                latestWeight
        );
    }

    @PutMapping
    public ProfileResponse updateProfile(@RequestBody ProfileRequest request,
                                         Authentication authentication) {
        User user = getCurrentUser(authentication);

        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseGet(() -> {
                    UserProfile newProfile = new UserProfile();
                    newProfile.setUser(user);
                    return newProfile;
                });

        profile.setAge(request.getAge());
        profile.setHeightCm(request.getHeightCm());
        profile.setStartingWeightKg(request.getStartingWeightKg());
        profile.setGoalWeightKg(request.getGoalWeightKg());
        profile.setDateOfBirth(request.getDateOfBirth());

        UserProfile saved = userProfileRepository.save(profile);

        Double latestWeight = weightEntryRepository.findByUserOrderByEntryDateDesc(user)
                .stream()
                .findFirst()
                .map(WeightEntry::getWeightKg)
                .orElse(null);

        return new ProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                saved.getAge(),
                saved.getHeightCm(),
                saved.getStartingWeightKg(),
                saved.getGoalWeightKg(),
                saved.getDateOfBirth(),
                latestWeight
        );
    }

    @GetMapping("/weights")
    public List<WeightEntryResponse> getWeights(Authentication authentication) {
        User user = getCurrentUser(authentication);

        return weightEntryRepository.findByUserOrderByEntryDateAsc(user)
                .stream()
                .map(entry -> new WeightEntryResponse(
                        entry.getId(),
                        entry.getWeightKg(),
                        entry.getEntryDate(),
                        entry.getNote()
                ))
                .toList();
    }

    @PostMapping("/weights")
    public WeightEntryResponse addWeight(@RequestBody WeightEntryRequest request,
                                         Authentication authentication) {
        User user = getCurrentUser(authentication);

        if (request.getWeightKg() == null || request.getWeightKg() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Weight must be greater than 0");
        }

        WeightEntry entry = new WeightEntry();
        entry.setUser(user);
        entry.setWeightKg(request.getWeightKg());
        entry.setEntryDate(request.getEntryDate() != null ? request.getEntryDate() : LocalDate.now());
        entry.setNote(request.getNote());

        WeightEntry saved = weightEntryRepository.save(entry);

        return new WeightEntryResponse(
                saved.getId(),
                saved.getWeightKg(),
                saved.getEntryDate(),
                saved.getNote()
        );
    }

    @DeleteMapping("/weights/{id}")
    public void deleteWeight(@PathVariable Long id, Authentication authentication) {
        User user = getCurrentUser(authentication);

        WeightEntry entry = weightEntryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Weight entry not found"));

        if (!entry.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed");
        }

        weightEntryRepository.delete(entry);
    }
}