package com.mapnaom.foodapp.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppSettingDTO {

    private Long id;
    private Boolean foodPricesActive = true;
    @Valid
    private PriceSharesDTO priceShares;
    @Valid
    @NotNull(message = "Reservation time is required")
    private ReservationTimeDTO reservationTime;

    @NotBlank(message = "Company name is required")
    @Size(max = 500, message = "Company name cannot exceed 500 characters")
    private String companyName;

    @NotBlank(message = "Address is required")
    @Size(max = 1000, message = "Address cannot exceed 1000 characters")
    private String address;


    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    private String phone;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriceSharesDTO {
        @Min(value = 0, message = "Employee price share cannot be negative")
        @Max(value = 100, message = "Employee price share cannot exceed 100")
        private Integer employeePriceShare;

        @Min(value = 0, message = "Employer price share cannot be negative")
        @Max(value = 100, message = "Employer price share cannot exceed 100")
        private Integer employerPriceShare;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReservationTimeDTO {
        @NotNull(message = "Hour is required")
        @Min(value = 0, message = "Hour must be between 0 and 23")
        @Max(value = 23, message = "Hour must be between 0 and 23")
        private Integer hour;

        @NotNull(message = "Minute is required")
        @Min(value = 0, message = "Minute must be between 0 and 59")
        @Max(value = 59, message = "Minute must be between 0 and 59")
        private Integer minute;
    }
}

