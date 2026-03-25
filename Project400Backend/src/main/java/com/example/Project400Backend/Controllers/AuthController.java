package com.example.Project400Backend.Controllers;

import com.example.Project400Backend.Auth.AuthResponse;
import com.example.Project400Backend.Auth.LoginRequest;
import com.example.Project400Backend.Auth.RegisterRequest;
import com.example.Project400Backend.Models.User;
import com.example.Project400Backend.Repositories.UserRepository;
import com.example.Project400Backend.Security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService,
                          AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        String email = normalizeEmail(request.getEmail());
        validateRegisterRequest(request, email);

        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already in use");
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser.getEmail(), Map.of(
                "userId", savedUser.getId(),
                "name", savedUser.getName()
        ));

        return new AuthResponse(token, savedUser.getId(), savedUser.getName(), savedUser.getEmail());
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        String email = normalizeEmail(request.getEmail());
        if (email == null || request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email and password are required");
        }

        authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(email, request.getPassword())
        );

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        String token = jwtService.generateToken(user.getEmail(), Map.of(
                "userId", user.getId(),
                "name", user.getName()
        ));

        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail());
    }

    @GetMapping("/me")
    public AuthResponse me(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing bearer token");
        }

        String token = authorizationHeader.substring(7);
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail());
    }

    private void validateRegisterRequest(RegisterRequest request, String email) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is required");
        }
        if (email == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valid email is required");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be at least 6 characters");
        }
    }

    private String normalizeEmail(String email) {
        if (email == null) {
            return null;
        }
        String normalized = email.trim().toLowerCase();
        return normalized.contains("@") ? normalized : null;
    }
}
