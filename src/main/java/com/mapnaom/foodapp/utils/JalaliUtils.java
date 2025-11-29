package com.mapnaom.foodapp.utils;

import com.github.eloyzone.jalalicalendar.DateConverter;
import com.github.eloyzone.jalalicalendar.JalaliDate;

import java.time.LocalDate;

public final class JalaliUtils {

    private static final DateConverter CONVERTER = new DateConverter();

    private JalaliUtils() { }          // utility class, no instances

    public static JalaliDate toJalali(LocalDate gregorian) {
        return CONVERTER.gregorianToJalali(
                gregorian.getYear(),
                gregorian.getMonthValue(),
                gregorian.getDayOfMonth()
        );
    }
}
