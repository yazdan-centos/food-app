package com.mapnaom.foodapp.security.jwt;

public final class JwtConstants {

    private JwtConstants() {
        // Private constructor to prevent instantiation
    }

    // Time units in milliseconds
    public static final long ONE_SECOND = 1000L;
    public static final long ONE_MINUTE = 60 * ONE_SECOND;
    public static final long ONE_HOUR = 60 * ONE_MINUTE;
    public static final long ONE_DAY = 24 * ONE_HOUR;
    public static final long ONE_WEEK = 7 * ONE_DAY;

    // Common JWT expiration times
    public static final long FIFTEEN_MINUTES = 15 * ONE_MINUTE;
    public static final long THIRTY_MINUTES = 30 * ONE_MINUTE;
    public static final long FIVE_HOURS = 5 * ONE_HOUR;
    public static final long ONE_MONTH = 30 * ONE_DAY;

    // Default expiration times
    public static final long DEFAULT_ACCESS_TOKEN_EXPIRATION = FIFTEEN_MINUTES;
    public static final long DEFAULT_REFRESH_TOKEN_EXPIRATION = ONE_WEEK;
    public static final long DEFAULT_REMEMBER_ME_EXPIRATION = ONE_MONTH;
}
