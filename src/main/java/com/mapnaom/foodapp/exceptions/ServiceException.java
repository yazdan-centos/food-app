package com.mapnaom.foodapp.exceptions;

public class ServiceException extends Throwable {
    public ServiceException(String failedToUpdateApplicationSettings, Exception e) {
        super(failedToUpdateApplicationSettings, e);
    }
}
