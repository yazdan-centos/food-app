package com.mapnaom.foodapp.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class ImportOptions {
    @Builder.Default
    boolean skipExisting = false;
    @Builder.Default
    boolean replaceExisting = false;
    @Builder.Default
    boolean strictValidation = false;
    @Builder.Default
    boolean stopOnError = false;
    @Builder.Default
    int sheetIndex = 0;
    @Builder.Default
    int headerRowIndex = 0;
    LocalDate startDate;
    LocalDate endDate;
}
