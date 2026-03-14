package com.taskmanager.controller;

// AuthController: Handles user registration and login
// These endpoints are PUBLIC - no JWT token required
// Endpoints:
//   POST /api/auth/register → Register new user
//   POST /api/auth/login    → Login and get JWT token

import com.taskmanager.dto.AuthResponse;
import com.taskmanager.dto.LoginRequest;
import com.taskmanager.dto.RegisterRequest;
import com.taskmanager.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// @RestController = @Controller + @ResponseBody
// All methods return JSON automatically
@RestController
// @RequestMapping defines the base URL for all methods in this controller
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // POST /api/auth/register
    // @RequestBody: reads the JSON from the request body
    // @Valid: triggers validation (checks @NotBlank, @Email, etc. in RegisterRequest)
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        // 201 CREATED is the appropriate status code when a new resource is created
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        // 200 OK for successful login
        return ResponseEntity.ok(response);
    }
}
