package com.mapnaom.foodapp.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mapnaom.foodapp.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link User}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
}