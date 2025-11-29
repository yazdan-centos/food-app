// AssignRoleRequestDto.java
package com.mapnaom.foodapp.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AssignRoleRequestDto {
    @NotBlank(message = "Role name is required")
    private String roleName;
}