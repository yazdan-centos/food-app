package com.mapnaom.foodapp.mappers;

import com.mapnaom.foodapp.dtos.DishDto;
import com.mapnaom.foodapp.models.Dish;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface DishMapper {

    Dish toEntity(DishDto dishDto);


    DishDto toDto(Dish dish);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)

    Dish partialUpdate(DishDto dishDto, @MappingTarget Dish dish);
}