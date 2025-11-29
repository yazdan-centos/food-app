package com.mapnaom.foodapp.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mapnaom.foodapp.enums.ReservationStatus;
import lombok.Data;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReservationDto {
    private Long id;
    private String username;
    private Long dailyMealId;
    private Long dailyMealDishId;
    private BigDecimal costShares;
    private ReservationStatus reservationStatus;
}
