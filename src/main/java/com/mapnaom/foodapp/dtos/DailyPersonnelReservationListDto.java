package com.mapnaom.foodapp.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mapnaom.foodapp.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.poi.hpsf.Decimal;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link com.mapnaom.foodapp.models.Reservation}
 */
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class
DailyPersonnelReservationListDto implements Serializable {
    private Long id;
    private Long personnelId;
    private String personnelPersCode;
    private String personnelFirstName;
    private String personnelLastName;
    private String dailyMealDishDishName;
    private Long dailyMealDishDishId;
    private Long dailyMealId;
    private LocalDate dailyMealDate;
    private Integer dailyMealDishDishPrice;
    private CostShareDto costShares;
    private ReservationStatus reservationStatus;
}