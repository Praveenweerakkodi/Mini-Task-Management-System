package com.taskmanager.security;

// JwtAuthFilter: A filter that runs on EVERY incoming HTTP request
// It checks if the request has a valid JWT token in the Authorization header
// If valid: it sets the user as authenticated in Spring Security's context
// If not: it just passes the request along (Spring Security will then block protected endpoints)
//
// Flow: Request → JwtAuthFilter → SecurityConfig (decide allow/deny) → Controller

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
// OncePerRequestFilter guarantees this filter runs exactly once per request
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain // filterChain lets the request continue to the next filter/controller
    ) throws ServletException, IOException {

        // Step 1: Get the Authorization header from the request
        // Format: "Authorization: Bearer eyJhbGci..."
        final String authHeader = request.getHeader("Authorization");

        // Step 2: If no token provided, skip this filter (let the request continue)
        // Spring Security will reject it if the endpoint requires authentication
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Pass to next filter
            return;
        }

        // Step 3: Extract the token (remove "Bearer " prefix, which is 7 characters)
        final String jwt = authHeader.substring(7);

        // Step 4: Extract email from the token
        final String userEmail = jwtUtil.extractUsername(jwt);

        // Step 5: If we got an email AND the user is not already authenticated
        // SecurityContextHolder.getContext().getAuthentication() == null means "no user logged in yet"
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Step 6: Load user details from database using the email
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // Step 7: Check if the token is valid for this user
            if (jwtUtil.isTokenValid(jwt, userDetails)) {

                // Step 8: Create an authentication object that Spring Security understands
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,        // The authenticated user
                        null,               // Credentials (not needed after authentication)
                        userDetails.getAuthorities() // User's roles/permissions
                );

                // Add extra request details to the auth token
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Step 9: Tell Spring Security this user is authenticated
                // Any following code in this request can call SecurityContextHolder.getContext().getAuthentication()
                // to get the current user
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Step 10: Continue with the next filter in the chain (or reach the controller)
        filterChain.doFilter(request, response);
    }
}
