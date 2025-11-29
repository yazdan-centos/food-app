package com.mapnaom.foodapp.specifications;

import com.mapnaom.foodapp.searchForms.RoleSearchForm;
import com.mapnaom.foodapp.models.Role;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class RoleSpecification {
    public static Specification<Role> getSpecification(RoleSearchForm searchForm) {
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
                        "%" + searchForm.getName().toLowerCase() + "%"
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
