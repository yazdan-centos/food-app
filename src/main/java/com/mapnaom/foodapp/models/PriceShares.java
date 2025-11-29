package com.mapnaom.foodapp.models;


import jakarta.persistence.Embeddable;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;



@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceShares {

    @Column(name = "employee_price_share")
    @Min(value = 0, message = "Employee price share cannot be negative")
    @Max(value = 100, message = "Employee price share cannot exceed 100")
    private Integer employeePriceShare;

    @Column(name = "employer_price_share")
    @Min(value = 0, message = "Employer price share cannot be negative")
    @Max(value = 100, message = "Employer price share cannot exceed 100")
    private Integer employerPriceShare;
}

