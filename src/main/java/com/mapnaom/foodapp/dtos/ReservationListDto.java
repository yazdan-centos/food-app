package com.mapnaom.foodapp.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.mapnaom.foodapp.models.Reservation}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReservationListDto implements Serializable {
    private Long id;
    private String personnelPersCode;
    private String personnelFirstName;
    private String personnelLastName;
    private LocalDate dailyMealDishDailyMealDate;
    private String dailyMealDishDishName;
    private BigDecimal costShares;
    private LocalDateTime createdTime = LocalDateTime.now();
}