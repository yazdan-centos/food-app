package com.mapnaom.foodapp.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A simple DTO representing an option in a select/dropdown list.
 * It typically contains a `value` (the actual data to be sent) and a `label` (the display text).
 * This class is immutable once constructed, as it only provides getters for its fields.
 */
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Getter
@SuppressWarnings("unused")
public class SelectOption {
    private Long value;
    private String label;

}
