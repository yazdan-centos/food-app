package com.mapnaom.foodapp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;

import static com.mapnaom.foodapp.security.Privileges.Privs.*;
import static com.mapnaom.foodapp.security.Privileges.Roles.*;

@Configuration
public class RoleHierarchyConfig {

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy(buildRoleHierarchy());
    }

    private String buildRoleHierarchy() {
        String adminPrivileges = String.join(" ",
                CREATE_USER, EDIT_USER, DELETE_USER, VIEW_USER,
                CREATE_DAILY_MEAL, EDIT_DAILY_MEAL, DELETE_DAILY_MEAL, VIEW_DAILY_MEAL,
                CREATE_DISH, EDIT_DISH, DELETE_DISH, VIEW_DISH,
                VIEW_REPORTS, MANAGE_SETTINGS, MANAGE_CONTRACTORS,
                UPDATE_DISH_AVAILABILITY, VIEW_ORDERS_TO_PREPARE, UPDATE_ORDER_STATUS,
                VIEW_OWN_PROFILE, EDIT_OWN_PROFILE,
                CREATE_RESERVATION, EDIT_OWN_RESERVATION, CANCEL_OWN_RESERVATION
        );

        String staffPrivileges = String.join(" ",
                VIEW_DAILY_MEAL, VIEW_DISH,
                UPDATE_DISH_AVAILABILITY, VIEW_ORDERS_TO_PREPARE, UPDATE_ORDER_STATUS,
                VIEW_OWN_PROFILE, EDIT_OWN_PROFILE,
                CREATE_RESERVATION, EDIT_OWN_RESERVATION, CANCEL_OWN_RESERVATION
        );

        String userPrivileges = String.join(" ",
                VIEW_DISH, VIEW_DAILY_MEAL,
                VIEW_OWN_PROFILE, EDIT_OWN_PROFILE,
                CREATE_RESERVATION, EDIT_OWN_RESERVATION, CANCEL_OWN_RESERVATION
        );

        String guestPrivileges = String.join(" ",
                VIEW_DISH, VIEW_DAILY_MEAL
        );

        return String.format(
                "%s > %s\n%s > %s\n%s > %s\n%s > %s",
                ADMIN, adminPrivileges,
                STAFF, staffPrivileges,
                USER, userPrivileges,
                GUEST, guestPrivileges
        );
    }
}
