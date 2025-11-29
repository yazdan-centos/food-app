package com.mapnaom.foodapp.services;

import com.github.eloyzone.jalalicalendar.DateConverter;
import com.github.eloyzone.jalalicalendar.JalaliDate;
import com.mapnaom.foodapp.dtos.*;
import com.mapnaom.foodapp.exceptions.DishNotFoundException;
import com.mapnaom.foodapp.exceptions.DuplicateDailyMealByDateException;
import com.mapnaom.foodapp.exceptions.DuplicateDishException;
import com.mapnaom.foodapp.exceptions.DuplicateDishInDailyMealException;
import com.mapnaom.foodapp.mappers.DailyMealListMapper;
import com.mapnaom.foodapp.mappers.DailyMealMapper;
import com.mapnaom.foodapp.models.DailyMeal;
import com.mapnaom.foodapp.models.DailyMealDish;
import com.mapnaom.foodapp.models.Dish;
import com.mapnaom.foodapp.repositories.DailyMealRepository;
import com.mapnaom.foodapp.repositories.DishRepository;
import com.mapnaom.foodapp.repositories.ReservationRepository;
import com.mapnaom.foodapp.searchForms.DailyMealSearchForm;
import com.mapnaom.foodapp.specifications.DailyMealSpecification;
import com.mapnaom.foodapp.utils.ExcelUtil;
import com.mapnaom.foodapp.utils.ImportOptions;
import com.mapnaom.foodapp.utils.ImportResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.web.multipart.MultipartFile;

