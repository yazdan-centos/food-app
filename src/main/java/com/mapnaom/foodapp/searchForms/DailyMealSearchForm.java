package com.mapnaom.foodapp.searchForms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor@JsonIgnoreProperties(ignoreUnknown = true)
public class DailyMealSearchForm {
    private Long id;
    private LocalDate date;
    private Integer itemsCount;
}
