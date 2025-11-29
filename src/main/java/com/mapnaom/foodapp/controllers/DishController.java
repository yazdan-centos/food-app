package com.mapnaom.foodapp.controllers;

import com.mapnaom.foodapp.dtos.DishDto;
import com.mapnaom.foodapp.dtos.SelectOption;
import com.mapnaom.foodapp.mappers.DishMapper;
import com.mapnaom.foodapp.repositories.DishRepository;
import com.mapnaom.foodapp.searchForms.DishSearchForm;
import com.mapnaom.foodapp.services.DishService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.management.Attribute;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/dishes")
@RequiredArgsConstructor
@Tag(name = "Dishes", description = "Dish management")
public class DishController {

    private final DishService dishService;
    private final DishRepository dishRepository;
    private final DishMapper dishMapper;

    @PostMapping
    @Operation(summary = "Create a new dish")
    public ResponseEntity<DishDto> createDish(@RequestBody DishDto dishDto) {
        DishDto createdDish = dishService.createDish(dishDto);
        return new ResponseEntity<>(createdDish, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a dish by ID")
    public ResponseEntity<DishDto> getDishById(@PathVariable Long id) {
        DishDto dishDto = dishService.getDishById(id);
        return ResponseEntity.ok(dishDto);
    }

    /**
     * Searches dishes based on the provided search criteria and returns a paginated and sorted page of DishDto objects.
     *
     * @param form   the search criteria.
     * @param page   zero-based page index.
     * @param size   the size of the page to be returned.
     * @param sortBy the property to sort by.
     * @param order  the sort direction ("ASC" for ascending, "DESC" for descending).
     * @return a Page of DishDto objects.
     */
    @GetMapping
    @Operation(summary = "Search for dishes with pagination and sorting")
    public ResponseEntity<Page<DishDto>> searchDishes(
            @ModelAttribute DishSearchForm form,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String order) {
        Page<DishDto> dishPage = dishService.searchDishes(form, page, size, sortBy, order);
        return ResponseEntity.ok(dishPage);
    }
    @GetMapping("/select")
    @Operation(summary = "Get a list of dishes as select options")
    public ResponseEntity<List<SelectOption>> selectOptionsDishes(@ModelAttribute DishSearchForm form) {
        List<SelectOption> dishes = dishService.selectOptionsDishes(form);
        return ResponseEntity.ok(dishes);
    }


    @PutMapping("/{id}")
    @Operation(summary = "Update an existing dish")
    public ResponseEntity<DishDto> updateDish(
            @PathVariable @NotNull Long id,
            @RequestBody DishDto dishDto
    ) {
        try {
            DishDto updatedDish = dishService.updateDish(id, dishDto);
            return ResponseEntity.ok(updatedDish);
        } catch (Exception e) {
                throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a dish by ID")
    public ResponseEntity<Void> deleteDish(@PathVariable Long id) {
        try {
            dishService.deleteDish(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Imports dishes from an uploaded Excel file.
     *
     * @param file the Excel file containing dish records
     * @return a list of imported DishDto objects
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file){
        List<DishDto> importedDishes = dishService.importDishesFromExcel(file);
        return ResponseEntity.ok("تعداد %d رکورد غذا با موفقیت از فایل اکسل وارد شد.".formatted(importedDishes.size()));
    }

    @GetMapping("/download-all-dishes.xlsx")
    @Operation(summary = "Export dishes to an Excel file")
    public ResponseEntity<byte[]> exportDishes(
            @ModelAttribute DishSearchForm form,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String order) {

        try {
            byte[] fileContent = dishService.exportDishesToExcel(form, sortBy, order);

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=dishes.xlsx")
                    .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .body(fileContent);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
