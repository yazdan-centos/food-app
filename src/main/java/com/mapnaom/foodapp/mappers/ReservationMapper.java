package com.mapnaom.foodapp.mappers;

import com.mapnaom.foodapp.dtos.ReservationDto;
import com.mapnaom.foodapp.models.Reservation;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReservationMapper {
    @Mapping(source = "dailyMealDishId", target = "dailyMealDish.id")
    @Mapping(source = "dailyMealId", target = "dailyMeal.id")
    @Mapping(source = "username", target = "personnel.username")
    @Mapping(source = "reservationStatus", target = "reservationStatus")
    Reservation toEntity(ReservationDto reservationDto);

    @InheritInverseConfiguration(name = "toEntity")
    ReservationDto toDto(Reservation reservation);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Reservation partialUpdate(ReservationDto reservationDto, @MappingTarget Reservation reservation);
}