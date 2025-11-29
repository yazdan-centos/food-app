package com.mapnaom.foodapp.controllers;

import com.mapnaom.foodapp.dtos.composite.UserDailyMealsDTO;
import com.mapnaom.foodapp.services.DailyMealViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/user-daily-meals")
public class UserDailyMealsController {

    private final DailyMealViewService dailyMealViewService;

    @Autowired
    public UserDailyMealsController(DailyMealViewService dailyMealViewService) {
        this.dailyMealViewService = dailyMealViewService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public UserDailyMealsDTO getUserDailyMeals(@RequestParam int jYear, @RequestParam int jMonth) {
        return dailyMealViewService.getUserDailyMealsView(jYear, jMonth);
    }
}
