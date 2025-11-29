package com.mapnaom.foodapp.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serial;
import java.io.Serializable;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import static com.mapnaom.foodapp.security.jwt.JwtConstants.DEFAULT_ACCESS_TOKEN_EXPIRATION;

/**
 * Low-level JWT token utility for parsing and validating tokens.
 * Responsible for JWT-specific operations only.
 */
@Slf4j
@Component
public class JwtTokenUtil implements Serializable {

    @Serial
    private static final long serialVersionUID = 464214880478737476L;

    @Value("${jwt.secret:${security.jwt.secret-key:}}")
    private String secret;

    @Value("${jwt.clock-skew-seconds:300}")
    private int clockSkewSeconds;

    /**
     * Parses and extracts a specific claim from the token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts username from token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts username even from expired tokens
     */
    public String extractUsernameIgnoringExpiration(String token) {
        try {
            return extractUsername(token);
        } catch (ExpiredJwtException e) {
            return e.getClaims().getSubject();
        }
    }

    /**
     * Extracts expiration date from token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts token type (ACCESS or REFRESH) from claims
     */
    public String extractTokenType(String token) {
        try {
            return extractClaim(token, claims -> claims.get("type", String.class));
        } catch (ExpiredJwtException e) {
            return e.getClaims().get("type", String.class);
        } catch (Exception e) {
            log.error("Error extracting token type", e);
            return null;
        }
    }

    /**
     * Checks if token is expired
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            log.error("Error checking token expiration", e);
            return true;
        }
    }

    /**
     * Validates token structure and signature (ignores expiration)
     */
    public boolean isTokenWellFormed(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            return true; // Structure is valid, just expired
        } catch (SignatureException | MalformedJwtException |
                 UnsupportedJwtException | IllegalArgumentException e) {
            log.debug("Invalid token structure: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Validates token against username
     */
    public boolean validateTokenForUser(String token, String username) {
        try {
            final String tokenUsername = extractUsername(token);
            return tokenUsername.equals(username) && !isTokenExpired(token);
        } catch (Exception e) {
            log.error("Token validation failed", e);
            return false;
        }
    }

    /**
     * Calculates remaining validity in milliseconds
     */
    public long getRemainingValidity(String token) {
        try {
            Date expiration = extractExpiration(token);
            long remainingTime = expiration.getTime() - System.currentTimeMillis();
            return Math.max(0, remainingTime);
        } catch (ExpiredJwtException e) {
            return 0;
        } catch (Exception e) {
            log.error("Error calculating remaining validity", e);
            return 0;
        }
    }

    /**
     * Builds a JWT token with the given parameters
     */
    public String buildToken(Map<String, Object> claims, String subject, Date issuedAt, Date expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts all claims from token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .setAllowedClockSkewSeconds(clockSkewSeconds)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Gets the signing key from secret
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}