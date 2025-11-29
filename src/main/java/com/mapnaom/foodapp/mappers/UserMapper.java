package com.mapnaom.foodapp.mappers;

import com.mapnaom.foodapp.dtos.UserDto;
import com.mapnaom.foodapp.models.User;
import org.mapstruct.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {RoleMapper.class})public interface UserMapper {
    User toEntity(UserDto userDto);

    UserDto toDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UserDto userDto, @MappingTarget User user);

    @AfterMapping
    default void linkRoles(@MappingTarget User user) {
        if (user.getRoles() != null) {
            user.getRoles().forEach(role -> {
                if (role.getUsers() != null) {
                    role.getUsers().add(user);
                }
            });
        }
    }
    @AfterMapping
    default String encodePassword(String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            return rawPassword;
        }

        // Check if already encoded
        if (rawPassword.matches("^\\$2[ayb]\\$\\d{2}\\$.{53}$")) {
            return rawPassword;
        }
        return new BCryptPasswordEncoder().encode(rawPassword);
    }
}
