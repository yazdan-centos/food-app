package com.mapnaom.foodapp.security.jwt;

import com.mapnaom.foodapp.security.jwt.dtos.*;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for JWT authentication endpoints.
 * Handles HTTP requests and delegates to services.
 */
@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationController {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;

    /**
     * Authenticates user and returns JWT tokens
     */
    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@Valid @RequestBody AuthenticationRequest request) {
        log.debug("Authentication attempt for user: {}", request.getUsername());

        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Generate tokens
            String accessToken = jwtService.generateAccessToken(userDetails);
            String refreshToken = jwtService.generateRefreshToken(userDetails);

            // Build response
            AuthenticationResponse response = AuthenticationResponse.builder()
                    .token(accessToken)
                    .refreshToken(refreshToken)
                    .type("Bearer")
                    .username(userDetails.getUsername())
                    .authorities(userDetails.getAuthorities().stream()
                            .map(Object::toString)
                            .toList())
                    .expiresIn(jwtService.getAccessTokenExpiration())
                    .build();

            log.info("User {} authenticated successfully", request.getUsername());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Authentication failed for user: {}", request.getUsername(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Authentication failed", "AUTH_FAILED"));
        }
    }

    /**
     * Refreshes the access token using a valid refresh token
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            String refreshToken = request.getRefreshToken();

            // Validate refresh token format
            if (!jwtService.isTokenWellFormed(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Invalid refresh token", "INVALID_TOKEN"));
            }

            // Check if it's actually a refresh token
            if (!jwtService.isRefreshToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Token is not a refresh token", "NOT_REFRESH_TOKEN"));
            }

            // Extract username from refresh token
            String username = jwtService.extractUsernameIgnoringExpiration(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Validate refresh token
            if (!jwtService.isTokenValid(refreshToken, userDetails)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Refresh token is expired or invalid", "EXPIRED_REFRESH_TOKEN"));
            }

            // Generate new access token
            String newAccessToken = jwtService.generateAccessToken(userDetails);

            RefreshTokenResponse response = RefreshTokenResponse.builder()
                    .token(newAccessToken)
                    .type("Bearer")
                    .expiresIn(jwtService.getAccessTokenExpiration())
                    .build();

            log.info("Token refreshed successfully for user: {}", username);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error refreshing token", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to refresh token", "REFRESH_ERROR"));
        }
    }

    /**
     * Validates the current token
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Missing or invalid Authorization header", "NO_TOKEN"));
        }

        String token = authHeader.substring(7);

        try {
            String username = jwtService.extractUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenValid(token, userDetails)) {
                long remainingValidity = jwtService.getTokenRemainingValidity(token);

                TokenValidationResponse response = TokenValidationResponse.builder()
                        .valid(true)
                        .username(username)
                        .remainingValidityMs(remainingValidity)
                        .authorities(userDetails.getAuthorities().stream()
                                .map(Object::toString)
                                .toList())
                        .build();

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new TokenValidationResponse(false, "Token is invalid or expired"));
            }

        } catch (JwtException e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new TokenValidationResponse(false, "Invalid token format"));
        } catch (Exception e) {
            log.error("Error validating token", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Token validation failed", "VALIDATION_ERROR"));
        }
    }

    /**
     * Logs out the user (client should remove the token)
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            // Try to log username, but don't fail if token is invalid
            try {
                String username = jwtService.extractUsernameIgnoringExpiration(token);
                log.info("User {} logged out", username);
            } catch (Exception e) {
                log.debug("Could not extract username from token during logout");
            }
        }

        SecurityContextHolder.clearContext();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        response.put("success", true);

        return ResponseEntity.ok(response);
    }

    /**
     * Gets the current user information from the token
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal() == null ||
                !(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Not authenticated", "NO_AUTH"));
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        CurrentUserResponse response = CurrentUserResponse.builder()
                .username(userDetails.getUsername())
                .authorities(userDetails.getAuthorities().stream()
                        .map(Object::toString)
                        .toList())
                .enabled(userDetails.isEnabled())
                .accountNonExpired(userDetails.isAccountNonExpired())
                .accountNonLocked(userDetails.isAccountNonLocked())
                .credentialsNonExpired(userDetails.isCredentialsNonExpired())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint for authentication service
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Authentication Service");
        health.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(health);
    }
}