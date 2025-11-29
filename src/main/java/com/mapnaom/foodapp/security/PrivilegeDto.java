package com.mapnaom.foodapp.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mapnaom.foodapp.models.Privilege;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * DTO for {@link Privilege}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrivilegeDto implements Serializable {
    private Long id;
    private String name;
}