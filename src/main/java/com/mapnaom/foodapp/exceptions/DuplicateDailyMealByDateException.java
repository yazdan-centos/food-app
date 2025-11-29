package com.mapnaom.foodapp.exceptions;

public class DuplicateDailyMealByDateException extends RuntimeException {
    public DuplicateDailyMealByDateException(String message) {
        super(message);
    }
}