package com.mapnaom.foodapp.exceptions;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        logger.error("Bad credentials exception: {}", ex.getMessage(), ex);
        return createErrorResponse("Invalid username or password", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UsernameNotFoundException ex) {
        logger.error("Username not found exception: {}", ex.getMessage(), ex);
        return createErrorResponse("User not found", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<Map<String, Object>> handleExpiredJwt(ExpiredJwtException ex) {
        logger.error("Expired JWT exception: {}", ex.getMessage(), ex);
        return createErrorResponse("JWT token has expired", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<Map<String, Object>> handleMalformedJwt(MalformedJwtException ex) {
        logger.error("Malformed JWT exception: {}", ex.getMessage(), ex);
        return createErrorResponse("Invalid JWT token", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<Map<String, Object>> handleSignatureException(SignatureException ex) {
        logger.error("JWT signature exception: {}", ex.getMessage(), ex);
        return createErrorResponse("Invalid JWT signature", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        logger.error("Generic exception: {}", ex.getMessage(), ex);
        return createErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String,Object>> handleDataIntegrityViolationException(DataIntegrityViolationException exception){
        logger.error("Data integrity violation exception: {}", exception.getMessage(), exception);
        return createErrorResponse(exception.getMessage(),HttpStatus.BAD_REQUEST);
    }

    // New exception handlers for validation and parsing errors with Persian messages
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error("Illegal argument exception: {}", ex.getMessage(), ex);
        return createErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String paramName = ex.getName();
        String actualValue = ex.getValue() != null ? ex.getValue().toString() : "null";
        String expectedType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";

        String message = String.format("نوع داده برای پارامتر '%s' صحیح نیست. مقدار وارد شده: '%s'، نوع مورد انتظار: %s",
                paramName, actualValue, expectedType);

        logger.error("Method argument type mismatch exception: {}", message, ex);

        return createErrorResponse(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String message = "فرمت داده‌های ورودی صحیح نیست";

        // Check if it's a Jackson parsing error
        if (ex.getCause() instanceof JsonParseException) {
            message = "فرمت JSON ارسالی معتبر نیست";
        } else if (ex.getCause() instanceof JsonMappingException) {
            message = "نگاشت داده‌ها با ساختار مورد انتظار مطابقت ندارد";
        }
        logger.error("HTTP message not readable exception: {}", message, ex);
        return createErrorResponse(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        String message = String.format("پارامتر '%s' الزامی است و نمی‌تواند خالی باشد", ex.getParameterName());
        logger.error("Missing servlet request parameter exception: {}", message, ex);
        return createErrorResponse(message, HttpStatus.BAD_REQUEST);
    }
//PersonnelHasReservationsException
    @ExceptionHandler(PersonnelHasReservationsException.class)
    public ResponseEntity<Map<String, Object>> handlePersonnelHasReservationsException(PersonnelHasReservationsException ex) {
        logger.error("Personnel has reservations exception: {}", ex.getMessage(), ex);
        return createErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    //DishHasAssociatedDailyMealsException
    @ExceptionHandler(DishHasAssociatedDailyMealsException.class)
    public ResponseEntity<Map<String, Object>> handleDishHasDailyMealsException(DishHasAssociatedDailyMealsException ex) {
        logger.error("Dish has daily meals exception: {}", ex.getMessage(), ex);
        return createErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
    //DuplicateDailyMealByDateException
@ExceptionHandler(DuplicateDailyMealByDateException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateDailyMealByDateException(DuplicateDailyMealByDateException ex) {
    logger.error("Duplicate daily meal by date exception: {}", ex.getMessage(), ex);
    return createErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
}
//DuplicateDishInDailyMealException
    @ExceptionHandler(DuplicateDishInDailyMealException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateDishInDailyMealException(DuplicateDishInDailyMealException ex) {
        logger.error("Duplicate dish in daily meal exception: {}", ex.getMessage(), ex);
        return createErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }



    private ResponseEntity<Map<String, Object>> createErrorResponse(String message, HttpStatus status) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("status", status.value());
        errorDetails.put("error", status.getReasonPhrase());
        errorDetails.put("message", message);

        return new ResponseEntity<>(errorDetails, status);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Validation failed");
        response.put("code", "VALIDATION_ERROR");
        response.put("errors", errors);
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
