package com.mapnaom.foodapp.enums;

import lombok.Getter;

@Getter
public enum Privilege {
    ADMIN("مدیر کل"),
    CREATE_USER("ایجاد کاربر"),
    EDIT_USER("ویرایش کاربر"),
    DELETE_USER("حذف کاربر"),
    VIEW_USER("مشاهده کاربر"),
    CREATE_DAILY_MEAL("ایجاد غذای روزانه"),
    EDIT_DAILY_MEAL("ویرایش غذای روزانه"),
    DELETE_DAILY_MEAL("حذف غذای روزانه"),
    VIEW_DAILY_MEAL("مشاهده غذای روزانه"),
    CREATE_DISH("ایجاد غذا"),
    EDIT_DISH("ویرایش غذا"),
    DELETE_DISH("حذف غذا"),
    VIEW_DISH("مشاهده غذا"),
    VIEW_REPORTS("مشاهده گزارشات"),
    MANAGE_SETTINGS("مدیریت تنظیمات"),
    MANAGE_CONTRACTORS("مدیریت پیمانکاران"),
    UPDATE_DISH_AVAILABILITY("بروزرسانی موجودی غذا"),
    VIEW_ORDERS_TO_PREPARE("مشاهده سفارشات آماده سازی"),
    UPDATE_ORDER_STATUS("بروزرسانی وضعیت سفارش"),
    VIEW_OWN_PROFILE("مشاهده پروفایل خود"),
    EDIT_OWN_PROFILE("ویرایش پروفایل خود"),
    CREATE_RESERVATION("ایجاد رزرو"),
    EDIT_OWN_RESERVATION("ویرایش رزرو خود"),
    CANCEL_OWN_RESERVATION("لغو رزرو خود"),
    STAFF("کارکنان"),
    UPDATE_APP_SETTING("بروزرسانی تنظیمات برنامه");

    private final String persianCaption;

    Privilege(String persianCaption) {
        this.persianCaption = persianCaption;
    }

}
