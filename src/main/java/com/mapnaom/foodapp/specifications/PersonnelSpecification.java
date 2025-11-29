package com.mapnaom.foodapp.specifications;

import com.mapnaom.foodapp.models.Personnel;
import com.mapnaom.foodapp.searchForms.PersonnelSearchForm;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * Specification for filtering Personnel entities based on search criteria.
 */
public class PersonnelSpecification {

    /**
     * Builds a Specification for Personnel based on the provided search form.
     *
     * @param form the search criteria encapsulated in a PersonnelSearchForm.
     * @return a Specification to be used with a JPA query.
     */
    public static Specification<PersonnelSearchForm> withFilter(PersonnelSearchForm form) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (form.getId() != null) {
                predicates.add(cb.equal(root.get("id"), form.getId()));
            }
            if (form.getUsername() != null && !form.getUsername().trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("username")), "%" + form.getUsername() + "%"));
            }
            if (form.getPersCode() != null && !form.getPersCode().trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("persCode")), "%" + form.getPersCode() + "%"));
            }
            if (form.getFirstName() != null && !form.getFirstName().trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("firstName")), "%" + form.getFirstName() + "%"));
            }
            if (form.getLastName() != null && !form.getLastName().trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("lastName")), "%" + form.getLastName() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
        public static Specification<Personnel> withFilterByFullName(String fullName){
            return (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (fullName != null && !fullName.trim().isEmpty()) {
                    String[] names = fullName.split(" ");
                    if (names.length == 2) {
                        String firstName = names[0];
                        String lastName = names[1];
                        predicates.add(cb.like(cb.lower(root.get("firstName")), "%" + firstName + "%"));
                        predicates.add(cb.like(cb.lower(root.get("lastName")), "%" + lastName + "%"));
                    } else if (names.length == 1) {
                        String name = names[0];
                    }
                }
                return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
            };

        }
    }
