package com.mapnaom.foodapp.searchForms;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserSearchForm {
    private Long id;
    private String username;
    private String persCode;
    private String firstName;
    private String lastName;
    private Boolean enabled;
    private Boolean accountLocked;
    private Boolean tokenExpired;
    private Boolean accountExpired;
    private Boolean credentialsExpired;
}
