package com.mapnaom.foodapp.controllers;

import com.mapnaom.foodapp.dtos.SelectOption;
import com.mapnaom.foodapp.searchForms.RoleSearchForm;
import com.mapnaom.foodapp.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @GetMapping("/options")
    public List<SelectOption> selectRoleOptions(@ModelAttribute RoleSearchForm searchForm) {
        return roleService.selectOptionsRoles(searchForm);
    }
}
