// UserResponseDto.java
package com.mapnaom.foodapp.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class UserResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private boolean enabled;
    private boolean accountLocked;
    private boolean accountExpired;
    private boolean credentialsExpired;
    private boolean tokenExpired;
    private Set<String> roles;
}