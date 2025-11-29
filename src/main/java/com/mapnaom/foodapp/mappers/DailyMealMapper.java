package com.mapnaom.foodapp.mappers;

import com.mapnaom.foodapp.dtos.DailyMealDto;
import com.mapnaom.foodapp.models.DailyMeal;
import com.mapnaom.foodapp.models.DailyMealDish;
import com.mapnaom.foodapp.repositories.DishRepository;
import org.mapstruct.*;

import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {DailyMealDishMapper.class})
public interface DailyMealMapper {


    DailyMeal toEntity(DailyMealDto dailyMealDto);

    @AfterMapping
    default void linkDailyMealDishes(@MappingTarget DailyMeal dailyMeal) {
        if (dailyMeal.getDailyMealDishes() != null) {
            dailyMeal.getDailyMealDishes().forEach(dailyMealDish -> dailyMealDish.setDailyMeal(dailyMeal));
        }
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    DailyMealDto toDto(DailyMeal dailyMeal);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    DailyMeal partialUpdate(DailyMealDto dailyMealDto, @MappingTarget DailyMeal dailyMeal);
}
