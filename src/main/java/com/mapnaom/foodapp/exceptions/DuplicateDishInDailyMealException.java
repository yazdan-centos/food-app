package com.mapnaom.foodapp.exceptions;

public class DuplicateDishInDailyMealException extends RuntimeException {
    public DuplicateDishInDailyMealException(String s) {
        super(s);
    }
}
