package com.mapnaom.foodapp.security;

/**
 * Centralized role and privilege constants.
 */
public final class Privileges {

    private Privileges() {
    }

    public static final class Roles {
        public static final String ADMIN = "ROLE_ADMIN";
        public static final String STAFF = "ROLE_STAFF";
        public static final String USER = "ROLE_USER";
        public static final String GUEST = "ROLE_GUEST";

        private Roles() {
        }
    }

    public static final class Privs {
        // User management
        public static final String CREATE_USER = "CREATE_USER";
        public static final String EDIT_USER = "EDIT_USER";
        public static final String DELETE_USER = "DELETE_USER";
        public static final String VIEW_USER = "VIEW_USER";

        // Daily meal management
        public static final String CREATE_DAILY_MEAL = "CREATE_DAILY_MEAL";
        public static final String EDIT_DAILY_MEAL = "EDIT_DAILY_MEAL";
        public static final String DELETE_DAILY_MEAL = "DELETE_DAILY_MEAL";
        public static final String VIEW_DAILY_MEAL = "VIEW_DAILY_MEAL";

        // Dish management
        public static final String CREATE_DISH = "CREATE_DISH";
        public static final String EDIT_DISH = "EDIT_DISH";
        public static final String DELETE_DISH = "DELETE_DISH";
        public static final String VIEW_DISH = "VIEW_DISH";
        public static final String UPDATE_DISH_AVAILABILITY = "UPDATE_DISH_AVAILABILITY";

        // System management
        public static final String VIEW_REPORTS = "VIEW_REPORTS";
        public static final String MANAGE_SETTINGS = "MANAGE_SETTINGS";
        public static final String MANAGE_CONTRACTORS = "MANAGE_CONTRACTORS";

        // Customer
        public static final String CREATE_RESERVATION = "CREATE_RESERVATION";
        public static final String EDIT_OWN_RESERVATION = "EDIT_OWN_RESERVATION";
        public static final String CANCEL_OWN_RESERVATION = "CANCEL_OWN_RESERVATION";

        // Profile
        public static final String VIEW_OWN_PROFILE = "VIEW_OWN_PROFILE";
        public static final String EDIT_OWN_PROFILE = "EDIT_OWN_PROFILE";

        // Kitchen/staff
        public static final String VIEW_ORDERS_TO_PREPARE = "VIEW_ORDERS_TO_PREPARE";
        public static final String UPDATE_ORDER_STATUS = "UPDATE_ORDER_STATUS";

        private Privs() {
        }
    }
}
