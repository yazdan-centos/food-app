package com.mapnaom.foodapp.enums;

import lombok.Getter;

@Getter
public enum ReservationStatus {
    PENDING("در انتظار"), // The reservation exists in the system but no action has been taken yet
    ACTIVE("فعال"), // The reservation date has arrived (it’s the day of the meal)
    DELIVERED("تحویل شده"), // the meal has been physically delivered to the employee
    EXPIRED("منقضی شده"), CANCELLED("لغو شده");
    private final String persianCaption;

    ReservationStatus(String persianCaption) {
        this.persianCaption = persianCaption;
    }

}