package com.mapnaom.foodapp.exceptions;

public class PersonnelHasReservationsException extends RuntimeException {


    public PersonnelHasReservationsException(String persCode,String fullName) {
        super("برای کدپرسنلی "+persCode+" "+fullName+" رزروهای مرتبط وجود دارد و قابل حذف نیست.");

    }
}
