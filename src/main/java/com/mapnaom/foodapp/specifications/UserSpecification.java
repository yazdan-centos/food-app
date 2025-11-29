package com.mapnaom.foodapp.specifications;


import com.mapnaom.foodapp.searchForms.UserSearchForm;
import com.mapnaom.foodapp.models.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * Specification for filtering User entities based on search criteria.
 */
public class UserSpecification {

    /**
     * Builds a Specification for User based on the provided search form.
     *
     * @param form the search criteria encapsulated in a UserSearchForm.
     * @return a Specification to be used with a JPA query.
     */
    public static Specification<User> withFilter(UserSearchForm form) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (form.getId() != null) {
                predicates.add(cb.equal(root.get("id"), form.getId()));
            }
            if (form.getUsername() != null && !form.getUsername().trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("username")),getFormatted(form.getUsername())));
            }
            if (form.getPersCode() != null && !form.getPersCode().trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("employeeCode")), getFormatted(form.getPersCode())));
            }
            if (form.getFirstName() != null && !form.getFirstName().trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("firstName")), getFormatted(form.getFirstName())));
            }
            if (form.getLastName() != null && !form.getLastName().trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("lastName")),getFormatted(form.getLastName())));
            }
            if (form.getEnabled() != null) {
                predicates.add(cb.equal(root.get("enabled"), form.getEnabled()));
            }
            if (form.getAccountLocked() != null) {
                predicates.add(cb.equal(root.get("accountLocked"), form.getAccountLocked()));
            }
            if (form.getTokenExpired() != null) {
                predicates.add(cb.equal(root.get("tokenExpired"), form.getTokenExpired()));
            }
            if (form.getAccountExpired() != null) {
                predicates.add(cb.equal(root.get("accountExpired"), form.getAccountExpired()));
            }
            if (form.getCredentialsExpired() != null) {
                predicates.add(cb.equal(root.get("credentialsExpired"), form.getCredentialsExpired()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static @NotNull String getFormatted(String string) {
        return "%%%s%%".formatted(string.toLowerCase());
    }

    public static Specification<User> withFilterByFullName(String fullName) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (fullName != null && !fullName.trim().isEmpty()) {
                String[] names = fullName.trim().split("\\s+");
                if (names.length >= 2) {
                    // If there are two or more parts, treat first as firstName and rest as lastName
                    String firstName = names[0];
                    String lastName = String.join(" ", java.util.Arrays.copyOfRange(names, 1, names.length));
                    predicates.add(cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%"));
                    predicates.add(cb.like(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%"));
                } else if (names.length == 1) {
                    // If only one name part, search in both firstName and lastName
                    String name = names[0].toLowerCase();
                    Predicate firstNamePredicate = cb.like(cb.lower(root.get("firstName")), "%" + name + "%");
                    Predicate lastNamePredicate = cb.like(cb.lower(root.get("lastName")), "%" + name + "%");
                    predicates.add(cb.or(firstNamePredicate, lastNamePredicate));
                }
            }
            return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
