package com.mapnaom.foodapp.controllers;

import com.mapnaom.foodapp.dtos.DailyMealDto;
import com.mapnaom.foodapp.dtos.DailyMealListDto;
import com.mapnaom.foodapp.searchForms.DailyMealSearchForm;
import com.mapnaom.foodapp.services.DailyMealService;
import com.mapnaom.foodapp.utils.ImportResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for managing DailyMeal operations.
 * <p>
 * Provides endpoints for creating, retrieving (by id and paginated), updating, and deleting DailyMeal entries.
 * </p>
 */
@CrossOrigin
@RestController
@RequestMapping("/api/daily-meals")
@RequiredArgsConstructor
@Tag(name = "Daily Meals", description = "Daily Meal management")
public class DailyMealController {
    private final DailyMealService dailyMealService;

    /**
     * Retrieves a List of DailyMeal entries for the given Jalali year and month.
     *
     * @param jYear  the optional Jalali year to filter DailyMeals.
     * @param jMonth the optional Jalali month to filter DailyMeals.
     * @return a ResponseEntity containing a List of DailyMealDto objects and HTTP status 200 (OK).
     */
    @GetMapping("/by-jYear-and-jMonth")
    @Operation(summary = "Get all daily meals by Jalali year and month")
    public ResponseEntity<List<DailyMealListDto>> getAllDailyMealListByjYearAndJMonth(
            @RequestParam(name = "jYear") Integer jYear,
            @RequestParam(name = "jMonth") Integer jMonth
    ) {
        List<DailyMealListDto> dailyMeals = dailyMealService.getAllDailyMealListByJalaliYearAndJalaliMonth(jYear, jMonth);
        return ResponseEntity.ok(dailyMeals);
    }



    /**
     * Retrieves a DailyMeal entry by its unique identifier.
     *
     * @param id the unique identifier of the DailyMeal.
     * @return a ResponseEntity containing the DailyMealDto and HTTP status 200 (OK).
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a daily meal by ID")
    public ResponseEntity<DailyMealDto> getDailyMealById(@PathVariable Long id) {
        DailyMealDto dailyMealDto = dailyMealService.getDailyMealById(id);
        return new ResponseEntity<>(dailyMealDto, HttpStatus.OK);
    }

    /**
     * Retrieves a paginated list of DailyMeal entries.
     *
     * @param page   zero-based page index (default=0).
     * @param size   the size of the page to be returned (default=10).
     * @param sortBy the property to sort by (default="id").
     * @param order  sort direction, either "ASC" (ascending) or "DESC" (descending) (default="ASC").
     * @return a ResponseEntity containing a Page of DailyMealDto objects and HTTP status 200 (OK).
     */
    @GetMapping
    @Operation(summary = "Get a paginated list of all daily meals")
    public ResponseEntity<Page<DailyMealDto>> getAllDailyMeals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String order,
            @ModelAttribute DailyMealSearchForm form
            ) {
        if (form == null) {
            form = new DailyMealSearchForm(); // Use an empty form if none provided.
        }
        Page<DailyMealDto> results = dailyMealService.getAllDailyMeals(form, page, size, sortBy, order);
        return ResponseEntity.ok(results);
    }

    /**
     * Retrieves DailyMeal entries based on the provided filter.
     *
     * @param form the search form containing filter criteria.
     * @return a list of DailyMeal DTOs matching the criteria.
     */
    @GetMapping("/select")
    @Operation(summary = "Search for daily meals based on criteria")
    public ResponseEntity<List<DailyMealDto>> searchDailyMeals(@ModelAttribute DailyMealSearchForm form) {
        List<DailyMealDto> results = dailyMealService.searchDailyMeals(form);
        return ResponseEntity.ok(results);
    }
    /**
     * Creates a new DailyMeal entry.
     *
     * @param dailyMealDto the DailyMeal data transfer object to be created.
     * @return a ResponseEntity containing the created DailyMealDto and HTTP status 201 (Created).
     */
    @PostMapping(path = {"","/"})
    @Operation(summary = "Create a new daily meal")
    public ResponseEntity<DailyMealDto> createDailyMeal(@RequestBody DailyMealDto dailyMealDto) {
        DailyMealDto createdDailyMeal = dailyMealService.createDailyMeal(dailyMealDto);
        return new ResponseEntity<>(createdDailyMeal, HttpStatus.CREATED);
    }

    /**
     * Updates an existing DailyMeal entry.
     *
     * @param id           the unique identifier of the DailyMeal to be updated.
     * @param dailyMealDto the DailyMeal data transfer object containing updated data.
     * @return a ResponseEntity containing the updated DailyMealDto and HTTP status 200 (OK).
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing daily meal")
    public ResponseEntity<DailyMealDto> updateDailyMeal(@PathVariable Long id, @RequestBody DailyMealDto dailyMealDto) {
        DailyMealDto updatedDailyMeal = dailyMealService.updateDailyMeal(id, dailyMealDto);
        return new ResponseEntity<>(updatedDailyMeal, HttpStatus.OK);
    }

    @GetMapping("/by-date/{date}")
    @Operation(summary = "Get a daily meal by date")
    public ResponseEntity<DailyMealDto> getDailyMealByDate(@PathVariable String date) {
        DailyMealDto dailyMealDto = dailyMealService.getDailyMealByDate(LocalDate.parse(date));
        return new ResponseEntity<>(dailyMealDto, HttpStatus.OK);
    }


    /**
     * Deletes a DailyMeal entry by its unique identifier.
     *
     * @param id the unique identifier of the DailyMeal to be deleted.
     * @return a ResponseEntity with HTTP status 204 (No Content) after deletion.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a daily meal by ID")
    public ResponseEntity<Void> deleteDailyMeal(@PathVariable Long id) {
        dailyMealService.deleteDailyMeal(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN') or hasRole('MEAL_MANAGER')")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            ImportResult result = dailyMealService.importDailyMealsFromExcel(file);

            if (result.getErrorCount() > 0) {
                return ResponseEntity.ok(
                        "تعداد %d وعده غذایی با موفقیت وارد شد. %d خطا رخ داد."
                                .formatted(result.getSuccessCount(), result.getErrorCount())
                );
            }

            return ResponseEntity.ok(
                    "تعداد %d وعده غذایی با موفقیت از فایل اکسل وارد شد."
                            .formatted(result.getSuccessCount())
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("خطا در وارد کردن فایل: %s".formatted(e.getMessage()));
        }
    }


}
