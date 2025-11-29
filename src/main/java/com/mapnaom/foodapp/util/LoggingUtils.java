package com.mapnaom.foodapp.util;

import org.slf4j.MDC;

import java.util.Map;

/**
 * Small helper for working with Mapped Diagnostic Context (MDC) values.
 */
public final class LoggingUtils {
    private LoggingUtils() {}

    public static void putAll(Map<String, String> map) {
        if (map == null) return;
        map.forEach(MDC::put);
    }

    public static void clear() {
        MDC.clear();
    }

    public static void remove(String key) {
        MDC.remove(key);
    }
}

