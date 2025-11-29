package com.mapnaom.foodapp.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mapnaom.foodapp.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link CostSharesDto}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CostShareDto implements Serializable {
    private Long id;
    private Long personnelId;
    private Long dailyMealDishId;
    private Integer quantity = 1;
    private BigDecimal totalCost;
    private BigDecimal employeePortion;
    private BigDecimal employerPortion;
    private BigDecimal employeeSharePercentage;
    private ReservationStatus status;
}