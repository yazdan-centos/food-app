package com.mapnaom.foodapp.services;

import com.mapnaom.foodapp.dtos.UserDto;
import com.mapnaom.foodapp.mappers.UserMapper;
import com.mapnaom.foodapp.searchForms.UserSearchForm;
import com.mapnaom.foodapp.models.Role;
import com.mapnaom.foodapp.security.RoleRepository;
import com.mapnaom.foodapp.models.User;
import com.mapnaom.foodapp.security.UserRepository;
import com.mapnaom.foodapp.dtos.RoleDto;
import com.mapnaom.foodapp.specifications.UserSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository; // Assuming a RoleRepository for managing roles
    private final UserMapper userMapper;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
    }


    public UserDto createUser(UserDto userDto, String password) {
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new IllegalArgumentException("Username already exists.");
        }
        final var entity = userMapper.toEntity(userDto);
        User savedUser = userRepository.save(entity);
        return convertToDto(savedUser);
    }


    @Transactional(readOnly = true)
    public Optional<UserDto> findUserById(Long id) {
        return userRepository.findById(id).map(this::convertToDto);
    }


    @Transactional(readOnly = true)
    public Optional<UserDto> findUserByUsername(String username) {
        return userRepository.findUserByUsername(username).map(this::convertToDto);
    }


    @Transactional(readOnly = true)
    public Collection<UserDto> findAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<UserDto> searchUsers(UserSearchForm form, int page, int size, String sortBy, String order) {
        Sort sort = order.equalsIgnoreCase("DESC")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<User> userPage = userRepository.findAll(UserSpecification.withFilter(form), pageRequest);
        return userPage.map(userMapper::toDto);
    }


    public UserDto updateUser(Long id, UserDto userDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: %d".formatted(id)));

        final var partialUpdate = userMapper.partialUpdate(userDto,existingUser);
        final var saved = userRepository.save(partialUpdate);
        return userMapper.toDto(saved);
    }


    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Maps a User entity to a UserDto.
     *
     * @param user The User entity.
     * @return The UserDto.
     */
    private UserDto convertToDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setUsername(user.getUsername());
        userDto.setEnabled(user.isEnabled());
        userDto.setAccountLocked(user.isAccountLocked());
        if (user.getRoles() != null) {
            userDto.setRoles(user.getRoles().stream()
                    .map(this::convertRoleToDto)
                    .collect(Collectors.toSet()));
        }
        return userDto;
    }

    /**
     * Maps a Role entity to a RoleDto.
     *
     * @param role The Role entity.
     * @return The RoleDto.
     */
    private RoleDto convertRoleToDto(Role role) {
        RoleDto roleDto = new RoleDto();
        roleDto.setId(role.getId());
        roleDto.setName(role.getName());
        // For simplicity, we are not mapping privileges in this example DTO conversion
        return roleDto;
    }

    /**
     * Maps a UserDto to a User entity.
     *
     * @param userDto The UserDto.
     * @return The User entity.
     */
    private User convertToEntity(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEnabled(userDto.isEnabled());
        user.setAccountLocked(userDto.isAccountLocked());
        return user;
    }
}