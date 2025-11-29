package com.mapnaom.foodapp.mappers;

import com.mapnaom.foodapp.models.Role;
import com.mapnaom.foodapp.dtos.RoleDto;
import org.mapstruct.*;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = PrivilegeMapper.class)
public interface RoleMapper {
    RoleDto toDto(Role role);

    Role toEntity(com.mapnaom.foodapp.security.RoleDto roleDto);

    com.mapnaom.foodapp.security.RoleDto toDto1(Role role);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Role partialUpdate(com.mapnaom.foodapp.security.RoleDto roleDto, @MappingTarget Role role);

    Role toEntity(RoleDto roleDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Role partialUpdate(RoleDto roleDto, @MappingTarget Role role);
}