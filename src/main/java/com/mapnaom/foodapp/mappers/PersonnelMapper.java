package com.mapnaom.foodapp.mappers;

import com.mapnaom.foodapp.dtos.PersonnelDto;
import com.mapnaom.foodapp.models.Personnel;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PersonnelMapper {
    Personnel toEntity(PersonnelDto personnelDto);

    PersonnelDto toDto(Personnel personnel);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)Personnel
    partialUpdate(PersonnelDto personnelDto, @MappingTarget Personnel personnel);
}