package com.mapnaom.foodapp.searchForms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mapnaom.foodapp.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReservationSearchForm {
    private Long id;
    private String personnelPersCode;
    private String personnelFirstName;
    private String personnelLastName;
    private LocalDate dailyMealDishDailyMealDate;
    private String dailyMealDishDishName;
    private LocalDateTime createdTime;
    private ReservationStatus reservationStatus;
}
/**
 * dailyMealDishDishName -> dailyMealDish.dish.name
 * dailyMealDishDailyMealMealType -> dailyMealDish.dailyMeal.mealType
 * dailyMealDishDailyMealDate -> dailyMealDish.dailyMeal.date
 * personnelName -> personnel.firstName
 * personnelLastName -> personnel.lastName
 * personnelPersCode -> personnel.persCode
 * */
