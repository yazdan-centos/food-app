package com.mapnaom.foodapp.mappers;

import com.mapnaom.foodapp.dtos.DailyMealListDto;
import com.mapnaom.foodapp.models.DailyMeal;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface DailyMealListMapper {
    DailyMeal toEntity(DailyMealListDto dailyMealListDto);

    @AfterMapping
    default void linkDailyMealDishes(@MappingTarget DailyMeal dailyMeal) {
        dailyMeal.getDailyMealDishes().forEach(dailyMealDish -> dailyMealDish.setDailyMeal(dailyMeal));
    }

    DailyMealListDto toDto(DailyMeal dailyMeal);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    DailyMeal partialUpdate(DailyMealListDto dailyMealListDto, @MappingTarget DailyMeal dailyMeal);
}