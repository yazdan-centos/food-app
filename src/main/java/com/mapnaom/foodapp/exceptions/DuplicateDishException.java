package com.mapnaom.foodapp.exceptions;

public class DuplicateDishException extends RuntimeException {
    private final String dishName;
    private final Integer dishPrice;

    public DuplicateDishException(String dishName,Integer dishPrice) {
        this.dishName = dishName;
        this.dishPrice = dishPrice;
    }
    @Override
    public String toString() {
        return getMessage();
    }

    @Override
    public String getMessage() {
        return "غذا " + dishName + " با قیمت " + dishPrice + " تومان تکراری است";
    }
}
