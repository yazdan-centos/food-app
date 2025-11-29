package com.mapnaom.foodapp.config;


import jakarta.validation.constraints.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
public class AuditingConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        return new AuditorAwareImpl();
    }

    public static class AuditorAwareImpl implements AuditorAware<String> {

        /**
         * Returns the current auditor (e.g., username) from the security context.
         *
         * @return An Optional containing the current auditor's name, or Optional.empty() if none is found.
         */
        @Override
        public @NotNull Optional<String> getCurrentAuditor() {
            // Get the current authentication object from the SecurityContext
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
                // No authenticated user, return empty to let the database handle default if any
                return Optional.empty();
            }

            // Assuming the principal is a Spring Security User object
            // Adjust this line based on your actual UserDetails implementation
            return Optional.of(authentication.getName());
        }
    }
}