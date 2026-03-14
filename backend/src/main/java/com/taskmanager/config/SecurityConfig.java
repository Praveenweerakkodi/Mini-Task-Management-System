package com.taskmanager.config;

// SecurityConfig: This is where we configure Spring Security
// We define:
// 1. Which endpoints are public (no auth needed) and which are protected
// 2. How to authenticate users (using JWT filter)
// 3. CORS settings (allow frontend to call backend)
// 4. Password encryption method

import com.taskmanager.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration // Marks this as a config class (Spring reads this on startup)
@EnableWebSecurity // Activates Spring Security
@EnableMethodSecurity // Allows @PreAuthorize annotations on controller methods
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Value("${frontend.url}")
    private String frontendUrl;

    // ---- Main Security Filter Chain ----
    // This method defines the security rules for our application
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF (Cross-Site Request Forgery) protection
            // CSRF is not needed for REST APIs using JWT (it protects form-based logins)
            .csrf(csrf -> csrf.disable())

            // Configure CORS using our corsConfigurationSource bean below
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // Define which endpoints require authentication and which don't
            .authorizeHttpRequests(auth -> auth
                // These endpoints are PUBLIC - anyone can access them (for login/register)
                .requestMatchers("/api/auth/**").permitAll()
                // All other endpoints REQUIRE a valid JWT token
                .anyRequest().authenticated()
            )

            // Use STATELESS sessions - we don't use server-side sessions
            // Each request must include the JWT token (the server doesn't "remember" users)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Set our custom authentication provider (uses our DB + BCrypt)
            .authenticationProvider(authenticationProvider())

            // Add our JWT filter BEFORE Spring's default username/password filter
            // This means JWT validation happens before Spring tries its own authentication
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ---- Authentication Provider ----
    // Tells Spring Security HOW to authenticate:
    // - WHERE to find users (our UserDetailsService which checks the database)
    // - HOW to check passwords (BCryptPasswordEncoder)
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // Use our custom service
        authProvider.setPasswordEncoder(passwordEncoder());     // Use BCrypt for password comparison
        return authProvider;
    }

    // ---- Password Encoder ----
    // BCrypt is a secure hashing algorithm for passwords
    // When user registers: BCrypt hashes their password before saving to DB
    // When user logs in: BCrypt compares entered password with stored hash
    // We NEVER store plain text passwords!
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ---- Authentication Manager ----
    // Used in AuthService to trigger the actual login/authentication process
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // ---- CORS Configuration ----
    // CORS = Cross-Origin Resource Sharing
    // Browsers block requests from one origin (localhost:3000) to another (localhost:8080)
    // This configuration tells the browser: "It's okay for localhost:3000 to talk to us"
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow requests from our frontend
        configuration.setAllowedOrigins(List.of(frontendUrl));

        // Allow specific HTTP methods
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // Allow all headers in requests (including Authorization header with JWT)
        configuration.setAllowedHeaders(List.of("*"));

        // Allow cookies/credentials to be sent
        configuration.setAllowCredentials(true);

        // Apply this configuration to all API paths
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
