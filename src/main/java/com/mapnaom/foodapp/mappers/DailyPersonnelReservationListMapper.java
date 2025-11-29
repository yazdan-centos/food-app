package com.mapnaom.foodapp.mappers;

import com.mapnaom.foodapp.dtos.DailyPersonnelReservationListDto;
import com.mapnaom.foodapp.models.Reservation;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {CostSharesDtoMapper.class})
public interface DailyPersonnelReservationListMapper {
    @Mapping(source = "dailyMealId", target = "dailyMeal.id")
    @Mapping(source = "dailyMealDishDishPrice", target = "dailyMealDish.dish.price")
    @Mapping(source = "dailyMealDishDishName", target = "dailyMealDish.dish.name")
    @Mapping(source = "dailyMealDishDishId", target = "dailyMealDish.dish.id")
    @Mapping(source = "personnelLastName", target = "personnel.lastName")
    @Mapping(source = "personnelFirstName", target = "personnel.firstName")
    @Mapping(source = "personnelPersCode", target = "personnel.persCode")
    @Mapping(source = "dailyMealDate", target = "dailyMeal.date")
    @Mapping(source = "personnelId", target = "personnel.id")
    @Mapping(source = "reservationStatus", target = "reservationStatus")
    @Mapping(source = "costShares", target = "costShare")
    Reservation toEntity(DailyPersonnelReservationListDto dailyPersonnelReservationListDto);

    @InheritInverseConfiguration(name = "toEntity")
    DailyPersonnelReservationListDto toDto(Reservation reservation);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Reservation partialUpdate(DailyPersonnelReservationListDto dailyPersonnelReservationListDto, @MappingTarget Reservation reservation);
}