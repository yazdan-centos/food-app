package com.mapnaom.foodapp.specifications;

import com.mapnaom.foodapp.dtos.ReservationInfoDTO;
import com.mapnaom.foodapp.models.Reservation;
import com.mapnaom.foodapp.searchForms.ReservationSearchForm;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Specification for filtering Reservation based on ReservationSearchForm.
 */
public class ReservationSpecification {

    /**
     * Builds a dynamic Specification using the fields in ReservationSearchForm.
     */
    public static Specification<Reservation> withFilter(ReservationSearchForm form) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by Reservation ID
            if (form.getId() != null) {
                predicates.add(cb.equal(root.get("id"), form.getId()));
            }

            // Filter by Personnel persCode (personnel.persCode)
            if (form.getPersonnelPersCode() != null && !form.getPersonnelPersCode().isEmpty()) {
                predicates.add(cb.equal(
                        root.get("personnel").get("persCode"),
                        form.getPersonnelPersCode()
                ));
            }

            // Filter by Personnel firstName (personnel.firstName)
            if (form.getPersonnelFirstName() != null && !form.getPersonnelFirstName().isEmpty()) {
                predicates.add(cb.like(
                        cb.lower(root.get("personnel").get("firstName")),
                        "%" + form.getPersonnelFirstName().toLowerCase() + "%"
                ));
            }

            // Filter by Personnel lastName (personnel.lastName)
            if (form.getPersonnelLastName() != null && !form.getPersonnelLastName().isEmpty()) {
                predicates.add(cb.like(
                        cb.lower(root.get("personnel").get("lastName")),
                        "%" + form.getPersonnelLastName().toLowerCase() + "%"
                ));
            }

            // Filter by DailyMeal date (dailyMealDish.dailyMeal.date)
            if (form.getDailyMealDishDailyMealDate() != null) {
                predicates.add(cb.equal(
                        root.get("dailyMealDish").get("dailyMeal").get("date"),
                        form.getDailyMealDishDailyMealDate()
                ));
            }

            // Filter by Dish name (dailyMealDish.dish.name)
            if (form.getDailyMealDishDishName() != null && !form.getDailyMealDishDishName().isEmpty()) {
                predicates.add(cb.like(
                        cb.lower(root.get("dailyMealDish").get("dish").get("name")),
                        "%" + form.getDailyMealDishDishName().toLowerCase() + "%"
                ));
            }

            // Filter by createdTime (exact match)
            if (form.getCreatedTime() != null) {
                predicates.add(cb.equal(
                        root.get("createdTime"),
                        form.getCreatedTime()
                ));
            }

            if (form.getReservationStatus() != null) {
                predicates.add(cb.equal(root.get("reservationStatus"), form.getReservationStatus()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Filter by Jalali year from DailyMeal
     */
    public static Specification<Reservation> byJalaliYear(int year) {
        return (root, query, cb) -> {
            return cb.equal(root.get("dailyMealDish").get("dailyMeal").get("jalaliYear"), year);
        };
    }

    /**
     * Filter by Jalali month from DailyMeal
     */
    public static Specification<Reservation> byJalaliMonth(int month) {
        return (root, query, cb) -> {
            return cb.equal(root.get("dailyMealDish").get("dailyMeal").get("jalaliMonth"), month);
        };
    }

    /**
     * Filter by personnel ID
     */
    public static Specification<Reservation> byPersonnel(Long personnelId) {
        return (root, query, cb) ->
                cb.equal(root.get("personnel").get("id"), personnelId);
    }

    /**
     * Filter by personnel username
     */
    public static Specification<Reservation> byUsername(String username) {
        return (root, query, cb) ->
                cb.equal(root.get("personnel").get("username"), username);
    }

    /**
     * Filter by personnel PersCode
     */
    public static Specification<Reservation> byPersonnelPersCode(String persCode) {
        return (root, query, cb) ->
                cb.equal(root.get("personnel").get("persCode"), persCode);
    }

    /**
     * Filter by dish ID
     */
    public static Specification<Reservation> byDishId(Long dishId) {
        return (root, query, cb) ->
                cb.equal(root.get("dailyMealDish").get("dish").get("id"), dishId);
    }

    /**
     * Filter by dish name (partial match)
     */
    public static Specification<Reservation> byDishName(String dishName) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("dailyMealDish").get("dish").get("name")),
                        "%" + dishName.toLowerCase() + "%");
    }

    /**
     * Filter by date range
     */
    public static Specification<Reservation> betweenDates(LocalDate startDate, LocalDate endDate) {
        return (root, query, cb) ->
                cb.between(root.get("dailyMealDish").get("dailyMeal").get("date"), startDate, endDate);
    }

    public static Specification<ReservationInfoDTO> byYearMonthAndPersCode(int year, int month, String persCode) {
        return (root, query, cb) -> {
            var personnelJoin = root.join("personnel");
            var dailyMealJoin = root.join("dailyMeal");
            assert query != null;
            query.distinct(true);
            return cb.and(
                    cb.equal(dailyMealJoin.get("jalaliYear"), year),
                    cb.equal(dailyMealJoin.get("jalaliMonth"), month),
                    cb.equal(personnelJoin.get("persCode"), persCode)
            );
        };
    }

    public static Specification<Reservation> byMealDate(LocalDate mealDate) {
        return (root, query, cb) ->
                cb.equal(root.get("dailyMealDish").get("dailyMeal").get("date"), mealDate);
    }
}