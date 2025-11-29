package com.mapnaom.foodapp.controllers;

import com.mapnaom.foodapp.dtos.DailyMealDishDto;
import com.mapnaom.foodapp.dtos.DishDto;
import com.mapnaom.foodapp.searchForms.DailyMealDishSearchForm;
import com.mapnaom.foodapp.services.DailyMealDishService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/dailyMealDishes")
@RequiredArgsConstructor
@Tag(name = "Daily Meal Dishes", description = "Daily Meal Dish management")
public class DailyMealDishController {

    private final DailyMealDishService dailyMealDishService;

    /**
     * Retrieves DailyMealDish entries based on the provided filter.
     *
     * @param form the search form containing filter criteria.
     * @return a list of DailyMealDish DTOs matching the criteria.
     */
    @GetMapping
    @Operation(summary = "Search for daily meal dishes based on criteria")
    public ResponseEntity<List<DailyMealDishDto>> searchDailyMealDishes(@ModelAttribute DailyMealDishSearchForm form) {
        try {
            List<DailyMealDishDto> results = dailyMealDishService.searchDailyMealDishes(form);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * Retrieves all dishes associated with a specific daily meal.
     *
     * @param dailyMealId the ID of the daily meal
     * @return ResponseEntity containing a list of DishDto objects
     */
    @GetMapping("/meal/{dailyMealId}/dishes")
    @Operation(summary = "Get all dishes for a specific daily meal")
    public ResponseEntity<List<DishDto>> getAllDishesByDailyMealId(@PathVariable Long dailyMealId) {
        try {
            List<DishDto> dishes = dailyMealDishService.getAllDishesByDailyMealId(dailyMealId);
            return ResponseEntity.ok(dishes);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
