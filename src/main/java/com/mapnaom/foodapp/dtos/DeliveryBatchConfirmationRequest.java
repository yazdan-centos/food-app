package com.mapnaom.foodapp.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryBatchConfirmationRequest {
    @NotNull
    private List<Long> reservationIds;

    @NotNull
    private String deliveredBy;
}
