package com.mapnaom.foodapp.dtos;

import lombok.Data;
import java.io.Serializable;


@Data
public class CostsDto implements Serializable {
    private Long id;
    private Double employeeShare;
    private Double employerShare;
    private Long reservationId;
}