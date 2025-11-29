package com.mapnaom.foodapp.exceptions;

public class AppSettingNotFoundException extends RuntimeException {

    public AppSettingNotFoundException(String message) {
        super(message);
    }

    public AppSettingNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}


