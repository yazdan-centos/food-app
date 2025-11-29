package com.mapnaom.foodapp.services;

import com.mapnaom.foodapp.dtos.composite.UserDailyMealsDTO;
import com.mapnaom.foodapp.models.DailyMeal;
import com.mapnaom.foodapp.models.Reservation;
import com.mapnaom.foodapp.repositories.DailyMealRepository;
import com.mapnaom.foodapp.repositories.ReservationRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DailyMealViewService {

    private final DailyMealRepository dailyMealRepository;
    private final ReservationRepository reservationRepository;

    // Constructor injection
    public DailyMealViewService(DailyMealRepository dailyMealRepository, ReservationRepository reservationRepository) {
        this.dailyMealRepository = dailyMealRepository;
        this.reservationRepository = reservationRepository;
    }

    public UserDailyMealsDTO getUserDailyMealsView(int jYear, int jMonth) {
        // 1. Get the current authenticated user's username.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        // 2. Fetch daily meals for the given month and year.
        // This is accessible to anyone now, and the new endpoint handles the exposure.
        List<DailyMeal> dailyMeals = dailyMealRepository.findByJalaliYearAndJalaliMonth(jYear, jMonth);

        // 3. Fetch reservations created by the current user for the same period.
        // The Auditable class makes this simple and efficient.
        List<Reservation> userReservations = reservationRepository.findByDailyMeal_JalaliYearAndDailyMeal_JalaliMonthAndCreatedBy(jYear, jMonth, currentUsername);

        // 4. Combine the data into a single DTO.
        UserDailyMealsDTO dto = new UserDailyMealsDTO();
        dto.setDailyMeals(dailyMeals);
        dto.setUserReservations(userReservations);

        return dto;
    }
}