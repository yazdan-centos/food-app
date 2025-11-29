package com.mapnaom.foodapp.mappers;

import com.mapnaom.foodapp.models.Privilege;
import com.mapnaom.foodapp.dtos.PrivilegeDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PrivilegeMapper {
    PrivilegeDto toDto(Privilege privilege);
    Privilege toEntity(PrivilegeDto privilegeDto);
}