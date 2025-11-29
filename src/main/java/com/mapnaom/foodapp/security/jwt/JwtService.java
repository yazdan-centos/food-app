package com.mapnaom.foodapp.security.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.mapnaom.foodapp.security.jwt.JwtConstants.DEFAULT_ACCESS_TOKEN_EXPIRATION;

/**
 * High-level JWT service for token generation and validation.
 * Orchestrates JWT operations with business logic.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtTokenUtil jwtTokenUtil;

    @Value("${jwt.expiration:${security.jwt.expiration-time:" + DEFAULT_ACCESS_TOKEN_EXPIRATION + "}}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration:604800000}") // 7 days
    private long refreshTokenExpiration;

    /**
     * Generates an access token for the user
     */
    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "ACCESS");
        claims.put("authorities", userDetails.getAuthorities());

        return generateToken(claims, userDetails.getUsername(), accessTokenExpiration);
    }

    /**
     * Generates a refresh token for the user
     */
    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "REFRESH");

        return generateToken(claims, userDetails.getUsername(), refreshTokenExpiration);
    }

    /**
     * Validates if a token is valid for the given user
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        return jwtTokenUtil.validateTokenForUser(token, userDetails.getUsername());
    }

    /**
     * Checks if the token is a refresh token
     */
    public boolean isRefreshToken(String token) {
        return "REFRESH".equals(jwtTokenUtil.extractTokenType(token));
    }

    /**
     * Checks if the token is an access token
     */
    public boolean isAccessToken(String token) {
        return "ACCESS".equals(jwtTokenUtil.extractTokenType(token));
    }

    /**
     * Extracts username from token
     */
    public String extractUsername(String token) {
        return jwtTokenUtil.extractUsername(token);
    }

    /**
     * Extracts username ignoring expiration
     */
    public String extractUsernameIgnoringExpiration(String token) {
        return jwtTokenUtil.extractUsernameIgnoringExpiration(token);
    }

    /**
     * Gets remaining token validity in milliseconds
     */
    public long getTokenRemainingValidity(String token) {
        return jwtTokenUtil.getRemainingValidity(token);
    }

    /**
     * Validates token structure without checking expiration
     */
    public boolean isTokenWellFormed(String token) {
        return jwtTokenUtil.isTokenWellFormed(token);
    }

    /**
     * Gets the configured access token expiration time
     */
    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    /**
     * Gets the configured refresh token expiration time
     */
    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }

    /**
     * Generates a token with custom claims
     */
    private String generateToken(Map<String, Object> claims, String username, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return jwtTokenUtil.buildToken(claims, username, now, expiryDate);
    }
}