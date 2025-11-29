package com.mapnaom.foodapp.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class for privilege-based security checks.
 */
@Component
public class PrivilegeChecker {

    /**
     * Check if the current user has a specific privilege.
     */
    public boolean hasPrivilege(String privilege) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }

        return authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(privilege));
    }

    /**
     * Check if the current user has any of the specified privileges.
     */
    public boolean hasAnyPrivilege(String... privileges) {
        Set<String> privilegeSet = Set.of(privileges);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }

        return authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(privilegeSet::contains);
    }

    /**
     * Check if the current user has all of the specified privileges.
     */
    public boolean hasAllPrivileges(String... privileges) {
        Set<String> privilegeSet = Set.of(privileges);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }

        Set<String> userAuthorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return userAuthorities.containsAll(privilegeSet);
    }

    /**
     * Get current user's privileges.
     */
    public Set<String> getCurrentUserPrivileges() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Set.of();
        }

        return authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }
}
