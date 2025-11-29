package com.mapnaom.foodapp.utils;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class ImportResult {
    int successCount;
    int errorCount;
    int skippedCount;
    int totalProcessed;
    List<String> errors;

    public boolean hasErrors() {
        return errorCount > 0 || (errors != null && !errors.isEmpty());
    }

    public String getSummary() {
        return String.format("Import completed: %d successful, %d errors, %d skipped",
                successCount, errorCount, skippedCount);
    }
}
