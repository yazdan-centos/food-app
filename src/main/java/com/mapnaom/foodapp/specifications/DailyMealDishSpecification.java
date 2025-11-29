package com.mapnaom.foodapp.specifications;

import com.mapnaom.foodapp.models.DailyMealDish;
import com.mapnaom.foodapp.searchForms.DailyMealDishSearchForm;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * Specification for filtering DailyMealDish entities based on search criteria.
 */
public class DailyMealDishSpecification {

    /**
     * Builds a Specification for DailyMealDish based on the provided search form.
     *
     * @param form the DailyMealDishSearchForm containing filter criteria.
     * @return a Specification to be used with a JPA query.
     */
    public static Specification<DailyMealDish> withFilter(DailyMealDishSearchForm form) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by id if provided
            if (form.getId() != null) {
                predicates.add(cb.equal(root.get("id"), form.getId()));
            }

            // Filter by dailyMealId if provided
            if (form.getDailyMealId() != null) {
                predicates.add(cb.equal(root.get("dailyMeal").get("id"), form.getDailyMealId()));
            }

            // Filter by dishId if provided
            if (form.getDishId() != null) {
                predicates.add(cb.equal(root.get("dish").get("id"), form.getDishId()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
