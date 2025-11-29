package com.mapnaom.foodapp.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LdapUserDto {
    private String firstName;
    private String lastName;
    private String employeeCode;
    private String username; // sAMAccountName
}