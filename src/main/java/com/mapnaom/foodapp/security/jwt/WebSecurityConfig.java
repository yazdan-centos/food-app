package com.mapnaom.foodapp.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security Configuration with Privilege-based Authorization.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig {

    // === Role Constants ===
    public static final String ADMIN = "ROLE_ADMIN";
    public static final String STAFF = "ROLE_STAFF";
    public static final String USER = "ROLE_USER";
    public static final String GUEST = "ROLE_GUEST";

    // === User Management Privileges ===
    public static final String CREATE_USER = "CREATE_USER";
    public static final String EDIT_USER = "EDIT_USER";
    public static final String DELETE_USER = "DELETE_USER";
    public static final String VIEW_USER = "VIEW_USER";

    // === Daily Meal Management Privileges ===
    public static final String CREATE_DAILY_MEAL = "CREATE_DAILY_MEAL";
    public static final String EDIT_DAILY_MEAL = "EDIT_DAILY_MEAL";
    public static final String DELETE_DAILY_MEAL = "DELETE_DAILY_MEAL";
    public static final String VIEW_DAILY_MEAL = "VIEW_DAILY_MEAL";

    // === Dish Management Privileges ===
    public static final String CREATE_DISH = "CREATE_DISH";
    public static final String EDIT_DISH = "EDIT_DISH";
    public static final String DELETE_DISH = "DELETE_DISH";
    public static final String VIEW_DISH = "VIEW_DISH";
    public static final String UPDATE_DISH_AVAILABILITY = "UPDATE_DISH_AVAILABILITY";

    // === System Management Privileges ===
    public static final String VIEW_REPORTS = "VIEW_REPORTS";
    public static final String MANAGE_SETTINGS = "MANAGE_SETTINGS";
    public static final String MANAGE_CONTRACTORS = "MANAGE_CONTRACTORS";

    // === Customer Privileges ===
    public static final String CREATE_RESERVATION = "CREATE_RESERVATION";
    public static final String EDIT_OWN_RESERVATION = "EDIT_OWN_RESERVATION";
    public static final String CANCEL_OWN_RESERVATION = "CANCEL_OWN_RESERVATION";

    // === Profile Management Privileges ===
    public static final String VIEW_OWN_PROFILE = "VIEW_OWN_PROFILE";
    public static final String EDIT_OWN_PROFILE = "EDIT_OWN_PROFILE";

    // === Kitchen/Staff Privileges ===
    public static final String VIEW_ORDERS_TO_PREPARE = "VIEW_ORDERS_TO_PREPARE";
    public static final String UPDATE_ORDER_STATUS = "UPDATE_ORDER_STATUS";

    private static final String[] PUBLIC_URLS = {
            "/api/v1/auth/authenticate",
            "/api/v1/auth/refresh",
            "/error",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    };


    // == Dependencies ==
    private final JwtUserDetailsService userDetailsService;
    private final JwtRequestFilter jwtRequestFilter;

    // == Configurable CORS allowed origins (set via application.properties) ==
    @Value("${app.cors.allowed-origins:http://localhost,http://localhost:3000,http://localhost:80}")
    private List<String> allowedOrigins;

    // == Define Role Hierarchy with Privilege Inheritance ==
    @Bean
    public static RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy(
                ("%s > " +
                        "%s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s\n%s >" +
                        " %s %s %s %s %s %s %s %s %s %s\n%s > %s %s %s %s %s %s %s\n%s > %s %s")
                        .formatted(ADMIN, CREATE_USER, EDIT_USER, DELETE_USER, VIEW_USER, CREATE_DAILY_MEAL,
                                EDIT_DAILY_MEAL, DELETE_DAILY_MEAL, VIEW_DAILY_MEAL, CREATE_DISH, EDIT_DISH,
                                DELETE_DISH, VIEW_DISH, VIEW_REPORTS, MANAGE_SETTINGS, MANAGE_CONTRACTORS,
                                UPDATE_DISH_AVAILABILITY, VIEW_ORDERS_TO_PREPARE, UPDATE_ORDER_STATUS,
                                VIEW_OWN_PROFILE, EDIT_OWN_PROFILE, CREATE_RESERVATION, EDIT_OWN_RESERVATION,
                                CANCEL_OWN_RESERVATION, STAFF, VIEW_DAILY_MEAL, VIEW_DISH, UPDATE_DISH_AVAILABILITY,
                                VIEW_ORDERS_TO_PREPARE, UPDATE_ORDER_STATUS, VIEW_OWN_PROFILE, EDIT_OWN_PROFILE,
                                CREATE_RESERVATION, EDIT_OWN_RESERVATION, CANCEL_OWN_RESERVATION, USER, VIEW_DISH,
                                VIEW_DAILY_MEAL, VIEW_OWN_PROFILE, EDIT_OWN_PROFILE, CREATE_RESERVATION,
                                EDIT_OWN_RESERVATION, CANCEL_OWN_RESERVATION, GUEST, VIEW_DISH, VIEW_DAILY_MEAL)
        );
    }

    @Bean
    protected BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService); // your custom service
        provider.setPasswordEncoder(bCryptPasswordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .securityMatcher("/api/**")
                // Enable CORS with custom configuration for Nginx reverse proxy
                .cors(cors -> cors.configurationSource(apiConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_URLS).permitAll()
                        .requestMatchers("/api/v1/auth/logout", "/api/v1/auth/validate").authenticated()
                        .anyRequest().authenticated()
                )
                .authenticationProvider(daoAuthenticationProvider()) // <== Register DAO provider here
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    protected CorsConfigurationSource apiConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Use configurable allowed origins from application.properties
        // In a monolithic setup with Nginx reverse proxy, this should include the Nginx public IP/domain
        configuration.setAllowedOrigins(allowedOrigins);

        // Allow standard HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // Allow all headers (including Authorization for JWT)
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "Origin",
                "X-Requested-With",
                "X-Forwarded-For",
                "X-Forwarded-Proto",
                "X-Forwarded-Host"
        ));

        // Expose headers that frontend might need to read
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Disposition"
        ));

        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);

        // Cache preflight response for 1 hour
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration); // Apply CORS settings to API paths only
        return source;
    }

    @Bean
    public DefaultWebSecurityExpressionHandler customWebSecurityExpressionHandler() {
        DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy());
        return expressionHandler;
    }
}