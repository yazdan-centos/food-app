package com.mapnaom.foodapp.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Collection;

/**
 * DTO for {@link com.mapnaom.foodapp.models.User}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String employeeCode;
    private String password;
    private boolean enabled = true;
    private boolean tokenExpired = false;
    private boolean accountLocked = false;
    private boolean accountExpired = false;
    private boolean credentialsExpired = false;
    private Collection<RoleDto> roles;
}