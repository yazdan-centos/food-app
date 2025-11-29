package com.mapnaom.foodapp.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * DTO for {@link com.mapnaom.foodapp.models.DailyMeal}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DailyMealExcelDto implements Serializable {
    private String date; // Jalali date as string, e.g., "1404/06/29"
    private Long dishId;
}