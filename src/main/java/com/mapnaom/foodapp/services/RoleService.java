package com.mapnaom.foodapp.services;

import com.mapnaom.foodapp.dtos.SelectOption;
import com.mapnaom.foodapp.searchForms.RoleSearchForm;
import com.mapnaom.foodapp.models.Role;
import com.mapnaom.foodapp.security.RoleRepository;
import com.mapnaom.foodapp.specifications.RoleSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    private static final Map<String, String> ROLE_CAPTIONS = Map.of(
            "ROLE_ADMIN", "مدیر سیستم",
            "ROLE_USER", "کاربر",
            "ROLE_MODIFIER", "سرپرست"
    );

    private String getPersianCaption(Role role) {
        return ROLE_CAPTIONS.get(role.getName());
    }


    public List<SelectOption> selectOptionsRoles(RoleSearchForm form) {
        // Create a specification based on the search form criteria
        final var specification = RoleSpecification.getSpecification(form);
        // Create a sort object for Role entities
        Sort sort = Sort.sort(Role.class);
        // Find all roles matching the specification and sort them
        List<Role> roleList = roleRepository.findAll(specification, sort);
        // Convert the list of roles to SelectOption objects
        return roleList.stream()
                .map(role ->
                        new SelectOption(
                                role.getId(),
                                this.getPersianCaption(role)))
                .toList();
    }

}
