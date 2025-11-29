package com.mapnaom.foodapp.exceptions;

public class PersonnelAlreadyExistsException extends RuntimeException {
    public PersonnelAlreadyExistsException(String message) {
        super(message);
    }
}