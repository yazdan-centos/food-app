package com.mapnaom.foodapp.services;

import com.mapnaom.foodapp.dtos.DailyMealDishDto;
import com.mapnaom.foodapp.dtos.DishDto;
import com.mapnaom.foodapp.exceptions.DishNotFoundException;
import com.mapnaom.foodapp.mappers.DailyMealDishMapper;
import com.mapnaom.foodapp.mappers.DishMapper;
import com.mapnaom.foodapp.models.DailyMealDish;
import com.mapnaom.foodapp.repositories.DailyMealDishRepository;
import com.mapnaom.foodapp.repositories.DishRepository; // <--- We need this
import com.mapnaom.foodapp.searchForms.DailyMealDishSearchForm;
import com.mapnaom.foodapp.specifications.DailyMealDishSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for managing DailyMealDish operations.
 * <p>
 * Provides methods for creating, retrieving, updating, and deleting DailyMealDish entries.
 * Also supports paginated retrieval of DailyMealDish records.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Transactional
public class DailyMealDishService {

    private final DailyMealDishRepository dailyMealDishRepository;
    private final DailyMealDishMapper dailyMealDishMapper;
    private final DishRepository dishRepository; // Injected for Dish existence checks
    private final DishMapper dishMapper;


    public List<DishDto> getAllDishesByDailyMealId(Long dailyMealId) {
        return dailyMealDishRepository.findAllByDailyMealId(dailyMealId)
                .stream()
                .map(DailyMealDish::getDish).map(dishMapper::toDto)
                .collect(Collectors.toList());
    }


    /**
     * Retrieves a DailyMealDish by its ID.
     *
     * @param id the unique identifier of the DailyMealDish.
     * @return the corresponding DailyMealDish as a DTO.
     * @throws RuntimeException if no DailyMealDish is found with the given ID.
     */
    public DailyMealDishDto getDailyMealDishById(Long id) {
        DailyMealDish dailyMealDish = dailyMealDishRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("DailyMealDish not found with id: %d".formatted(id)));
                         return dailyMealDishMapper.toDto(dailyMealDish);
    }

    /**
     * Retrieves DailyMealDish entries based on the provided filter.
     *
     * @param form the search form containing filter criteria.
     * @return a list of DailyMealDish DTOs matching the criteria.
     */
    public List<DailyMealDishDto> searchDailyMealDishes(DailyMealDishSearchForm form) {
        return dailyMealDishRepository.findAll(DailyMealDishSpecification.withFilter(form))
                .stream()
                .map(dailyMealDishMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all DailyMealDish entries with pagination and sorting.
     *
     * @param page   zero-based page index.
     * @param size   the size of the page to be returned.
     * @param sortBy the property to sort by.
     * @param order  the sort direction ("ASC" for ascending, "DESC" for descending).
     * @return a Page of DailyMealDishDto objects.
     */
    public Page<DailyMealDishDto> getAllDailyMealDishes(int page, int size, String sortBy, String order) {
        Sort sort = order.equalsIgnoreCase("DESC")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<DailyMealDish> dailyMealDishPage = dailyMealDishRepository.findAll(pageRequest);
        return dailyMealDishPage.map(dailyMealDishMapper::toDto);
    }


    /**
     * Utility method to validate the existence of Dish for each DailyMealDishDto in the list.
     *
     * @param dailyMealDishDtos the list of DailyMealDishDto objects to validate.
     * @throws DishNotFoundException if any dish does not exist.
     */
    private void validateAllDishIds(List<DailyMealDishDto> dailyMealDishDtos) throws DishNotFoundException {
        if (dailyMealDishDtos == null) {
            return; // nothing to validate
        }
        for (DailyMealDishDto dailyMealDishDto : dailyMealDishDtos) {
            Long dishId = dailyMealDishDto.getDishId();
            if (dishId != null && !dishRepository.existsById(dishId)) {
                // Throw custom exception with Persian message
                throw new DishNotFoundException("غذایی با شناسه %d یافت نشد.".formatted(dishId));
            }
        }
    }
}
