package com.mapnaom.foodapp.mappers;

import com.mapnaom.foodapp.dtos.CostSharesDto;

import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CostSharesDtoMapper {
    CostSharesDto toEntity(CostSharesDto costSharesDtoDto);

    CostSharesDto toDto(CostSharesDto costSharesDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    CostSharesDto partialUpdate(CostSharesDto costSharesDtoDto, @MappingTarget CostSharesDto costSharesDto);
}