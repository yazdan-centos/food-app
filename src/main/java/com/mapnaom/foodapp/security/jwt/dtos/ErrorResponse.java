package com.mapnaom.foodapp.security.jwt.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private String code;
    private long timestamp = System.currentTimeMillis();

    public ErrorResponse(String message, String code) {
        this.message = message;
        this.code = code;
    }
}
