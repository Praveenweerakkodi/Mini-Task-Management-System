package com.taskmanager.service;

// AuthService: Handles all authentication-related logic
// register() → creates a new user account
// login() → verifies credentials and returns a JWT token

import com.taskmanager.dto.AuthResponse;
import com.taskmanager.dto.LoginRequest;
import com.taskmanager.dto.RegisterRequest;
import com.taskmanager.entity.User;
import com.taskmanager.enums.Role;
import com.taskmanager.repository.UserRepository;
import com.taskmanager.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // BCrypt from SecurityConfig
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager; // From SecurityConfig

    // ---- Register a new user ----
    public AuthResponse register(RegisterRequest request) {

        // Check if someone already registered with this email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered: " + request.getEmail());
        }

        // Determine the role - default to USER if not specified
        Role role = Role.USER; // default
        if (request.getRole() != null && !request.getRole().isBlank()) {
            try {
                role = Role.valueOf(request.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid role. Must be 'ADMIN' or 'USER'");
            }
        }

        // Create the User entity
        // IMPORTANT: We hash the password before saving! Never store plain text passwords.
        User newUser = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // BCrypt hash
                .role(role)
                .build();

        // Save the user to the database
        User savedUser = userRepository.save(newUser);

        // Generate a JWT token for the newly registered user (auto-login after register)
        String token = jwtUtil.generateToken(savedUser);

        // Return the token and user info
        return new AuthResponse(token, savedUser.getId(), savedUser.getName(), savedUser.getEmail(), savedUser.getRole().name());
    }

    // ---- Login an existing user ----
    public AuthResponse login(LoginRequest request) {

        // AuthenticationManager handles the actual verification:
        // It calls CustomUserDetailsService to load the user by email,
        // then compares the entered password with the stored BCrypt hash
        // If wrong email or password, it throws BadCredentialsException
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),    // username (email in our case)
                        request.getPassword()  // raw password (BCrypt comparison happens here)
                )
        );

        // If we reach here, authentication was SUCCESSFUL
        // Now find the user to get their details for the response
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found")); // Should never happen

        // Generate a JWT token for this user
        String token = jwtUtil.generateToken(user);

        // Return token + user info
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole().name());
    }
}
