package com.mapnaom.foodapp.dtos;

import lombok.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryConfirmationRequest {
    @NotNull
    private Long reservationId;

    @NotNull
    private String deliveredBy;

    private LocalDateTime deliveryTime;
}