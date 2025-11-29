package com.mapnaom.foodapp.dtos.composite;

import com.mapnaom.foodapp.models.DailyMeal;
import com.mapnaom.foodapp.models.Reservation;
import lombok.Data;

import java.util.List;
@Data
public class UserDailyMealsDTO {
    private List<DailyMeal> dailyMeals;
    private List<Reservation> userReservations;
}
