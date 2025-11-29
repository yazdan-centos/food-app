package com.mapnaom.foodapp.specifications;

import com.mapnaom.foodapp.models.DailyMeal;
import com.mapnaom.foodapp.searchForms.DailyMealSearchForm;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Specification for filtering DailyMeal entities based on search criteria.
 */
public class DailyMealSpecification {

    /**
     * Builds a Specification for DailyMeal based on the provided search form.
     *
     * @param form the DailyMealSearchForm containing filter criteria.
     * @return a Specification to be used with a JPA query.
     */
    public static Specification<DailyMeal> withFilter(DailyMealSearchForm form) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by id if provided
            if (form.getId() != null) {
                predicates.add(cb.equal(root.get("id"), form.getId()));
            }

            // Filter by date if provided
            if (form.getDate() != null) {
                predicates.add(cb.equal(root.get("date"), form.getDate()));
            }

            // Filter by the number of DailyMealDishes if provided
            if (form.getItemsCount() != null) {
                // Uses CriteriaBuilder.size() to compare the collection size of dailyMealDishes.
                predicates.add(cb.equal(cb.size(root.get("dailyMealDishes")), form.getItemsCount()));
            }


            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
