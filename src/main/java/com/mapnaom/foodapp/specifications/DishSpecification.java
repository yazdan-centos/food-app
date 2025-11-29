package com.mapnaom.foodapp.specifications;

import com.mapnaom.foodapp.models.Dish;
import com.mapnaom.foodapp.searchForms.DishSearchForm;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;


public class DishSpecification {

    public static Specification<Dish> getSpecification(DishSearchForm searchForm) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by id if provided
            if (searchForm.getId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), searchForm.getId()));
            }

            // Filter by name if provided (case-insensitive search)
            if (searchForm.getName() != null && !searchForm.getName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%%%s%%".formatted(searchForm.getName().toLowerCase())
                ));
            }
            // Filter by price if provided
            if (searchForm.getPrice() != null) {
                predicates.add(criteriaBuilder.equal(root.get("price"), searchForm.getPrice()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
