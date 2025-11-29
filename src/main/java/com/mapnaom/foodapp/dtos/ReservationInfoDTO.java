package com.mapnaom.foodapp.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
@Getter
@Setter
public class ReservationInfoDTO {
    private Long id;
    private String personnelCode;
    private String dishName;
    private BigDecimal costShares;
    private Integer jalaliYear;
    private Integer jalaliMonth;

    public ReservationInfoDTO(Long id, String personnelCode, String dishName, Integer jalaliYear, Integer jalaliMonth) {
        this.id = id;
        this.personnelCode = personnelCode;
        this.dishName = dishName;
        this.jalaliYear = jalaliYear;
        this.jalaliMonth = jalaliMonth;
    }
}