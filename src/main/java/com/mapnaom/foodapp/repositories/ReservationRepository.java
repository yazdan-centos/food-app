package com.mapnaom.foodapp.repositories;

import com.mapnaom.foodapp.enums.ReservationStatus;
import com.mapnaom.foodapp.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long>, JpaSpecificationExecutor<Reservation> {

    @Query("select (count(r) > 0) from Reservation r where r.personnel.id = :personnelId")
    boolean existsAllByPersonnel_Id(@Param("personnelId") Long personnelId);

    @Query("select r from Reservation r where r.personnel.id = :personnelId and r.dailyMeal.id = :dailyMealId")
    Optional<Reservation> findByPersonnelIdAndDailyMeal_Id(@Param("personnelId") Long personnelId, @Param("dailyMealId") Long dailyMealId);

    @Query("select (count(r) > 0) from Reservation r where r.dailyMeal.id = :dailyMealId")
    boolean existsAllByDailyMeal_Id(@Param("dailyMealId") Long dailyMealId);

    @Query("select (count(r) > 0) from Reservation r where r.id = :aLong")
    @Override
    boolean existsById(@Param("aLong") Long aLong);

    @Query("SELECT r FROM Reservation r " +
            "LEFT JOIN FETCH r.personnel p " +
            "LEFT JOIN FETCH r.dailyMeal dm " +
            "LEFT JOIN FETCH r.dailyMealDish dmd " +
            "LEFT JOIN FETCH dmd.dish d " +
            "WHERE r.id = :id")
    Optional<Reservation> findByIdWithDetails(@Param("id") Long id);

    @Query("select r from Reservation r where r.personnel = :personnel and r.dailyMeal.date = :date")
    Optional<Reservation> findReservationByPersonnelAndDailyMeal_Date(@Param("personnel") Personnel personnel, @Param("date") LocalDate date);


    @Query("""
            select r from Reservation r
            where r.dailyMeal.jalaliYear = :jYear and r.dailyMeal.jalaliMonth = :jMonth and r.createdBy = :currentUsername""")
    List<Reservation> findByDailyMeal_JalaliYearAndDailyMeal_JalaliMonthAndCreatedBy(@Param("jYear") int jYear, @Param("jMonth") int jMonth, @Param("currentUsername") String currentUsername);


    @Query("SELECT r FROM Reservation r WHERE r.reservationStatus = :status AND r.dailyMeal.date = :date")
    List<Reservation> findByReservationStatusAndDailyMeal_Date(@Param("status") com.mapnaom.foodapp.enums.ReservationStatus status, @Param("date") LocalDate date);

    @Query("select (count(r) > 0) from Reservation r where r.personnel = :personnel and r.dailyMealDish = :dailyMealDish")
    boolean existsByPersonnelAndDailyMealDish(@Param("personnel") Personnel personnel, @Param("dailyMealDish") DailyMealDish dailyMealDish);

    // In ReservationRepository
    boolean existsByPersonnelUsernameAndDailyMealIdAndDailyMealDishId(
            String username, Long dailyMealId, Long dailyMealDishId);

    List<Reservation> findByPersonnelUsernameAndDailyMealIdAndReservationStatus(String username, Long dailyMealId, ReservationStatus status);

    Collection<Reservation> findByDailyMeal_Date(LocalDate date);
}
