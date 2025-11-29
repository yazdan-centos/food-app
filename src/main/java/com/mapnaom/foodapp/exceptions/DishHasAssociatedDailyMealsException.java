package com.mapnaom.foodapp.exceptions;

import java.text.NumberFormat;
import java.util.Locale;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DishHasAssociatedDailyMealsException extends RuntimeException {
    private String dishName;
    private Integer price;
    public DishHasAssociatedDailyMealsException(String dishName, Integer price) {
        this.dishName = dishName;
        this.price = price;
    }
    @Override
    public String toString(){
        return getMessage();
    }

    public String getMessage(){
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("fa-IR"));
        return "غذای " + dishName + " با قیمت " + formatter.format(price) + " در منوهای روزانه استفاده شده است. لطفا ابتدا آنها را حذف نمایید.";
    }
}
