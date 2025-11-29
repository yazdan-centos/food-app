package com.mapnaom.foodapp.searchForms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonnelSearchForm {
    private Long id;
    private String username;
    private String persCode;
    private String firstName;
    private String lastName;
}
