package com.mapnaom.foodapp.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, String key, Object val) {
        super(resource + " not found for " + key + "=" + val);
    }

    public ResourceNotFoundException(String msg) {
        super(msg);
    }
    
}