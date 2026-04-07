package com.example.Project400Backend.Repositories;

import com.example.Project400Backend.Models.User;
import com.example.Project400Backend.Models.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUser(User user);
}