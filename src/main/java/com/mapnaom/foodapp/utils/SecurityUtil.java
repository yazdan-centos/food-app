package com.mapnaom.foodapp.utils;

import java.util.Optional;

public class SecurityUtil {
    private SecurityUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Retrieves the username of the currently authenticated user from the Spring Security context.
     *
     * @return An {@link Optional} containing the username if a user is authenticated,
     *         otherwise an empty {@link Optional}.
     */
    public static Optional<String> getCurrentUsername() {
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            return Optional.of(((org.springframework.security.core.userdetails.UserDetails) principal).getUsername());
        } else if (principal instanceof String) {
            return Optional.of((String) principal);
        }

        return Optional.empty();
    }

}