/**
 * Service layer for managing DailyMeal operations.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class DailyMealService {

    private final DailyMealRepository dailyMealRepository;
    private final DailyMealMapper dailyMealMapper;
    private final DishRepository dishRepository;
    private final ReservationRepository reservationRepository;
    private final DailyMealListMapper dailyMealListMapper;

    /**
     * Retrieves a DailyMeal by its ID.
     *
     * @param id The ID of the DailyMeal to retrieve.
     * @return The retrieved DailyMeal as a DTO.
     * @throws RuntimeException if no DailyMeal is found with the given ID.
     */
    public DailyMealDto getDailyMealById(Long id) {
        DailyMeal dailyMeal = dailyMealRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("منو روزانه‌ای با شناسه " + id + " یافت نشد.")
                );
        return dailyMealMapper.toDto(dailyMeal);
    }

    /**
     * Retrieves a DailyMeal by its date.
     *
     * @param date The date of the DailyMeal to retrieve.
     * @return The retrieved DailyMeal as a DTO.
     */
    public DailyMealDto getDailyMealByDate(LocalDate date) {
        Optional<DailyMeal> dailyMeal = dailyMealRepository.findDailyMealByDate(date);
        return dailyMeal.map(dailyMealMapper::toDto).orElse(null);
    }

    /**
     * Searches for DailyMeals based on the provided search form.
     *
     * @param form A form containing the search filters.
     * @return A list of DailyMeals matching the search criteria.
     */
    public List<DailyMealDto> searchDailyMeals(DailyMealSearchForm form) {
        return dailyMealRepository.findAll(DailyMealSpecification.withFilter(form))
                .stream()
                .map(dailyMealMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<SelectOption> getAllMealTypes() {
        List<DailyMeal> list = dailyMealRepository.findAll();
        return list.stream()
                .map(meal ->
                        new SelectOption(meal.getId(),
                                String.format("منوی تاریخ %s - %s",
                                        convertGregorianToJalali(meal.getDate()),
                                        meal.getDate().getDayOfWeek().getDisplayName(java.time.format.TextStyle.FULL, new Locale("fa")))))
                // expect : منوی تاریح 1404/03/11 - پنجشنبه
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a paginated and sorted list of DailyMeals based on the search form.
     *
     * @param form   The search filters.
     * @param page   The page number to retrieve.
     * @param size   The number of items per page.
     * @param sortBy The field to sort by.
     * @param order  The sorting order ("ASC" or "DESC").
     * @return A paginated list of DailyMeals as DTOs.
     */
    public Page<DailyMealDto> getAllDailyMeals(DailyMealSearchForm form, int page, int size, String sortBy, String order) {
        Sort sort = order.equalsIgnoreCase("DESC") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<DailyMeal> dailyMealPage = dailyMealRepository.findAll(
                DailyMealSpecification.withFilter(form), pageRequest);
        return dailyMealPage.map(dailyMealMapper::toDto);
    }

    @Transactional
    public DailyMealDto createDailyMeal(DailyMealDto dailyMealDto) {
        LocalDate date = LocalDate.parse(
                DateTimeFormatter.ISO_DATE.format(dailyMealDto.getDate())
        );

        // ❌ Check if there is already a DailyMeal for this date
        if (dailyMealRepository.existsByDate(date)) {
            String dateJalali = convertGregorianToJalali(date);
            throw new DuplicateDailyMealByDateException(
                    "برای تاریخ " + dateJalali + " قبلا منوی روزانه ثبت شده است."
            );
        }

        DailyMeal dailyMeal = dailyMealMapper.toEntity(dailyMealDto);

        // ✅ Prevent duplicate dishes for the same day
        Set<Long> dishIds = new HashSet<>();
        dailyMeal.getDailyMealDishes().forEach(dailyMealDish -> {
            Long dishId = dailyMealDish.getDish().getId();
            if (!dishIds.add(dishId)) {
                throw new DuplicateDishInDailyMealException(
                        "غذای تکراری در منوی تاریخ " +
                                convertGregorianToJalali(date) +
                                " یافت شد: شناسه غذا " + dishId
                );
            }
        });

        // Set back-references and load dishes from DB
        dailyMeal.getDailyMealDishes().forEach(dailyMealDish -> {
            dailyMealDish.setDailyMeal(dailyMeal);
            dailyMealDish.setDish(
                    dishRepository.findById(dailyMealDish.getDish().getId())
                            .orElseThrow(() ->
                                    new DishNotFoundException(
                                            "غذا با شناسه " + dailyMealDish.getDish().getId() + " یافت نشد."
                                    )
                            )
            );
        });

        return dailyMealMapper.toDto(dailyMealRepository.save(dailyMeal));
    }


    @Transactional
    public DailyMealDto updateDailyMeal(Long id, DailyMealDto dailyMealDto) {
        // 1️⃣ Fetch the existing DailyMeal
        DailyMeal dailyMeal = dailyMealRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("منو روزانه\u200Cای با شناسه %d یافت نشد.".formatted(id))
                );

       if (dailyMealRepository.existsByDateAndIdNot(dailyMealDto.getDate(), id)) {
           String newDate = convertGregorianToJalali(dailyMealDto.getDate());
            throw new DuplicateDailyMealByDateException("منو روزانه‌ای با تاریخ " + newDate + " وجود دارد.");
       }

        Map<Long, Dish> dishMap = dishRepository.findAll().stream().collect(Collectors.toMap(Dish::getId, Function.identity()));

        List<DailyMealDishDto> duplicates = dailyMealDto.getDailyMealDishes().stream()
                .filter(d -> Collections.frequency(
                    dailyMealDto.getDailyMealDishes()
                            .stream()
                            .map(DailyMealDishDto::getDishId)
                            .toList(),
                    d.getDishId()) > 1)
                    .distinct().toList();
                       if (!duplicates.isEmpty()) {
                           throw new DuplicateDishException(
                                dishMap.get(duplicates.get(0).getDishId()).getName(),
                                dishMap.get(duplicates.get(0).getDishId()).getPrice()
                           );
                 }
        // 5️⃣ Clear old dishes and add the updated list
        dailyMeal.getDailyMealDishes().clear();
        dailyMealDto.getDailyMealDishes().forEach(d -> {
            DailyMealDish dailyMealDish = new DailyMealDish();
            dailyMealDish.setDailyMeal(dailyMeal);
            dailyMealDish.setDish(
                    dishRepository.findById(d.getDishId())
                            .orElseThrow(() ->
                                    new DishNotFoundException(
                                            "غذا با شناسه %d یافت نشد.".formatted(d.getId())
                                    )
                            )
            );
            dailyMeal.getDailyMealDishes().add(dailyMealDish);
        });

        // 6️⃣ Save and return
        return dailyMealMapper.toDto(dailyMealRepository.save(dailyMeal));
    }


    /**
     * Deletes a DailyMeal by its ID.
     *
     * @param id The ID of the DailyMeal to delete.
     * @throws RuntimeException         if the DailyMeal with the given ID does not exist.
     * @throws IllegalArgumentException if reservations are associated with the DailyMeal.
     */
    public void deleteDailyMeal(Long id) {
        // 1) یافتن منو یا خطا اگر وجود نداشته باشد
        DailyMeal dailyMeal = dailyMealRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("منو روزانه\u200Cای با شناسه %d یافت نشد.".formatted(id))
                );
        if (reservationRepository.existsAllByDailyMeal_Id(id)) {
            String dateJalali = convertGregorianToJalali(dailyMeal.getDate());
            throw new IllegalArgumentException(
                    "برای %s رزروی ثبت شده است و قابل حذف نمی\u200Cباشد.".formatted(dateJalali)
            );
        }

        // 3) حذف منو
        dailyMealRepository.delete(dailyMeal);
    }

    private static DateConverter getDateConverter() {
        return new DateConverter();
    }

    /**
     * Converts a Gregorian date to Jalali format.
     *
     * @param localDate The Gregorian date to convert.
     * @return The corresponding Jalali date as a string (YYYY/MM/DD).
     */
    public static String convertGregorianToJalali(LocalDate localDate) {
        DateConverter dateConverter = getDateConverter();
        JalaliDate jalaliDate = dateConverter.gregorianToJalali(
                localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth());
        return String.format("%d/%02d/%02d",
                jalaliDate.getYear(), jalaliDate.getMonthPersian().getValue(), jalaliDate.getDay());
    }

    /**
     * Retrieves all DailyMeals for the specified Jalali year and month.
     *
     * @param jalaliYear  The Jalali year to filter by.
     * @param jalaliMonth The Jalali month to filter by.
     * @return A list of DailyMeals sorted by date.
     * @throws IllegalArgumentException if jalaliYear or jalaliMonth is invalid
     */
    public List<DailyMealListDto> getAllDailyMealListByJalaliYearAndJalaliMonth(Integer jalaliYear, Integer jalaliMonth) {
        // Validate jalaliYear
        if (jalaliYear == null) {
            throw new IllegalArgumentException("سال جلالی نمی‌تواند خالی باشد");
        }
        if (jalaliYear < 1 || jalaliYear > 9999) {
            throw new IllegalArgumentException("سال جلالی باید بین ۱ تا ۹۹۹۹ باشد، ولی مقدار وارد شده: %d".formatted(jalaliYear));
        }

        // Validate jalaliMonth
        if (jalaliMonth == null) {
            throw new IllegalArgumentException("ماه جلالی نمی‌تواند خالی باشد");
        }
        if (jalaliMonth < 1 || jalaliMonth > 12) {
            throw new IllegalArgumentException("ماه جلالی باید بین ۱ تا ۱۲ باشد، ولی مقدار وارد شده: %d".formatted(jalaliMonth));
        }

        return dailyMealRepository.findByJalaliYearAndJalaliMonth(jalaliYear, jalaliMonth)
                .stream()
                .sorted(Comparator.comparing(DailyMeal::getDate))
                .map(dailyMealListMapper::toDto)
                .collect(Collectors.toList());
    }

    public ImportResult importDailyMealsFromExcel(MultipartFile file) throws IOException {
        ImportResult.ImportResultBuilder resultBuilder = ImportResult.builder();
        List<String> errors = new ArrayList<>();
        int successCount = 0;
        int errorCount = 0;

        try {
            // Process Excel file with default settings
            List<DailyMealExcelDto> excelDtos = new ExcelUtil().processExcel(
                    file,
                    DailyMealExcelDto.class,
                    0, // default sheet index
                    0  // default header row index
            );

            // Filter valid DTOs
            List<DailyMealExcelDto> validDtos = excelDtos.stream()
                    .filter(dto -> dto.getDate() != null && !dto.getDate().isBlank() && dto.getDishId() != null)
                    .toList();

            // Convert Jalali dates to LocalDate and group by date
            DateConverter dateConverter = getDateConverter();
            Map<LocalDate, List<Long>> mealsByDate = new HashMap<>();

            for (DailyMealExcelDto dto : validDtos) {
                try {
                    LocalDate gregorianDate = parseJalaliDate(dto.getDate(), dateConverter);
                    mealsByDate.computeIfAbsent(gregorianDate, k -> new ArrayList<>())
                            .add(dto.getDishId());
                } catch (Exception e) {
                    errors.add("Invalid date format: " + dto.getDate() + " - " + e.getMessage());
                    errorCount++;
                }
            }

            // Pre-fetch all dishes for validation
            Set<Long> allDishIds = validDtos.stream()
                    .map(DailyMealExcelDto::getDishId)
                    .collect(Collectors.toSet());

            Map<Long, Dish> dishMap = dishRepository.findAllById(allDishIds).stream()
                    .collect(Collectors.toMap(Dish::getId, Function.identity()));

            // Process each date
            for (Map.Entry<LocalDate, List<Long>> entry : mealsByDate.entrySet()) {
                LocalDate mealDate = entry.getKey();
                List<Long> dishIds = entry.getValue().stream().distinct().toList(); // remove duplicates

                try {
                    // Check if daily meal already exists
                    List<DailyMeal> existingMeals = dailyMealRepository.findByDate(mealDate);
                    DailyMeal dailyMeal;

                    if (!existingMeals.isEmpty()) {
                        // Update existing meal
                        dailyMeal = existingMeals.get(0);
                    } else {
                        // Create new daily meal
                        dailyMeal = DailyMeal.builder()
                                .date(mealDate)
                                .dailyMealDishes(new ArrayList<>())
                                .build();
                    }

                    // Get existing dish IDs to avoid duplicates
                    Set<Long> existingDishIds = dailyMeal.getDailyMealDishes().stream()
                            .map(dmd -> dmd.getDish().getId())
                            .collect(Collectors.toSet());

                    // Add new dishes
                    boolean hasNewDishes = false;
                    for (Long dishId : dishIds) {
                        if (!existingDishIds.contains(dishId)) {
                            Dish dish = dishMap.get(dishId);
                            if (dish != null) {
                                DailyMealDish dailyMealDish = new DailyMealDish(dailyMeal, dish);
                                dailyMeal.getDailyMealDishes().add(dailyMealDish);
                                hasNewDishes = true;
                            } else {
                                errors.add("Dish not found with ID: " + dishId + " for date: " + mealDate);
                            }
                        }
                    }

                    // Save only if there are changes
                    if (hasNewDishes || existingMeals.isEmpty()) {
                        dailyMealRepository.save(dailyMeal);
                        successCount++;
                    }

                } catch (Exception e) {
                    errors.add("Error processing date " + mealDate + ": " + e.getMessage());
                    errorCount++;
                }
            }

        } catch (ExcelUtil.ExcelProcessingException e) {
            errors.add("Excel processing failed: " + e.getMessage());
            if (e.getErrors() != null) {
                e.getErrors().forEach(error ->
                        errors.add("Row " + error.getRowNumber() + ": " + error.getMessage())
                );
            }
            errorCount++;
        }

        return resultBuilder
                .successCount(successCount)
                .errorCount(errorCount)
                .skippedCount(0) // no skipping logic in simplified version
                .errors(errors)
                .totalProcessed(successCount + errorCount)
                .build();
    }

    private LocalDate parseJalaliDate(String jalaliDateStr, DateConverter dateConverter) {
        String[] parts = jalaliDateStr.split("/");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid date format. Expected: yyyy/mm/dd");
        }

        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);

        return dateConverter.jalaliToGregorian(year, month, day);
    }

    private void handleExcelProcessingException(ExcelUtil.ExcelProcessingException e,
                                                List<String> errors) {
        errors.add("Excel processing failed: %s".formatted(e.getMessage()));
        if (e.getErrors() != null) {
            e.getErrors().forEach(error ->
                    errors.add(String.format("Row %d: %s",
                            error.getRowNumber(), error.getMessage()))
            );
        }
    }

}
