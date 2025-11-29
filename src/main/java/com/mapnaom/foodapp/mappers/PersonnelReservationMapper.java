package com.mapnaom.foodapp.mappers;

import com.mapnaom.foodapp.dtos.PersonnelReservationDto;
import com.mapnaom.foodapp.models.DailyMealDish;
import com.mapnaom.foodapp.models.Reservation;
import org.mapstruct.*;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PersonnelReservationMapper {

    /* ---------- entity → DTO ---------- */


    @Mapping(source = "dailyMealDish.dish.id",   target = "dailyMealDish.dishId")
        // the Meal object itself is copied automatically
    PersonnelReservationDto toDto(Reservation reservation);

    /* Convert **each** DailyMealDish in the list to DishesDto */
    @Mapping(source = "dish.id",   target = "dishId")
    @Mapping(source = "dish.name", target = "dishName")
    PersonnelReservationDto.DailyMealDishDto1.DailyMealDto1.DishesDto
    toDishDto(DailyMealDish dailyMealDish);

    /* ---------- DTO → entity (for creates / updates) ---------- */


    @Mapping(source = "dailyMealDish.dishId", target = "dailyMealDish.dish.id")
    Reservation toEntity(PersonnelReservationDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)

    @Mapping(source = "dailyMealDish.dishId", target = "dailyMealDish.dish.id")
    Reservation partialUpdate(PersonnelReservationDto dto,
                              @MappingTarget Reservation reservation);
}
