package com.taskmanager.security;

// JwtUtil: A utility class that handles everything related to JWT tokens
// JWT = JSON Web Token
// It has 3 parts separated by dots: header.payload.signature
// Example: eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGVtYWlsLmNvbSJ9.abc123

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

// @Component tells Spring to manage this class as a bean (we can @Autowire it anywhere)
@Component
public class JwtUtil {

    // These values are read from application.properties which reads from environment variables
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    // ---- Token Generation ----

    // Generate a JWT token for a logged-in user
    // Claims are extra data we embed in the token (like user's role)
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();
        // We stored role as string in the token so the frontend can read it
        extraClaims.put("role", userDetails.getAuthorities().iterator().next().getAuthority());
        return buildToken(extraClaims, userDetails);
    }

    // Build the actual JWT token string
    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)                     // Add extra data (role)
                .setSubject(userDetails.getUsername())      // Subject = email (identifies the user)
                .setIssuedAt(new Date(System.currentTimeMillis())) // When token was created
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // When token expires
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Sign with our secret key
                .compact(); // Build the final token string
    }

    // ---- Token Validation ----

    // Check if the token is valid for this user (correct user + not expired)
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        // Token is valid if: email matches AND token is not expired
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // ---- Data Extraction from Token ----

    // Get the email (subject) from the token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Get the expiration date from the token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Generic method to extract any piece of data from the token's claims
    // claimsResolver is a function that says "what data do you want from the claims?"
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Parse the token and get all claims (the data inside the token)
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // Use our secret key to verify the signature
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Check if the token's expiration time has passed
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Convert our secret string into a proper cryptographic key
    // The key is used to sign tokens so nobody can fake them without knowing the secret
    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
